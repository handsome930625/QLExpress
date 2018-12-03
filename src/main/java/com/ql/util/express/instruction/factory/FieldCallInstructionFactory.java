package com.ql.util.express.instruction.factory;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.instruction.env.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.operator.OperatorBase;
import com.ql.util.express.instruction.operator.OperatorField;
import com.ql.util.express.parse.ExpressNode;

import java.util.Stack;


/**
 * @author wangyijie
 */
public class FieldCallInstructionFactory extends InstructionFactory {

    @Override
    public boolean createInstruction(ExpressRunner aCompile,
                                     InstructionSet result, Stack<ForRelBreakContinue> forStack,
                                     ExpressNode node, boolean isRoot) throws Exception {
        boolean returnVal;
        ExpressNode[] children = node.getChildren();
        //处理对象
        returnVal = aCompile.createInstructionSetPrivate(result, forStack, children[0], false);

        //处理属性名称
        if (!children[1].getNodeType().getName().equalsIgnoreCase("CONST_STRING")) {
            throw new Exception("对象属性名称不是字符串常量:" + children[1]);
        }

        String fieldName = (String) children[1].getObjectValue();


        OperatorBase op = new OperatorField(fieldName);
        result.addInstruction(new InstructionOperator(op, 1).setLine(node.getLine()));
        return returnVal;
    }

}
