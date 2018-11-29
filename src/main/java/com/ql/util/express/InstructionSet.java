package com.ql.util.express;

import com.ql.util.express.instruction.FunctionInstructionSet;
import com.ql.util.express.instruction.OperateDataCacheManager;
import com.ql.util.express.instruction.detail.*;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 表达式执行编译后形成的指令集合
 *
 * @author qhlhl2010@gmail.com
 */

public class InstructionSet implements Serializable {

    private static final long serialVersionUID = 1841743860792681669L;

    private static final transient Log log = LogFactory.getLog(InstructionSet.class);
    public static AtomicInteger uniqIndex = new AtomicInteger(1);
    public static String TYPE_MAIN = "main";
    public static String TYPE_CLASS = "VClass";
    public static String TYPE_FUNCTION = "function";
    public static String TYPE_MARCO = "marco";

    public static boolean printInstructionError = true;

    private String type = "main";
    private String name;
    private String globeName;

    /**
     * 指令
     */
    private Instruction[] instructionList = new Instruction[0];
    /**
     * 函数和宏定义
     */
    private Map<String, FunctionInstructionSet> functionDefine = new HashMap<>();
    /**
     * 为了增加性能，开始的时候缓存为数组
     */
    private Map<String, Object> cacheFunctionSet = null;

    private List<ExportItem> exportVar = new ArrayList<>();
    /**
     * 函数参数定义
     */
    private List<OperateDataLocalVar> parameterList = new ArrayList<>();

    public static int getUniqClassIndex() {
        return uniqIndex.getAndIncrement();
    }

    public InstructionSet(String aType) {
        this.type = aType;
    }

    public String[] getOutFunctionNames() throws Exception {
        Map<String, String> result = new TreeMap<>();
        for (Instruction instruction : instructionList) {
            if (instruction instanceof InstructionCallSelfDefineFunction) {
                String functionName = ((InstructionCallSelfDefineFunction) instruction).getFunctionName();
                if (!functionDefine.containsKey(functionName)) {
                    result.put(functionName, null);
                }
            }
        }
        return result.keySet().toArray(new String[0]);

    }

    public String[] getOutAttrNames() throws Exception {
        Map<String, String> result = new TreeMap<>();
        for (Instruction instruction : instructionList) {
            if (instruction instanceof InstructionLoadAttr) {
                if ("null".equals(((InstructionLoadAttr) instruction).getAttrName())) {
                    continue;
                }
                result.put(((InstructionLoadAttr) instruction).getAttrName(), null);
            }
        }

        // 剔除本地变量定义和别名定义
        for (int i = 0; i < instructionList.length; i++) {
            Instruction instruction = instructionList[i];
            if (instruction instanceof InstructionOperator) {
                String opName = ((InstructionOperator) instruction)
                        .getOperator().getName();
                if (opName != null) {
                    // addOperator(op)中op.name有可能为空
                    if ("def".equalsIgnoreCase(opName)
                            || "exportDef".equalsIgnoreCase(opName)) {
                        String varLocalName = (String) ((InstructionConstData) instructionList[i - 1])
                                .getOperateData().getObject(null);
                        result.remove(varLocalName);
                    } else if ("alias".equalsIgnoreCase(opName)
                            || "exportAlias".equalsIgnoreCase(opName)) {
                        String varLocalName = (String) ((InstructionConstData) instructionList[i - 2])
                                .getOperateData().getObject(null);
                        result.remove(varLocalName);
                    }
                }
            }
        }
        return result.keySet().toArray(new String[0]);
    }


    /**
     * 添加指令，为了提高运行期的效率，指令集用数组存储
     *
     * @param item 指令
     */
    private void addArrayItem(Instruction item) {
        Instruction[] newArray = new Instruction[this.instructionList.length + 1];
        System.arraycopy(this.instructionList, 0, newArray, 0, this.instructionList.length);
        newArray[this.instructionList.length] = item;
        this.instructionList = newArray;
    }

    /**
     * 插入数据
     *
     * @param position 出入位置索引
     * @param item     插入指令对象
     */
    private void insertArrayItem(int position, Instruction item) {
        Instruction[] newArray = new Instruction[this.instructionList.length + 1];
        System.arraycopy(this.instructionList, 0, newArray, 0, position);
        System.arraycopy(this.instructionList, position, newArray, position + 1, this.instructionList.length - position);
        newArray[position] = item;
        this.instructionList = newArray;
    }

