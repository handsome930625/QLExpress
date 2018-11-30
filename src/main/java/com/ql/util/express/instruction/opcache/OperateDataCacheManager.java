package com.ql.util.express.instruction.opcache;

import com.ql.util.express.*;
import com.ql.util.express.instruction.opdata.*;

import java.util.Stack;


/**
 * @author wangyijie
 */
public class OperateDataCacheManager {

    private static ThreadLocal<RunnerDataCache> m_OperateDataObjectCache = new ThreadLocal<RunnerDataCache>() {
        @Override
        protected RunnerDataCache initialValue() {
            return new RunnerDataCache();
        }
    };

    public static void push(ExpressRunner runner) {
        m_OperateDataObjectCache.get().push(runner);
    }

    public static IOperateDataCache getOperateDataCache() {
        return m_OperateDataObjectCache.get().cache;
    }

    public static OperateData fetchOperateData(Object obj, Class<?> aType) {
        return getOperateDataCache().fetchOperateData(obj, aType);
    }

    public static OperateDataAttr fetchOperateDataAttr(String name, Class<?> aType) {
        return getOperateDataCache().fetchOperateDataAttr(name, aType);
    }

    public static OperateDataLocalVar fetchOperateDataLocalVar(String name, Class<?> aType) {
        return getOperateDataCache().fetchOperateDataLocalVar(name, aType);
    }

    public static OperateDataField fetchOperateDataField(Object aFieldObject, String aFieldName) {
        return getOperateDataCache().fetchOperateDataField(aFieldObject, aFieldName);
    }

    public static OperateDataArrayItem fetchOperateDataArrayItem(OperateData aArrayObject, int aIndex) {
        return getOperateDataCache().fetchOperateDataArrayItem(aArrayObject, aIndex);
    }

    public static OperateDataKeyValue fetchOperateDataKeyValue(OperateData aKey, OperateData aValue) {
        return getOperateDataCache().fetchOperateDataKeyValue(aKey, aValue);
    }

    /**
     * 获取运行环境上下文
     */
    public static RunEnvironment fetRunEnvironment(InstructionSet instructionSet, InstructionSetContext context, boolean isTrace) {
        return getOperateDataCache().fetRunEnvironment(instructionSet, context, isTrace);
    }

    public static CallResult fetchCallResult(Object aReturnValue, boolean aIsExit) {
        return getOperateDataCache().fetchCallResult(aReturnValue, aIsExit);
    }

    /**
     * 获取指令上下文
     */
    public static InstructionSetContext fetchInstructionSetContext(boolean isExpandToParent, ExpressRunner runner,
                                                                   IExpressContext<String, Object> parent,
                                                                   ExpressLoader expressLoader, boolean
                                                                           isSupportDynamicFieldName) {
        return getOperateDataCache().fetchInstructionSetContext(isExpandToParent, runner, parent, expressLoader, isSupportDynamicFieldName);
    }

    public static long getFetchCount() {
        return getOperateDataCache().getFetchCount();
    }

    public static void resetCache(ExpressRunner aRunner) {
        getOperateDataCache().resetCache();
        m_OperateDataObjectCache.get().pop(aRunner);
    }

}

class RunnerDataCache {
    IOperateDataCache cache;

    private Stack<ExpressRunner> stack = new Stack<>();

    public void push(ExpressRunner runner) {
        this.cache = runner.getOperateDataCache();
        this.stack.push(runner);
    }

    public IOperateDataCache getOperateDataCache() {
        return this.cache;
    }

    public void pop(ExpressRunner runner) {
        this.stack.pop();
        if (!this.stack.isEmpty()) {
            this.cache = this.stack.peek().getOperateDataCache();
        } else {
            this.cache = null;
        }
    }

}
