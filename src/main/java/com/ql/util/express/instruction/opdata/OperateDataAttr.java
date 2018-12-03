package com.ql.util.express.instruction.opdata;

import com.ql.util.express.utils.ExpressUtil;
import com.ql.util.express.instruction.env.InstructionSetContext;
import com.ql.util.express.instruction.operator.OperateData;

/**
 * 变量操作对象
 *
 * @author wangyijie
 */
public class OperateDataAttr extends OperateData {
    private static final long serialVersionUID = 7791088774033845299L;
    /**
     * 变量名
     */
    protected String name;

    public OperateDataAttr(String aName, Class<?> aType) {
        super(null, aType);
        this.name = aName;
    }

    public void initialDataAttr(String aName, Class<?> aType) {
        super.initial(null, aType);
        this.name = aName;
    }

    public void clearDataAttr() {
        super.clear();
        this.name = null;
    }

    public void setDefineType(Class<?> orgiType) {
        this.type = orgiType;
    }

    @Override
    public Class<?> getDefineType() {
        return this.type;
    }

    public String getName() {
        return name;
    }

    @Override
    public void toResource(StringBuilder builder, int level) {
        builder.append(this.name);
    }

    @Override
    public String toString() {
        try {
            String str;
            if (this.type == null) {
                str = name;
            } else {
                str = name + "[" + ExpressUtil.getClassName(this.type) + "]";
            }
            return str;
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @Override
    public Object getObjectInner(InstructionSetContext context) throws Exception {
        if ("null".equalsIgnoreCase(this.name)) {
            return null;
        }
        if (context == null) {
            throw new RuntimeException("没有设置表达式计算的上下文，不能获取属性：\"" + this.name
                    + "\"请检查表达式");
        }
        try {
            return context.get(this.name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<?> getType(InstructionSetContext context) throws Exception {
        if (this.type != null) {
            return this.type;
        }
        Object obj = context.get(name);
        if (obj == null) {
            return null;
        } else {
            return obj.getClass();
        }
    }

    @Override
    public void setObject(InstructionSetContext parent, Object object) throws Exception {
        try {
            parent.put(this.name, object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
