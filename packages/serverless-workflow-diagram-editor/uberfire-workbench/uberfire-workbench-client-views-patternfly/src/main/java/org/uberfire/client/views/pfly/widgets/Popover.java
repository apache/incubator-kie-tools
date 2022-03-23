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

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;
import org.uberfire.client.views.pfly.widgets.JQueryProducer.JQueryElement;

/**
 * Wrapper component for PatternFly's <a href="http://www.patternfly.org/pattern-library/widgets/#popover">Popover</a>
 */
@JsType(isNative = true)
public interface Popover extends JQueryElement {

    void popover();

    void popover(String method);

    void popover(PopoverOptions options);

    @JsOverlay
    default void destroy() {
        popover("destroy");
    }

    @JsOverlay
    default void show() {
        popover("show");
    }

    @JsOverlay
    default void hide() {
        popover("hide");
    }

    @JsOverlay
    default void toggle() {
        popover("toggle");
    }

    void on(String event,
            PopoverEventCallback callback);

    @JsOverlay
    default void addShowListener(final PopoverEventCallback callback) {
        on("show.bs.popover",
           callback);
    }

    @JsOverlay
    default void addShownListener(final PopoverEventCallback callback) {
        on("shown.bs.popover",
           callback);
    }

    @JsOverlay
    default void addHiddenListener(final PopoverEventCallback callback) {
        on("hidden.bs.popover",
           callback);
    }

    @JsOverlay
    default void addHideListener(final PopoverEventCallback callback) {
        on("hide.bs.popover",
           callback);
    }

    @JsOverlay
    default void addInsertedListener(final PopoverEventCallback callback) {
        on("inserted.bs.popover",
           callback);
    }

    @JsFunction
    @FunctionalInterface
    interface PopoverEventCallback {

        void onEvent();
    }
}
