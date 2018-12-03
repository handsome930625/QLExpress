package com.ql.util.express.instruction.operator;

import com.ql.util.express.instruction.env.ArraySwap;
import com.ql.util.express.instruction.env.InstructionSetContext;

/**
 * @author wangyijie
 */
public class OperatorIf extends OperatorBase {
    private static final long serialVersionUID = -3021674142290780291L;

    public OperatorIf(String aName) {
        this.name = aName;
    }

    public OperatorIf(String aAliasName, String aName, String aErrorInfo) {
        this.name = aName;
        this.aliasName = aAliasName;
        this.errorInfo = aErrorInfo;
    }

    @Override
    public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
        if (list.length < 2) {
            throw new Exception("\"" + this.aliasName + "\"操作至少要两个操作数");
        }
        Object obj = list.get(0).getObject(parent);
        if (obj == null) {
            String msg = "\"" + this.aliasName + "\"的判断条件不能为空";
            throw new Exception(msg);
        } else if (!(obj instanceof Boolean)) {
            String msg = "\"" + this.aliasName + "\"的判断条件 必须是 Boolean,不能是：";
            throw new Exception(msg + obj.getClass().getName());
        } else {
            if ((Boolean) obj) {
                return list.get(1);
            } else {
                if (list.length == 3) {
                    return list.get(2);
                }
            }
            return null;
        }
    }
}
