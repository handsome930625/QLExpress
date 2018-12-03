package com.ql.util.express.parse;

import java.io.Serializable;

/**
 * 输出给其它指令共享使用的对象
 *
 * @author xuannan
 */
public class ExportItem implements Serializable {
    private static final long serialVersionUID = 5440012774123494760L;
    public static String TYPE_ALIAS = "alias";
    public static String TYPE_DEF = "def";
    public static String TYPE_FUNCTION = "function";
    public static String TYPE_MACRO = "macro";
    String globeName;
    String name;
    /**
     * def,alias
     */
    String type;
    /**
     * 类名或者别名
     */
    String desc;

    public ExportItem(String aName, String aType, String aDesc) {
        this.globeName = aName;
        this.name = aName;
        this.type = aType;
        this.desc = aDesc;
    }

    public ExportItem(String aGlobeName, String aName, String aType, String aDesc) {
        this.globeName = aGlobeName;
        this.name = aName;
        this.type = aType;
        this.desc = aDesc;
    }

    @Override
    public String toString() {
        return this.globeName + "[" + this.type + ":" + this.name + " " + this.desc + "]";
    }

    public String getGlobeName() {
        return globeName;
    }

    public void setGlobeName(String globeName) {
        this.globeName = globeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
