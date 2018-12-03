package com.ql.util.express.match;


/**
 * 匹配类型
 *
 * @author xuannan
 */
public interface INodeType {
    /**
     * 获取节点名字
     *
     * @return 名字
     */
    String getName();

    /**
     * 获取节点管理器
     *
     * @return 节点管理器
     */
    INodeTypeManager getManager();

    /**
     * 获取匹配节点
     *
     * @return 匹配节点
     */
    QLPatternNode getPatternNode();
}
