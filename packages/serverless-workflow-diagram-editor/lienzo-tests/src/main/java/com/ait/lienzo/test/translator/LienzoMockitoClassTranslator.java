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

package com.ait.lienzo.test.translator;

import java.util.LinkedList;
import java.util.List;

import com.ait.lienzo.test.settings.Settings;
import com.ait.lienzo.test.util.LienzoMockitoLogger;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.Translator;

/**
 * Custom javassist translator class that wraps the one given by GwtMockito
 * and applies different behaviors for handling the overlay types and native interfaces. Each of those
 * behaviors are given by the translator interceptor classes, so this class just orchestrates the different interceptors.
 *
 * @author Roger Martinez
 * @since 1.0
 */
public class LienzoMockitoClassTranslator implements Translator {

    public interface TranslatorInterceptor {

        boolean interceptBeforeParent(ClassPool classPool, String name) throws NotFoundException, CannotCompileException;

        void interceptAfterParent(ClassPool classPool, String name) throws NotFoundException, CannotCompileException;
    }

    private final TranslatorInterceptor[] interceptors;

    private final Translator parent;

    public LienzoMockitoClassTranslator(final Settings settings, final Translator parent) {
        this.parent = parent;

        this.interceptors = initInterceptors(settings);
    }

    private TranslatorInterceptor[] initInterceptors(final Settings settings) {
        final List<TranslatorInterceptor> result = new LinkedList<>(settings.getAdditionalTranslators());

        // Configure the translator interceptor classes with the required settings.
        for (final LienzoMockitoClassTranslator.TranslatorInterceptor interceptor : result) {
            if (interceptor instanceof HasSettings) {
                final HasSettings hasSettings = (HasSettings) interceptor;

                hasSettings.useSettings(settings);
            }
        }
        return result.toArray(new TranslatorInterceptor[result.size()]);
    }

    @Override
    public void onLoad(final ClassPool pool, final String name) throws NotFoundException, CannotCompileException {
        log("onLoad for '" + name + "'");

        ensureDefrost(pool, name);

        boolean continueLoad = true;

        for (final TranslatorInterceptor interceptor : interceptors) {
            if (interceptor.interceptBeforeParent(pool, name)) {
                continueLoad = false;
            }
        }
        if (continueLoad && (null != parent)) {
            ensureDefrost(pool, name);

            parent.onLoad(pool, name);

            for (final TranslatorInterceptor interceptor : interceptors) {
                interceptor.interceptAfterParent(pool, name);
            }
        }
    }

    private void ensureDefrost(final ClassPool pool, final String name) throws NotFoundException {
        final CtClass ctClass = pool.get(name);

        if (ctClass.isFrozen()) {
            ctClass.defrost();
        }
    }

    @Override
    public void start(final ClassPool pool) throws NotFoundException, CannotCompileException {
        log("Start");

        if (null != parent) {
            this.parent.start(pool);
        }
    }

    private void log(final String message) {
        LienzoMockitoLogger.log("LienzoMockitoClassTranslator", message);
    }
}
