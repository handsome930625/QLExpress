package com.ql.util.express.test;

import org.junit.Assert;
import org.junit.Test;

import com.ql.util.express.instruction.env.DefaultContext;
import com.ql.util.express.ExpressRunner;

public class ExportDefine {

	@Test
	public void testABC() throws Exception {
		String express = 
				"function initial(){" +
				"   exportAlias qh example.child ; exportDef int abc = 100;" +
				"}; " +
				"initial();" +
				"abc = abc + 10000;" +
				"System.out.println(abc);" +
				"{" +
				"   alias qh example.child.a;" 
				+ " qh =qh + \"-ssss\";" 
				+ "};" 
				+" qh.a = qh.a +\"-qh\";" + " return example.child.a ";
		ExpressRunner runner = new ExpressRunner(false,true);
		DefaultContext<String, Object> context = new DefaultContext<String, Object>();
		context.put("example", new BeanExample());
		Object r = runner.execute(express, context, null, false, true);
		System.out.println(r);
		Assert.assertTrue("别名export实现 错误", r.toString().equalsIgnoreCase("qh-ssss-qh"));
		Assert.assertTrue("别名export实现 错误", ((BeanExample) context.get("example")).child.a.toString().equalsIgnoreCase("qh-ssss-qh"));
		
	}
}
