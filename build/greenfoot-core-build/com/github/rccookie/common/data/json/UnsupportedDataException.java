package com.github.rccookie.common.data.json;

class UnsupportedDataException extends RuntimeException {

    private static final long serialVersionUID = -7876646723174010238L;
    
    public UnsupportedDataException(Object data) {
        super(data.getClass().getName());
    }
}
