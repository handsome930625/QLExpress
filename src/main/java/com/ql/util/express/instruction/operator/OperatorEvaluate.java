package com.ql.util.express.instruction.operator;

import com.ql.util.express.instruction.env.ArraySwap;
import com.ql.util.express.utils.ExpressUtil;
import com.ql.util.express.instruction.env.InstructionSetContext;

/**
 * @author wangyijie
 */
public class OperatorEvaluate extends OperatorBase {
    private static final long serialVersionUID = 1708908116230612117L;

    public OperatorEvaluate(String name) {
        this.name = name;
    }

    public OperatorEvaluate(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        return executeInner(parent, list.get(0), list.get(1));
    }

    public OperateData executeInner(InstructionSetContext parent,
                                    OperateData op1, OperateData op2) throws Exception {
        Class<?> targetType = op1.getDefineType();
        Class<?> sourceType = op2.getType(parent);
        if (targetType != null) {
            if (!ExpressUtil.isAssignable(targetType, sourceType)) {
                throw new Exception("赋值时候，类型转换错误："
                        + ExpressUtil.getClassName(sourceType) + " 不能转换为 "
                        + ExpressUtil.getClassName(targetType));
            }

        }
        Object result = op2.getObject(parent);
        if (targetType != null) {
            result = ExpressUtil.castObject(result, targetType, false);
        }
        op1.setObject(parent, result);
        return op1;
    }

}
