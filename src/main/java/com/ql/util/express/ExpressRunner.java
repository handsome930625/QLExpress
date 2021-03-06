package com.ql.util.express;

import com.ql.util.express.instruction.env.IExpressContext;
import com.ql.util.express.instruction.env.InstructionSet;
import com.ql.util.express.instruction.env.InstructionSetRunner;
import com.ql.util.express.instruction.factory.ForRelBreakContinue;
import com.ql.util.express.instruction.factory.InstructionFactory;
import com.ql.util.express.instruction.operator.*;
import com.ql.util.express.instruction.opcache.IOperateDataCache;
import com.ql.util.express.instruction.opcache.OperateDataCacheImpl;
import com.ql.util.express.parse.*;
import com.ql.util.express.rule.Condition;
import com.ql.util.express.rule.Rule;
import com.ql.util.express.rule.RuleManager;
import com.ql.util.express.rule.RuleResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 语法分析和计算的入口类
 *
 * @author xuannan
 */
public class ExpressRunner {

    private static final Log log = LogFactory.getLog(ExpressRunner.class);
    private static final String GLOBAL_DEFINE_NAME = "全局定义";
    /**
     * 是否输出所有的跟踪信息，同时还需要log级别是DEBUG级别
     */
    private boolean isTrace = false;

    /**
     * 是否使用逻辑短路特性增强质量的效率
     */
    private boolean isShortCircuit = true;

    /**
     * 是否需要高精度计算
     */
    private boolean isPrecise = false;

    /**
     * 一段文本对应的指令集的缓存
     */
    private final Map<String, InstructionSet> expressInstructionSetCache = new HashMap<>();

    /**
     * 一段文本对应的规则的缓存
     */
    private final Map<String, Rule> ruleCache = new HashMap<>();

    private ExpressLoader loader;

    private IExpressResourceLoader expressResourceLoader;
    /**
     * 语法定义的管理器
     */
    private NodeTypeManager manager;
    /**
     * 操作符的管理器
     */
    private OperatorFactory operatorManager;
    /**
     * 语法分析器
     */
    private ExpressParse parse;

    /**
     * 缺省的Class查找的包管理器
     */
    private ExpressPackage rootExpressPackage = new ExpressPackage(null);

    private AppendingClassMethodManager appendingClassMethodManager;

    private AppendingClassFieldManager appendingClassFieldManager;

    private ThreadLocal<IOperateDataCache> m_OperateDataObjectCache = new ThreadLocal<IOperateDataCache>() {
        @Override
        protected IOperateDataCache initialValue() {
            return new OperateDataCacheImpl(30);
        }
    };

    public ExpressRunner() {
        this(false, false);
    }

    /**
     * @param isPrecise 是否需要高精度计算支持
     * @param isTrace   是否跟踪执行指令的过程
     */
    public ExpressRunner(boolean isPrecise, boolean isTrace) {
        this(isPrecise, isTrace, new DefaultExpressResourceLoader(), null);
    }

    public ExpressRunner(boolean isPrecise, boolean isTrace, NodeTypeManager manager) {
        this(isPrecise, isTrace, new DefaultExpressResourceLoader(), manager);
    }

    /**
     * @param isPrecise             是否需要高精度计算支持
     * @param isTrace               是否跟踪执行指令的过程
     * @param expressResourceLoader 表达式的资源装载器
     */
    public ExpressRunner(boolean isPrecise, boolean isTrace, IExpressResourceLoader expressResourceLoader,
                         NodeTypeManager manager) {
        this.isTrace = isPrecise;
        this.isPrecise = isTrace;
        this.expressResourceLoader = expressResourceLoader;
        if (manager == null) {
            this.manager = new NodeTypeManager();
        } else {
            this.manager = manager;
        }
        this.operatorManager = new OperatorFactory(this.isPrecise);
        this.loader = new ExpressLoader(this);
        this.parse = new ExpressParse(this.manager, this.expressResourceLoader, this.isPrecise);
        rootExpressPackage.addPackage("java.lang");
        rootExpressPackage.addPackage("java.util");
        // 系统级别的方法
        this.addSystemFunctions();
        this.addSystemOperators();
    }

