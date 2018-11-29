package com.ql.util.express.instruction.detail;

import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

import java.util.List;

/**
 * 常量指令
 *
 * @author wangyijie
 */
public class InstructionConstData extends Instruction {
    private static final long serialVersionUID = 745531116947232321L;
    /**
     * 操作的数据对象
     */
    private OperateData operateData;

    public InstructionConstData(OperateData data) {
        this.operateData = data;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList)
            throws Exception {
        if (environment.isTrace() && log.isDebugEnabled()) {
            if (this.operateData instanceof OperateDataAttr) {
                log.debug(this + ":"
                        + this.operateData.getObject(environment.getContext()));
            } else {
                log.debug(this);
            }
        }
        environment.push(this.operateData);
        environment.programPointAddOne();
    }

    @Override
    public String toString() {
        if (this.operateData instanceof OperateDataAttr) {
            return "LoadData attr:" + this.operateData.toString();
        } else {
            return "LoadData " + this.operateData.toString();
        }
    }

    public OperateData getOperateData() {
        return this.operateData;
    }

}
