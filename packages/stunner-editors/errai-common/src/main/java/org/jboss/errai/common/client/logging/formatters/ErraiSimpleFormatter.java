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

package org.jboss.errai.common.client.logging.formatters;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import org.jboss.errai.common.client.logging.util.StringFormat;

/**
 * Emulates the behaviour of {@link SimpleFormatter}, but the format string is
 * stored as a JSNI value.
 * 
 * @see ErraiSimpleFormatter#setSimpleFormatString(String)
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class ErraiSimpleFormatter extends Formatter {

  public static final String defaultFormat = "%1$tH:%1$tM:%1$tS %4$s [%3$s] %5$s";

  private Date date;
  private String customFormat;

  public ErraiSimpleFormatter() {
    date = new Date();
  }
  
  public ErraiSimpleFormatter(String customFormat) {
    this();
    this.customFormat = customFormat;
  }
  

  @Override
  public synchronized String format(LogRecord record) {
    date.setTime(record.getMillis());
    // Get simple class name of logger
    String name = record.getLoggerName().substring(Math.max(record.getLoggerName().lastIndexOf(".") + 1, 0));
    Object thrown = (record.getThrown() != null) ? record.getThrown() : "";

    return StringFormat.format(getSimpleFormatString(customFormat), date, record.getLoggerName(), name, record
            .getLevel().getName(), record.getMessage(), thrown);
  }
  
  /**
   * Set the value for the format string used by all
   * {@link ErraiSimpleFormatter} instances. The provided format string is
   * called with {@link StringFormat} like so:
   * 
   * <pre>
   *    {@code StringFormat.format(formatString, date, fullLoggerName, simpleLoggerName, level, throwable)}
   * </pre>
   * 
   * The parameters are as follows:
   * <ul>
   * <li>{@code formatString}: the format string (see
   * {@link StringFormat#format(String, Object...)} for details).</li>
   * <li>{@code date}: the log date, either a {@link Long} or {@code Date}.</li>
   * <li>{@code fullLoggerName}: the full name of the logger.</li>
   * <li>{@code simpleLoggerName}: a substring of the full logger name, starting
   * after the last '.', or the full name if no '.' occurs (i.e. "a.b.c" -->
   * "c", "abc" --> "abc").</li>
   * <li>{@code level}: the level of the this log entry (i.e. TRACE, DEBUG,
   * INFO, WARN, ERROR).</li>
   * <li>{@code throwable}: the {@code throwable} accompanying this log entry,
   * or {@code null} if none provided.</li>
   * </ul>
   * The call to {@link StringFormat#format(String, Object...)} is designed to
   * mimic the call to {@link String#format(String, Object...)} in
   * {@link SimpleFormatter}, however there are some differences. Please see
   * {@link StringFormat#format(String, Object...)} for details.
   * 
   * @param format
   *          The format string for used by all {@link ErraiSimpleFormatter} instances.
   */
  public static native void setSimpleFormatString(String format) /*-{
    if (format == null) {
      $wnd.erraiSimpleFormatString = null;
    }
    else {
      $wnd.erraiSimpleFormatString = format;
    }
  }-*/;

  /**
   * @return The global format string.
   * @see ErraiSimpleFormatter#setSimpleFormatString(String)
   */
  public static native String getSimpleFormatString() /*-{
    if ($wnd.erraiSimpleFormatString === null || $wnd.erraiSimpleFormatString === undefined
      || $wnd.erraiSimpleFormatString.length === 0) {
      return ""; 
    } 
    else {
      return $wnd.erraiSimpleFormatString;
    }
  }-*/;

  /**
   * @param customFormat
   *          the custom format provided when creating this formatter (see
   *          {@link ErraiSimpleFormatter#ErraiSimpleFormatter(String)})
   * @return The custom format string, if provided, otherwise the global format
   *         string, or default format if no global format has been set.
   */
  public String getSimpleFormatString(String customFormat) {
    if (customFormat != null) {
      return customFormat;
    }

    String retVal = getSimpleFormatString();
    return (!retVal.equals("")) ? retVal : defaultFormat;
  }

}
