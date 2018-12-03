package com.ql.util.express.instruction.factory;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.instruction.env.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionNewVirClass;
import com.ql.util.express.parse.ExpressNode;

import java.util.Stack;


/**
 * @author wangyijie
 */
public class NewVClassInstructionFactory extends InstructionFactory {

    @Override
    public boolean createInstruction(ExpressRunner aCompile,
                                     InstructionSet result, Stack<ForRelBreakContinue> forStack,
                                     ExpressNode node, boolean isRoot) throws Exception {
        ExpressNode[] children = node.getChildren();
        boolean returnVal = false;
        String virClassName = children[0].getValue();
        for (int i = 1; i < children.length; i++) {
            boolean tmpHas = aCompile.createInstructionSetPrivate(result, forStack, children[i], false);
            returnVal = returnVal || tmpHas;
        }
        result.addInstruction(new InstructionNewVirClass(virClassName, children.length - 1).setLine(node.getLine()));
        return returnVal;
    }
}