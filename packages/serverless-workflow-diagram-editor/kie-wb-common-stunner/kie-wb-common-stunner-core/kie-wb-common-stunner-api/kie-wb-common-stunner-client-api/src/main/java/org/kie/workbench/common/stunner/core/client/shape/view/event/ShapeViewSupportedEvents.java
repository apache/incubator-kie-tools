/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.shape.view.event;

/**
 * Different view event type instances used along different shape views.
 */
public class ShapeViewSupportedEvents {

    public static final ViewEventType[] ALL_EVENT_TYPES = new ViewEventType[]{
            ViewEventType.MOUSE_CLICK,
            ViewEventType.MOUSE_DBL_CLICK,
            ViewEventType.MOUSE_ENTER,
            ViewEventType.MOUSE_EXIT,
            ViewEventType.TEXT_ENTER,
            ViewEventType.TEXT_EXIT,
            ViewEventType.TEXT_CLICK,
            ViewEventType.TEXT_DBL_CLICK,
            ViewEventType.DRAG,
            ViewEventType.RESIZE
    };

    public static final ViewEventType[] ALL_DESKTOP_EVENT_TYPES = new ViewEventType[]{
            ViewEventType.MOUSE_CLICK,
            ViewEventType.MOUSE_DBL_CLICK,
            ViewEventType.MOUSE_ENTER,
            ViewEventType.MOUSE_EXIT,
            ViewEventType.TEXT_ENTER,
            ViewEventType.TEXT_EXIT,
            ViewEventType.TEXT_CLICK,
            ViewEventType.TEXT_DBL_CLICK,
            ViewEventType.DRAG,
            ViewEventType.RESIZE
    };

    public static final ViewEventType[] DESKTOP_NO_RESIZE_EVENT_TYPES = new ViewEventType[]{
            ViewEventType.MOUSE_CLICK,
            ViewEventType.MOUSE_DBL_CLICK,
            ViewEventType.MOUSE_ENTER,
            ViewEventType.MOUSE_EXIT,
            ViewEventType.TEXT_ENTER,
            ViewEventType.TEXT_EXIT,
            ViewEventType.TEXT_CLICK,
            ViewEventType.TEXT_DBL_CLICK,
            ViewEventType.DRAG
    };

    public static final ViewEventType[] CONNECTOR_EVENT_TYPES = new ViewEventType[]{
            ViewEventType.MOUSE_CLICK,
            ViewEventType.MOUSE_DBL_CLICK
    };

    public static final ViewEventType[] DESKTOP_CONNECTOR_EVENT_TYPES = new ViewEventType[]{
            ViewEventType.MOUSE_CLICK,
            ViewEventType.MOUSE_DBL_CLICK
    };
}
