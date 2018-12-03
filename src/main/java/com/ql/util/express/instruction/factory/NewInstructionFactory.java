package com.ql.util.express.instruction.factory;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.utils.ExpressUtil;
import com.ql.util.express.instruction.env.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.operator.OperatorBase;
import com.ql.util.express.parse.ExpressNode;

import java.util.Stack;


/**
 * @author wangyijie
 */
public class NewInstructionFactory extends InstructionFactory {

    @Override
    public boolean createInstruction(ExpressRunner aCompile,
                                     InstructionSet result, Stack<ForRelBreakContinue> forStack,
                                     ExpressNode node, boolean isRoot) throws Exception {
        OperatorBase op = aCompile.getOperatorFactory().newInstance("new");
        ExpressNode[] children = node.getChildren();
        if (node.isTypeEqualsOrChild("NEW_ARRAY")) {
            StringBuilder tempStr = new StringBuilder(children[0].getValue());
            for (int i = 0; i < children.length - 1; i++) {
                tempStr.append("[]");
            }
            children[0].setValue(tempStr.toString());
            children[0].setOrigValue(tempStr.toString());
            children[0].setObjectValue(ExpressUtil.getJavaClass(tempStr.toString()));
        } else if (node.isTypeEqualsOrChild("anonymousNewArray")) {
            op = aCompile.getOperatorFactory().newInstance("anonymousNewArray");
        }

        boolean returnVal = false;
        // 需要重新获取数据
        children = node.getChildren();
        for (ExpressNode aChildren : children) {
            boolean tmpHas = aCompile.createInstructionSetPrivate(result, forStack, aChildren, false);
            returnVal = returnVal || tmpHas;
        }
        result.addInstruction(new InstructionOperator(op, children.length).setLine(node.getLine()));
        return returnVal;
    }
}
