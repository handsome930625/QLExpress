package com.ql.util.express.match;

public class NodeTypeManagerTestImpl implements INodeTypeManager {

    @Override
    public INodeType findNodeType(String name) {
        return new TestNodeTypeImpl(name);
    }

}

class TestNodeTypeImpl implements INodeType {
    String name;

    public TestNodeTypeImpl(String aName) {
        this.name = aName;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public INodeTypeManager getManager() {
        throw new RuntimeException("没有实现的方法");
    }

    @Override
    public QLPatternNode getPatternNode() {
        throw new RuntimeException("没有实现的方法");
    }
}
