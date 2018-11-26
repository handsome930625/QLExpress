package com.ql.util.express.instruction.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
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
