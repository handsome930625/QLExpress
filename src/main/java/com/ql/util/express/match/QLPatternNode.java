package com.ql.util.express.match;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

enum MatchMode {
    /**
     * 并
     */
    AND,
    /**
     * 或
     */
    OR,

    DETAIL,
    /**
     * 空
     */
    NULL
}

/**
 * @author wangyijie
 */
public class QLPatternNode {

    private static final Log log = LogFactory.getLog(QLPatternNode.class);

    private INodeTypeManager nodeTypeManager;
    /**
     * 原字符串
     */
    String name;
    /**
     * 原始的字符串
     */
    private String origContent;
    /**
     * 匹配模式
     */
    MatchMode matchMode = MatchMode.NULL;
    /**
     * 是否一个子匹配模式
     */
    private boolean isChildMode = false;
    /**
     * 层次
     */
    private int level = 0;
    /**
     * 是否根节点,例如：if^
     */
    protected boolean isTreeRoot;
    /**
     * 最小匹配次数，0..n
     */
    protected int minMatchNum = 1;
    /**
     * 最大匹配次数
     */
    protected int maxMatchNum = 1;
    /**
     * 匹配类型，例如 ID,if,SELECT
     */
    protected INodeType nodeType;

    /**
     * 匹配到的节点需要转换成的类型，例如 ID->CONST_STRING
     */
    protected INodeType targetNodeType;

    /**
     * 需要转为的虚拟类型，例如：(ID$(,$ID)*)#COL_LIST
     */
    protected INodeType rootNodeType;

    /**
     * 是否匹配成功，但在输出的时候忽略,用"~"表示
     * CONST$(,~$CONST)*
     */
    protected boolean isSkip;

    /**
     * 取反，例如：+@,匹配不是+的所有字符
     */
    protected boolean blame = false;


    /**
     * 子匹配模式
     */
    List<QLPatternNode> children = new ArrayList<>();

    protected QLPatternNode(INodeTypeManager manager, String name, String origContent) throws Exception {
        this(manager, name, origContent, false, 1);
        if (!this.toString().equals(origContent)) {
            throw new Exception("语法定义解析后的结果与原始值不一致，原始值:" + origContent + " 解析结果:" + this.toString());
        }
    }

    protected QLPatternNode(INodeTypeManager manager, String name, String origContent, boolean isChildMode, int
            level) throws Exception {
        this.nodeTypeManager = manager;
        this.name = name;
        this.origContent = origContent;
        this.isChildMode = isChildMode;
        this.level = level;
        this.splitChild();
    }

