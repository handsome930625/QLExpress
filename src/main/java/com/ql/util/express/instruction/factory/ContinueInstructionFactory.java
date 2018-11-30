package com.ql.util.express.instruction.factory;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionGoTo;
import com.ql.util.express.parse.ExpressNode;

import java.util.Stack;

/**
 * continue 指令生成工厂
 *
 * @author wangyijie
 */
public class ContinueInstructionFactory extends InstructionFactory {

    @Override
    public boolean createInstruction(ExpressRunner aCompile, InstructionSet result,
                                     Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot)
            throws Exception {
        // continue 预占跳转偏移量
        InstructionGoTo continueInstruction = new InstructionGoTo(result.getCurrentPoint() + 1);
        continueInstruction.name = "continue";
        forStack.peek().continueList.add(continueInstruction);
        result.addInstruction(continueInstruction.setLine(node.getLine()));
        return false;
    }
}
