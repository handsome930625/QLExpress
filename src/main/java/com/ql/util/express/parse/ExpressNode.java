package com.ql.util.express.parse;

import com.ql.util.express.match.IDataNode;
import com.ql.util.express.match.INodeType;

import java.util.ArrayList;
import java.util.List;


/**
 * @author wangyijie
 */
public class ExpressNode implements IDataNode {
    /**
     * 节点类型
     */
    private NodeType nodeType;
    /**
     * 节点类型树 与nodeType包含关系
     */
    private NodeType treeType;
    /**
     * 节点值
     */
    private String value;
    /**
     * 节点原始值
     */
    private String origValue;

    private Object objectValue;
    /**
     * 父节点
     */
    private ExpressNode parent;
    private List<ExpressNode> leftChildren;
    private List<ExpressNode> rightChildren;
    private boolean isSplitStatement = false;
    /**
     * 行号
     */
    private int line;
    /**
     * 列号
     */
    private int col;
    /**
     * word的序号
     */
    private int wordIndex = -1;

    public int getWordIndex() {
        return wordIndex;
    }

    public ExpressNode(NodeType aType, String aValue) throws Exception {
        this(aType, aValue, null, null, null, -1, -1, -1);
    }

    public ExpressNode(NodeType aType, String aValue, String aOrgiValue, Object aObjectValue, NodeType aTreeType, int aLine, int aCol, int wordIndex) throws Exception {
        if (aType == null) {
            throw new Exception(aValue + " 没有找到对应的节点类型");
        }
        this.nodeType = aType;
        this.treeType = aTreeType;
        if (aValue != null && aValue.length() > 0) {
            this.value = aValue;
        }
        if (aOrgiValue != null && aOrgiValue.length() > 0) {
            this.origValue = aOrgiValue;
        }
        if (aObjectValue != null) {
            this.objectValue = aObjectValue;
        }
        this.line = aLine;
        this.col = aCol;
        this.wordIndex = wordIndex;
    }

    public boolean isTypeEqualsOrChild(String parent) {
        boolean result = this.getTreeType().isEqualsOrChild(parent);
        if (!result && this.treeType != null) {
            result = this.getNodeType().isEqualsOrChild(parent);
        }
        return result;
    }

    @Override
    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType type) {
        this.nodeType = type;
    }

    @Override
    public String getValue() {
        if (value == null) {
            return this.nodeType.getName();
        } else {
            return value;
        }
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSplitStatement() {
        return isSplitStatement;
    }

    public void setSplitStatement(boolean isSplitStatement) {
        this.isSplitStatement = isSplitStatement;
    }

    public String getInstructionFactory() {
        if (this.nodeType.getInstructionFactory() != null) {
            return this.nodeType.getInstructionFactory();
        }
        if (this.treeType != null && this.treeType.getInstructionFactory() != null) {
            return this.treeType.getInstructionFactory();
        }
        throw new RuntimeException("没有定义节点的指令InstructionFactory信息：" + this.nodeType.getName() + (this.treeType == null ? "" : " 或者 " + this.treeType.getName()));
    }

    public String getOrigValue() {
        return origValue;
    }

    public void setOrigValue(String origValue) {
        this.origValue = origValue;
    }

    public Object getObjectValue() {
        return objectValue;
    }

    @Override
    public void setObjectValue(Object objectValue) {
        this.objectValue = objectValue;
    }

    public ExpressNode getParent() {
        return parent;
    }

    public void setParent(ExpressNode parent) {
        this.parent = parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public NodeType getRealTreeType() {
        return this.treeType;
    }

    @Override
    public NodeType getTreeType() {
        if (this.treeType == null) {
            return this.nodeType;
        } else {
            return treeType;
        }
    }

    public void setTreeType(NodeType treeType) {
        this.treeType = treeType;
    }

    public List<ExpressNode> getLeftChildren() {
        return leftChildren;
    }

    public void setLeftChildren(List<ExpressNode> leftChildren) {
        this.leftChildren = leftChildren;
    }

    public List<ExpressNode> getRightChildren() {
        return rightChildren;
    }

    public void setRightChildren(List<ExpressNode> rightChildren) {
        this.rightChildren = rightChildren;
    }

    public void addLeftChild(ExpressNode leftChild) {
        if (leftChild == null) {
            return;
        }
        if (this.leftChildren == null) {
            this.leftChildren = new ArrayList<>();
        }
        this.leftChildren.add(leftChild);
    }

    public void addRightChild(ExpressNode rightChild) {
        if (rightChild == null) {
            return;
        }
        if (this.leftChildren == null) {
            this.leftChildren = new ArrayList<>();
        }
        this.leftChildren.add(rightChild);
    }

    /**
     * 获取左右节点数组
     * 注意先左节点再右节点
     */
    public ExpressNode[] getChildren() {
        List<ExpressNode> result = new ArrayList<>();
        if (this.leftChildren != null && this.leftChildren.size() > 0) {
            result.addAll(this.leftChildren);
        }
        if (this.rightChildren != null && this.rightChildren.size() > 0) {
            result.addAll(this.rightChildren);
        }
        return result.toArray(new ExpressNode[0]);
    }

    @Override
    public String toString() {
        return (this.origValue == null ? this.getValue() : this.origValue) + (this.nodeType.getName() == null ? "" : (":" + this.nodeType.getName()));
    }

    @Override
    public IDataNode createExpressNode(INodeType type, String value) throws Exception {
        return new ExpressNode((NodeType) type, value);
    }

    @Override
    public void setNodeType(INodeType type) {
        this.setNodeType((NodeType) type);

    }

    @Override
    public void addLeftChild(IDataNode ref) {
        this.addLeftChild((ExpressNode) ref);
    }

    @Override
    public void setTreeType(INodeType aTreeType) {
        this.setTreeType((NodeType) aTreeType);
    }
}
