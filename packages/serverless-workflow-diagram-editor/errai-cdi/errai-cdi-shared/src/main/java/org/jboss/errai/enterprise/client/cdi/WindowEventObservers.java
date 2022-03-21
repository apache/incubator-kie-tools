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

package org.jboss.errai.enterprise.client.cdi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsinterop.annotations.JsType;

@JsType
public class WindowEventObservers {
  private final Map<String,  List<JsTypeEventObserver<?>>> observers = new HashMap<String, List<JsTypeEventObserver<?>>>();

  public static WindowEventObservers createOrGet() {
    if (!windowEventObserversDefined()) {
      setWindowEventObservers(new WindowEventObservers());
    }
    return getWindowEventObservers();
  }

  public void add(final String eventType, final JsTypeEventObserver<?> observer) {
    if (!observers.containsKey(eventType)) {
      observers.put(eventType, new ArrayList<JsTypeEventObserver<?>>());
    }
    observers.get(eventType).add(observer);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void fireEvent(final String eventType, final Object evt) {
    for (final JsTypeEventObserver observer : get(eventType)) {
      observer.onEvent(evt);
    }
  }

  public JsTypeEventObserver<?>[] get(final String eventType) {
    if (!observers.containsKey(eventType)) {
      return new JsTypeEventObserver<?>[0];
    }
    return observers.get(eventType).toArray(new JsTypeEventObserver[0]);
  }

  private static native WindowEventObservers getWindowEventObservers() /*-{
    return $wnd.eventObservers;
  }-*/;

  private static native void setWindowEventObservers(WindowEventObservers eo) /*-{
    $wnd.eventObservers = eo;
  }-*/;

  private static native boolean windowEventObserversDefined() /*-{
    return !($wnd.eventObservers === undefined);
  }-*/;

}
