/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ui.test.common.client.dom;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * @author Max Barkley <mbarkley@redhat.com>
 */
@JsType(isNative = true, name = "HTMLDocument", namespace = JsPackage.GLOBAL)
public abstract class Document {

  @JsProperty(namespace = JsPackage.GLOBAL)
  public static native Document getDocument();

  private Document() {}

  public native Element createElement(String tagName);

  @JsOverlay
  public final TextInputElement createTextInputElement() {
    return (TextInputElement) createElement("input");
  }

  @JsOverlay
  public final ButtonElement createButtonElement() {
    return (ButtonElement) createElement("button");
  }

}
