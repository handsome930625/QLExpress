package com.ql.util.express.test;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.instruction.env.DefaultContext;
import com.ql.util.express.ExpressRunner;

public class ObjectTest {
	@Test
	public void testABC() throws Exception {
		String express = "object.amount*2+object.volume";
		ExpressRunner runner = new ExpressRunner(false,true);
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		ObjectBean tempObject= new ObjectBean(100,60);
		context.put("object", tempObject);
		Object r =  runner.execute(express, context, null, false,
				true);
		System.out.println(r);
		Assert.assertTrue("数据执行错误", r.toString().equals(260) == false);
	}
    
    @Test
    public void testABC2() throws Exception {
        String express = "object.getAmount(1)";
        ExpressRunner runner = new ExpressRunner(false,true);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        ObjectBean tempObject= new ObjectBean(100,60);
        context.put("object", tempObject);
        Object r =  runner.execute(express, context, null, false,
                true);
        System.out.println(r);
    }

}
