package com.ql.util.express.instruction.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;


/**
 * 处理 ",","(",")",";"
 *
 * @author wangyijie
 */

public class OperatorNullOp extends OperatorBase {
    private static final long serialVersionUID = -5066312735249228017L;

    public OperatorNullOp(String name) {
        this.name = name;
    }

    public OperatorNullOp(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        return executeInner(parent);
    }

    public OperateData executeInner(IExpressContext<String, Object> parent) throws Exception {
        return null;
    }
}
