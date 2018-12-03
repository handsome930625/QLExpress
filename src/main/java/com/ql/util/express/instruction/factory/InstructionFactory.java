package com.ql.util.express.instruction.factory;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.instruction.env.InstructionSet;
import com.ql.util.express.parse.ExpressNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * 指令工厂
 *
 * @author wangyijie
 */
public abstract class InstructionFactory {
    /**
     * 指令工厂map
     */
    private static final Map<String, InstructionFactory> INSTRUCTION_FACTORY = new HashMap<>();

    /**
     * 获取对应 node type 的指令工厂
     */
    public static InstructionFactory getInstructionFactory(String factory) {
        try {
            InstructionFactory result = INSTRUCTION_FACTORY.get(factory);
            if (result == null) {
                synchronized (INSTRUCTION_FACTORY) {
                    result = (InstructionFactory) Class.forName(factory)
                            .newInstance();
                    INSTRUCTION_FACTORY.put(factory, result);
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract boolean createInstruction(ExpressRunner compile, InstructionSet result,
                                              Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot)
            throws Exception;
}