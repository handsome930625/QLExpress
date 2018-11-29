package com.ql.util.express.instruction.factory;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.parse.ExpressNode;

import java.util.Stack;


/**
 * @author wangyijie
 */
public class InInstructionFactory extends InstructionFactory {

    @Override
    public boolean createInstruction(ExpressRunner aCompile,
                                     InstructionSet result, Stack<ForRelBreakContinue> forStack,
                                     ExpressNode node, boolean isRoot) throws Exception {
        ExpressNode[] children = node.getChildren();
        if (children[1].isTypeEqualsOrChild("CHILD_EXPRESS")) {
            node.getLeftChildren().remove(1);
            ExpressNode[] parameterList = children[1].getChildren();
            for (ExpressNode aParameterList : parameterList) {
                node.getLeftChildren().add(aParameterList);
            }
        }

        boolean returnVal = false;
        children = node.getChildren();
        for (ExpressNode aChildren : children) {
            boolean tmpHas = aCompile.createInstructionSetPrivate(result, forStack, aChildren, false);
            returnVal = returnVal || tmpHas;
        }
        OperatorBase op = aCompile.getOperatorFactory().newInstance(node);
        result.addInstruction(new InstructionOperator(op, children.length).setLine(node.getLine()));
        return returnVal;
    }
}
