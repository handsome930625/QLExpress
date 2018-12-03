package com.ql.util.express.instruction.operator;

/**
 * @author wangyijie
 */
public class OperatorNot extends Operator {
    private static final long serialVersionUID = -8918445138814724619L;

    public OperatorNot(String name) {
        this.name = name;
    }

    public OperatorNot(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0]);
    }

    public Object executeInner(Object op)
            throws Exception {
        Object result;
        if (op == null) {
            throw new Exception("null 不能执行操作：" + this.getAliasName());
        }
        if (Boolean.class.equals(op.getClass())) {
            result = !(Boolean) op;
        } else {
            //
            String msg = "没有定义类型" + op.getClass().getName() + " 的 " + this.name
                    + "操作";
            throw new Exception(msg);
        }
        return result;
    }
}
