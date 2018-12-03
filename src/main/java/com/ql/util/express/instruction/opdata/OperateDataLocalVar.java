package com.ql.util.express.instruction.opdata;

import com.ql.util.express.instruction.env.InstructionSetContext;


/**
 * 方法参数变量
 *
 * @author wangyijie
 */
public class OperateDataLocalVar extends OperateDataAttr {
    private static final long serialVersionUID = -5180877680560388138L;

    public OperateDataLocalVar(String name, Class<?> type) {
        super(name, type);
    }

    public void initialDataLocalVar(String name, Class<?> type) {
        super.initialDataAttr(name, type);
    }

    public void clearDataLocalVar() {
        super.clear();
    }

    @Override
    public String toString() {
        try {
            return name + ":localVar";
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @Override
    public Object getObjectInner(InstructionSetContext context) {
        try {
            return this.dataObject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<?> getType(InstructionSetContext context) throws Exception {
        return this.type;
    }

    @Override
    public void setObject(InstructionSetContext parent, Object value) {
        this.dataObject = value;
    }
}
