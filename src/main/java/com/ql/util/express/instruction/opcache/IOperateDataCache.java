package com.ql.util.express.instruction.opcache;

import com.ql.util.express.*;
import com.ql.util.express.instruction.opdata.*;

/**
 * @author wangyijie
 */
public interface IOperateDataCache {

    OperateData fetchOperateData(Object obj, Class<?> aType);

    OperateDataAttr fetchOperateDataAttr(String name, Class<?> aType);

    OperateDataLocalVar fetchOperateDataLocalVar(String name, Class<?> aType);

    OperateDataField fetchOperateDataField(Object aFieldObject, String aFieldName);

    OperateDataArrayItem fetchOperateDataArrayItem(OperateData aArrayObject, int aIndex);

    OperateDataKeyValue fetchOperateDataKeyValue(OperateData aKey, OperateData aValue);

    RunEnvironment fetRunEnvironment(InstructionSet aInstructionSet, InstructionSetContext aContext, boolean aIsTrace);

    CallResult fetchCallResult(Object aReturnValue, boolean aIsExit);

    InstructionSetContext fetchInstructionSetContext(boolean aIsExpandToParent, ExpressRunner aRunner, IExpressContext<String, Object> aParent, ExpressLoader aExpressLoader, boolean aIsSupportDynamicFieldName);

    void resetCache();

    long getFetchCount();
}
