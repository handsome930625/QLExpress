package com.ql.util.express.instruction.op;

import com.ql.util.express.Operator;

/**
 * @author wangyijie
 */
public class OperatorPrint extends Operator {
    private static final long serialVersionUID = 2678813623617099639L;

    public OperatorPrint(String name) {
        this.name = name;
    }

    public OperatorPrint(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        if (list.length != 1) {
            throw new Exception("操作数异常,有且只能有一个操作数");
        }
        System.out.print(list[0]);
        return null;
    }


}
