package com.ql.util.express.parse;

import com.ql.util.express.match.INodeTypeManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangyijie
 */
public class NodeTypeManager implements INodeTypeManager {

    private static final Log log = LogFactory.getLog(NodeTypeManager.class);

    public String[] splitWord;
    private String[] keyWords;
    private String[] nodeTypeDefines;
    protected String[][] instructionFactoryMapping;
    protected Map<String, NodeType> nodeTypes = new HashMap<>();

    /**
     * 所有的函数定义
     */
    protected Map<String, String> functions = new HashMap<>();

    public NodeTypeManager() {
        this(new KeyWordDefine4Java());
    }

    public NodeTypeManager(KeyWordDefine4SQL keyWorkDefine) {
        this.splitWord = keyWorkDefine.splitWord;
        this.keyWords = keyWorkDefine.keyWords;
        this.nodeTypeDefines = keyWorkDefine.nodeTypeDefines;
        this.initial();
    }

    public NodeTypeManager(KeyWordDefine4Java keyWorkDefine) {
        this.splitWord = keyWorkDefine.splitWord;
        WordSplit.sortSplitWord(this.splitWord);
        this.keyWords = keyWorkDefine.keyWords;
        this.nodeTypeDefines = keyWorkDefine.nodeTypeDefines;
        this.instructionFactoryMapping = keyWorkDefine.instructionFactoryMapping;
        this.initial();
        this.addOperatorWithRealNodeType("and", "&&");
        this.addOperatorWithRealNodeType("or", "||");

    }

    /**
     * 初始化关键字、类型
     */
    public void initial() {
        // 创建所有的关键字
        NodeType[] tempKeyWordNodeTypes = new NodeType[splitWord.length + keyWords.length];
        for (int i = 0; i < splitWord.length; i++) {
            tempKeyWordNodeTypes[i] = this.createNodeType(splitWord[i] + ":TYPE=KEYWORD");
        }

        for (int i = splitWord.length; i < tempKeyWordNodeTypes.length; i++) {
            tempKeyWordNodeTypes[i] = this.createNodeType(keyWords[i - splitWord.length] + ":TYPE=KEYWORD");
        }
        // 初始化所有的类型信息，
        for (NodeType tempKeyWordNodeType : tempKeyWordNodeTypes) {
            tempKeyWordNodeType.initial();
        }

        // 创建所有的类型信息，但不能初始化
        NodeType[] nodeTypes = new NodeType[nodeTypeDefines.length];
        for (int i = 0; i < nodeTypeDefines.length; i++) {
            nodeTypes[i] = this.createNodeType(nodeTypeDefines[i]);
        }
        // 初始化所有的类型信息，
        for (NodeType nodeType : nodeTypes) {
            nodeType.initial();
        }

        // 初始化指令Factory，node type 绑定指令工厂
        if (this.instructionFactoryMapping != null) {
            for (String[] list : this.instructionFactoryMapping) {
                for (String s : list[0].split(",")) {
                    this.findNodeType(s).setInstructionFactory(list[1]);
                }
            }
        }
    }

    /**
     * 创建节点类型，需要注意的是不能初始化，必须所有的类型都创建完成后才能调用初始化方法
     *
     * @param defineStr 定义的字符串
     * @return 节点
     */
    private NodeType createNodeType(String defineStr) {
        // 避免对操作符号":"的错误处理
        int index = defineStr.indexOf(":", 1);
        String name = defineStr.substring(0, index).trim();
        NodeType define = nodeTypes.get(name);
        if (define != null) {
            log.warn("节点类型定义重复:" + name + " 定义1=" + define.getDefineStr() + " 定义2=" + defineStr);
            throw new RuntimeException("节点类型定义重复:" + name + " 定义1=" + define.getDefineStr() + " 定义2=" + defineStr);
        }
        define = new NodeType(this, name, defineStr);
        nodeTypes.put(name, define);
        return define;
    }

    /**
     * 根据类型名称查找节点类型
     *
     * @param name 节点名称
     * @return 节点对象
     */
    @Override
    public NodeType findNodeType(String name) {
        NodeType result = nodeTypes.get(name);
        if (result == null) {
            throw new RuntimeException("没有定义的节点类型：" + name);
        }
        while (result.getRealNodeType() != null) {
            result = result.getRealNodeType();
        }
        return result;
    }

    /**
     * 增加关键字，但是用实际的类型代替，例如 :"如果"->"if"
     *
     * @param keyWordName
     * @param realName
     */
    public void addOperatorWithRealNodeType(String keyWordName, String realName) {
        NodeType target = this.createNodeType(keyWordName + ":TYPE=KEYWORD,REAL=" + realName);
        target.initial();
    }

    /**
     * 增加新的操作符号，其优先级别，以及语法关系与参照的操作符号一致
     *
     * @param operName    操作符名
     * @param refOperName 引用
     * @throws Exception 异常
     */
    public void addOperatorWithLevelOfReference(String operName, String refOperName) throws Exception {
        NodeType target = this.createNodeType(operName + ":TYPE=KEYWORD");
        target.initial();
        NodeType[] list = this.getNodeTypesByKind(NodeTypeKind.OPERATOR);
        NodeType refNodeType = this.findNodeType(refOperName);
        target.setInstructionFactory(refNodeType.getInstructionFactory());
        for (NodeType item : list) {
            if (item.isContainerChild(refNodeType)) {
                item.addChild(target);
                return;
            }
        }
    }

    /**
     * 判断是否存在节点类型定义
     *
     * @param name
     * @return
     */
    public NodeType isExistNodeTypeDefine(String name) {
        NodeType result = nodeTypes.get(name);
        if (result != null && result.getRealNodeType() != null) {
            result = result.getRealNodeType();
        }
        return result;
    }

    /**
     * 根据操作符类型找到对应类型的节点集合
     */
    public NodeType[] getNodeTypesByKind(NodeTypeKind aKind) {
        List<NodeType> result = new ArrayList<>();
        for (NodeType item : this.nodeTypes.values()) {
            if (item.getKind() == aKind) {
                result.add(item);
            }
        }
        return result.toArray(new NodeType[0]);
    }

    public boolean isFunction(String name) {
        return this.functions.containsKey(name);
    }

    public void addFunctionName(String name) {
        this.functions.put(name, name);
    }
}
