package com.ql.util.express.instruction.operator;

import com.ql.util.express.instruction.env.ArraySwap;
import com.ql.util.express.utils.ExpressUtil;
import com.ql.util.express.instruction.env.InstructionSetContext;
import com.ql.util.express.instruction.opcache.OperateDataCacheManager;

/**
 * @author wangyijie
 */
public class OperatorCast extends OperatorBase {
    private static final long serialVersionUID = -6084105062942205114L;

    public OperatorCast(String aName) {
        this.name = aName;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        Class<?> tmpClass = (Class<?>) list.get(0).getObject(parent);
        Object castObj = ExpressUtil.castObject(list.get(1).getObject(parent), tmpClass, true);
        return OperateDataCacheManager.fetchOperateData(castObj, tmpClass);
    }
}
