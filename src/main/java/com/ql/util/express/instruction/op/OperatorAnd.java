package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;

/**
 * 处理 And,Or,&&,||操作
 *
 * @author wangyijie
 */

public class OperatorAnd extends Operator {
    private static final long serialVersionUID = -2668503716238606325L;

    public OperatorAnd(String name) {
        this.name = name;
    }

    public OperatorAnd(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0], list[1]);
    }

    public Object executeInner(Object op1,
                               Object op2) throws Exception {

        boolean r1;
        boolean r2;
        if (op1 == null) {
            r1 = false;
        } else if (op1 instanceof Boolean) {
            r1 = (Boolean) op1;
        } else {
            String msg = "没有定义类型" + op1 + "和" + op2 + " 的 " + this.name + "操作";
            throw new Exception(msg);
        }
        if (op2 == null) {
            r2 = false;
        } else if (op2 instanceof Boolean) {
            r2 = (Boolean) op2;
        } else {
            String msg = "没有定义类型" + op1 + "和" + op2 + " 的 " + this.name + "操作";
            throw new Exception(msg);
        }
        boolean result = r1 && r2;
        return result;

    }
}
