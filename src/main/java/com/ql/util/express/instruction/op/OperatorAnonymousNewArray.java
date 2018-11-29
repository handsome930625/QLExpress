package com.ql.util.express.instruction.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.ExpressUtil;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.opcache.OperateDataCacheManager;

import java.lang.reflect.Array;

/**
 * @author wangyijie
 */
public class OperatorAnonymousNewArray extends OperatorBase {

    private static final long serialVersionUID = -5010977625760866537L;

    public OperatorAnonymousNewArray(String aName) {
        this.name = aName;
    }

    public OperatorAnonymousNewArray(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext context, ArraySwap list) throws Exception {

        Class<?> type = this.findArrayClassType(context, list);
        type = ExpressUtil.getSimpleDataType(type);
        int[] dims = new int[1];
        dims[0] = list.length;
        Object data = Array.newInstance(type, dims);
        for (int i = 0; i < list.length; i++) {
            Array.set(data, i, list.get(i).getObject(context));
        }
        return OperateDataCacheManager.fetchOperateData(data, data.getClass());
    }

    private Class<?> findArrayClassType(InstructionSetContext context, ArraySwap list) throws Exception {
        Class<?> type = null;
        for (int i = 0; i < list.length; i++) {
            Class<?> type1 = list.get(i).getType(context);
            if (type == null) {
                //第一次赋值
                type = type1;
            } else if (type1.isAssignableFrom(type)) {
                //寻找更基础的类
                type = type1;
            } else {
                type = Object.class;
            }
        }
        if (type == null) {
            // 参数全部为null的情况
            type = Object.class;
        }
        return type;
    }
}
