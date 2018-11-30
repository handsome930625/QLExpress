package com.ql.util.express;


/**
 * @author wangyijie
 */
public final class RunEnvironment {
    private boolean isTrace;
    private int point = -1;
    protected int programPoint = 0;
    /**
     * 操作数栈
     */
    private OperateData[] dataContainer;

    private ArraySwap arraySwap = new ArraySwap();
    /**
     * 是否退出
     */
    private boolean isExit = false;
    /**
     * 返回值
     */
    private Object returnValue = null;
    /**
     * 运行环境指令
     */
    private InstructionSet instructionSet;
    /**
     * 变量上下文
     */
    private InstructionSetContext context;


    public RunEnvironment(InstructionSet instructionSet, InstructionSetContext context, boolean isTrace) {
        this.dataContainer = new OperateData[15];
        this.instructionSet = instructionSet;
        this.context = context;
        this.isTrace = isTrace;
    }

    public void initial(InstructionSet instructionSet, InstructionSetContext context, boolean isTrace) {
        this.instructionSet = instructionSet;
        this.context = context;
        this.isTrace = isTrace;
    }

    public void clear() {
        isTrace = false;
        point = -1;
        programPoint = 0;

        isExit = false;
        returnValue = null;

        instructionSet = null;
        context = null;

    }

    public InstructionSet getInstructionSet() {
        return instructionSet;
    }


    public InstructionSetContext getContext() {
        return this.context;
    }

    public void setContext(InstructionSetContext aContext) {
        this.context = aContext;
    }

    public boolean isExit() {
        return isExit;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object value) {
        this.returnValue = value;
    }

    public void quitExpress(Object aReturnValue) {
        this.isExit = true;
        this.returnValue = aReturnValue;
    }

    public void quitExpress() {
        this.isExit = true;
        this.returnValue = null;
    }

    public boolean isTrace() {
        return this.isTrace;
    }

    public int getProgramPoint() {
        return programPoint;
    }

    public void programPointAddOne() {
        programPoint++;
    }

    public void gotoLastWhenReturn() {
        programPoint = this.instructionSet.getInstructionLength();
    }

    public int getDataStackSize() {
        return this.point + 1;
    }

    public void push(OperateData data) {
        this.point++;
        if (this.point >= this.dataContainer.length) {
            ensureCapacity(this.point + 1);
        }
        this.dataContainer[point] = data;
    }

    public OperateData peek() {
        if (point < 0) {
            throw new RuntimeException("系统异常，堆栈指针错误");
        }
        return this.dataContainer[point];
    }

    public OperateData pop() {
        if (point < 0) {
            throw new RuntimeException("系统异常，堆栈指针错误");
        }
        OperateData result = this.dataContainer[point];
        this.point--;
        return result;
    }

    public void clearDataStack() {
        this.point = -1;
    }

    public void gotoWithOffset(int aOffset) {
        this.programPoint = this.programPoint + aOffset;
    }

    /**
     * 此方法是调用最频繁的，因此尽量精简代码，提高效率
     *
     * @param context
     * @param len
     * @return
     * @throws Exception
     */
    public ArraySwap popArray(InstructionSetContext context, int len) throws Exception {
        int start = point - len + 1;
        this.arraySwap.swap(this.dataContainer, start, len);
        point = point - len;
        return this.arraySwap;
    }

    public OperateData[] popArrayOld(InstructionSetContext context, int len) throws Exception {
        int start = point - len + 1;
        OperateData[] result = new OperateData[len];
        System.arraycopy(this.dataContainer, start, result, 0, len);
        point = point - len;
        return result;
    }

    public OperateData[] popArrayBackUp(InstructionSetContext context, int len) throws Exception {
        int start = point - len + 1;
        if (start < 0) {
            throw new Exception("堆栈溢出，请检查表达式是否错误");
        }
        OperateData[] result = new OperateData[len];
        for (int i = 0; i < len; i++) {
            result[i] = this.dataContainer[start + i];
            if (void.class.equals(result[i].getType(context))) {
                throw new Exception("void 不能参与任何操作运算,请检查使用在表达式中使用了没有返回值的函数,或者分支不完整的if语句");
            }
        }
        point = point - len;
        return result;
    }

    /**
     * 扩容 3/2 + 1 的大小
     */
    private void ensureCapacity(int minCapacity) {
        int oldCapacity = this.dataContainer.length;
        if (minCapacity > oldCapacity) {
            int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            OperateData[] tempList = new OperateData[newCapacity];
            System.arraycopy(this.dataContainer, 0, tempList, 0, oldCapacity);
            this.dataContainer = tempList;
        }
    }
}
