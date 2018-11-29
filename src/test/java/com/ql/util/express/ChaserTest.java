package com.ql.util.express;

import org.junit.Test;

/**
 * description: 源码测试
 *
 * @author 王亦杰（yijie.wang01@ucarinc.com）
 * @version 1.0
 * @date 2018/11/26 10:51
 */
public class ChaserTest {

    @Test
    public void testSimple() {
        ExpressRunner runner = new ExpressRunner(true, true);
        DefaultContext<String, Object> context = new DefaultContext<>();

//        String express = "n=10;sum=0;for(i=0;i<n;i++){sum=sum+i;}return sum;";
        String express = "1+1";
        Object r = null;
        try {
            r = runner.execute(express, context, null, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(r);
    }
}
