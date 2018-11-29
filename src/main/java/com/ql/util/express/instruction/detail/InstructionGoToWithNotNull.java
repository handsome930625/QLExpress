package com.ql.util.express.instruction.detail;

import com.ql.util.express.RunEnvironment;

import java.util.List;

/**
 * @author wangyijie
 */
public class InstructionGoToWithNotNull extends Instruction {
    private static final long serialVersionUID = -2314675800146495935L;
    /**
     * 跳转指令的偏移量
     */
    private int offset;
    private boolean isPopStackData;

    public InstructionGoToWithNotNull(int aOffset, boolean aIsPopStackData) {
        this.offset = aOffset;
        this.isPopStackData = aIsPopStackData;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        Object o;
        if (!this.isPopStackData) {
            o = environment.peek().getObject(environment.getContext());
        } else {
            o = environment.pop().getObject(environment.getContext());
        }
        if (o != null) {
            if (environment.isTrace() && log.isDebugEnabled()) {
                log.debug("goto +" + this.offset);
            }
            environment.gotoWithOffset(this.offset);
        } else {
            if (environment.isTrace() && log.isDebugEnabled()) {
                log.debug("programPoint ++ ");
            }
            environment.programPointAddOne();
        }
    }

    @Override
    public String toString() {
        String result = "GoToIf[NOTNULL,isPop=" + this.isPopStackData + "] ";
        if (this.offset >= 0) {
            result = result + "+";
        }
        result = result + this.offset;
        return result;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isPopStackData() {
        return isPopStackData;
    }

    public void setPopStackData(boolean isPopStackData) {
        this.isPopStackData = isPopStackData;
    }


}
