package com.ql.util.express.match;

import java.util.List;

/**
 * @author wangyijie
 */
public class QLMatchResult {
    /**
     * 匹配节点树
     */
    protected List<QLMatchResultTree> matches;
    /**
     * 最后匹配到的节点索引
     */
    protected int matchLastIndex;

    public QLMatchResult(List<QLMatchResultTree> aList, int aIndex) {
        this.matchLastIndex = aIndex;
        this.matches = aList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (QLMatchResultTree item : matches) {
            item.printNode(builder, 1);
        }
        return builder.toString();
    }

    public List<QLMatchResultTree> getMatches() {
        return matches;
    }

    public void setMatches(List<QLMatchResultTree> matches) {
        this.matches = matches;
    }

    public int getMatchLastIndex() {
        return matchLastIndex;
    }

    public void setMatchLastIndex(int matchLastIndex) {
        this.matchLastIndex = matchLastIndex;
    }
}
