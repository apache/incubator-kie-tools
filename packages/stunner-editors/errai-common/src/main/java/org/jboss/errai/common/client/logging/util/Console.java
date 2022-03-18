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

package org.jboss.errai.common.client.logging.util;

import jsinterop.annotations.JsType;

/**
 * Provides direct access to the logging methods on the browser window's console.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
@JsType(isNative = true, namespace = "window", name = "console")
public abstract class Console {

  private Console() {}

  public static native void log(String obj);
  public static native void info(String obj);
  public static native void warn(String obj);
  public static native void error(String obj);

}
