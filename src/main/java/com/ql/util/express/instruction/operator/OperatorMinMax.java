package com.ql.util.express.instruction.operator;

/**
 * @author wangyijie
 */
public class OperatorMinMax extends Operator {
    private static final long serialVersionUID = 8408715470323231589L;

    public OperatorMinMax(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        if (list.length == 0) {
            throw new Exception("操作数异常");
        }
        Object result = list[0];

        for (int i = 1; i < list.length; i++) {
            result = executeInner(result, list[i]);
        }
        return result;
    }

    public Object executeInner(Object op1,
                               Object op2) throws Exception {
        Object result = null;
        int compareResult = Operator.compareData(op1, op2);
        if ("min".equals(this.name)) {
            if (compareResult < 0) {
                result = op1;
            } else {
                result = op2;
            }
        } else if ("max".equals(this.name)) {
            if (compareResult < 0) {
                result = op2;
            } else {
                result = op1;
            }
        }
        return result;
    }
}
