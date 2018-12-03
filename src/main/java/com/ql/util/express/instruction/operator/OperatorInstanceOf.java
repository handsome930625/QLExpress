package com.ql.util.express.instruction.operator;

/**
 * @author tianqiao
 * @date 17/9/19
 */
public class OperatorInstanceOf extends Operator {

    private static final long serialVersionUID = -6095169774998252219L;

    public OperatorInstanceOf(String anInstanceof) {
        this.name = anInstanceof;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        Object obj = list[0];
        Object cls = list[1];
        if (obj != null && cls instanceof Class) {
            Class targetClass = (Class) cls;
            Class fromClass = obj.getClass();
            //noinspection unchecked
            return targetClass.isAssignableFrom(fromClass);
        }
        return false;
    }
}
