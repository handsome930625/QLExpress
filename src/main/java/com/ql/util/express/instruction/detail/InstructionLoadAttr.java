package com.ql.util.express.instruction.detail;

import com.ql.util.express.InstructionSet;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.opdata.OperateDataAttr;

import java.util.List;

/**
 * @author wangyijie
 */
public class InstructionLoadAttr extends Instruction {
    private static final long serialVersionUID = -2761666977949467250L;
    private String attrName;

    public InstructionLoadAttr(String aName) {
        this.attrName = aName;
    }

    public String getAttrName() {
        return this.attrName;
    }

    @Override
    public void execute(RunEnvironment environment, List<String> errorList) throws Exception {
        Object o = environment.getContext().getSymbol(this.attrName);
        if (o instanceof InstructionSet) {
            //是函数，则执行
            if (environment.isTrace() && log.isDebugEnabled()) {
                log.debug("指令转换： LoadAttr -- >CallMacro ");
            }
            InstructionCallMacro macro = new InstructionCallMacro(this.attrName);
            macro.setLog(log);
            macro.execute(environment, errorList);
            //注意，此处不能在增加指令，因为在InstructionCallMacro已经调用 environment.programPointAddOne();
        } else {
            if (environment.isTrace() && log.isDebugEnabled()) {
                log.debug(this + ":" + ((OperateDataAttr) o).getObject(environment.getContext()));
            }
            environment.push((OperateDataAttr) o);
            environment.programPointAddOne();
        }
    }

    @Override
    public String toString() {
        return "LoadAttr:" + this.attrName;
    }
}
