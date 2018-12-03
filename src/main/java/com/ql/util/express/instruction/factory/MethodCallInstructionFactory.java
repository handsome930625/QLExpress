package com.ql.util.express.instruction.factory;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.instruction.env.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.operator.OperatorBase;
import com.ql.util.express.instruction.operator.OperatorMethod;
import com.ql.util.express.parse.ExpressNode;

import java.util.Stack;


/**
 * @author wangyijie
 */
public class MethodCallInstructionFactory extends InstructionFactory {

    @Override
    public boolean createInstruction(ExpressRunner aCompile,
                                     InstructionSet result, Stack<ForRelBreakContinue> forStack,
                                     ExpressNode node, boolean isRoot) throws Exception {
        boolean returnVal;
        ExpressNode[] children = node.getChildren();
        //处理对象
        boolean tmpHas = aCompile.createInstructionSetPrivate(result, forStack, children[0], false);
        returnVal = tmpHas;
        //处理方法名称
        if (!children[1].getNodeType().getName().equalsIgnoreCase("CONST_STRING")) {
            throw new Exception("对象方法名称不是字符串常量:" + children[1]);
        }
        String methodName = (String) children[1].getObjectValue();
        //处理方法参数
        for (int i = 2; i < children.length; i++) {
            tmpHas = aCompile.createInstructionSetPrivate(result, forStack, children[i], false);
            returnVal = returnVal || tmpHas;
        }
        OperatorBase op = new OperatorMethod(methodName);
        result.addInstruction(new InstructionOperator(op, children.length - 1).setLine(node.getLine()));
        return returnVal;
    }

}
