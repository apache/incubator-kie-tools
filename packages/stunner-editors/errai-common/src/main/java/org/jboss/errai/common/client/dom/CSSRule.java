/**
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

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 *
 * @deprecated Use Elemental 2 for new development
 *
 * @author Max Barkley <mbarkley@redhat.com>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/CSSRule">Web API</a>
 */
@JsType(isNative = true)
@Deprecated
public interface CSSRule {
  @JsOverlay static short UNKNOWN_RULE = 0;
  @JsOverlay static short STYLE_RULE = 1;
  @JsOverlay static short CHARSET_RULE = 2;
  @JsOverlay static short IMPORT_RULE = 3;
  @JsOverlay static short MEDIA_RULE = 4;
  @JsOverlay static short FONT_FACE_RULE = 5;
  @JsOverlay static short PAGE_RULE = 6;

  @JsProperty short getType();
  @JsProperty String getCssText();
  @JsProperty void setCssText(String cssText);

  @JsProperty CSSStyleSheet getParentStyleSheet();
  @JsProperty CSSRule getParentRule();
}
