package com.ql.util.express;

/**
 * @author wangyijie
 */
public final class ArraySwap {
    /**
     * 操作数
     */
    private OperateData[] arrays;
    private int start;
    public int length;

    public void swap(OperateData[] aArrays, int aStart, int aLength) {
        this.arrays = aArrays;
        this.start = aStart;
        this.length = aLength;
    }

    public OperateData get(int i) {
        return this.arrays[i + start];
    }

}
