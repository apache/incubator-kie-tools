/*
 * Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ait.lienzo.test.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.ait.lienzo.test.translator.LienzoMockitoClassTranslator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation allows to add additional settings for the testing framework on concrete test classes.
 *
 * @author Roger Martinez
 * @since 1.0
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Settings {

    /**
     * The FQ class names that cannot be mocked as contains final methods.
     * <p>
     * Here you can specify your custom class names, if not present by default, that the library
     * will prepare for being mocked by stripping <code>final</code> modifiers.
     * The class names provided here will be processed * by the
     * class translator <code>com.ait.lienzo.test.translator.StripFinalModifiersTranslatorInterceptor</code>.
     * <p>
     * See default values at <code>com.ait.lienzo.test.settings.DefaultSettingsHolder</code>.
     */
    public String[] mocks() default {};

    /**
     * The FQ stub classes provided for the testing scope.
     * <p>
     * Here you can specify your custom stub classes, if any, that will be replaced at runtime
     * by the original ones.
     * <p>
     * See default values at <code>com.ait.lienzo.test.settings.DefaultSettingsHolder</code>.
     */
    public Class<?>[] stubs() default {};

    /**
     * The overlay types to be stubbed.
     * <p>
     * Provide here the FQ class names for lienzo classes that are GWT overlay types,
     * so inherit from <code>com.google.gwt.core.client.JavaScriptObject</code> and causes your tests to fail
     * due to linkage errors with native interfaces.
     * <p>
     * The class names provided here will be processed
     * by the class translator <code>com.ait.lienzo.test.translator.LienzoJSOStubTranslatorInterceptor</code>.
     * <p>
     * The resulting behavior for the <code>make</code> method for those overlay types is to create new stub instances
     * for the overlay.
     * <p>
     * Must be strings as some of the overlay types can be private classes than cannot
     * be referenced here using type-safe code.
     * <p>
     * See default values at <code>com.ait.lienzo.test.settings.DefaultSettingsHolder</code>.
     */
    public String[] jsoStubs() default {};

    /**
     * The overlay types to be mocked.
     * <p>
     * Provide here the FQ class names for lienzo classes that are GWT overlay types,
     * so inherit from <code>com.google.gwt.core.client.JavaScriptObject</code> and causes your tests to fail
     * due to linkage errors with native interfaces.
     * <p>
     * The class names provided here will be processed
     * by the class translator <code>com.ait.lienzo.test.translator.LienzoJSOMockTranslatorInterceptor</code>.
     * <p>
     * The resulting behavior for the <code>make</code> method for those overlay types is to create mock instances
     * for the overlay, so you can use the mockito API to provide your mocking behaviors.
     * <p>
     * Must be strings as some of the overlay types can be private classes than cannot
     * be referenced here using type-safe code.
     * <p>
     * See default values at <code>com.ait.lienzo.test.settings.DefaultSettingsHolder</code>.
     */
    public String[] jsoMocks() default {};

    /**
     * The class translators used by this framework.
     * <p>
     * You can build your own translators, if necessary, and make them available on your test classes
     * by using this annotation.
     * <p>
     * See default values at <code>com.ait.lienzo.test.settings.DefaultSettingsHolder</code>.
     */
    public Class<? extends LienzoMockitoClassTranslator.TranslatorInterceptor>[] translators() default {};

    /**
     * Enable or disable the testing framework logger, basically for its debugging purposes.
     * <p>
     * Disabled by default.
     */
    public boolean logEnabled() default false;
}
