/*
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

package org.jboss.errai.ioc.client;

/**
 *
 * @author Max Barkley <mbarkley@redhat.com>
 * @see WindowInjectionContext
 */
public class WindowInjectionContextStorage {

  public static final WindowInjectionContext createOrGet() {
    if (!isWindowInjectionContextDefined()) {
      setWindowInjectionContext(new WindowInjectionContextImpl());
    }
    return getWindowInjectionContext();
  }

  public static final void reset() {
    setWindowInjectionContext(null);
  }

  public static final boolean isWindowInjectionContextDefined() {
    return getWindowInjectionContext() != null;
  }

  public static native WindowInjectionContext getWindowInjectionContext()/*-{
    return $wnd.windowInjectionContext || null;
  }-*/;

  public static native void setWindowInjectionContext(WindowInjectionContext ic)/*-{
    $wnd.windowInjectionContext = ic;
  }-*/;

}