    public void splitChild() throws Exception {
        String orgStr = this.origContent;
        if ("(".equals(orgStr) || ")".equals(orgStr) || "|".equals(orgStr) || "||".equals(orgStr) || "/**".equals(orgStr) || "**/".equals(orgStr) || "*".equals(orgStr)) {
            this.matchMode = MatchMode.DETAIL;
            this.nodeType = this.nodeTypeManager.findNodeType(orgStr);
            return;
        }

        StringBuilder tempStr = new StringBuilder();

        int count = 0;
        for (int i = 0; i < orgStr.length(); i++) {
            if (orgStr.charAt(i) == '(') {
                tempStr.append(orgStr.charAt(i));
                count = count + 1;
            } else if (orgStr.charAt(i) == ')') {
                tempStr.append(orgStr.charAt(i));
                count = count - 1;
            } else if (count > 0) {
                tempStr.append(orgStr.charAt(i));
            } else if (orgStr.charAt(i) == '$') {
                if (this.matchMode != MatchMode.NULL
                        && this.matchMode != MatchMode.AND) {
                    throw new Exception("不正确的模式串,在一个匹配模式中不能|,$并存,请使用字串模式:"
                            + orgStr);
                }
                // 添加一个匿名的匹配
                children.add(new QLPatternNode(this.nodeTypeManager, "ANONY_PATTERN", tempStr.toString(), false, this.level + 1));
                this.matchMode = MatchMode.AND;
                tempStr = new StringBuilder();
            } else if (orgStr.charAt(i) == '|') {
                if (this.matchMode != MatchMode.NULL
                        && this.matchMode != MatchMode.OR) {
                    throw new Exception("不正确的模式串,在一个匹配模式中不能|,$并存,请使用字串模式:"
                            + orgStr);
                }
                children.add(new QLPatternNode(this.nodeTypeManager, "ANONY_PATTERN", tempStr.toString(), false, this.level + 1));
                this.matchMode = MatchMode.OR;
                tempStr = new StringBuilder();
            } else if (orgStr.charAt(i) == '#') {
                this.rootNodeType = this.nodeTypeManager.findNodeType(orgStr.substring(i + 1));
                break;
            } else {
                tempStr.append(orgStr.charAt(i));
            }
        }

        // 处理没有()的内容
        if (count > 0) {
            throw new Exception("不正确的模式串,(没有找到对应的):" + orgStr);
        }

        if (this.children.size() > 0) {
            children.add(new QLPatternNode(this.nodeTypeManager, "ANONY_PATTERN", tempStr.toString(), false, this.level + 1));
            tempStr = new StringBuilder();
        }

        // 需要剔除乘法*的情况
        if (tempStr.toString().endsWith("*") && tempStr.length() > 1) {
            this.minMatchNum = 0;
            this.maxMatchNum = Integer.MAX_VALUE;
            tempStr = new StringBuilder(tempStr.substring(0, tempStr.length() - 1));
        }

        if (tempStr.toString().endsWith("}")) {
            int index = tempStr.lastIndexOf("{");
            if (index > 0) {
                String numStr = tempStr.substring(index + 1, tempStr.length() - 1);
                int index2 = numStr.indexOf(':');
                if (index2 > 0) {
                    this.minMatchNum = Integer.parseInt(numStr.substring(0, index2));
                    this.maxMatchNum = Integer.parseInt(numStr.substring(index2 + 1));
                } else {
                    this.minMatchNum = Integer.parseInt(numStr);
                    this.maxMatchNum = Integer.parseInt(numStr);
                }
                tempStr = new StringBuilder(tempStr.substring(0, index));
            }
        }
        if (tempStr.toString().endsWith("^") && tempStr.length() > 1) {
            this.isTreeRoot = true;
            tempStr = new StringBuilder(tempStr.substring(0, tempStr.length() - 1));
        }


        if (tempStr.toString().endsWith("~") && tempStr.length() > 1) {
            this.isSkip = true;
            tempStr = new StringBuilder(tempStr.substring(0, tempStr.length() - 1));
        }
        if (tempStr.toString().endsWith("@") && tempStr.length() > 1) {
            this.blame = true;
            tempStr = new StringBuilder(tempStr.substring(0, tempStr.length() - 1));
        }

        //处理(ABC|bcd)模式
        if (tempStr.length() > 2 && tempStr.charAt(0) == '(' && tempStr.charAt(tempStr.length() - 1) == ')') {
            this.isChildMode = true;
            this.children.add(new QLPatternNode(this.nodeTypeManager, "ANONY_PATTERN", tempStr.substring(1, tempStr.length() - 1), false, this.level + 1));
            this.matchMode = MatchMode.AND;
            tempStr = new StringBuilder();

        }

        int index = tempStr.indexOf("->");
        if (index > 0) {
            this.targetNodeType = this.nodeTypeManager.findNodeType(tempStr.substring(index + 2));
            tempStr = new StringBuilder(tempStr.substring(0, index));
        }
        if (tempStr.length() > 0) {
            this.matchMode = MatchMode.DETAIL;
            this.nodeType = this.nodeTypeManager.findNodeType(tempStr.toString());
        }
    }

    public List<QLPatternNode> getChildren() {
        return this.children;
    }

    public INodeType getNodeType() {
        return this.nodeType;
    }

    public boolean isDetailMode() {
        return this.matchMode == MatchMode.DETAIL;
    }

    public boolean isAndMode() {
        return this.matchMode == MatchMode.AND;
    }

    @Override
    public String toString() {
        String result;
        if (this.matchMode == MatchMode.AND) {
            result = this.joinStringList(this.children, "$");
        } else if (this.matchMode == MatchMode.OR) {
            result = this.joinStringList(this.children, "|");
        } else {
            result = this.nodeType.getName();
        }
        if (this.targetNodeType != null) {
            result = result + "->" + this.targetNodeType.getName();
        }
        if (this.isChildMode) {
            result = "(" + result + ")";
        }
        if (this.isSkip) {
            result = result + '~';
        }
        if (this.blame) {
            result = result + '@';
        }
        if (this.isTreeRoot) {
            result = result + '^';
        }
        if (this.minMatchNum == 0 && this.maxMatchNum == Integer.MAX_VALUE) {
            result = result + '*';
        } else if (this.minMatchNum == this.maxMatchNum && this.maxMatchNum > 1) {
            result = result + "{" + this.maxMatchNum + "}";
        } else if (this.minMatchNum != this.maxMatchNum) {
            result = result + "{" + this.minMatchNum + ":" + this.maxMatchNum + "}";
        }

        if (this.rootNodeType != null) {
            result = result + '#' + this.rootNodeType.getName();
        }
        return result;
    }

    public String joinStringList(List<QLPatternNode> list, String splitChar) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                buffer.append(splitChar);
            }
            buffer.append(list.get(i));
        }
        return buffer.toString();
    }
}

