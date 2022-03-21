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

import com.google.gwt.canvas.client.Canvas;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * Translator interceptor for Canvas class.
 * <p>
 * This translator enabled the GWT canvas support on the unit testing scope, so although there is no real browser neither
 * js script engine, GWT interprets that they're present.
 * <p>
 * Canvas support is required to be enabled for some Lienzo core functionality, such as context transformations, etc.
 *
 * @author Roger Martinez
 * @since 1.0
 */
public class CanvasSupportTranslatorInterceptor implements LienzoMockitoClassTranslator.TranslatorInterceptor {

    private static final String CANVAS_CLASS = Canvas.class.getName();

    private static final String METHOD_IS_SUPPORTED = "isSupported";

    @Override
    public boolean interceptBeforeParent(final ClassPool classPool, final String name) throws NotFoundException, CannotCompileException {
        if (name.equals(CANVAS_CLASS)) {
            final CtClass clazz = classPool.get(name);

            for (final CtMethod method : clazz.getDeclaredMethods()) {
                if (METHOD_IS_SUPPORTED.equals(method.getName())) {
                    method.setBody("{ return true; }");
                }
            }
        }
        // Let parent loader do additional job.
        return false;
    }

    @Override
    public void interceptAfterParent(final ClassPool classPool, final String name) throws NotFoundException, CannotCompileException {
        // Nothing required for now.
    }
}
