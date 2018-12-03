package com.ql.util.express.instruction.operator;

import com.ql.util.express.utils.OperatorOfNumber;

/**
 * @author wangyijie
 */
public class OperatorReduce extends Operator {
    private static final long serialVersionUID = -1028416168048696136L;

    public OperatorReduce(String name) {
        this.name = name;
    }

    public OperatorReduce(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return OperatorOfNumber.subtract(list[0], list[1], this.isPrecise);
    }
}
