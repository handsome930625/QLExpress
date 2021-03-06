package com.ql.util.express.instruction.factory;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.instruction.env.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionClearDataStack;
import com.ql.util.express.instruction.detail.InstructionCloseNewArea;
import com.ql.util.express.instruction.detail.InstructionOpenNewArea;
import com.ql.util.express.parse.ExpressNode;

import java.util.Stack;

/**
 * @author wangyijie    block semicolon child_express {
 */
public class BlockInstructionFactory extends InstructionFactory {

    @Override
    public boolean createInstruction(ExpressRunner compile, InstructionSet result,
                                     Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot)
            throws Exception {
        // 分割符说明操作结束了,堆栈重新设置
        if (node.isTypeEqualsOrChild("STAT_SEMICOLON")
                && result.getCurrentPoint() >= 0 && !(result.getInstruction(result.getCurrentPoint()) instanceof InstructionClearDataStack)) {
            result.addInstruction(new InstructionClearDataStack().setLine(node.getLine()));
        }

        int tmpPoint = result.getCurrentPoint() + 1;
        boolean returnVal;
        boolean hasDef = false;
        for (ExpressNode tmpNode : node.getChildren()) {
            boolean tmpHas = compile.createInstructionSetPrivate(result, forStack, tmpNode, false);
            hasDef = hasDef || tmpHas;
        }
        if (hasDef && !isRoot
                && node.getTreeType().isEqualsOrChild("STAT_BLOCK")) {
            result.insertInstruction(tmpPoint, new InstructionOpenNewArea().setLine(node.getLine()));
            result.insertInstruction(result.getCurrentPoint() + 1, new InstructionCloseNewArea().setLine(node.getLine()));
            returnVal = false;
        } else {
            returnVal = hasDef;
        }
        return returnVal;
    }
}
