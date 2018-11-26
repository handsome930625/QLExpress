package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;
import com.ql.util.express.OperatorOfNumber;

/**
 * @author wangyijie
 */
public class OperatorAdd extends Operator {
    private static final long serialVersionUID = -5494291963506436204L;

    public OperatorAdd(String name) {
        this.name = name;
    }

    public OperatorAdd(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return OperatorOfNumber.add(list[0], list[1], this.isPrecise);
    }
}
