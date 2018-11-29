package com.ql.util.express.instruction.op;

import com.ql.util.express.*;
import com.ql.util.express.instruction.opcache.OperateDataCacheManager;

/**
 * @author wangyijie
 */
public class OperatorDoubleAddReduce extends OperatorBase {
    private static final long serialVersionUID = 569547687600745096L;

    public OperatorDoubleAddReduce(String name) {
        this.name = name;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent,
                                    ArraySwap list) throws Exception {
        Object obj = list.get(0).getObject(parent);
        Object result = null;
        if ("++".equals(this.getName())) {
            result = OperatorOfNumber.add(obj, 1, this.isPrecise);
        } else if ("--".equals(this.getName())) {
            result = OperatorOfNumber.subtract(obj, 1, this.isPrecise);
        }
        list.get(0).setObject(parent, result);

        if (result == null) {
            return OperateDataCacheManager.fetchOperateData(null, null);
        } else {
            return OperateDataCacheManager.fetchOperateData(result, ExpressUtil.getSimpleDataType(result.getClass()));
        }
    }
}
