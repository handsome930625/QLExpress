
package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


/**
 * 语法解析类
 * 1、单词分解
 *
 * @author xuannan
 */

public class WordSplit {
    /**
     * 文本分析函数，“.”作为操作符号处理
     *
     * @param str String
     * @return String[]
     */
    public static Word[] parse(String[] splitWord, String str) throws Exception {
        if (str == null) {
            return new Word[0];
        }
        char c;
        int line = 1;
        List<Word> list = new ArrayList<>();
        int i = 0;
        int point = 0;
        while (i < str.length()) {
            c = str.charAt(i);
            if (c == '"' || c == '\'') {
                // 字符串处理
                int index = str.indexOf(c, i + 1);
                // 处理字符串中的”问题
                while (index > 0 && str.charAt(index - 1) == '\\') {
                    index = str.indexOf(c, index + 1);
                }
                if (index < 0) {
                    throw new Exception("字符串没有关闭");
                }
                String tempDealStr = str.substring(i, index + 1);
                //处理 \\，\"的情况
                StringBuilder tmpResult = new StringBuilder();
                int tmpPoint = tempDealStr.indexOf("\\");
                while (tmpPoint >= 0) {
                    tmpResult.append(tempDealStr, 0, tmpPoint);
                    if (tmpPoint == tempDealStr.length() - 1) {
                        throw new Exception("字符串中的" + "\\错误:" + tempDealStr);
                    }
                    tmpResult.append(tempDealStr, tmpPoint + 1, tmpPoint + 2);
                    tempDealStr = tempDealStr.substring(tmpPoint + 2);
                    tmpPoint = tempDealStr.indexOf("\\");
                }
                tmpResult.append(tempDealStr);
                list.add(new Word(tmpResult.toString(), line, i));

                if (point < i) {
                    list.add(new Word(str.substring(point, i), line, i));
                }
                i = index + 1;
                point = i;
            } else if (c == '.' && point < i && isNumber(str.substring(point, i))) {
                // 小数点的特殊处理
                i = i + 1;
            } else if (c == ' ' || c == '\r' || c == '\n' || c == '\t' || c == '\u000C') {
                if (point < i) {
                    list.add(new Word(str.substring(point, i), line, i));
                }
                if (c == '\n') {
                    line = line + 1;
                }
                i = i + 1;
                point = i;
            } else {
                boolean isFind = false;
                for (String s : splitWord) {
                    int length = s.length();
                    if (i + length <= str.length() && str.substring(i, i + length).equals(s)) {
                        if (point < i) {
                            list.add(new Word(str.substring(point, i), line, i));
                        }
                        list.add(new Word(str.substring(i, i + length), line, i));
                        i = i + length;
                        point = i;
                        isFind = true;
                        break;
                    }
                }
                if (!isFind) {
                    i = i + 1;
                }
            }
        }
        if (point < i) {
            list.add(new Word(str.substring(point, i), line, i));
        }
        return list.toArray(new Word[0]);
    }

    /**
     * 根据操作符长度排序
     *
     * @param splitWord 运算符
     */
    public static void sortSplitWord(String[] splitWord) {
        Arrays.sort(splitWord, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.compare(o2.length(), o1.length());

            }
        });
    }

    protected static boolean isNumber(String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        char c = str.charAt(0);
        // 数字
        return c >= '0' && c <= '9';
    }


    public static String getPrintInfo(Object[] list, String splitOp) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < list.length; i++) {
            if (i > 0) {
                buffer.append(splitOp);
            }
            buffer.append("{").append(list[i]).append("}");
        }
        return buffer.toString();
    }

}
