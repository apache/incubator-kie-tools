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

import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

/**
 * It removes the <code>final</code> modifier class declared methods, so those can be further mocked using
 * regular mockito API.
 *
 * @author Roger Martinez
 * @since 1.0
 */
public abstract class AbstractStripFinalModifiersTranslatorInterceptor implements LienzoMockitoClassTranslator.TranslatorInterceptor {

    protected abstract Set<String> getClassNames();

    @Override
    public boolean interceptBeforeParent(final ClassPool classPool, final String name) throws NotFoundException, CannotCompileException {
        if (doIntercept(name)) {
            final CtClass clazz = classPool.get(name);

            // Remove final modifiers for methods, so can be mocked.
            for (final CtMethod method : clazz.getDeclaredMethods()) {
                method.setModifiers(method.getModifiers() & ~Modifier.FINAL);
            }
            // Intercept class loading. Avoid parent's job..
            return true;
        }
        // Let parent loader do the job.
        return false;
    }

    private boolean doIntercept(final String name) {
        return (getClassNames() != null) && getClassNames().contains(name);
    }
}
