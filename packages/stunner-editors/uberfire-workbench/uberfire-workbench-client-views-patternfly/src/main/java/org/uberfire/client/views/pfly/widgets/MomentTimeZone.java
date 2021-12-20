/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Wrapper class for <a href="https://momentjs.com/timezone">moment-timezone.min.js</a> library
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface MomentTimeZone {

    String[] names();

    double utcOffset();

    String format(final String format);

    class Builder {

        @JsProperty(name = "moment.tz", namespace = JsPackage.GLOBAL)
        public static native MomentTimeZone tz();

        @JsMethod(name = "moment.tz", namespace = JsPackage.GLOBAL)
        public static native MomentTimeZone tz(final String timeZone);
    }
}
