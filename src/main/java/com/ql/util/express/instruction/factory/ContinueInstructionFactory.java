package com.ql.util.express.instruction.factory;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionGoTo;
import com.ql.util.express.parse.ExpressNode;

import java.util.Stack;

/**
 * @author wangyijie
 */
public class ContinueInstructionFactory extends InstructionFactory {

    @Override
    public boolean createInstruction(ExpressRunner aCompile, InstructionSet result,
                                     Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot)
            throws Exception {
        InstructionGoTo continueInstruction = new InstructionGoTo(result.getCurrentPoint() + 1);
        continueInstruction.name = "continue";
        forStack.peek().continueList.add(continueInstruction);
        result.addInstruction(continueInstruction.setLine(node.getLine()));
        return false;
    }
}
