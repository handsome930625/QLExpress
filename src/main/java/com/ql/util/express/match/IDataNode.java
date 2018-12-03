package com.ql.util.express.match;


/**
 * @author wangyijie
 */
public interface IDataNode {
    /**
     * 设置该数据节点类型
     *
     * @param type 节点类型
     */
    void setNodeType(INodeType type);

    /**
     * 如果是节点树，那么设置节点树的类型
     *
     * @param findNodeType 树类型
     */
    void setTreeType(INodeType findNodeType);

    /**
     * 获取该数据节点类型
     *
     * @return 获取该数据节点类型
     */
    INodeType getNodeType();

    /**
     * 获取节点树
     *
     * @return 获取节点树
     */
    INodeType getTreeType();

    /**
     * 添加左子节点
     *
     * @param ref 左孩子节点
     */
    void addLeftChild(IDataNode ref);

    /**
     * 创建DataNode节点
     *
     * @param type  节点类型
     * @param value 值
     * @return DataNode节点
     * @throws Exception 异常
     */
    IDataNode createExpressNode(INodeType type, String value) throws Exception;

    /**
     * 获取节点的值
     *
     * @return 节点值
     */
    String getValue();

    /**
     * 设置节点值
     *
     * @param value 值
     */
    void setObjectValue(Object value);
}
