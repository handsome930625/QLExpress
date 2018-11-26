package com.ql.util.express.parse;

/**
 * @author wangyijie
 */
public class Word {
    public String word;
    public int line;
    public int col;
    public int index;

    public Word(String aWord, int aLine, int aCol) {
        this.word = aWord;
        this.line = aLine;
        this.col = aCol - aWord.length() + 1;
    }

    @Override
    public String toString() {
        return this.word;
    }
}
