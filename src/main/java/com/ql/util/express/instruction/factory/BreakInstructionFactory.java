package com.ql.util.express.instruction.factory;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.instruction.env.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionGoTo;
import com.ql.util.express.parse.ExpressNode;

import java.util.Stack;

/**
 * break 指令
 *
 * @author wangyijie
 */
public class BreakInstructionFactory extends InstructionFactory {

    @Override
    public boolean createInstruction(ExpressRunner aCompile, InstructionSet result,
                                     Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot)
            throws Exception {
        InstructionGoTo breakInstruction = new InstructionGoTo(result.getCurrentPoint() + 1);
        breakInstruction.name = "break";
        forStack.peek().breakList.add(breakInstruction);
        result.addInstruction(breakInstruction.setLine(node.getLine()));
        return false;
    }
}
