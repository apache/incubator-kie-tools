/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package com.ait.lienzo.tools.client.event;

public enum EventType {

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

    private String type;
    private int code;

    EventType(final String type, final int code) {
        this.type = type;
        this.code = code;
    }

    public String getType() {
        return this.type;
    }

    public int getCode() {
        return code;
    }
}
