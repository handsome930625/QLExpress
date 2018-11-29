package com.ql.util.express.instruction.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
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
