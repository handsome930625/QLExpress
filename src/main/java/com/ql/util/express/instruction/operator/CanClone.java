package com.ql.util.express.instruction.operator;

/**
 * @author wangyijie
 */
public interface CanClone {
    OperatorBase cloneMe(String name, String errorInfo) throws Exception;
}
