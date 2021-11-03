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

import javax.enterprise.inject.Produces;

import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true)
public abstract class JQueryElementalProducer {

    /**
     * <p>
     * Declares a producer for the JQuery function, allowing it to be injected via Errai IoC.
     * <p>
     * <p>
     * {@link JsProperty} is used so that GWT translates method calls to property access of the globally-scoped {@code $} symbol.
     */
    @Produces
    @JsProperty(name = "$", namespace = JsPackage.GLOBAL)
    public static native JQuery get();

    /**
     * <p>
     * The JQuery function, used to enhance regular elements.
     */
    @JsFunction
    @FunctionalInterface
    public interface JQuery<T extends JQueryElement> {

        T wrap(HTMLElement element);
    }

    /**
     * <p>
     * Interface for enhanced JQuery elements.
     * <p>
     * <p>
     */
    @JsType(isNative = true)
    public interface JQueryElement {

    }
}
