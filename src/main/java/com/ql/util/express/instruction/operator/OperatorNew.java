package com.ql.util.express.instruction.operator;

import com.ql.util.express.instruction.env.ArraySwap;
import com.ql.util.express.utils.ExpressUtil;
import com.ql.util.express.instruction.env.InstructionSetContext;
import com.ql.util.express.instruction.opcache.OperateDataCacheManager;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

/**
 * @author wangyijie
 */
public class OperatorNew extends OperatorBase {

    private static final long serialVersionUID = 5417820944225052011L;

    public OperatorNew(String aName) {
        this.name = aName;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        Class<?> obj = (Class<?>) list.get(0).getObject(parent);
        if (obj.isArray()) {
            Class<?> tmpClass = obj;
            int dim = 0;
            while (tmpClass.isArray()) {
                tmpClass = tmpClass.getComponentType();
                dim = dim + 1;
            }
            int[] dimLength = new int[dim];
            for (int index = 0; index < dim; index++) {
                dimLength[index] = ((Number) (list.get(index + 1).getObject(parent)))
                        .intValue();
            }
            return OperateDataCacheManager.fetchOperateData(Array.newInstance(tmpClass, dimLength), obj);
        }
        Class<?>[] types = new Class[list.length - 1];
        Object[] objs = new Object[list.length - 1];
        Object tmpObj;
        for (int i = 0; i < types.length; i++) {
            tmpObj = list.get(i + 1).getObject(parent);
            types[i] = list.get(i + 1).getType(parent);
            objs[i] = tmpObj;
        }
        Constructor<?> c = ExpressUtil.findConstructorWithCache(obj, types);

        if (c == null) {
            // "没有找到" + obj.getName() + "的构造方法："
            StringBuilder s = new StringBuilder();
            s.append("没有找到").append(obj.getName()).append("的构造方法：").append(obj.getName()).append("(");
            for (int i = 0; i < types.length; i++) {
                if (i > 0) {
                    s.append(",");
                }
                s.append(types[i].getName());
            }
            s.append(")");
            throw new Exception(s.toString());
        }

        tmpObj = c.newInstance(objs);
        return OperateDataCacheManager.fetchOperateData(tmpObj, obj);
    }
}
