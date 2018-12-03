package com.ql.util.express.parse;

/**
 * 加载表达式资源接口
 *
 * @author xuannan
 */
public interface IExpressResourceLoader {
    /**
     * 根据表达式名称获取表达式的内容
     *
     * @param expressName 表达式名字
     * @return 表达式内容
     * @throws Exception 异常
     */
    String loadExpress(String expressName) throws Exception;
}
