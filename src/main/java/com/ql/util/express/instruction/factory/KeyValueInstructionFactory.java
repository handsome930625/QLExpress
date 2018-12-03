package com.ql.util.express.instruction.factory;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.instruction.env.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.operator.OperatorBase;
import com.ql.util.express.parse.ExpressNode;

import java.util.Stack;


/**
 * @author wangyijie
 */
public class KeyValueInstructionFactory extends InstructionFactory {

    @Override
    public boolean createInstruction(ExpressRunner aCompile, InstructionSet result, Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot)
            throws Exception {
        boolean returnVal = false;
        ExpressNode[] children = node.getChildren();
        if (node.getParent() != null && node.getParent().isTypeEqualsOrChild("STATEMENT")) {
            children[0].setNodeType(aCompile.getNodeTypeManager().findNodeType("CONST_STRING"));
            children[0].setTreeType(aCompile.getNodeTypeManager().findNodeType("CONST"));
            children[0].setObjectValue(children[0].getValue());
        }
        if (node.getParent() != null && node.getParent().isTypeEqualsOrChild("STATEMENT") && children[1].isTypeEqualsOrChild("STAT_BLOCK")) {
            returnVal = new MacroInstructionFactory().createInstruction(aCompile, result, forStack, node, isRoot);
        } else if (node.getParent() != null && node.getParent().isTypeEqualsOrChild("STATEMENT")) {
            for (ExpressNode tmpNode : children) {
                boolean tmpHas = aCompile.createInstructionSetPrivate(result, forStack, tmpNode, false);
                returnVal = returnVal || tmpHas;
            }
            OperatorBase op = aCompile.getOperatorFactory().newInstance("alias");
            result.addInstruction(new InstructionOperator(op, children.length).setLine(node.getLine()));
            returnVal = true;
        } else {
            for (ExpressNode tmpNode : children) {
                boolean tmpHas = aCompile.createInstructionSetPrivate(result, forStack, tmpNode, false);
                returnVal = returnVal || tmpHas;
            }

            OperatorBase op = aCompile.getOperatorFactory().newInstance(node);
            result.addInstruction(new InstructionOperator(op, children.length).setLine(node.getLine()));
        }
        return returnVal;
    }
}

