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
 * @see <a href="https://www.w3.org/TR/DOM-Level-2-Style/css.html#CSS-CSSValue">Web API</a>
 */
@JsType(isNative = true)
@Deprecated
public interface CSSValue {
  @JsOverlay static short CSS_INHERIT = 0;
  @JsOverlay static short CSS_PRIMITIVE_VALUE = 1;
  @JsOverlay static short CSS_VALUE_LIST = 2;
  @JsOverlay static short CSS_CUSTOM = 3;

  @JsProperty String getCssText();
  @JsProperty void setCssText(String cssText);

  @JsProperty short getCssValueType();
}
