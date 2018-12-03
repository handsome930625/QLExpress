package com.ql.util.express.instruction.operator;

import com.ql.util.express.instruction.env.ArraySwap;
import com.ql.util.express.instruction.env.InstructionSetContext;
import com.ql.util.express.instruction.opdata.OperateDataAlias;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

/**
 * @author wangyijie
 */
public class OperatorMacro extends OperatorBase {
    private static final long serialVersionUID = -1866302411516691866L;

    public OperatorMacro(String aName) {
        this.name = aName;
    }

    public OperatorMacro(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext context, ArraySwap list) throws Exception {
        String varName = (String) list.get(0).getObjectInner(context);
        OperateDataAttr realAttr = (OperateDataAttr) list.get(1);
        OperateDataAttr result = new OperateDataAlias(varName, realAttr);
        context.addSymbol(varName, result);
        return result;
    }
}
