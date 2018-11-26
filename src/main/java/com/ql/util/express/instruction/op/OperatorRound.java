package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;
import com.ql.util.express.OperatorOfNumber;

/**
 * @author wangyijie
 */
public class OperatorRound extends Operator {
    private static final long serialVersionUID = -6209535778524982369L;

    public OperatorRound(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0], list[1]);
    }

    public Object executeInner(Object op1, Object op2) throws Exception {
        return OperatorOfNumber.round(
                ((Number) op1).doubleValue(), ((Number) op2).intValue());
    }
}
