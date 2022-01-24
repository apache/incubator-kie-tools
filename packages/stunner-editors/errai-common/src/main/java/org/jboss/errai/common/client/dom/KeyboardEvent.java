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
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent">Web API</a>
 */
@BrowserEvent({
  "keydown",
  "keypress",
  "keyup"
})
@JsType(isNative = true)
@Deprecated
public interface KeyboardEvent extends UIEvent {

  @JsOverlay static final int DOM_KEY_LOCATION_STANDARD = 0;
  @JsOverlay static final int DOM_KEY_LOCATION_LEFT = 1;
  @JsOverlay static final int DOM_KEY_LOCATION_RIGHT = 2;
  @JsOverlay static final int DOM_KEY_LOCATION_NUMPAD = 3;
  @JsOverlay static final int DOM_KEY_LOCATION_MOBILE = 4;
  @JsOverlay static final int DOM_KEY_LOCATION_JOYSTICK = 5;

  @JsProperty(name = "altKey") boolean isAltKey();
  @JsProperty String getCode();
  @JsProperty(name = "ctrlKey") boolean isCtrlKey();
  @JsProperty(name = "isComposing") boolean isComposing();
  @JsProperty String getKey();
  @JsProperty int getKeyCode();
  @JsProperty String getKeyIdentifier();
  @JsProperty String getLocale();
  @JsProperty int getLocation();
  @JsProperty(name = "metaKey") boolean isMetaKey();
  @JsProperty(name = "repeat") boolean isRepeat();
  @JsProperty(name = "shiftKey") boolean isShiftKey();

  boolean getModifierState();

}
