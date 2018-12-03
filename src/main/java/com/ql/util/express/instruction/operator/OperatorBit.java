package com.ql.util.express.instruction.operator;

import java.util.Arrays;

/**
 * @author tianqiao
 * @date 16/12/15
 */
public class OperatorBit extends Operator {

    private static final long serialVersionUID = -9048292554191967104L;

    public OperatorBit(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        if ("~".equals(this.name)) {
            if (list.length == 1 && list[0] instanceof Number) {
                if (list[0] instanceof Integer) {
                    return ~(((Number) list[0]).intValue());
                } else {
                    return ~(((Number) list[0]).longValue());
                }
            } else {
                throw new Exception("取反操作符 ~ 参数不合法:" + Arrays.toString(list));
            }
        }
        if ("&".equals(this.name)) {
            if (list.length == 2 && list[0] instanceof Number && list[1] instanceof Number) {
                if (list[0] instanceof Integer && list[1] instanceof Integer) {
                    return (Integer) list[0] & (Integer) list[1];
                }
                return (((Number) list[0]).longValue()) & (((Number) list[1]).longValue());
            } else {
                throw new Exception("按位与操作符 & 两边的参数不合法:" + Arrays.toString(list));
            }
        }
        if ("|".equals(this.name)) {
            if (list.length == 2 && list[0] instanceof Number && list[1] instanceof Number) {
                if (list[0] instanceof Integer && list[1] instanceof Integer) {
                    return (Integer) list[0] | (Integer) list[1];
                }
                return (((Number) list[0]).longValue()) | (((Number) list[1]).longValue());
            } else {
                throw new Exception("按位或操作符 | 两边的参数不合法:" + Arrays.toString(list));
            }
        }
        if ("^".equals(this.name)) {
            if (list.length == 2 && list[0] instanceof Number && list[1] instanceof Number) {
                if (list[0] instanceof Integer && list[1] instanceof Integer) {
                    return (Integer) list[0] ^ (Integer) list[1];
                }
                return (((Number) list[0]).longValue()) ^ (((Number) list[1]).longValue());
            } else {
                throw new Exception("按位异或操作符 ^ 两边的参数不合法:" + Arrays.toString(list));
            }
        }
        if ("<<".equals(this.name)) {
            if (list.length == 2 && list[0] instanceof Number && list[1] instanceof Number) {
                if (list[0] instanceof Integer && list[1] instanceof Integer) {
                    return (Integer) list[0] << (Integer) list[1];
                }
                return (((Number) list[0]).longValue()) << (((Number) list[1]).longValue());
            } else {
                throw new Exception("左移操作符 << 两边的参数不合法:" + Arrays.toString(list));
            }
        }
        if (">>".equals(this.name)) {
            if (list.length == 2 && list[0] instanceof Number && list[1] instanceof Number) {
                if (list[0] instanceof Integer && list[1] instanceof Integer) {
                    return (Integer) list[0] >> (Integer) list[1];
                }
                return (((Number) list[0]).longValue()) >> (((Number) list[1]).longValue());
            } else {
                throw new Exception("右移操作符 >> 两边的参数不合法:" + Arrays.toString(list));
            }
        }
        throw new Exception("不支持的位运算操作符:" + Arrays.toString(list));
    }
}
