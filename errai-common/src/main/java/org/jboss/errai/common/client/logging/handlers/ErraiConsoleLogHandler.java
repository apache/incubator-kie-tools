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

import static org.jboss.errai.common.client.logging.util.StackTraceFormatter.getStackTraces;

import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.jboss.errai.common.client.logging.formatters.ErraiSimpleFormatter;
import org.jboss.errai.common.client.logging.util.Console;

import com.google.gwt.logging.client.ConsoleLogHandler;

/**
 * An extension of {@link ConsoleLogHandler} that uses a given {@link Formatter} and prints stack traces that Google
 * Chrome can translate with source maps.
 *
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class ErraiConsoleLogHandler extends ConsoleLogHandler implements ErraiLogHandler {

  @FunctionalInterface
  private static interface LevelLogger {

    void log(String msg);

  }

  /*
   * Workaround to so that superlcass does not overwrite log level
   */
  private boolean init = false;

  public ErraiConsoleLogHandler(final Formatter formatter) {
    setFormatter(formatter);
    init = true;
  }

  public ErraiConsoleLogHandler() {
    this(new ErraiSimpleFormatter());
  }

  @Override
  public void publish(final LogRecord record) {
    if (!isLoggable(record)) {
      return;
    }
    String msg = getFormatter().format(record);
    int val = record.getLevel().intValue();
    if (val >= Level.SEVERE.intValue()) {
      error(msg, record);
    } else if (val >= Level.WARNING.intValue()) {
      warn(msg, record);
    } else if (val >= Level.INFO.intValue()) {
      info(msg, record);
    } else {
      log(msg, record);
    }
  }

  private void log(String msg, LogRecord record) {
    logWith(msg, record, m -> Console.log(m));
  }

  private void info(String msg, LogRecord record) {
    logWith(msg, record, m -> Console.info(m));
  }

  private void warn(String msg, LogRecord record) {
    logWith(msg, record, m -> Console.warn(m));
  }

  private void error(final String msg, final LogRecord record) {
    logWith(msg, record, m -> Console.error(m));
  }

  private void logWith(final String msg, final LogRecord record, final LevelLogger logger) {
    logger.log(msg);
    maybeLogException(record, logger);
  }

  private void maybeLogException(final LogRecord record, final LevelLogger logger) {
    if (record.getThrown() != null) {
      final List<String> stackTraces = getStackTraces(record.getThrown());
      for (final String stackTrace : stackTraces) {
        logger.log(stackTrace);
      }
    }
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
    $wnd.erraiConsoleLogHandlerLevel = newLevel;
  }-*/;

  public static native String staticGetLevel() /*-{
    if ($wnd.erraiConsoleLogHandlerLevel === undefined) {
      return "ALL";
    } else {
      return $wnd.erraiConsoleLogHandlerLevel;
    }
  }-*/;
}
