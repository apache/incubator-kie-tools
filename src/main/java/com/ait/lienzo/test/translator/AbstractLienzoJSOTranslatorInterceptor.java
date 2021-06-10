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
 * The implementations can provides no-op stubs or mocks for methods on the Lienzo's overlay types.
 * <p>
 * Delegates most of the methods stubbing to parent and takes about some concrete
 * methods such as <code>make</code>, <code>create</code>, <code>makeXXX</code>
 * or <code>createXXX</code>.
 *
 * @author Roger Martinez
 * @since 1.0
 */
public abstract class AbstractLienzoJSOTranslatorInterceptor implements LienzoMockitoClassTranslator.TranslatorInterceptor {

    protected static final String METHOD_MAKE = "make";

    protected static final String METHOD_CREATE = "create";

    protected static final String METHOD_OF = "of";

    protected abstract Set<String> getJSOClasses();

    protected abstract void setMakeMethodBody(String fqcn, CtClass ctClass, CtMethod ctMethod) throws NotFoundException, CannotCompileException;

    @Override
    public boolean interceptBeforeParent(final ClassPool classPool, final String name) throws NotFoundException, CannotCompileException {
        // Nothing required for now.
        // Let parent loader do the job.
        return false;
    }

    @Override
    public void interceptAfterParent(final ClassPool classPool, final String name) throws NotFoundException, CannotCompileException {
        doTheJob(classPool, name);
    }

    protected void doTheJob(final ClassPool classPool, final String name) throws NotFoundException, CannotCompileException {
        final CtClass clazz = classPool.get(name);

        if (getJSOClasses().contains(name)) {
            // Get the fully qualified class name ( for inner classes as well ).
            final String fqcn = name.contains("$") ? name.replaceAll("\\$", "\\.") : name;

            // Create stub/mock implementations for certain methods.
            for (final CtMethod method : clazz.getDeclaredMethods()) {
                if (isMakeMethod(method, name) || isCreateMethod(method, name) || isOfMethod(method, name)) {
                    method.setModifiers(method.getModifiers() & ~java.lang.reflect.Modifier.NATIVE);

                    method.setModifiers(method.getModifiers() & ~Modifier.FINAL);

                    setMakeMethodBody(fqcn, clazz, method);
                }
            }
        }
    }

    protected boolean isMakeMethod(final CtMethod method, final String className) throws NotFoundException {
        return isMethod(method, className, METHOD_MAKE);
    }

    protected boolean isCreateMethod(final CtMethod method, final String className) throws NotFoundException {
        return isMethod(method, className, METHOD_CREATE);
    }

    protected boolean isOfMethod(final CtMethod method, final String className) throws NotFoundException {
        return isMethod(method, className, METHOD_OF);
    }

    protected boolean isMethod(final CtMethod method, final String className, final String methodName) throws NotFoundException {
        final String mName = method.getName();

        final String rName = method.getReturnType().getName();

        return mName.startsWith(methodName) && className.equals(rName);
    }
}
