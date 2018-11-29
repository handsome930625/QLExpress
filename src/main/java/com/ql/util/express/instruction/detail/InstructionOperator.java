package com.ql.util.express.instruction.detail;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

import java.util.List;

/**
 * 操作符指令
 *
 * @author wangyijie
 */
public class InstructionOperator extends Instruction {
    private static final long serialVersionUID = -1217916524030161947L;
    /**
     * 操作符
     */
    private OperatorBase operator;
    /**
     * 操作数据对象个数
     */
    private int opDataNumber;

    public InstructionOperator(OperatorBase aOperator, int aOpDataNumber) {
        this.operator = aOperator;
        this.opDataNumber = aOpDataNumber;
    }

    public OperatorBase getOperator() {
        return this.operator;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        ArraySwap parameters = environment.popArray(environment.getContext(), this.opDataNumber);
        if (environment.isTrace() && log.isDebugEnabled()) {
            StringBuilder str = new StringBuilder(this.operator.toString() + "(");
            OperateData p;
            for (int i = 0; i < parameters.length; i++) {
                p = parameters.get(i);
                if (i > 0) {
                    str.append(",");
                }
                if (p instanceof OperateDataAttr) {
                    str.append(p).append(":").append(p.getObject(environment.getContext()));
                } else {
                    str.append(p);
                }
            }
            str.append(")");
            log.debug(str.toString());
        }
        try {
            OperateData result = this.operator.execute(environment.getContext(), parameters, errorList);
            environment.push(result);
            environment.programPointAddOne();
        } catch (Exception e) {
            throw new Exception(getExceptionPrefix(), e);
        }
    }

    @Override
    public String toString() {
        return "OP : " + this.operator.toString() + " OPNUMBER[" + this.opDataNumber + "]";
    }
}
