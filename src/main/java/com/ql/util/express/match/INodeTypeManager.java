package com.ql.util.express.match;

/**
 * 节点管理器
 *
 * @author wangyijie
 */
public interface INodeTypeManager {

    /**
     * 根据节点类型名字找到对应节点
     *
     * @param name 节点类型名
     * @return 该节点
     */
    INodeType findNodeType(String name);
}
