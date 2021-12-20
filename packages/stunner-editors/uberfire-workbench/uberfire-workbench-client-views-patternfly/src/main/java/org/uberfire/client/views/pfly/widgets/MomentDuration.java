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

import com.google.gwt.core.client.JavaScriptObject;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

import static jsinterop.annotations.JsPackage.GLOBAL;

/**
 * Wrapper class for the <a href="https://momentjs.com/docs/#/durations/">moment.duration</a> function.
 */
@JsType(isNative = true)
public abstract class MomentDuration {

    @JsProperty(namespace = GLOBAL, name = "moment")
    public static MomentDuration moment;

    public native MomentDurationObject duration(final String pattern);

    public native MomentDuration duration(final JavaScriptObject properties);

    public native String toISOString();
}
