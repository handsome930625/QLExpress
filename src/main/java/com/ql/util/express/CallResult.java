package com.ql.util.express;

/**
 * 调用结果返回类
 *
 * @author wangyijie
 */
public class CallResult {
    /**
     * 结果
     */
    private Object returnValue;
    /**
     * 是否退出了
     */
    private boolean isExit;

    public CallResult(Object aReturnValue, boolean aIsExit) {
        this.initial(aReturnValue, aIsExit);
    }

    public void initial(Object aReturnValue, boolean aIsExit) {
        this.returnValue = aReturnValue;
        this.isExit = aIsExit;
    }

    public void clear() {
        this.returnValue = null;
        this.isExit = false;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public boolean isExit() {
        return isExit;
    }

}

