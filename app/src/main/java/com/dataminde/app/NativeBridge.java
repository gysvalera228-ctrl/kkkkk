package com.dataminde.app;

public final class NativeBridge {
    static {
        System.loadLibrary("dataminde");
    }

    private NativeBridge() {}

    public static native String engineInfo();
    public static native float distance(float x1, float y1, float x2, float y2);
}
