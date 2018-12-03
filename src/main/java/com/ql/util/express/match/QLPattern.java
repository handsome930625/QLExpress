package com.ql.util.express.match;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wangyijie
 */
public class QLPattern {

    private static final Log log = LogFactory.getLog(QLPattern.class);

    /**
     * 将定义好的表达式翻译成QLPatternNode
     *
     * @see com.ql.util.express.parse.KeyWordDefine4Java#nodeTypeDefines
     */
    public static QLPatternNode createPattern(INodeTypeManager nodeTypeManager, String name, String pattern) throws Exception {
        return new QLPatternNode(nodeTypeManager, name, pattern);
    }

    public static QLMatchResult findMatchStatement(INodeTypeManager aManager, QLPatternNode pattern, List<? extends IDataNode> nodes, int point) throws Exception {
        //
        AtomicLong maxMatchPoint = new AtomicLong();

        QLMatchResult result = findMatchStatementWithAddRoot(aManager, pattern, nodes, point, true, maxMatchPoint);

        if (result == null || result.matches.size() == 0) {
            throw new Exception("程序错误，不满足语法规范，没有匹配到合适的语法,最大匹配致[0:" + (maxMatchPoint.longValue() - 1) + "]");
        } else if (result.matches.size() != 1) {
            throw new Exception("程序错误，不满足语法规范，必须有一个根节点：" + pattern + ",最大匹配致[0:" + (maxMatchPoint.longValue() - 1) + "]");
        }
        return result;
    }

    private static QLMatchResult findMatchStatementWithAddRoot(INodeTypeManager aManager, QLPatternNode pattern, List<? extends IDataNode> nodes, int point, boolean isRoot, AtomicLong maxMatchPoint) throws Exception {
        QLMatchResult result;
        List<QLMatchResultTree> tempList = null;
        int count = 0;
        int lastPoint = point;
        while (true) {
            QLMatchResult tempResult;
            if (pattern.matchMode == MatchMode.DETAIL) {
                tempResult = matchDetailOneTime(aManager, pattern, nodes, lastPoint, maxMatchPoint);
            } else if (pattern.matchMode == MatchMode.AND) {
                tempResult = matchAndOneTime(aManager, pattern, nodes, lastPoint, maxMatchPoint);
            } else if (pattern.matchMode == MatchMode.OR) {
                tempResult = matchOrOneTime(aManager, pattern, nodes, lastPoint, maxMatchPoint);
            } else {
                throw new Exception("不正确的类型：" + pattern.matchMode.toString());
            }
            // 判断匹配到的次数
            if (tempResult == null) {
                // 没找到匹配的，比较下匹配次数，如果满足也算匹配 see *
                if (count >= pattern.minMatchNum && count <= pattern.maxMatchNum) {
                    // 正确匹配
                    if (tempList == null) {
                        tempList = new ArrayList<>();
                    }
                    result = new QLMatchResult(tempList, lastPoint);
                } else {
                    result = null;
                }
                break;
            } else {
                // 匹配到了
                if (tempList == null) {
                    tempList = new ArrayList<>();
                }
                lastPoint = tempResult.matchLastIndex;
                if (pattern.isTreeRoot) {
                    if (tempResult.matches.size() > 1) {
                        throw new Exception("根节点的数量必须是1");
                    }
                    if (tempList.size() == 0) {
                        tempList.addAll(tempResult.matches);
                    } else {
                        tempResult.matches.get(0).addLeftAll(tempList);
                        tempList = tempResult.matches;
                    }
                } else {
                    tempList.addAll(tempResult.matches);
                }
            }
            // 计算下匹配到的次数，如果等于最大匹配次数，那么跳出
            count = count + 1;
            if (count == pattern.maxMatchNum) {
                result = new QLMatchResult(tempList, lastPoint);
                break;
            }
        }
        // while done
        // skip see (~) 字符
        if (result != null && pattern.isSkip) {
            // 忽略跳过所有匹配到的节点
            result.matches.clear();
        }

        if (result != null && result.matches.size() > 0 && pattern.rootNodeType != null) {
            QLMatchResultTree tempTree = new QLMatchResultTree(pattern.rootNodeType, nodes.get(0).createExpressNode(pattern.rootNodeType, null));
            tempTree.addLeftAll(result.matches);
            result.matches.clear();
            result.matches.add(tempTree);
        }
        return result;
    }

