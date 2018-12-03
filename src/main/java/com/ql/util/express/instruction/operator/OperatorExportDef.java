package com.ql.util.express.instruction.operator;

import com.ql.util.express.instruction.env.ArraySwap;
import com.ql.util.express.instruction.env.InstructionSetContext;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

/**
 * @author wangyijie
 */
public class OperatorExportDef extends OperatorBase {
    private static final long serialVersionUID = 5014421642481022256L;

    public OperatorExportDef(String aName) {
        this.name = aName;
    }

    public OperatorExportDef(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext context, ArraySwap list) throws Exception {
        Class<?> tmpClass = (Class<?>) list.get(0).getObject(context);
        String varName = (String) list.get(1).getObject(context);
        OperateDataAttr result = (OperateDataAttr) context.getSymbol(varName);
        result.setDefineType(tmpClass);
        return result;
    }
}
