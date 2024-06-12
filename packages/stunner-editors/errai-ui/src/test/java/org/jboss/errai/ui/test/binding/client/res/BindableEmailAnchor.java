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

package org.jboss.errai.ui.test.binding.client.res;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;
import org.jboss.errai.common.client.api.annotations.Element;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.ui.HasValue;

@Element("a")
@JsType(isNative = true)
public interface BindableEmailAnchor extends Anchor, HasValue<String> {

  @JsOverlay @Override
  default String getValue() {
    return getTextContent();
  }

  @JsOverlay @Override
  default void setValue(final String value) {
    setTextContent(value);
    setHref("mailto:" + value);
  }
}
