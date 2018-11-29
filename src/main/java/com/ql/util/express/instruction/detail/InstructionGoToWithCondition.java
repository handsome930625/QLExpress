package com.ql.util.express.instruction.detail;

import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.opcache.OperateDataCacheManager;

import java.util.List;

/**
 * @author wangyijie
 */
public class InstructionGoToWithCondition extends Instruction {
    private static final long serialVersionUID = -4817805156872407837L;
    /**
     * 跳转指令的偏移量
     */
    private int offset;
    /**
     * 跳转条件
     */
    private boolean condition;
    private boolean isPopStackData;

    public InstructionGoToWithCondition(boolean condition, int offset, boolean isPopStackData) {
        this.offset = offset;
        this.condition = condition;
        this.isPopStackData = isPopStackData;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        Object o;
        if (!this.isPopStackData) {
            o = environment.peek().getObject(environment.getContext());
            if (o == null) {
                environment.pop();
                environment.push(OperateDataCacheManager.fetchOperateData(false, boolean.class));
            }
        } else {
            o = environment.pop().getObject(environment.getContext());
        }
        boolean r;
        if (o == null) {
            r = false;
        } else if (o instanceof Boolean) {
            r = (Boolean) o;
        } else {
            throw new Exception(getExceptionPrefix() + "指令错误:" + o + " 不是Boolean");
        }
        if (r == this.condition) {
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
        String result = "GoToIf[" + this.condition + ",isPop=" + this.isPopStackData + "] ";
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

    public boolean isCondition() {
        return condition;
    }

    public void setCondition(boolean condition) {
        this.condition = condition;
    }

    public boolean isPopStackData() {
        return isPopStackData;
    }

    public void setPopStackData(boolean isPopStackData) {
        this.isPopStackData = isPopStackData;
    }

}
