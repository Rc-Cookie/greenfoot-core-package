package com.github.rccookie.common.data.json;

import java.util.LinkedHashMap;

public class JsonMap extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = -4110122669944597892L;
    
    JsonMap() {
        super();
    }

    @Override
    public Object get(Object varName) {
        return super.get(varName);
    }

    public long getLong(String varName) {
        return (long)get(varName);
    }

    public int getInt(String varName) {
        return Long.valueOf(getLong(varName)).intValue();
    }

    public Short getShort(String varName) {
        return Long.valueOf(getLong(varName)).shortValue();
    }

    public double getDouble(String varName) {
        return (double)get(varName);
    }

    public float getFloat(String varName) {
        return Double.valueOf(getDouble(varName)).floatValue();
    }

    public String getString(String varName) {
        return (String)get(varName);
    }

    public boolean getBoolean(String varName) {
        return (boolean)get(varName);
    }
}