    /**
     * 详细匹配，我理解的是严格匹配
     */
    private static QLMatchResult matchDetailOneTime(INodeTypeManager aManager, QLPatternNode pattern, List<? extends IDataNode> nodes, int point, AtomicLong maxMatchPoint) throws Exception {
        QLMatchResult result = null;
        INodeType eofNodeType = aManager.findNodeType("EOF");

        if (pattern.nodeType == eofNodeType && point == nodes.size()) {
            // 如果是程序结尾
            result = new QLMatchResult(new ArrayList<QLMatchResultTree>(), point + 1);
        } else if (pattern.nodeType == eofNodeType && point < nodes.size() && "}".equals(nodes.get(point).getValue())) {
            // 如果是程序结尾符，并且是 }
            result = new QLMatchResult(new ArrayList<QLMatchResultTree>(), point);
        } else if (point == nodes.size() && pattern.nodeType.getPatternNode() != null) {
            result = findMatchStatementWithAddRoot(aManager, pattern.nodeType.getPatternNode(), nodes, point, false, maxMatchPoint);
        } else if (point < nodes.size()) {
            INodeType tempNodeType = nodes.get(point).getTreeType();

            if (tempNodeType == null) {
                tempNodeType = nodes.get(point).getNodeType();
            }
            if (tempNodeType != null && !tempNodeType.equals(pattern.nodeType)) {
                tempNodeType = null;
            }

            if (tempNodeType != null) {
                List<QLMatchResultTree> tempList = new ArrayList<>();
                // 注意 targetNodeType
                tempList.add(new QLMatchResultTree(tempNodeType, nodes.get(point), pattern.targetNodeType));
                point = point + 1;
                result = new QLMatchResult(tempList, point);

                traceLog(pattern, result, nodes, point - 1, 1);
            } else if (pattern.nodeType.getPatternNode() != null) {
                result = findMatchStatementWithAddRoot(aManager, pattern.nodeType.getPatternNode(), nodes, point, false, maxMatchPoint);
                if (pattern.targetNodeType != null && result != null && result.matches.size() > 0) {
                    if (result.matches.size() > 1) {
                        throw new Exception("设置了类型转换的语法，只能有一个根节点");
                    }
                    result.matches.get(0).targetNodeType = pattern.targetNodeType;
                }
            }

            if (pattern.blame) {
                // 取返处理
                if (result == null) {
                    List<QLMatchResultTree> tempList = new ArrayList<>();
                    tempList.add(new QLMatchResultTree(null, nodes.get(point), null));
                    point = point + 1;
                    result = new QLMatchResult(tempList, point);
                } else {
                    result = null;
                }
            }
        }


        if (result != null && result.matchLastIndex > maxMatchPoint.longValue()) {
            maxMatchPoint.set(result.matchLastIndex);
        }

        return result;

    }

    private static QLMatchResult matchOrOneTime(INodeTypeManager aManager,
                                                QLPatternNode pattern, List<? extends IDataNode> nodes, int point,
                                                AtomicLong maxMatchPoint) throws Exception {
        QLMatchResult result;
        for (QLPatternNode item : pattern.children) {
            result = findMatchStatementWithAddRoot(aManager, item, nodes, point, false, maxMatchPoint);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private static QLMatchResult matchAndOneTime(INodeTypeManager aManager, QLPatternNode pattern, List<? extends IDataNode> nodes, int point, AtomicLong maxMatchPoint) throws Exception {
        int origPoint = point;
        QLMatchResultTree root = null;
        // 用于调试日志的输出
        int matchCount = 0;
        List<QLMatchResultTree> tempList = null;
        for (QLPatternNode item : pattern.children) {
            if (point > nodes.size()) {
                return null;
            }
            QLMatchResult tempResult = findMatchStatementWithAddRoot(aManager, item, nodes,
                    point, false, maxMatchPoint);
            // 有一个不匹配就直接返回
            if (tempResult == null) {
                return null;
            }

            if (tempResult.matches.size() > 0) {
                matchCount = matchCount + 1;
            }
            if (tempList == null) {
                tempList = new ArrayList<>();
            }
            point = tempResult.matchLastIndex;
            // 区分是否根节点
            if (item.isTreeRoot && tempResult.matches.size() > 0) {
                if (tempResult.matches.size() > 1) {
                    throw new Exception("根节点的数量必须是1");
                }
                if (root == null) {
                    // 递归获取最左节点
                    QLMatchResultTree tempTree = tempResult.matches.get(0);
                    while (tempTree.getLeft() != null && tempTree.getLeft().size() > 0) {
                        tempTree = tempTree.getLeft().get(0);
                    }
                    tempTree.addLeftAll(tempList);
                    tempList.clear();
                } else {
                    tempResult.matches.get(0).addLeft(root);
                }
                root = tempResult.matches.get(0);
            } else if (root != null) {
                root.addRightAll(tempResult.matches);
            } else {
                tempList.addAll(tempResult.matches);
            }
        }

        if (root != null) {
            tempList.add(root);
        }

        QLMatchResult result = new QLMatchResult(tempList, point);

        traceLog(pattern, result, nodes, origPoint, matchCount);

        return result;
    }


    /**
     * 纯打印
     */
    public static void traceLog(QLPatternNode pattern, QLMatchResult result,
                                List<? extends IDataNode> nodes, int point, int matchCount) {
        if (log.isTraceEnabled() && (pattern.matchMode == MatchMode.DETAIL || pattern.matchMode == MatchMode.AND && matchCount > 1 && !"ANONY_PATTERN".equals(pattern.name))) {
            log.trace("匹配--" + pattern.name + "[" + point + ":" + (result.matchLastIndex - 1) + "]:" + pattern);
        }
    }
}


