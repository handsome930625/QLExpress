package com.ql.util.express.instruction.detail;

import com.ql.util.express.instruction.env.InstructionSetContext;
import com.ql.util.express.instruction.env.RunEnvironment;
import com.ql.util.express.instruction.opcache.OperateDataCacheManager;

import java.util.List;

/**
 * 开辟一个新的变量内存空间
 *
 * @author wangyijie
 */
public class InstructionOpenNewArea extends Instruction {
    private static final long serialVersionUID = -118527079334123637L;

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        // 目前的模式，不需要执行任何操作
        if (environment.isTrace() && log.isDebugEnabled()) {
            log.debug(this);
        }
        InstructionSetContext parentContext = environment.getContext();
        environment.setContext(OperateDataCacheManager.fetchInstructionSetContext(
                true,
                parentContext.getExpressRunner(),
                parentContext,
                parentContext.getExpressLoader(),
                parentContext.isSupportDynamicFieldName()));
        environment.programPointAddOne();
    }

    @Override
    public String toString() {
        return "openNewArea";
    }
}
