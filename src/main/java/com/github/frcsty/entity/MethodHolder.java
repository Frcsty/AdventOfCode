package com.github.frcsty.entity;

import java.lang.reflect.Method;

public class MethodHolder {
    private final int part;
    private final Method method;

    public  MethodHolder(int part, Method method) {
        this.part = part;
        this.method = method;
    }

    public int getPart() {
        return this.part;
    }

    public Method getMethod() {
        return this.method;
    }
}