package com.ait.lienzo.tools.client.event;

public enum EventType
{

    CLICKED("click", 1),
    DOUBLE_CLICKED("dblclick", 2),


    MOUSE_UP("mouseup", 3),
    MOUSE_DOWN("mousedown", 4),

    MOUSE_MOVE("mousemove", 5),



    MOUSE_OUT("mouseout", 6),
    MOUSE_OVER("mouseover", 7),

    MOUSE_WHEEL("mousewheel", 8),


    TOUCH_START("touchstart", 9),
    TOUCH_END("touchend", 10),
    TOUCH_CANCEL("touchcancel", 11),
    TOUCH_MOVE("touchmove", 12);


//    GESTURE_START("gesturestart", 11),
//    GESTURE_UPDATE("gestureupdate", 11),
//    GESTURE_END("gestureend", 11),


//    XXXX("mousewheel", 20);

    private String  type;
    private int code;

    EventType(final String type, final int code)
    {
        this.type = type;
        this.code = code;
    }

    public String getType()
    {
        return this.type;
    }

    public int getCode()
    {
        return code;
    }
}
