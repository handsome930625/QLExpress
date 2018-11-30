package com.ql.util.express.instruction.factory;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionGoTo;
import com.ql.util.express.instruction.detail.InstructionGoToWithCondition;
import com.ql.util.express.parse.ExpressNode;

import java.util.Stack;

/**
 * 生成 if 相关指令
 *
 * @author wangyijie
 */
public class IfInstructionFactory extends InstructionFactory {

    @Override
    public boolean createInstruction(ExpressRunner compile, InstructionSet result,
                                     Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot)
            throws Exception {
        // 至少要有条件语句 和 执行语句
        ExpressNode[] oldChildren = node.getChildren();
        if (oldChildren.length < 2) {
            throw new Exception("if 操作符至少需要2个操作数 ");
        } else if (oldChildren.length > 5) {
            throw new Exception("if 操作符最多只有5个操作数 ");
        }
        ExpressNode[] children = new ExpressNode[3];
        int point = 0;
        // 忽略这些关键字
        for (ExpressNode anOldChildren : oldChildren) {
            if (anOldChildren.isTypeEqualsOrChild("then")
                    || anOldChildren.isTypeEqualsOrChild("else")
                    || anOldChildren.isTypeEqualsOrChild("?")
                    || anOldChildren.isTypeEqualsOrChild(":")) {
                continue;
            }
            children[point] = anOldChildren;
            point = point + 1;
        }
        // 如果没有 else 加一个空的 else
        if (point == 2) {
            children[2] = new ExpressNode(compile.getNodeTypeManager().findNodeType("STAT_BLOCK"), null);
        }
        int[] finishPoint = new int[children.length];
        // condition
        boolean r1 = compile.createInstructionSetPrivate(result, forStack, children[0], false);
        finishPoint[0] = result.getCurrentPoint();
        // true
        boolean r2 = compile.createInstructionSetPrivate(result, forStack, children[1], false);
        result.insertInstruction(finishPoint[0] + 1, new InstructionGoToWithCondition(false, result.getCurrentPoint() - finishPoint[0] + 2, true).setLine(node.getLine()));
        finishPoint[1] = result.getCurrentPoint();
        // false
        boolean r3 = compile.createInstructionSetPrivate(result, forStack, children[2], false);
        result.insertInstruction(finishPoint[1] + 1, new InstructionGoTo(result.getCurrentPoint() - finishPoint[1] + 1).setLine(node.getLine()));
        return r1 || r2 || r3;
    }
}
