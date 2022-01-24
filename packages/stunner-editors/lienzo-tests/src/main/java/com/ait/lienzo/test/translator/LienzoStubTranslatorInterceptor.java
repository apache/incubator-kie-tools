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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.ait.lienzo.test.settings.Settings;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

/**
 * Translator interceptor for stub classes.
 * <p>
 * It replaces the stub classes given at compile time by the Lienzo original classes.
 *
 * @author Roger Martinez
 * @since 1.0
 */
public class LienzoStubTranslatorInterceptor implements LienzoMockitoClassTranslator.TranslatorInterceptor,
                                                        HasSettings {

    private final Map<String, String> stubs = new LinkedHashMap<>();

    public LienzoStubTranslatorInterceptor() {
    }

    @Override
    public boolean interceptBeforeParent(final ClassPool classPool, final String name) throws NotFoundException, CannotCompileException {
        // Check if the concrete class can be translated using our concrete stubbed ones with method implementations.
        if (stubs.keySet().contains(name)) {
            final String translationClass = stubs.get(name);

            if ((null != translationClass) && (translationClass.trim().length() > 0)) {
                try {
                    final CtClass ctClass = classPool.getCtClass(name);

                    if (ctClass.isFrozen()) {
                        ctClass.defrost();
                    }
                    classPool.getAndRename(translationClass, name);
                    final CtClass stubCtClass = classPool.get(name);
                    // Check the if the parent type for the stub class, if any,
                    // contains some reference to any other stub as well.
                    // If found other stub references on parent, replace
                    // parent types for the expected parent type.
                    if (null != stubCtClass.getSuperclass()) {
                        final String superStubName = stubCtClass.getSuperclass().getName();
                        if (stubs.containsValue(superStubName)) {
                            final String superName = getKey(superStubName);
                            if (null != superName) {
                                final CtClass pp = classPool.get(superName);
                                stubCtClass.setSuperclass(pp);
                            }
                        }
                    }
                } catch (final NotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // Let parent loader do additional job.
        return false;
    }

    @Override
    public void useSettings(final Settings settings) {
        assert null != settings;

        this.stubs.putAll(settings.getStubs());
    }

    @Override
    public void interceptAfterParent(final ClassPool classPool, final String name) throws NotFoundException, CannotCompileException {
        // Nothing required for now.
    }

    private String getKey(final String value) {
        final Set<Map.Entry<String, String>> entries = stubs.entrySet();
        for (final Map.Entry<String, String> entry : entries) {
            final String _key = entry.getKey();
            final String _value = entry.getValue();
            if (_value.equals(value)) {
                return _key;
            }
        }
        return null;
    }
}
