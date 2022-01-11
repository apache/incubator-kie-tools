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

package org.jboss.errai.common.client.dom;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

/**
 * @deprecated Use Elemental 2 for new development
 *
 * @author Max Barkley <mbarkley@redhat.com>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date">Web API</a>
 */
@JsType(isNative = true)
@Deprecated
public class Date {

  public Date() {}
  public Date(final int value) {}
  public Date(final String date) {}
  public Date(
          final int year,
          final int month) {}
  public Date(
          final int year,
          final int month,
          final int day) {}
  public Date(
          final int year,
          final int month,
          final int day,
          final int minutes) {}
  public Date(
          final int year,
          final int month,
          final int day,
          final int minutes,
          final int seconds) {}
  public Date(
          final int year,
          final int month,
          final int day,
          final int minutes,
          final int seconds,
          final int milliseconds) {}

  @JsMethod(name = "UTC")
  public static native int utc(
          int year,
          int month);
  @JsMethod(name = "UTC")
  public static native int utc(
          int year,
          int month,
          int day);
  @JsMethod(name = "UTC")
  public static native int utc(
          int year,
          int month,
          int day,
          int hour);
  @JsMethod(name = "UTC")
  public static native int utc(
          int year,
          int month,
          int day,
          int hour,
          int minute);
  @JsMethod(name = "UTC")
  public static native int utc(
          int year,
          int month,
          int day,
          int hour,
          int minute,
          int second);
  @JsMethod(name = "UTC")
  public static native int utc(
          int year,
          int month,
          int day,
          int hour,
          int minute,
          int second,
          int millisecond);

  public native int getDate();
  public native void setDate(int date);

  public native int getDay();

  public native int getFullYear();
  public native void setFullYear(int fullYear);

  public native int getHours();
  public native void setHours(int hours);

  public native int getMinutes();
  public native void setMinutes(int minutes);

  public native int getMilliseconds();
  public native void setMilliseconds(int milliseconds);

  public native int getMonth();
  public native void setMonth(int month);

  public native int getSeconds();
  public native void setSeconds(int seconds);

  public native int getTime();
  public native void setTime(int time);

  public native int getTimezoneOffset();

  public native int getUTCDate();
  public native void setUTCDate(int date);

  public native int getUTCDay();

  public native int getUTCFullYear();
  public native void setUTCFullYear(int fullYear);

  public native int getUTCHours();
  public native void setUTCHours(int hours);

  public native int getUTCMilliseconds();
  public native void setUTCMilliseconds(int milliseconds);

  public native int getUTCMinutes();
  public native void setUTCMinutes(int minutes);

  public native int getUTCMonth();
  public native void getUTCMonth(int month);

  public native int getUTCSeconds();
  public native void setUTCSeconds(int seconds);

  public native int getYear();
  public native void setYear(int year);

  public native String toDateString();

  public native String toISOString();

  public native String toJSON();

  public native String toGMTString();

  public native String toLocaleDateString();

  public native String toLocaleString();

  public native String toLocaleTimeString();

  @Override
  public native String toString();

  public native String toTimeString();

  public native String toUTCString();

  public native int valueOf();
}
