package com.ql.util.express.instruction;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
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

    private static Map<String, InstructionFactory> instructionFactory = new HashMap<>();

    public static InstructionFactory getInstructionFactory(String factory) {
        try {
            InstructionFactory result = instructionFactory.get(factory);
            if (result == null) {
                result = (InstructionFactory) Class.forName(factory)
                        .newInstance();
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract boolean createInstruction(ExpressRunner aCompile, InstructionSet result,
                                              Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot)
            throws Exception;
}
