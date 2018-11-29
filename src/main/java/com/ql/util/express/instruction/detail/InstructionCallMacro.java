package com.ql.util.express.instruction.detail;

import com.ql.util.express.*;
import com.ql.util.express.instruction.OperateDataCacheManager;

import java.util.List;


/**
 * 调用 宏指令
 *
 * @author wangyijie
 */
public class InstructionCallMacro extends Instruction {
    private static final long serialVersionUID = -5760553701305043649L;

    private String name;

    public InstructionCallMacro(String aName) {
        this.name = aName;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        if (environment.isTrace() && log.isDebugEnabled()) {
            log.debug(this);
        }

        InstructionSetContext context = environment.getContext();

        Object functionSet = context.getSymbol(this.name);

        Object result = InstructionSetRunner.execute(
                context.getExpressRunner(),
                (InstructionSet) functionSet,
                context.getExpressLoader(),
                context,
                errorList,
                environment.isTrace(),
                false, false, log,
                environment.getContext().isSupportDynamicFieldName());
        if (result instanceof OperateData) {
            environment.push((OperateData) result);
        } else {
            environment.push(OperateDataCacheManager.fetchOperateData(result, null));
        }

        environment.programPointAddOne();
    }

    @Override
    public String toString() {
        return "call macro " + this.name;
    }
}
