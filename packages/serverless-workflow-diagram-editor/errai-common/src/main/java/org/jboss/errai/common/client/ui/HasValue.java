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

package org.jboss.errai.common.client.ui;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

/**
 * Like {@link com.google.gwt.user.client.ui.HasValue} but for native {@link JsType JsTypes}.
 *
 * When a native type implementing {@link HasValue} is bound with Errai Data-Binding, the {@link #getValue()} and
 * {@link #setValue(Object)} will be used. (This is only supported in declarative data-binding because of limitations
 * with native JS types.)
 *
 * It is possible to create a native {@link JsType} where {@link #getValue()} and {@link #setValue(Object)} are
 * implemented as {@link JsOverlay} methods, providing an easy way to define value accessors for native types with Errai
 * Data-Binding.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@JsType(isNative = true)
public interface HasValue<T> {

  T getValue();

  void setValue(T value);

}
