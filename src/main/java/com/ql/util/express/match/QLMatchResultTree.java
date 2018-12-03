package com.ql.util.express.match;

import java.util.ArrayList;
import java.util.List;

/**
 * 语法分析树节点类型类
 *
 * @author wangyijie
 */
public class QLMatchResultTree {
    /**
     * 匹配到的节点类型
     */
    public INodeType matchNodeType;
    /**
     * 数据
     */
    public IDataNode ref;
    /**
     * 目标解析类型，比如不同的 ( 解析的方式都不相同
     */
    public INodeType targetNodeType;
    /**
     * 左子树
     */
    private List<QLMatchResultTree> left;
    /**
     * 右子树
     */
    private List<QLMatchResultTree> right;

    public QLMatchResultTree(INodeType nodeType, IDataNode ref, INodeType targetNodeType) {
        this(nodeType, ref);
        this.targetNodeType = targetNodeType;
    }

    public QLMatchResultTree(INodeType nodeType, IDataNode ref) {
        this.matchNodeType = nodeType;
        this.ref = ref;
    }

    public IDataNode getRef() {
        return ref;
    }

    public List<QLMatchResultTree> getLeft() {
        return this.left;
    }

    public void addLeft(QLMatchResultTree node) {
        if (this.left == null) {
            this.left = new ArrayList<>();
        }
        this.left.add(node);
    }

    public void addLeftAll(List<QLMatchResultTree> list) {
        if (this.left == null) {
            this.left = new ArrayList<>();
        }
        this.left.addAll(list);
    }

    public void addRightAll(List<QLMatchResultTree> list) {
        if (this.right == null) {
            this.right = new ArrayList<>();
        }
        this.right.addAll(list);
    }

    /**
     * 将匹配到的节点转成 targetType
     */
    public void transferExpressNodeType(IDataNode sourceNode, INodeType targetType) {
        sourceNode.setNodeType(targetType);
        if (targetType == targetType.getManager().findNodeType("CONST_STRING")) {
            sourceNode.setObjectValue(sourceNode.getValue());
            sourceNode.setTreeType(targetType.getManager().findNodeType("CONST"));
        }
    }

    /**
     * 类型替换，构建ref的左右节点
     */
    public void buildExpressNodeTree() {
        if (this.targetNodeType != null) {
            transferExpressNodeType(this.ref, this.targetNodeType);
        }
        if (this.left != null) {
            for (QLMatchResultTree item : left) {
                this.ref.addLeftChild(item.ref);
                item.buildExpressNodeTree();
            }
        }
        if (this.right != null) {
            for (QLMatchResultTree item : right) {
                this.ref.addLeftChild(item.ref);
                item.buildExpressNodeTree();
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        printNode(builder, 1);
        return builder.toString();
    }

    public void printNode(StringBuilder builder, int level) {
        builder.append(level).append(":");
        for (int i = 0; i < level; i++) {
            builder.append("   ");
        }
        builder.append(ref.getValue()).append(":").append(this.matchNodeType.getName())
                .append("\n");
        if (this.left != null) {
            for (QLMatchResultTree item : this.left) {
                item.printNode(builder, level + 1);
            }
        }
        if (this.right != null) {
            for (QLMatchResultTree item : this.right) {
                item.printNode(builder, level + 1);
            }
        }
    }
}