    private void addSystemOperators() {
        try {
            this.addOperator("instanceof", new OperatorInstanceOf("instanceof"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addSystemFunctions() {
        this.addFunction("max", new OperatorMinMax("max"));
        this.addFunction("min", new OperatorMinMax("min"));
        this.addFunction("round", new OperatorRound("round"));
        this.addFunction("print", new OperatorPrint("print"));
        this.addFunction("println", new OperatorPrintln("println"));
    }

    /**
     * 获取语法定义的管理器
     *
     * @return
     */
    public NodeTypeManager getNodeTypeManager() {
        return this.manager;
    }

    /**
     * 获取操作符号管理器
     *
     * @return
     */
    public OperatorFactory getOperatorFactory() {
        return this.operatorManager;
    }

    public IExpressResourceLoader getExpressResourceLoader() {
        return this.expressResourceLoader;
    }

    /**
     * 添加宏定义 例如： macro 玄难 { abc(userinfo.userId);}
     *
     * @param macroName：玄难
     * @param express      ：abc(userinfo.userId);
     * @throws Exception
     */
    public void addMacro(String macroName, String express) throws Exception {
        String macroExpress = "macro " + macroName + " {" + express + "}";
        this.loader.parseInstructionSet(GLOBAL_DEFINE_NAME, macroExpress);
    }

    /**
     * 装载表达式，但不执行，例如一些宏定义，或者自定义函数
     *
     * @param groupName
     * @param express
     * @throws Exception
     */
    public void loadMutilExpress(String groupName, String express) throws Exception {
        if (groupName == null || groupName.trim().length() == 0) {
            groupName = GLOBAL_DEFINE_NAME;
        }
        this.loader.parseInstructionSet(groupName, express);
    }

    /**
     * 装载文件中定义的Express
     *
     * @param expressName
     * @throws Exception
     */
    public void loadExpress(String expressName) throws Exception {
        this.loader.loadExpress(expressName);
    }

    /**
     * 添加函数定义
     *
     * @param name 函数名称
     * @param op   对应的操作实现类
     */
    public void addFunction(String name, OperatorBase op) {
        this.operatorManager.addOperator(name, op);
        this.manager.addFunctionName(name);
    }

    ;

    /**
     * 添加函数定义扩展类的方法
     *
     * @param name
     * @param bindingClass
     * @param op
     */
    public void addFunctionAndClassMethod(String name, Class<?> bindingClass, OperatorBase op) {
        this.addFunction(name, op);
        this.addClassMethod(name, bindingClass, op);

    }

    ;

    /**
     * 添加类的方法
     *
     * @param field
     * @param bindingClass
     * @param op
     */
    public void addClassField(String field, Class<?> bindingClass, Operator op) {
        this.addClassField(field, bindingClass, Object.class, op);
    }

    /**
     * 添加类的方法
     *
     * @param field
     * @param bindingClass
     * @param returnType
     * @param op
     */
    public void addClassField(String field, Class<?> bindingClass, Class<?> returnType, Operator op) {
        if (this.appendingClassFieldManager == null) {
            this.appendingClassFieldManager = new AppendingClassFieldManager();
        }
        this.appendingClassFieldManager.addAppendingField(field, bindingClass, returnType, op);
    }

    /**
     * 添加类的方法
     *
     * @param name
     * @param bindingClass
     * @param op
     */
    public void addClassMethod(String name, Class<?> bindingClass, OperatorBase op) {
        if (this.appendingClassMethodManager == null) {
            this.appendingClassMethodManager = new AppendingClassMethodManager();
        }
        this.appendingClassMethodManager.addAppendingMethod(name, bindingClass, op);
    }

    /**
     * 获取函数定义，通过函数定义可以拿到参数的说明信息
     *
     * @param name 函数名称
     * @return
     */
    public OperatorBase getFunciton(String name) {
        return this.operatorManager.getOperator(name);
    }

    /**
     * 添加一个类的函数定义，例如：Math.abs(double) 映射为表达式中的 "取绝对值(-5.0)"
     *
     * @param name                 函数名称
     * @param aClassName           类名称
     * @param aFunctionName        类中的方法名称
     * @param aParameterClassTypes 方法的参数类型名称
     * @param errorInfo            如果函数执行的结果是false，需要输出的错误信息
     * @throws Exception
     */
    public void addFunctionOfClassMethod(String name, String aClassName,
                                         String aFunctionName, Class<?>[] aParameterClassTypes,
                                         String errorInfo) throws Exception {
        this.addFunction(name, new OperatorSelfDefineClassFunction(name,
                aClassName, aFunctionName, aParameterClassTypes, null, null, errorInfo));

    }

    /**
     * 添加一个类的函数定义，例如：Math.abs(double) 映射为表达式中的 "取绝对值(-5.0)"
     *
     * @param name                 函数名称
     * @param aClassName           类名称
     * @param aFunctionName        类中的方法名称
     * @param aParameterClassTypes 方法的参数类型名称
     * @param aParameterDesc       方法的参数说明
     * @param aParameterAnnotation 方法的参数注解
     * @param errorInfo            如果函数执行的结果是false，需要输出的错误信息
     * @throws Exception
     */
    public void addFunctionOfClassMethod(String name, String aClassName,
                                         String aFunctionName, Class<?>[] aParameterClassTypes,
                                         String[] aParameterDesc, String[] aParameterAnnotation,
                                         String errorInfo) throws Exception {
        this.addFunction(name, new OperatorSelfDefineClassFunction(name,
                aClassName, aFunctionName, aParameterClassTypes, aParameterDesc, aParameterAnnotation, errorInfo));

    }

    /**
     * 添加一个类的函数定义，例如：Math.abs(double) 映射为表达式中的 "取绝对值(-5.0)"
     *
     * @param name            函数名称
     * @param aClassName      类名称
     * @param aFunctionName   类中的方法名称
     * @param aParameterTypes 方法的参数类型名称
     * @param errorInfo       如果函数执行的结果是false，需要输出的错误信息
     * @throws Exception
     */
    public void addFunctionOfClassMethod(String name, String aClassName,
                                         String aFunctionName, String[] aParameterTypes, String errorInfo)
            throws Exception {
        this.addFunction(name, new OperatorSelfDefineClassFunction(name,
                aClassName, aFunctionName, aParameterTypes, null, null, errorInfo));
    }

    /**
     * 添加一个类的函数定义，例如：Math.abs(double) 映射为表达式中的 "取绝对值(-5.0)"
     *
     * @param name                 函数名称
     * @param aClassName           类名称
     * @param aFunctionName        类中的方法名称
     * @param aParameterTypes      方法的参数类型名称
     * @param aParameterDesc       方法的参数说明
     * @param aParameterAnnotation 方法的参数注解
     * @param errorInfo            如果函数执行的结果是false，需要输出的错误信息
     * @throws Exception
     */
    public void addFunctionOfClassMethod(String name, String aClassName,
                                         String aFunctionName, String[] aParameterTypes,
                                         String[] aParameterDesc, String[] aParameterAnnotation,
                                         String errorInfo)
            throws Exception {
        this.addFunction(name, new OperatorSelfDefineClassFunction(name,
                aClassName, aFunctionName, aParameterTypes, aParameterDesc, aParameterAnnotation, errorInfo));

    }

    /**
     * 用于将一个用户自己定义的对象(例如Spring对象)方法转换为一个表达式计算的函数
     *
     * @param name
     * @param aServiceObject
     * @param aFunctionName
     * @param aParameterClassTypes
     * @param errorInfo
     * @throws Exception
     */
    public void addFunctionOfServiceMethod(String name, Object aServiceObject,
                                           String aFunctionName, Class<?>[] aParameterClassTypes,
                                           String errorInfo) throws Exception {
        this.addFunction(name, new OperatorSelfDefineServiceFunction(name,
                aServiceObject, aFunctionName, aParameterClassTypes, null, null, errorInfo));

    }

    /**
     * 用于将一个用户自己定义的对象(例如Spring对象)方法转换为一个表达式计算的函数
     *
     * @param name
     * @param aServiceObject
     * @param aFunctionName
     * @param aParameterClassTypes
     * @param aParameterDesc       方法的参数说明
     * @param aParameterAnnotation 方法的参数注解
     * @param errorInfo
     * @throws Exception
     */
    public void addFunctionOfServiceMethod(String name, Object aServiceObject,
                                           String aFunctionName, Class<?>[] aParameterClassTypes,
                                           String[] aParameterDesc, String[] aParameterAnnotation,
                                           String errorInfo) throws Exception {
        this.addFunction(name, new OperatorSelfDefineServiceFunction(name,
                aServiceObject, aFunctionName, aParameterClassTypes, aParameterDesc, aParameterAnnotation, errorInfo));

    }

    /**
     * 用于将一个用户自己定义的对象(例如Spring对象)方法转换为一个表达式计算的函数
     *
     * @param name
     * @param aServiceObject
     * @param aFunctionName
     * @param aParameterTypes
     * @param errorInfo
     * @throws Exception
     */
    public void addFunctionOfServiceMethod(String name, Object aServiceObject,
                                           String aFunctionName, String[] aParameterTypes, String errorInfo)
            throws Exception {
        this.addFunction(name, new OperatorSelfDefineServiceFunction(name,
                aServiceObject, aFunctionName, aParameterTypes, null, null, errorInfo));

    }

    public void addFunctionOfServiceMethod(String name, Object aServiceObject,
                                           String aFunctionName, String[] aParameterTypes,
                                           String[] aParameterDesc, String[] aParameterAnnotation,
                                           String errorInfo)
            throws Exception {
        this.addFunction(name, new OperatorSelfDefineServiceFunction(name,
                aServiceObject, aFunctionName, aParameterTypes, aParameterDesc, aParameterAnnotation, errorInfo));

    }

    /**
     * 添加操作符号，此操作符号的优先级与 "*"相同，语法形式也是  data name data
     *
     * @param name
     * @param op
     * @throws Exception
     */
    public void addOperator(String name, Operator op) throws Exception {
        this.addOperator(name, "*", op);
    }

    /**
     * 添加操作符号，此操作符号与给定的参照操作符号在优先级别和语法形式上一致
     *
     * @param name         操作符号名称
     * @param refOpername 参照的操作符号，例如 "+","--"等
     * @param op           操作符实例
     * @throws Exception 异常
     */
    public void addOperator(String name, String refOpername, Operator op) throws Exception {
        this.manager.addOperatorWithLevelOfReference(name, refOpername);
        this.operatorManager.addOperator(name, op);
    }

    /**
     * 添加操作符和关键字的别名，同时对操作符可以指定错误信息。
     * 例如：addOperatorWithAlias("加","+",null)
     *
     * @param keyWordName     操作符别名
     * @param realKeyWordName 目前操作符关键字
     * @param errorInfo       增加错误信息
     * @throws Exception 异常
     */
    public void addOperatorWithAlias(String keyWordName, String realKeyWordName,
                                     String errorInfo) throws Exception {
        if (errorInfo != null && errorInfo.trim().length() == 0) {
            errorInfo = null;
        }
        // 添加函数别名
        if (this.manager.isFunction(realKeyWordName)) {
            this.manager.addFunctionName(keyWordName);
            this.operatorManager.addOperatorWithAlias(keyWordName, realKeyWordName, errorInfo);
            return;
        }
        NodeType realNodeType = this.manager.findNodeType(realKeyWordName);
        if (realNodeType == null) {
            throw new Exception("关键字：" + realKeyWordName + "不存在");
        }
        boolean isExist = this.operatorManager.isExistOperator(realNodeType.getName());
        if (!isExist && errorInfo != null) {
            throw new Exception("关键字：" + realKeyWordName + "是通过指令来实现的，不能设置错误的提示信息，errorInfo 必须是 null");
        }
        if (!isExist || errorInfo == null) {
            //不需要新增操作符号，只需要建立一个关键子即可
            this.manager.addOperatorWithRealNodeType(keyWordName, realNodeType.getName());
        } else {
            this.manager.addOperatorWithLevelOfReference(keyWordName, realNodeType.getName());
            this.operatorManager.addOperatorWithAlias(keyWordName, realNodeType.getName(), errorInfo);
        }
    }

    /**
     * 替换操作符处理
     *
     * @param name
     */
    public OperatorBase replaceOperator(String name, OperatorBase op) {
        return this.operatorManager.replaceOperator(name, op);
    }

    public ExpressPackage getRootExpressPackage() {
        return this.rootExpressPackage;
    }

    /**
     * 清除缓存
     */
    public void clearExpressCache() {
        this.expressInstructionSetCache.clear();
    }

    /**
     * 根据表达式的名称进行执行
     *
     * @param name
     * @param context
     * @param errorList
     * @param isTrace
     * @param isCatchException
     * @param aLog
     * @return
     * @throws Exception
     */
    public Object executeByExpressName(String name, IExpressContext<String, Object> context, List<String> errorList,
                                       boolean isTrace, boolean isCatchException, Log aLog) throws Exception {
        return InstructionSetRunner.executeOuter(this, this.loader.getInstructionSet(name), this.loader, context, errorList,
                isTrace, isCatchException, aLog, false);

    }

    /**
     * 执行指令集(兼容老接口,请不要自己管理指令缓存，直接使用execute(InstructionSet instructionSets,....... )
     * 清理缓存可以使用clearExpressCache()函数
     *
     * @param instructionSets
     * @param context
     * @param errorList
     * @param isTrace
     * @param isCatchException
     * @param aLog
     * @return
     * @throws Exception
     */
    @Deprecated
    public Object execute(InstructionSet[] instructionSets, IExpressContext<String, Object> context, List<String> errorList,
                          boolean isTrace, boolean isCatchException, Log aLog) throws Exception {
        return InstructionSetRunner.executeOuter(this, instructionSets[0], this.loader, context, errorList,
                isTrace, isCatchException, aLog, false);
    }

    /**
     * 执行指令集
     *
     * @param instructionSets
     * @param context
     * @param errorList
     * @param isTrace
     * @param isCatchException
     * @param aLog
     * @return
     * @throws Exception
     */
    public Object execute(InstructionSet instructionSets, IExpressContext<String, Object> context, List<String> errorList,
                          boolean isTrace, boolean isCatchException, Log aLog) throws Exception {
        return InstructionSetRunner.executeOuter(this, instructionSets, this.loader, context, errorList,
                isTrace, isCatchException, aLog, false);
    }

    /**
     * 执行一段文本
     *
     * @param expressString 程序文本
     * @param context       执行上下文
     * @param errorList     输出的错误信息List
     * @param isCache       是否使用Cache中的指令集
     * @param isTrace       是否输出详细的执行指令信息
     * @return
     * @throws Exception
     */
    public Object execute(String expressString, IExpressContext<String, Object> context,
                          List<String> errorList, boolean isCache, boolean isTrace) throws Exception {
        return this.execute(expressString, context, errorList, isCache, isTrace, null);
    }

    /**
     * 执行一段文本
     *
     * @param expressString 程序文本
     * @param context       执行上下文
     * @param errorList     输出的错误信息List
     * @param isCache       是否使用Cache中的指令集
     * @param isTrace       是否输出详细的执行指令信息
     * @param aLog          输出的log
     * @return
     * @throws Exception
     */
    public Object execute(String expressString, IExpressContext<String, Object> context,
                          List<String> errorList, boolean isCache, boolean isTrace, Log aLog)
            throws Exception {
        InstructionSet parseResult;
        if (isCache) {
            parseResult = expressInstructionSetCache.get(expressString);
            if (parseResult == null) {
                synchronized (expressInstructionSetCache) {
                    parseResult = expressInstructionSetCache.get(expressString);
                    if (parseResult == null) {
                        parseResult = this.parseInstructionSet(expressString);
                        expressInstructionSetCache.put(expressString,
                                parseResult);
                    }
                }
            }
        } else {
            // main
            parseResult = this.parseInstructionSet(expressString);
        }
        return InstructionSetRunner.executeOuter(this, parseResult, this.loader, context, errorList,
                isTrace, false, aLog, false);
    }

    public RuleResult executeRule(String expressString, IExpressContext<String, Object> context, boolean isCache, boolean isTrace)
            throws Exception {
        Rule rule;
        if (isCache) {
            rule = ruleCache.get(expressString);
            if (rule == null) {
                synchronized (ruleCache) {
                    rule = ruleCache.get(expressString);
                    if (rule == null) {
                        rule = this.parseRule(expressString);
                        ruleCache.put(expressString,
                                rule);
                    }
                }
            }
        } else {
            rule = this.parseRule(expressString);
        }
        return RuleManager.executeRule(this, rule, context, isCache, isTrace);
    }

    private static Pattern patternRule = Pattern.compile("rule[\\s]+'([^']+)'[\\s]+name[\\s]+'([^']+)'[\\s]+");

    public Rule parseRule(String text)
            throws Exception {
        String ruleName = null;
        String ruleCode = null;
        Matcher matcher = patternRule.matcher(text);
        if (matcher.find()) {
            ruleCode = matcher.group(1);
            ruleName = matcher.group(2);
            text = text.substring(matcher.end());
        }

        Map<String, String> selfDefineClass = new HashMap<String, String>();
        for (ExportItem item : this.loader.getExportInfo()) {
            if (item.getType().equals(InstructionSet.TYPE_CLASS)) {
                selfDefineClass.put(item.getName(), item.getName());
            }
        }

        Word[] words = this.parse.splitWords(rootExpressPackage, text, isTrace, selfDefineClass);
        ExpressNode root = this.parse.parse(rootExpressPackage, words, text, isTrace, selfDefineClass);
        Rule rule = RuleManager.createRule(root, words);
        rule.setCode(ruleCode);
        rule.setName(ruleName);
        return rule;
    }

    public Condition parseContition(String text)
            throws Exception {

        Map<String, String> selfDefineClass = new HashMap<String, String>();
        for (ExportItem item : this.loader.getExportInfo()) {
            if (item.getType().equals(InstructionSet.TYPE_CLASS)) {
                selfDefineClass.put(item.getName(), item.getName());
            }
        }

        Word[] words = this.parse.splitWords(rootExpressPackage, text, isTrace, selfDefineClass);
        ExpressNode root = this.parse.parse(rootExpressPackage, words, text, isTrace, selfDefineClass);
        return RuleManager.createCondition(root, words);
    }

    /**
     * 解析一段文本，生成指令集合
     *
     * @param text 脚本
     * @return 指令集合
     * @throws Exception 异常
     */
    public InstructionSet parseInstructionSet(String text)
            throws Exception {
        Map<String, String> selfDefineClass = new HashMap<>();
        for (ExportItem item : this.loader.getExportInfo()) {
            if (item.getType().equals(InstructionSet.TYPE_CLASS)) {
                selfDefineClass.put(item.getName(), item.getName());
            }
        }

        ExpressNode root = this.parse.parse(this.rootExpressPackage, text, isTrace, selfDefineClass);
        InstructionSet result = createInstructionSet(root, "main");
        if (this.isTrace && log.isDebugEnabled()) {
            log.debug(result);
        }
        return result;
    }

    /**
     * 输出全局定义信息
     *
     * @return
     */
    public ExportItem[] getExportInfo() {
        return this.loader.getExportInfo();
    }

    /**
     * 优先从本地指令集缓存获取指令集，没有的话生成并且缓存在本地
     *
     * @param expressString
     * @return
     * @throws Exception
     */
    public InstructionSet getInstructionSetFromLocalCache(String expressString)
            throws Exception {
        InstructionSet parseResult = expressInstructionSetCache.get(expressString);
        if (parseResult == null) {
            synchronized (expressInstructionSetCache) {
                parseResult = expressInstructionSetCache.get(expressString);
                if (parseResult == null) {
                    parseResult = this.parseInstructionSet(expressString);
                    expressInstructionSetCache.put(expressString,
                            parseResult);
                }
            }
        }
        return parseResult;
    }

    public InstructionSet createInstructionSet(ExpressNode root, String type)
            throws Exception {
        InstructionSet result = new InstructionSet(type);
        createInstructionSet(root, result);
        return result;
    }

    /**
     * 将语法树构建成指令集
     */
    public void createInstructionSet(ExpressNode root, InstructionSet result)
            throws Exception {
        Stack<ForRelBreakContinue> forStack = new Stack<>();
        createInstructionSetPrivate(result, forStack, root, true);
        if (forStack.size() > 0) {
            throw new Exception("For处理错误");
        }
    }

    /**
     * 创建指令
     */
    public boolean createInstructionSetPrivate(InstructionSet result,
                                               Stack<ForRelBreakContinue> forStack, ExpressNode node,
                                               boolean isRoot) throws Exception {
        InstructionFactory factory = InstructionFactory
                .getInstructionFactory(node.getInstructionFactory());
        return factory.createInstruction(this, result, forStack, node, isRoot);
    }

    /**
     * 获取一个表达式需要的外部变量名称列表
     *
     * @param express
     * @return
     * @throws Exception
     */
    public String[] getOutVarNames(String express) throws Exception {
        return this.parseInstructionSet(express).getOutAttrNames();
    }

    public String[] getOutFunctionNames(String express) throws Exception {
        return this.parseInstructionSet(express).getOutFunctionNames();
    }


    public boolean isShortCircuit() {
        return isShortCircuit;
    }

    public void setShortCircuit(boolean isShortCircuit) {
        this.isShortCircuit = isShortCircuit;
    }

    public AppendingClassMethodManager getAppendingClassMethodManager() {
        return appendingClassMethodManager;
    }

    public AppendingClassFieldManager getAppendingClassFieldManager() {
        return appendingClassFieldManager;
    }

    public IOperateDataCache getOperateDataCache() {
        return this.m_OperateDataObjectCache.get();
    }
}
