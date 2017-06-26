package com.ait.lienzo.client.widget;

public enum DragMouseControl {
    LEFT_MOUSE_ONLY (true,false,false),
    MIDDLE_MOUSE_ONLY (false,true,false),
    RIGHT_MOUSE_ONLY (false,false,true),
    LEFT_AND_RIGHT_MOUSE (true,false,true),
    LEFT_AND_MIDDLE_MOUSE (true,true,false),
    RIGHT_AND_MIDDLE_MOUSE (false,true,true),
    ANY_MOUSE_BUTTON (true,true,true);

    public final boolean allowLeft;
    public final boolean allowMiddle;
    public final boolean allowRight;

    DragMouseControl(boolean left, boolean middle, boolean right){
        allowLeft = left;
        allowMiddle = middle;
        allowRight = right;
    }

    public boolean allowDrag(boolean isLeft, boolean isMiddle, boolean isRight) {
        return !((isLeft && !allowLeft) || (isMiddle && !allowMiddle) || (isRight && !allowRight));
    }
}
