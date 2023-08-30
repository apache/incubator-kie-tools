/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package com.ait.lienzo.test.translator;

import java.util.HashSet;
import java.util.Set;

import com.ait.lienzo.client.core.shape.IPrimitive;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import jsinterop.base.JsPropertyMap;
import org.apache.commons.lang3.ArrayUtils;

public class LienzoPrimitiveTranslatorInterceptor extends AbstractStripFinalModifiersTranslatorInterceptor {

    private static final String PRIM_CLASS = IPrimitive.class.getName();

    @Override
    protected Set<String> getClassNames() {
        return new HashSet<String>(1) {
            {
                add(PRIM_CLASS);
            }
        };
    }

    @Override
    public boolean interceptBeforeParent(final ClassPool classPool, final String name) throws NotFoundException, CannotCompileException {
        super.interceptBeforeParent(classPool, name);

        if (name.equals(PRIM_CLASS)) {
            final CtClass clazz = classPool.get(name);

            addJsPropertyMapType(classPool, clazz);

            // Intercept class loading. Avoid parent's job..
            return true;
        }
        // Let parent loader do the job.
        return false;
    }

    // All nodes are usually being casted to JsPropertyMap instances at runtime.
    private void addJsPropertyMapType(ClassPool classPool, CtClass clazz) throws NotFoundException {
        CtClass[] interfaces = clazz.getInterfaces();
        final CtClass jsPropertyMapClazz = classPool.get(JsPropertyMap.class.getName());
        if (!ArrayUtils.contains(interfaces, jsPropertyMapClazz)) {
            clazz.addInterface(jsPropertyMapClazz);
        }
    }

    @Override
    public void interceptAfterParent(final ClassPool classPool, final String name) throws NotFoundException, CannotCompileException {
        // Nothing required for now.
    }
}
