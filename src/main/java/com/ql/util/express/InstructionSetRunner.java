package com.ql.util.express;

import com.ql.util.express.instruction.OperateDataCacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * @author wangyijie
 */
public class InstructionSetRunner {
    private static final Log log = LogFactory.getLog(InstructionSetRunner.class);

    public static Object executeOuter(ExpressRunner runner, InstructionSet sets, ExpressLoader loader,
                                      IExpressContext<String, Object> aContext, List<String> errorList,
                                      boolean isTrace, boolean isCatchException,
                                      Log aLog, boolean isSupportDynamicFieldName) throws Exception {
        try {
            OperateDataCacheManager.push(runner);
            return execute(runner, sets, loader, aContext, errorList, isTrace, isCatchException, true, aLog, isSupportDynamicFieldName);
        } finally {
            OperateDataCacheManager.resetCache(runner);
        }
    }

    /**
     * 批量执行指令集合，指令集间可以共享 变量和函数
     *
     * @param runner                    执行器
     * @param sets                      指令集
     * @param loader                    表达式装载器
     * @param aContext                  上下文数据
     * @param errorList                 错误集合
     * @param isTrace                   是否链路日志
     * @param isCatchException          是否捕捉异常
     * @param isReturnLastData
     * @param aLog                      日志器
     * @param isSupportDynamicFieldName 是否支持动态列名
     * @return 表达式结果
     * @throws Exception 异常
     */
    public static Object execute(ExpressRunner runner, InstructionSet sets, ExpressLoader loader,
                                 IExpressContext<String, Object> aContext, List<String> errorList,
                                 boolean isTrace, boolean isCatchException,
                                 boolean isReturnLastData, Log aLog, boolean isSupportDynamicFieldName)
            throws Exception {
        InstructionSetContext context = OperateDataCacheManager.fetchInstructionSetContext(
                true, runner, aContext, loader, isSupportDynamicFieldName);
        return execute(sets, context, errorList, isTrace, isCatchException, isReturnLastData, aLog);
    }

    public static Object execute(InstructionSet set,
                                 InstructionSetContext context, List<String> errorList, boolean isTrace, boolean isCatchException,
                                 boolean isReturnLastData, Log log) throws Exception {
        RunEnvironment environment = OperateDataCacheManager.fetRunEnvironment(set,
                context, isTrace);
        try {
            CallResult tempResult = set.execute(environment, context, errorList,
                    isReturnLastData, log);
            if (tempResult.isExit()) {
                return tempResult.getReturnValue();
            }
        } catch (Exception e) {
            if (isCatchException) {
                if (log != null) {
                    log.error(e.getMessage(), e);
                }
            } else {
                throw e;
            }
        }
        return null;
    }
}
