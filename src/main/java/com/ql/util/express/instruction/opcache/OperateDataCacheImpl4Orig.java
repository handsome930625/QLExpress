package com.ql.util.express.instruction.opcache;

import com.ql.util.express.*;
import com.ql.util.express.instruction.env.*;
import com.ql.util.express.instruction.opdata.*;
import com.ql.util.express.instruction.operator.OperateData;
import com.ql.util.express.parse.ExpressLoader;


class OperateDataCacheImpl4Orig implements IOperateDataCache {

    @Override
    public OperateData fetchOperateData(Object obj, Class<?> aType) {
        return new OperateData(obj, aType);
    }

    @Override
    public OperateDataAttr fetchOperateDataAttr(String name, Class<?> aType) {
        return new OperateDataAttr(name, aType);
    }

    @Override
    public OperateDataLocalVar fetchOperateDataLocalVar(String name, Class<?> aType) {
        return new OperateDataLocalVar(name, aType);
    }

    @Override
    public OperateDataField fetchOperateDataField(Object aFieldObject, String aFieldName) {
        return new OperateDataField(aFieldObject, aFieldName);
    }

    @Override
    public OperateDataArrayItem fetchOperateDataArrayItem(OperateData aArrayObject, int aIndex) {
        return new OperateDataArrayItem(aArrayObject, aIndex);
    }

    @Override
    public OperateDataKeyValue fetchOperateDataKeyValue(OperateData aKey, OperateData aValue) {
        return new OperateDataKeyValue(aKey, aValue);
    }

    @Override
    public RunEnvironment fetRunEnvironment(InstructionSet aInstructionSet, InstructionSetContext aContext, boolean aIsTrace) {
        return new RunEnvironment(aInstructionSet, aContext, aIsTrace);
    }

    @Override
    public CallResult fetchCallResult(Object aReturnValue, boolean aIsExit) {
        return new CallResult(aReturnValue, aIsExit);
    }

    @Override
    public InstructionSetContext fetchInstructionSetContext(boolean aIsExpandToParent, ExpressRunner aRunner, IExpressContext<String, Object> aParent, ExpressLoader aExpressLoader, boolean aIsSupportDynamicFieldName) {
        return new InstructionSetContext(aIsExpandToParent, aRunner, aParent, aExpressLoader, aIsSupportDynamicFieldName);
    }

    @Override
    public void resetCache() {

    }

    @Override
    public long getFetchCount() {
        return 0;
    }

}
