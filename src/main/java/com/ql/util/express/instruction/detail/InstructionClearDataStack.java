package com.ql.util.express.instruction.detail;

import com.ql.util.express.instruction.env.RunEnvironment;

import java.util.List;


/**
 * 清除栈针
 *
 * @author wangyijie
 */
public class InstructionClearDataStack extends Instruction {

    private static final long serialVersionUID = 6286430548739444891L;

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        //目前的模式，不需要执行任何操作
        if (environment.isTrace() && log.isDebugEnabled()) {
            log.debug(this);
        }
        environment.clearDataStack();
        environment.programPointAddOne();
    }

    @Override
    public String toString() {
        return "clearDataStack";
    }
}

