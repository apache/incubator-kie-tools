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

package org.jboss.errai.common.client.logging.handlers;

import java.util.logging.Formatter;
import java.util.logging.Level;

import org.jboss.errai.common.client.logging.formatters.ErraiSimpleFormatter;

import com.google.gwt.logging.client.SystemLogHandler;

/**
 * An extension of {@link SystemLogHandler} that uses a given {@link Formatter}.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class ErraiSystemLogHandler extends SystemLogHandler implements ErraiLogHandler {
  
  /*
   * Workaround to so that superlcass does not overwrite log level
   */
  private boolean init = false;
  
  private ErraiSystemLogHandler(final Formatter formatter) {
    setFormatter(formatter);
    init = true;
  }
  
  public ErraiSystemLogHandler() {
    this(new ErraiSimpleFormatter());
  }

  @Override
  public boolean isEnabled() {
    return !getLevel().equals(Level.OFF);
  }
  
  @Override
  public void setLevel(Level newLevel) {
    if (init)
      staticSetLevel(newLevel.getName());
  }
  
  @Override
  public Level getLevel() {
    return Level.parse(staticGetLevel());
  }
  
  public static native void staticSetLevel(String newLevel) /*-{
    $wnd.erraiSystemLogHandlerLevel = newLevel;
  }-*/;
  
  public static native String staticGetLevel() /*-{
    if ($wnd.erraiSystemLogHandlerLevel === undefined) {
      return "ALL";
    } else {
      return $wnd.erraiSystemLogHandlerLevel;
    }
  }-*/;

}
