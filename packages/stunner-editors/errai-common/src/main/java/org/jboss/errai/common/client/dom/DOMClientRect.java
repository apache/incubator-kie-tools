/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except compliance with the License.
 * You may obtaa copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.errai.common.client.dom;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 *
 * @deprecated Use Elemental 2 for new development
 *
 * @author Eder Ignatowicz <ederign@redhat.com>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Mozilla/Tech/XPCOM/Reference/Interface/nsIDOMClientRect">Web API</a>
 */
@JsType( isNative = true )
@Deprecated
public interface DOMClientRect {

  @JsProperty Double getBottom();

  @JsProperty Double getHeight();

  @JsProperty Double getLeft();

  @JsProperty Double getRight();

  @JsProperty Double getTop();

  @JsProperty Double getWidth();

  //This is undefined in Google Chrome. That is the reason to
  //customizes the name of the member in generated JavaScript
  @JsProperty(name = "left") Double getX();

  //This is undefined in Google Chrome. That is the reason to
  //customizes the name of the member in generated JavaScript
  @JsProperty(name = "top") Double getY();

}
