/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.widgets;

import java.util.Date;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Wrapper class for <a href="http://momentjs.com">Moment.js</a> library
 */
@JsType(isNative = true)
public interface Moment {

    Moment hours(int hours);

    Moment minutes(int minutes);

    Moment seconds(int seconds);

    Moment milliseconds(int milliseconds);

    Moment subtract(int number,
                    String period);

    Moment startOf(String type);

    Moment endOf(String type);

    Moment add(int number,
               String period);

    String format(String format);

    String fromNow();

    String format();

    String toString();

    Double valueOf();

    int hours();

    int minutes();

    int seconds();

    boolean isValid();

    @JsOverlay
    default Long asLong(){
        return valueOf().longValue();
    }

    @JsOverlay
    default Date asDate(){
        return new Date(asLong());
    }

    boolean isSame(Moment moment);

    boolean isSame(Moment moment, String period);

    class Builder {

        @JsMethod(namespace = JsPackage.GLOBAL)
        public static native Moment moment();

        public static Moment moment(Long time) {
            return moment(new Double(time));
        }

        @JsMethod(namespace = JsPackage.GLOBAL)
        public static native Moment moment(String time, String format);

        @JsMethod(namespace = JsPackage.GLOBAL)
        protected static native Moment moment(Double time);
    }
}
