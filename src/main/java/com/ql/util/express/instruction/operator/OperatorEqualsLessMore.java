package com.ql.util.express.instruction.operator;

/**
 * 处理 =,==,>,>=,<,<=,!=,<>
 *
 * @author wangyijie
 */
public class OperatorEqualsLessMore extends Operator {
    private static final long serialVersionUID = 2272390825078649080L;

    public OperatorEqualsLessMore(String aName) {
        this.name = aName;
    }

    public OperatorEqualsLessMore(String aAliasName, String aName,
                                  String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        return executeInner(list[0], list[1]);
    }

    public Object executeInner(Object op1, Object op2) throws Exception {
        return executeInner(this.name, op1, op2);
    }

    public static boolean executeInner(String opStr, Object obj1, Object obj2)
            throws Exception {

        if (obj1 == null && obj2 == null) {
            switch (opStr) {
                case "==":
                    return true;
                case "!=":
                case "<>":
                    return false;
                default:
                    throw new Exception("两个空操作数不能执行这个操作：" + opStr);
            }
        } else if (obj1 == null || obj2 == null) {
            if ("==".equals(opStr)) {
                return false;
            } else if ("!=".equals(opStr) || "<>".equals(opStr)) {
                return true;
            } else {
                throw new Exception("空操作数不能执行这个操作：" + opStr);
            }
        }

        int i = Operator.compareData(obj1, obj2);
        boolean result;
        if (i > 0) {
            result = ">".equals(opStr) || ">=".equals(opStr) || "!=".equals(opStr)
                    || "<>".equals(opStr);
        } else if (i == 0) {
            result = "=".equals(opStr) || "==".equals(opStr) || ">=".equals(opStr)
                    || "<=".equals(opStr);
        } else {
            result = "<".equals(opStr) || "<=".equals(opStr) || "!=".equals(opStr)
                    || "<>".equals(opStr);
        }
        return result;
    }
}