    /**
     * @param environment      运行环境
     * @param context          变量上下文
     * @param errorList        错误集合
     * @param isReturnLastData 是否最后的结果，主要是在执行宏定义的时候需要
     * @param log              日志
     * @return 执行结果
     * @throws Exception 异常
     */
    public CallResult execute(RunEnvironment environment, InstructionSetContext context,
                              List<String> errorList, boolean isReturnLastData, Log log)
            throws Exception {
        // 将函数export到上下文中,这儿就是重入也没有关系，不需要考虑并发
        if (cacheFunctionSet == null) {
            Map<String, Object> tempMap = new HashMap<>();
            for (FunctionInstructionSet s : this.functionDefine.values()) {
                tempMap.put(s.name, s.instructionSet);
            }
            cacheFunctionSet = tempMap;
        }

        context.addSymbol(cacheFunctionSet);

        this.executeInnerOrigiInstruction(environment, errorList, log);
        if (!environment.isExit()) {
            // 是在执行完所有的指令后结束的代码
            if (environment.getDataStackSize() > 0) {
                OperateData tmpObject = environment.pop();
                if (tmpObject == null) {
                    environment.quitExpress(null);
                } else {
                    if (isReturnLastData) {
                        if (tmpObject.getType(context) != null && tmpObject.getType(context).equals(void.class)) {
                            environment.quitExpress(null);
                        } else {
                            environment.quitExpress(tmpObject.getObject(context));
                        }
                    } else {
                        environment.quitExpress(tmpObject);
                    }
                }
            }
        }
        if (environment.getDataStackSize() > 1) {
            throw new Exception("在表达式执行完毕后，堆栈中还存在多个数据");
        }
        return OperateDataCacheManager.fetchCallResult(environment.getReturnValue(), environment.isExit());
    }

    private void executeInnerOrigiInstruction(RunEnvironment environment, List<String> errorList, Log log) throws
            Exception {
        Instruction instruction = null;
        try {
            while (environment.programPoint < this.instructionList.length) {
                instruction = this.instructionList[environment.programPoint];
                // 设置log
                instruction.setLog(log);
                instruction.execute(environment, errorList);
            }
        } catch (Exception e) {
            if (printInstructionError) {
                log.error("当前ProgramPoint = " + environment.programPoint);
                log.error("当前指令" + instruction);
                log.error(e);
            }
            throw e;
        }
    }

    public int getInstructionLength() {
        return this.instructionList.length;
    }

    public void addMacroDefine(String macroName, FunctionInstructionSet iset) {
        this.functionDefine.put(macroName, iset);
    }

    public FunctionInstructionSet getMacroDefine(String macroName) {
        return this.functionDefine.get(macroName);
    }

    public FunctionInstructionSet[] getFunctionInstructionSets() {
        return this.functionDefine.values().toArray(new FunctionInstructionSet[0]);
    }

    public void addExportDef(ExportItem e) {
        this.exportVar.add(e);
    }

    public List<ExportItem> getExportDef() {
        return new ArrayList<>(this.exportVar);
    }


    public OperateDataLocalVar[] getParameters() {
        return this.parameterList.toArray(new OperateDataLocalVar[0]);
    }

    public void addParameter(OperateDataLocalVar localVar) {
        this.parameterList.add(localVar);
    }

    public void addInstruction(Instruction instruction) {
        this.addArrayItem(instruction);
    }

    public void insertInstruction(int point, Instruction instruction) {
        this.insertArrayItem(point, instruction);
    }

    public Instruction getInstruction(int point) {
        return this.instructionList[point];
    }

    public int getCurrentPoint() {
        return this.instructionList.length - 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGlobeName() {
        return globeName;
    }

    public void setGlobeName(String globeName) {
        this.globeName = globeName;
    }

    public boolean hasMain() {
        return this.instructionList.length > 0;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "\n" + toString(0);
    }

    public String toString(int level) {
        try {
            StringBuffer buffer = new StringBuffer();
            // 输出宏定义
            for (FunctionInstructionSet set : this.functionDefine.values()) {
                appendSpace(buffer, level);
                buffer.append(set.type).append(":").append(set.name).append("(");
                for (int i = 0; i < set.instructionSet.parameterList.size(); i++) {
                    OperateDataLocalVar var = set.instructionSet.parameterList.get(i);
                    if (i > 0) {
                        buffer.append(",");
                    }
                    buffer.append(var.getType(null).getName()).append(" ").append(var.getName());
                }
                buffer.append("){\n");
                buffer.append(set.instructionSet.toString(level + 1));
                appendSpace(buffer, level);
                buffer.append("}\n");
            }
            for (int i = 0; i < this.instructionList.length; i++) {
                appendSpace(buffer, level);
                buffer.append(i + 1).append(":").append(this.instructionList[i])
                        .append("\n");
            }
            return buffer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void appendSpace(StringBuffer buffer, int level) {
        for (int i = 0; i < level; i++) {
            buffer.append("    ");
        }
    }

    public Instruction[] getInstructionList() {
        return instructionList;
    }

    public List<ExportItem> getExportVar() {
        return exportVar;
    }

    public List<OperateDataLocalVar> getParameterList() {
        return parameterList;
    }
}

	
