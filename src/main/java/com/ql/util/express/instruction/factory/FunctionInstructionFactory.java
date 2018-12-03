package com.ql.util.express.instruction.factory;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.instruction.env.InstructionSet;
import com.ql.util.express.instruction.FunctionInstructionSet;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;
import com.ql.util.express.parse.ExpressNode;

import java.util.Stack;

/**
 * 定义代码中的函数
 *
 * @author wangyijie
 */
public class FunctionInstructionFactory extends InstructionFactory {

    @Override
    public boolean createInstruction(ExpressRunner compile, InstructionSet result,
                                     Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot)
            throws Exception {
        ExpressNode[] children = node.getChildren();
        if (children.length != 3) {
            throw new Exception("function 操作符需要3个操作数 ");
        }
        String functionName = children[0].getValue();
        ExpressNode[] varDefines = children[1].getChildren();

        String instructionSetType;
        if (node.isTypeEqualsOrChild("class")) {
            instructionSetType = InstructionSet.TYPE_CLASS;
        } else {
            instructionSetType = InstructionSet.TYPE_FUNCTION;
        }
        InstructionSet functionSet = new InstructionSet(instructionSetType);

        int point = 0;
        while (point < varDefines.length) {
            if (!varDefines[point].isTypeEqualsOrChild("def")) {
                throw new Exception("function的参数定义错误," + varDefines[point] + "不是一个Class");
            }
            Class<?> varClass = (Class<?>) varDefines[point].getChildren()[0].getObjectValue();
            String varName = varDefines[point].getChildren()[1].getValue();
            OperateDataLocalVar tmpVar = new OperateDataLocalVar(varName, varClass);
            functionSet.addParameter(tmpVar);
            point = point + 1;
        }

        ExpressNode functionRoot = new ExpressNode(compile.getNodeTypeManager().findNodeType("FUNCTION_DEFINE"), "function-" + functionName);
        for (ExpressNode tempNode : children[2].getChildren()) {
            functionRoot.addLeftChild(tempNode);
        }
        compile.createInstructionSet(functionRoot, functionSet);
        result.addMacroDefine(functionName, new FunctionInstructionSet(functionName, instructionSetType, functionSet));
        return false;
    }
}
