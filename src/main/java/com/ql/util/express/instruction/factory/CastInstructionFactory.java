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
public class CastInstructionFactory extends InstructionFactory {

    @Override
    public boolean createInstruction(ExpressRunner aCompile, InstructionSet result, Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot)
            throws Exception {
        boolean returnVal = false;
        OperatorBase op = aCompile.getOperatorFactory().newInstance(node);
        ExpressNode[] children = node.getChildren();
        if (children.length == 0) {
            throw new Exception("扩展类型不存在");
        } else if (children.length > 2) {
            throw new Exception("扩展操作只能有一个类型为Class的操作数");
        } else if (!children[0].getNodeType().isEqualsOrChild("CONST_CLASS")) {
            throw new Exception("扩展操作只能有一个类型为Class的操作数,当前的数据类型是：" + children[0].getNodeType().getName());
        }

        for (ExpressNode aChildren : children) {
            boolean tmpHas = aCompile.createInstructionSetPrivate(result, forStack, aChildren, false);
            returnVal = returnVal || tmpHas;
        }
        result.addInstruction(new InstructionOperator(op, children.length).setLine(node.getLine()));
        return returnVal;
    }
}
