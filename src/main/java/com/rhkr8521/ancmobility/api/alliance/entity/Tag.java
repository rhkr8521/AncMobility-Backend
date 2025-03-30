package com.rhkr8521.ancmobility.api.alliance.entity;

public enum Tag {
    DREAM_T("꿈T"),
    NEMO("네모"),
    BLUE("블루"),
    VENTI("벤티"),
    BLACK("블랙");

    private final String value;

    Tag(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
