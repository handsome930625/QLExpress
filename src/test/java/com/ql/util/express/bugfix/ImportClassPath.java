package com.ql.util.express.bugfix;

import com.ql.util.express.instruction.env.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.instruction.env.IExpressContext;
import org.junit.Test;

/**
 * Created by tianqiao on 17/6/23.
 */
public class ImportClassPath {
    
    @Test
    public void test() {
    
        ExpressRunner runner = new ExpressRunner();
        String exp ="return new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\").format(new Date())";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        Object result = null;
        try {
            result = runner.execute(exp,context,null,false,false);
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void test2() throws Exception {
        
        ExpressRunner runner = new ExpressRunner();
        String exp ="return new java.text.SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\").format(new Date())";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        Object result = null;
        result = runner.execute(exp,context,null,false,false);
        System.out.println(result);
    }
    
    @Test
    public void test3() throws Exception {
        
        ExpressRunner runner = new ExpressRunner();
        String exp ="import java.text.SimpleDateFormat; return new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\").format(new Date())";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        Object result = null;
        result = runner.execute(exp,context,null,false,false);
        System.out.println(result);
    }
}
