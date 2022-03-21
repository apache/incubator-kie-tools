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

package com.ait.lienzo.test.settings;

import java.util.LinkedList;
import java.util.List;

import com.ait.lienzo.test.annotation.JSOMocks;
import com.ait.lienzo.test.annotation.JSOStubs;
import com.ait.lienzo.test.annotation.Mocks;
import com.ait.lienzo.test.annotation.Settings;
import com.ait.lienzo.test.annotation.StubClass;
import com.ait.lienzo.test.annotation.Stubs;
import com.ait.lienzo.test.annotation.Translators;
import com.ait.lienzo.test.translator.LienzoMockitoClassTranslator;

/**
 * The runtime settings builder.
 *
 * @author Roger Martinez
 * @since 1.0
 */
public final class SettingsBuilder {

    public static com.ait.lienzo.test.settings.Settings build(Settings settingsAnnotation, final Stubs stubsAnnotation, final JSOStubs jsoStubsAnnotation, final JSOMocks jsoMocksAnnotation, final Mocks mocksAnnotation, final Translators translatorAnnotation) throws Exception {
        // Handle default settings if no annotation present on test class.
        if (null == settingsAnnotation) {
            settingsAnnotation = DefaultSettingsHolder.INSTANCE.getClass().getAnnotation(Settings.class);
        }

        // Additional interceptors.
        final Class<? extends LienzoMockitoClassTranslator.TranslatorInterceptor>[] interceptorClasses = settingsAnnotation.translators();

        final List<LienzoMockitoClassTranslator.TranslatorInterceptor> interceptors = new LinkedList<>();

        if ((null != interceptorClasses) && (interceptorClasses.length > 0)) {
            for (final Class<? extends LienzoMockitoClassTranslator.TranslatorInterceptor> interceptorClass : interceptorClasses) {
                interceptors.add(interceptorClass.newInstance());
            }
        }
        if (null != translatorAnnotation) {
            for (final Class<? extends LienzoMockitoClassTranslator.TranslatorInterceptor> interceptorClass : translatorAnnotation.value()) {
                interceptors.add(interceptorClass.newInstance());
            }
        }

        // Build the settings instance for this unit test run.
        final com.ait.lienzo.test.settings.Settings result = new com.ait.lienzo.test.settings.Settings(interceptors);

        // Overlay stubs.
        addJSOStubs(settingsAnnotation.jsoStubs(), result);

        if (null != jsoStubsAnnotation) {
            addJSOStubs(jsoStubsAnnotation.value(), result);
        }

        // Overlay mocks.
        addJSOMocks(settingsAnnotation.jsoMocks(), result);

        if (null != jsoMocksAnnotation) {
            addJSOMocks(jsoMocksAnnotation.value(), result);
        }

        // Custom stubs.
        addStubs(settingsAnnotation.stubs(), result);

        if (null != stubsAnnotation) {
            addStubs(stubsAnnotation.value(), result);
        }

        // Add mocks.
        addMocks(settingsAnnotation.mocks(), result);

        if (null != mocksAnnotation) {
            addMocks(mocksAnnotation.value(), result);
        }
        return result;
    }

    private static void addMocks(final String[] mocks, final com.ait.lienzo.test.settings.Settings settings) {
        if ((null != mocks) && (mocks.length > 0)) {
            for (final String mock : mocks) {
                settings.getMocks().add(mock);
            }
        }
    }

    private static void addJSOStubs(final String[] jsoStubs, final com.ait.lienzo.test.settings.Settings settings) {
        if ((null != jsoStubs) && (jsoStubs.length > 0)) {
            for (final String jsoStub : jsoStubs) {
                settings.getJSOStubs().add(jsoStub);
            }
        }
    }

    private static void addJSOMocks(final String[] jsoMocks, final com.ait.lienzo.test.settings.Settings settings) {
        if ((null != jsoMocks) && (jsoMocks.length > 0)) {
            for (final String jsoMock : jsoMocks) {
                settings.getJSOMocks().add(jsoMock);
            }
        }
    }

    private static void addStubs(final Class<?>[] stubs, final com.ait.lienzo.test.settings.Settings settings) {
        if ((null != stubs) && (stubs.length > 0)) {
            for (final Class<?> stubTargetClass : stubs) {
                if (!stubTargetClass.isAnnotationPresent(StubClass.class)) {
                    throw new RuntimeException("The stub class [" + stubTargetClass.getName() + "] does not have the StubClass annotation.");
                }
                final StubClass stubClassAnnotation = stubTargetClass.getAnnotation(StubClass.class);

                settings.getStubs().put(stubClassAnnotation.value(), stubTargetClass.getName());
            }
        }
    }
}
