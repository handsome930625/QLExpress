package com.ql.util.express.instruction.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.OperateDataCacheManager;

/**
 * @author wangyijie
 */
public class OperatorKeyValue extends OperatorBase {

    private static final long serialVersionUID = -348954748887331584L;

    public OperatorKeyValue(String aName) {
        this.name = aName;
    }

    public OperatorKeyValue(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext context, ArraySwap list) throws Exception {
        return OperateDataCacheManager.fetchOperateDataKeyValue(list.get(0), list.get(1));
    }
}
