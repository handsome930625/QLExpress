package com.ql.util.express.instruction.detail;

import com.ql.util.express.instruction.env.InstructionSetContext;
import com.ql.util.express.instruction.env.RunEnvironment;

import java.util.List;

/**
 * @author wangyijie
 */
public class InstructionCloseNewArea extends Instruction {
    private static final long serialVersionUID = -996832248972683705L;

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        // 目前的模式，不需要执行任何操作
        if (environment.isTrace() && log.isDebugEnabled()) {
            log.debug(this);
        }
        environment.setContext((InstructionSetContext) environment.getContext().getParent());
        environment.programPointAddOne();
    }

    @Override
    public String toString() {
        return "closeNewArea";
    }
}
