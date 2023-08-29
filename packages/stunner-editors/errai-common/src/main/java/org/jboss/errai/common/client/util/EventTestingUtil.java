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


package org.jboss.errai.common.client.util;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.HasValue;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;

/**
 * Contains utility methods for intercepting calls to add and remove web browser event listeners necessary for tests
 * with certain versions of HTMLUnit.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public abstract class EventTestingUtil {

  private EventTestingUtil() {}

  /**
   * This is a really disgusting workaround for the inability to
   * dispatch native browser events in the version of HtmlUnit currently
   * bundled in gwt-dev.
   *
   * What does this do?
   * This replaces "addEventListener" and "removeEventListener"
   * in the HTMLElement prototype with functions that intercept
   * and store registered listeners.
   *
   * Why does it do it?
   * So that subsequent calls to "invokeEventListeners" can
   * manually call any functions added with "addEventListener".
   *
   * In short because we cannot dispatch browser events, to test
   * binding of native elements we must store and then manually invoke
   * all event listeners.
   */
  public static native void setupAddEventListenerInterceptor() /*-{
    console.log("Setting up event listener interceptors.");
    function ListenerMap() {
      var map = new Map();

      this.add = function(element, type, listener) {
        var curList = this.get(element, type);
        console.debug("Adding listener for " + type + " event in " + element + ". Total of " + (curList.length + 1) + " listeners.");
        curList.push(listener);
      };

      this.remove = function(element, type, listener) {
        var listeners = this.get(element, type);
        var index = listeners.indexOf(listener);
        if (index > -1) {
          listeners.splice(index, 1);
        }
      };

      this.get = function(element, type) {
        if (map.get(element) === undefined) {
          map.set(element, new Map());
        }
        if (map.get(element).get(type) === undefined) {
          map.get(element).set(type, []);
        }
        return map.get(element).get(type);
      };
    };
    if ($wnd.HTMLElement.prototype._addEventListener === undefined) {
      listeners = new ListenerMap();
      $wnd.HTMLElement.prototype._addEventListener = $wnd.HTMLElement.prototype.addEventListener;
      $wnd.HTMLElement.prototype._removeEventListener = $wnd.HTMLElement.prototype.removeEventListener;
      console.log("Replacing addEventListener.");
      $wnd.HTMLElement.prototype.addEventListener = function(type, listener, capture) {
        console.debug("Intercepted addEventListener(" + this + ", " + type + ", " + capture + ")");
        listeners.add(this, type, listener);
        this._addEventListener(type, listener, capture);
      };
      console.log("Replacing removeEventListener.");
      $wnd.HTMLElement.prototype.removeEventListener = function(type, listener, capture) {
        console.debug("Intercepted removeEventListener for " + this + " with type " + type);
        listeners.remove(this, type, listener);
        this._removeEventListener(type, listener, capture);
      };
    }
  }-*/;

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static void invokeEventListeners(final HTMLElement element, final String eventType) {
    invokeEventListeners((Object) element, eventType);
    if ("change".equals(eventType)) {
      final ElementWrapperWidget elem = ElementWrapperWidget.getWidget(element);
      if (elem instanceof HasValue) {
        ValueChangeEvent.fire(((HasValue) elem), ((HasValue) elem).getValue());
      }
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static void invokeEventListeners(final Element element, final String eventType) {
    invokeEventListeners((Object) element, eventType);
    if ("change".equals(eventType)) {
      final ElementWrapperWidget elem = ElementWrapperWidget.getWidget(element);
      if (elem instanceof HasValue) {
        ValueChangeEvent.fire(((HasValue) elem), ((HasValue) elem).getValue());
      }
    }
  }

  public static void invokeEventListeners(final Object element, final String eventType) {
    final NativeEvent event = Document.get().createHtmlEvent(eventType, true, true);
    invokeEventListeners(element, eventType, event);
  }

  public static native void invokeEventListeners(Object element, String type, Object evt) /*-{
    var foundListeners = listeners.get(element, type);
    console.debug("Found " + foundListeners.length + " for " + type + " event on element " + element);
    foundListeners.forEach(function(l) { l(evt); });
  }-*/;

}
