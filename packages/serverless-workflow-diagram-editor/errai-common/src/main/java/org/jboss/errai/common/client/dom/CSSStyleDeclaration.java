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

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 *
 * @deprecated Use Elemental 2 for new development
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@JsType(isNative = true)
@Deprecated
public interface CSSStyleDeclaration {
  @JsProperty String getCssText();
  @JsProperty void setCssText(String cssText);

  String getPropertyValue(String propertyName);
  CSSValue getPropertyCSSValue(String propertyName);
  String removeProperty(String propertyName);
  String getPropertyPriority(String propertyName);
  void setProperty(String propertyName, String value, String priority);
  void setProperty(String propertyName, String value);

  @JsProperty int getLength();
  String item(int index);
  @JsProperty CSSRule getParentRule();
}
