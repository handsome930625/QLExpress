package com.ql.util.express.instruction.operator;

import com.ql.util.express.instruction.env.ArraySwap;
import com.ql.util.express.instruction.env.InstructionSetContext;
import com.ql.util.express.instruction.opcache.OperateDataCacheManager;

/**
 * @author wangyijie
 */
public class OperatorField extends OperatorBase {
    private static final long serialVersionUID = 3972563318137930166L;
    private String filedName;

    public OperatorField() {
        this.name = "FieldCall";
    }

    public OperatorField(String aFieldName) {
        this.name = "FieldCall";
        this.filedName = aFieldName;

    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        Object obj = list.get(0).getObject(parent);
        return OperateDataCacheManager.fetchOperateDataField(obj, this.filedName);
    }

    @Override
    public String toString() {
        return this.name + ":" + this.filedName;
    }
}
