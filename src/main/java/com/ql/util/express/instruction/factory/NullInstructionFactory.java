package com.ql.util.express.instruction.factory;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.parse.ExpressNode;

import java.util.Stack;

/**
 * @author wangyijie
 */
public class NullInstructionFactory extends InstructionFactory {

    @Override
    public boolean createInstruction(ExpressRunner aCompile, InstructionSet result,
                                     Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot)
            throws Exception {
        return false;
    }
}
