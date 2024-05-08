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

package org.jboss.errai.common.client.logging.util;

import com.google.gwt.core.client.ScriptInjector;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;

/**
 * A utility class for accessing arbitrary properties of native Javascript types.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@JsType(isNative = true, namespace = "org.jboss.errai")
public class ReflectableJSO {

  private ReflectableJSO(final Object wrapped) {}

  @JsOverlay
  public static final ReflectableJSO create(final Object wrapped) {
    ScriptInjector
            .fromString(
                    "org = {"
                    + "'jboss' : {"
                      + "'errai' : {"
                        + "'ReflectableJSO' : function(wrapped) {"
                          + "this.get = function(name) {"
                            + "return wrapped[name];"
                          + "};"
                        + "this.set = function(name, value) {"
                          + "wrapped[name] = value;"
                        + "};"
                        + "this.properties = function() {"
                          + "var retVal = [];"
                          + "for (key in wrapped) {"
                            + "retVal.push(key);"
                          + "}"
                          + "return retVal;"
                        + "};"
                        + "this.unwrap = function() {"
                          + "return wrapped;"
                        + "}"
                      + "}"
                    + "}"
                  + "}"
                + "};")
            .setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(true).inject();
    return new ReflectableJSO(wrapped);
  }

  @JsOverlay
  public final boolean hasProperty(final String name) {
    return get(name) != null;
  }

  public final native Object get(final String name);

  public final native void set(final String name, final Object value);

  public final native String[] properties();

  public final native Object unwrap();
}