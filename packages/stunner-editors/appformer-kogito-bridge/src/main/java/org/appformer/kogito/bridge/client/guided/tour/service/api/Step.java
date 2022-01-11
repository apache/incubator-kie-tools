/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.appformer.kogito.bridge.client.guided.tour.service.api;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Native class defined into the Guided Tour component.
 * Defines a single step in a tutorial.
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class Step {

    @JsProperty
    public native void setMode(final Mode mode);

    @JsProperty
    public native void setContent(final String content);

    @JsProperty
    public native void setSelector(final String selector);

    @JsProperty
    public native void setHighlightEnabled(final boolean highlightEnabled);

    @JsProperty
    public native void setNavigatorEnabled(final boolean navigatorEnabled);

    /**
     * Sets the position of the step in the screen.
     * Allowed values: "right" | "bottom" | "center" | "left". Any different type ise handled as "center".
     */
    @JsProperty
    public native void setPosition(final String position);

    @JsProperty
    public native void setNegativeReinforcementMessage(final String negativeReinforcementMessage);
}
