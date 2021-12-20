package com.ait.lienzo.client.core.shape.wires.handlers;

public final class MouseEvent {

    private final int x;
    private final int y;
    private final boolean isShiftKeyDown;
    private final boolean isAltKeyDown;
    private final boolean isCtrlKeyDown;

    public MouseEvent(int x,
                      int y,
                      boolean isShiftKeyDown,
                      boolean isAltKeyDown,
                      boolean isCtrlKeyDown) {
        this.x = x;
        this.y = y;
        this.isShiftKeyDown = isShiftKeyDown;
        this.isAltKeyDown = isAltKeyDown;
        this.isCtrlKeyDown = isCtrlKeyDown;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isShiftKeyDown() {
        return isShiftKeyDown;
    }

    public boolean isAltKeyDown() {
        return isAltKeyDown;
    }

    public boolean isCtrlKeyDown() {
        return isCtrlKeyDown;
    }
}
