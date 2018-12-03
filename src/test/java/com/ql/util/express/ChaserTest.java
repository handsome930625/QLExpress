package com.ql.util.express;

import com.ql.util.express.instruction.env.DefaultContext;
import com.ql.util.express.instruction.operator.Operator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 源码测试
 *
 * @author 王亦杰（yijie.wang01@ucarinc.com）
 * @version 1.0
 * @date 2018/11/26 10:51
 */
public class ChaserTest {

    @Test
    public void testIf() {
        ExpressRunner runner = new ExpressRunner(true, true);
        List<String> errorList = new ArrayList<>();
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("n", 1);
        String express = "if(1==10){return 1;}else{return 2;}";
        Object r = null;
        try {
            r = runner.execute(express, context, errorList, false, true);
            System.out.println(errorList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(r);
    }

    @Test
    public void testFor() {
        String express = "sum=0;for(i=0;i<10;i++){" +
                "sum=sum+i;" +
                "}" +
                "return i;";
        ExpressRunner runner = new ExpressRunner(true, true);
        List<String> errorList = new ArrayList<>();
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object r = null;
        try {
            r = runner.execute(express, context, errorList, false, true);
            System.out.println(errorList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(r);
    }

    @Test
    public void testDefFunction() {
        String express = "function add(int a,int b){" +
                "  return a+b;" +
                "};";
        ExpressRunner runner = new ExpressRunner(true, true);
        List<String> errorList = new ArrayList<>();
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object r = null;
        try {
            r = runner.execute(express, context, errorList, false, true);
            System.out.println(errorList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(r);
    }

    @Test
    public void testAddOp() throws Exception {
        ExpressRunner runner = new ExpressRunner(true, true);
        runner.addOperator("join", new JoinOperator());
        String express = "1 join 2 join 3";
        List<String> errorList = new ArrayList<>();
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object r = null;
        try {
            r = runner.execute(express, context, errorList, false, true);
            System.out.println(errorList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(r);
    }

    public class JoinOperator extends Operator {
        public Object executeInner(Object[] list) throws Exception {
            Object opdata1 = list[0];
            Object opdata2 = list[1];
            if (opdata1 instanceof java.util.List) {
                ((java.util.List) opdata1).add(opdata2);
                return opdata1;
            } else {
                java.util.List result = new java.util.ArrayList();
                result.add(opdata1);
                result.add(opdata2);
                return result;
            }
        }
    }

    @Test
    public void testAddMacro() throws Exception {
        ExpressRunner runner = new ExpressRunner(true, true);
        runner.addMacro("计算平均成绩", "(语文+数学+英语)/3.0");
    }

    @Test
    public void testMulti() {
        String express = "n=1+2*3+4*5";
        ExpressRunner runner = new ExpressRunner(true, true);
        List<String> errorList = new ArrayList<>();
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object r = null;
        try {
            r = runner.execute(express, context, errorList, false, true);
            System.out.println(errorList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(r);
    }
}
