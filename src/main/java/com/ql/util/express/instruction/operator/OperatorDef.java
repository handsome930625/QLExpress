package com.ql.util.express.instruction.operator;

import com.ql.util.express.instruction.env.ArraySwap;
import com.ql.util.express.instruction.env.InstructionSetContext;
import com.ql.util.express.instruction.opcache.OperateDataCacheManager;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;
import com.ql.util.express.instruction.opdata.OperateDataVirClass;

/**
 * @author wangyijie
 */
public class OperatorDef extends OperatorBase {
    private static final long serialVersionUID = -1210304488897891063L;

    public OperatorDef(String aName) {
        this.name = aName;
    }

    public OperatorDef(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext context, ArraySwap list) throws Exception {
        Object type = list.get(0).getObject(context);
        String varName = (String) list.get(1).getObject(context);
        Class<?> tmpClass;
        if (type instanceof Class) {
            tmpClass = (Class<?>) type;
        } else {
            tmpClass = OperateDataVirClass.class;
        }
        OperateDataLocalVar result = OperateDataCacheManager.fetchOperateDataLocalVar(varName, tmpClass);
        context.addSymbol(varName, result);
        return result;
    }
}
