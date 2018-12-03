package com.ql.util.express.instruction.operator;

/**
 * @author wangyijie
 */
public class OperatorNor extends Operator {
    private static final long serialVersionUID = 5136185116685848004L;

    public OperatorNor(String name) {
        this.name = name;
    }

    public OperatorNor(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0], list[1]);
    }

    public Object executeInner(Object op1, Object op2) throws Exception {

        if (op1 != null) {
            return op1;
        } else {
            return op2;
        }
    }
}
