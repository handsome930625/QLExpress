package com.ql.util.express.instruction.operator;

/**
 * @author wangyijie
 */
public class OperatorPrintln extends Operator {
    private static final long serialVersionUID = -7629744355090478444L;

    public OperatorPrintln(String name) {
        this.name = name;
    }

    public OperatorPrintln(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        if (list.length != 1) {
            throw new Exception("操作数异常,有且只能有一个操作数");
        }
        System.out.println(list[0]);
        return null;
    }


}
