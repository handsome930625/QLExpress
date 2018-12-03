package com.ql.util.express.instruction.detail;

import com.ql.util.express.instruction.env.RunEnvironment;

import java.util.List;

/**
 * return 指令
 *
 * @author wangyijie
 */
public class InstructionReturn extends Instruction {
    private static final long serialVersionUID = -4991998239277488949L;
    private boolean haveReturnValue;

    public InstructionReturn(boolean aHaveReturnValue) {
        this.haveReturnValue = aHaveReturnValue;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        // 目前的模式，不需要执行任何操作
        if (environment.isTrace() && log.isDebugEnabled()) {
            log.debug(this);
        }
        // 是否有返回值
        if (this.haveReturnValue) {
            // 将栈中指针指向的变量弹出
            environment.quitExpress(environment.pop().getObject(environment.getContext()));
        } else {
            environment.quitExpress();
        }
        // 程序指针指向程序结尾一位
        environment.gotoLastWhenReturn();
    }

    @Override
    public String toString() {
        if (this.haveReturnValue) {
            return "return [value]";
        } else {
            return "return";
        }
    }
}
