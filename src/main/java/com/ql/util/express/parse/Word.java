package com.ql.util.express.parse;

/**
 * 分词的token
 *
 * @author wangyijie
 */
public class Word {
    /**
     * token
     */
    public String word;
    /**
     * 行
     */
    public int line;
    /**
     * 列
     */
    public int col;
    /**
     * 第几个token
     */
    public int index;

    public Word(String word, int line, int col) {
        this.word = word;
        this.line = line;
        this.col = col - word.length() + 1;
    }

    @Override
    public String toString() {
        return this.word;
    }
}
