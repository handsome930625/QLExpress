package com.ql.util.express.instruction.factory;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.instruction.env.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionCallSelfDefineFunction;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.operator.OperatorBase;
import com.ql.util.express.parse.ExpressNode;

import java.util.Stack;


/**
 * @author wangyijie
 */
public class CallFunctionInstructionFactory extends InstructionFactory {

    @Override
    public boolean createInstruction(ExpressRunner aCompile,
                                     InstructionSet result, Stack<ForRelBreakContinue> forStack,
                                     ExpressNode node, boolean isRoot) throws Exception {
        ExpressNode[] children = node.getChildren();
        String functionName = children[0].getValue();
        boolean returnVal = false;
        children = node.getChildren();
        for (int i = 1; i < children.length; i++) {
            boolean tmpHas = aCompile.createInstructionSetPrivate(result,
                    forStack, children[i], false);
            returnVal = returnVal || tmpHas;
        }

        OperatorBase op = aCompile.getOperatorFactory().getOperator(
                functionName);
        int opNum = children.length - 1;
        if (op != null) {
            result.addInstruction(new InstructionOperator(op, opNum).setLine(node.getLine()));
        } else {
            result.addInstruction(new InstructionCallSelfDefineFunction(
                    functionName, opNum).setLine(children[0].getLine()));
        }
        return returnVal;
    }
}

