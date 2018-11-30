package com.ql.util.express;

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

}
