/*
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.common.client.dom;

import org.jboss.errai.common.client.api.annotations.BrowserEvent;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 *
 * @deprecated Use Elemental 2 for new development
 *
 * @author Max Barkley <mbarkley@redhat.com>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/MouseEvent">Web API</a>
 */
@BrowserEvent({
  "click",
  "contextmenu",
  "dblclick",
  "mousedown",
  "mouseenter",
  "mouseleave",
  "mousemove",
  "mouseout",
  "mouseover",
  "mouseup",
  "show"
})
@JsType(isNative = true)
@Deprecated
public interface MouseEvent extends UIEvent {

  @JsOverlay static final int BUTTON_LEFT = 0;
  @JsOverlay static final int BUTTON_MIDDLE = 1;
  @JsOverlay static final int BUTTON_RIGHT = 2;
  @JsOverlay static final int BUTTON_BACK = 3;
  @JsOverlay static final int BUTTON_FORWARD = 4;

  @JsOverlay static final int BUTTONS_NONE = 0;
  @JsOverlay static final int BUTTONS_LEFT = 1;
  @JsOverlay static final int BUTTONS_MIDDLE = 2;
  @JsOverlay static final int BUTTONS_RIGHT = 4;
  @JsOverlay static final int BUTTONS_BACK = 8;
  @JsOverlay static final int BUTTONS_FORWARD = 16;


  @JsProperty(name = "altKey") boolean isAltKey();
  @JsProperty(name = "ctrlKey") boolean isCtrlKey();
  @JsProperty(name = "metaKey") boolean isMetaKey();
  @JsProperty(name = "shiftKey") boolean isShiftKey();
  @JsProperty int getButton();
  @JsProperty int getButtons();
  @JsProperty int getClientX();
  @JsProperty int getClientY();
  @JsProperty int getMovementX();
  @JsProperty int getMovementY();
  @JsProperty int getOffsetX();
  @JsProperty int getOffsetY();
  @JsProperty int getPageX();
  @JsProperty int getPageY();
  @JsProperty int getScreenX();
  @JsProperty int getScreenY();
  @JsProperty String getRegion();
  @JsProperty EventTarget getRelatedTarget();

  boolean getModifierState();
}
