package com.ql.util.express.instruction.detail;

import com.ql.util.express.instruction.env.RunEnvironment;

import java.util.List;

/**
 * @author wangyijie
 */
public class InstructionGoTo extends Instruction {
    private static final long serialVersionUID = 198094562177756098L;
    /**
     * 跳转指令的偏移量
     */
    int offset;
    public String name;

    public InstructionGoTo(int aOffset) {
        this.offset = aOffset;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        if (environment.isTrace() && log.isDebugEnabled()) {
            log.debug(this);
        }
        environment.gotoWithOffset(this.offset);
    }

    @Override
    public String toString() {
        String result = (this.name == null ? "" : this.name + ":") + "GoTo ";
        if (this.offset >= 0) {
            result = result + "+";
        }
        result = result + this.offset + " ";
        return result;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

}
