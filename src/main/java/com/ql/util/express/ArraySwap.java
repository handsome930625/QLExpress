package com.ql.util.express;

/**
 * @author wangyijie
 */
public final class ArraySwap {
    /**
     * 操作数集合，这边是持有这个操作数的引用
     */
    private OperateData[] arrays;
    /**
     * 可以使用的操作数地址开始索引
     */
    private int start;
    /**
     * 可以使用的长度
     */
    public int length;

    public void swap(OperateData[] arrays, int start, int length) {
        this.arrays = arrays;
        this.start = start;
        this.length = length;
    }

    public OperateData get(int i) {
        return this.arrays[i + start];
    }

}
