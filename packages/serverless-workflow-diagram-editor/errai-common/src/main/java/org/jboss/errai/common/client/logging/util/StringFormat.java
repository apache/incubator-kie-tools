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

package org.jboss.errai.common.client.logging.util;

import java.util.Arrays;
import java.util.Date;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * A utility class for replacing the {@link String#format(String, Object...)} method.
 * 
 * @author Max Barkley <mbarkley@redhat.com>
 */
public class StringFormat {

  private static enum Conversion {
    bool, hexStr, str, uniChar, decInt, octInt, hexInt, sciInt, decNum, compNum, hexNum, date, escLit, line
  }

  private static final String argIndex = "(?:(\\d+)\\$)?";
  private static final String flags = "([-#+ 0,(]*)?";
  private static final String width = "(\\d+)?";
  private static final String prec = "(?:\\.(\\d+))?";
  private static final String dateSuffix = "([HIklMSLNpzZsQBbhAaCYyjmdeRTrDFc])?";
  // Includes date/time suffix
  private static final String conv = "([bBhHsScCdoxXeEfgGaA%n]|(?:[tT]" + dateSuffix + "))";

  private static RegExp convPattern = RegExp.compile("%" + argIndex + flags + width + prec + conv);

  /**
   * This method emulates {@link String#format(String, Object...)} with some
   * notable differences:
   * <ul>
   * <li>flags are unsupported (and are silently ignored)</li>
   * <li>the '{@code a}' and '{@code A}' conversions are unsupported</li>
   * <li>the other scientific notation flags use {@link NumberFormat} and thus
   * produce slightly different output</li>
   * </ul>
   * 
   * @param format
   *          The format string.
   * @param args
   *          The values available for format string conversions.
   * @return A formatted string, similar to the result of calling
   *         {@link String#format(String, Object...)} with {@code format} and
   *         {@code args}.
   * @see String#format(String, Object...)
   */
  public static String format(String format, Object... args) {
    StringBuffer buffer = new StringBuffer(format.length());
    final String originalFormat = format;
    int count = 0;
    
    // Special case: user does StringFormat.format("...", null)
    if (args == null) {
      args = new Object[] {null};
    }
    
    int i = 0;
    while (i < format.length()) {
      if (format.charAt(i) == '%') {
        final String subStr = format.substring(i);
        final MatchResult match = convPattern.exec(subStr);
        if (match == null || match.getIndex() != 0) {
          throw new IllegalArgumentException("Bad conversion at index " + i + " in format String: " + format);
        }
        
        if (match.getGroup(2) != null && !match.getGroup(2).equals("")) {
          throw new UnsupportedOperationException("Flags are not yet supported in this implementation.");
        }
   
        // TODO: check preconditions and possibly throw IllegalFormatException
        final Object arg;
        final int width;
        final int prec;
        final String suffix;
        final Conversion conv = getConversion(match.getGroup(5).charAt(0));
        final boolean autoIndexed = match.getGroup(1) == null || match.getGroup(1).equals("");

        if (conv.equals(Conversion.escLit) || conv.equals(Conversion.line)) {
          arg = null;
        }
        else if (autoIndexed) {
          arg = args[count];
        }
        else {
          arg = args[Integer.valueOf(match.getGroup(1)) - 1];
        }

        if (match.getGroup(3) == null || match.getGroup(3).equals("")) {
          width = 0;
        }
        else {
          width = Integer.valueOf(match.getGroup(3));
        }

        if (match.getGroup(4) == null || match.getGroup(4).equals("")) {
          prec = Integer.MAX_VALUE;
        }
        else {
          prec = Integer.valueOf(match.getGroup(4));
        }

        if (match.getGroup(6) == null || match.getGroup(6).equals("")) {
          suffix = "";
        }
        else {
          suffix = match.getGroup(6);
        }

        final boolean upper = match.getGroup(5).toUpperCase().equals(match.getGroup(5));
        String replacement;
        try {
          replacement = buildReplacement(conv, upper, width, prec, suffix, arg);
        }
        catch (Exception e) {
          throw new IllegalArgumentException("Error processing substitution " + (count + 1) + ".\nFormat: "
                  + originalFormat + "\nArgs: " + Arrays.toString(args), e);
        }
        
        buffer.append(replacement);
        i += match.getGroup(0).length();

        // Auto-index is incremented for non-explicitly indexed conversions
        if (autoIndexed)
          count += 1;
      }
      else {
        buffer.append(format.charAt(i++));
      }
    }

    return buffer.toString();
  }

  private static String buildReplacement(Conversion conv, boolean upper, int width, int prec, String suffix, Object arg) {
    String res = null;
    switch (conv) {
    case bool:
      if (arg instanceof Boolean) {
        if ((Boolean) arg) {
          res = "true";
        }
        else {
          res = "false";
        }
      } else if (arg != null) {
        res = "true";
      } else {
        res = "false";
      }
      break;
    case date:
      if (suffix == null || suffix.length() != 1)
        throw new IllegalArgumentException("Must provide suffix with date conversion.");
      if (arg instanceof Long)
        arg = new Date((Long) arg);
      res = processDate((Date) arg, upper, suffix.charAt(0));
      break;
    case decInt:
      res = String.valueOf((Integer) arg);
      break;
    case decNum:
      if (arg instanceof Float) {
        res = String.valueOf((Float) arg);
      }
      else {
        res = String.valueOf((Double) arg);
      }
      break;
    case hexInt:
      res = Integer.toHexString((Integer) arg);
      break;
    case hexStr:
      if (arg == null)
        res = "null";
      else
        res = Integer.toHexString(arg.hashCode());
      break;
    case octInt:
      res = Integer.toOctalString((Integer) arg);
      break;
    case str:
      if (arg == null)
        res = "null";
      else
        res = arg.toString();
      break;
    case uniChar:
      if (arg instanceof Integer)
        arg = Character.valueOf((char) ((Integer) arg).intValue());
      res = Character.toString((Character) arg);
      break;
    case compNum:
    case sciInt:
      if (arg instanceof Float) {
        arg = Double.valueOf((Float) arg);
      }
      if (Integer.MAX_VALUE == prec)
        prec = 6;
      final StringBuilder formatString = new StringBuilder(prec+5);
      formatString.append("0.");
      for (int i = 0; i < prec; i++)
        formatString.append('0');
      formatString.append("E00");
      
      res = NumberFormat.getFormat(formatString.toString()).format((Double) arg);
      if (!upper)
        res = res.toLowerCase();
      
      return padOrTrunc(res, width, Integer.MAX_VALUE);
      // TODO
    case hexNum:
      throw new UnsupportedOperationException();
    case line:
      return "\n";
    case escLit:
      return "%";
    }
    res = padOrTrunc(res, width, prec);
    if (upper)
      return res.toUpperCase();
    else
      return res;
  }

  @SuppressWarnings("deprecation")
  private static String processDate(Date date, boolean upper, char suffix) {
    String retVal = null;
    switch (suffix) {
    case 'k':
      retVal = String.valueOf(date.getHours());
      break;
    case 'H':
      retVal = String.valueOf(padInt(date.getHours(), 2));
      break;
    case 'l':
      retVal = String.valueOf(date.getHours() % 12);
      break;
   case 'I':
      retVal = String.valueOf(padInt(date.getHours() % 12, 2));
      break;
   case 'M':
      retVal = String.valueOf(padInt(date.getMinutes(), 2));
      break;
    case 'S':
      retVal = String.valueOf(padInt(date.getSeconds(), 2));
      break;
    case 'L':
      retVal = String.valueOf(padInt((int) (date.getTime() % 1000), 3));
      break;
    case 'N':
      retVal = String.valueOf(padInt((int) ((date.getTime() % 1000) * 1000000), 9));
      break;
    case 'p':
      retVal = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().ampms()[date.getHours() / 12];
      break;
    case 's':
      retVal = String.valueOf(date.getTime() / 1000);
      break;
    case 'Q':
      retVal = String.valueOf(date.getTime());
      break;
    case 'C':
      retVal = String.valueOf(padInt((date.getYear() + 1900) / 100, 2));
      break;
    case 'Y':
      retVal = String.valueOf(padInt(date.getYear() + 1900, 4));
      break;
    case 'y':
      retVal = String.valueOf(padInt(date.getYear() % 100, 2));
      break;
    case 'j':
      final Date lastYear = new Date(date.getTime());
      lastYear.setYear(date.getYear() - 1);
      lastYear.setMonth(11);
      lastYear.setDate(31);

      retVal = String.valueOf(padInt(CalendarUtil.getDaysBetween(lastYear, date), 3));
      break;
    case 'z':
      retVal = TimeZone.createTimeZone(date.getTimezoneOffset()).getRFCTimeZoneString(date);
      break;
    case 'm':
      retVal = String.valueOf(padInt(date.getMonth() + 1, 2));
      break;
    case 'd':
      retVal = String.valueOf(padInt(date.getDate(), 2));
      break;
    case 'e':
      retVal = String.valueOf(date.getDate());
      break;
    case 'R':
      retVal = processDate(date, false, 'H') + ":" + processDate(date, false, 'M');
      break;
    case 'T':
      retVal = processDate(date, false, 'R') + ":" + processDate(date, false, 'S');
      break;
    case 'r':
      retVal = processDate(date, false, 'I') + ":" + processDate(date, false, 'M') + ":" + processDate(date, upper, 'S')
              + " " + processDate(date, true, 'p');
      break;
    case 'D':
      retVal = processDate(date, false, 'm') + "/" + processDate(date, false, 'd') + "/" + processDate(date, false, 'y');
      break;
    case 'F':
      retVal = processDate(date, false, 'Y') + "-" + processDate(date, false, 'm') + "-" + processDate(date, false, 'd');
      break;
    case 'Z':
      retVal = TimeZone.createTimeZone(date.getTimezoneOffset()).getShortName(date);
      break;
    case 'B':
      retVal = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().monthsFull()[date.getMonth()];
      break;
    case 'b':
    case 'h':
      retVal = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().monthsShort()[date.getMonth()];
      break;
    case 'A':
      retVal = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().weekdaysFull()[date.getDay()];
      break;
    case 'a':
      retVal = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().weekdaysShort()[date.getDay()];
      break;
    case 'c':
      retVal = processDate(date, false, 'a') + " " + processDate(date, false, 'b') + " " + processDate(date, false, 'd')
              + " " + processDate(date, false, 'T') + " " + processDate(date, false, 'Z') + " "
              + processDate(date, false, 'Y');
      break;
    default:
      throw new IllegalArgumentException("Invalid date suffix: " + suffix);
    }
    
    if (upper)
      return retVal.toUpperCase();
    else
      return retVal;
  }

  private static String padInt(int num, int width) {
    final StringBuilder builder = new StringBuilder(width);
    // num %= (int) Math.pow(10, width);
    for (int d = width - 1; d >= 0; d--) {
      int div = (int) Math.pow(10, d);
      builder.append(num / div);
      num %= div;
    }

    return builder.toString();
  }

  private static String padOrTrunc(String res, int min, int max) {
    if (res.length() < min) {
      final StringBuilder builder = new StringBuilder(min);
      for (int i = 0; i < min - res.length(); i++) {
        builder.append(" ");
      }
      builder.append(res);
      return builder.toString();
    }
    else if (res.length() > max) {
      return res.substring(0, max);
    }
    else {
      return res;
    }
  }

  private static Conversion getConversion(char c) {
    switch (c) {
    case 'b':
    case 'B':
      return Conversion.bool;
    case 'h':
    case 'H':
      return Conversion.hexStr;
    case 's':
    case 'S':
      return Conversion.str;
    case 'c':
    case 'C':
      return Conversion.uniChar;
    case 'd':
      return Conversion.decInt;
    case 'o':
      return Conversion.octInt;
    case 'x':
    case 'X':
      return Conversion.hexInt;
    case 'e':
    case 'E':
      return Conversion.sciInt;
    case 'f':
      return Conversion.decNum;
    case 'g':
    case 'G':
      return Conversion.compNum;
    case 'a':
    case 'A':
      return Conversion.hexNum;
    case 'T':
    case 't':
      return Conversion.date;
    case '%':
      return Conversion.escLit;
    case 'n':
      return Conversion.line;
    default:
      throw new IllegalArgumentException(c + " is not a valid conversion character.");
    }
  }

}
