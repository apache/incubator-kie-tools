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

import java.util.LinkedHashSet;
import java.util.Set;

import com.ait.lienzo.test.settings.Settings;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * Translator interceptor for Lienzo's overlay types.
 * <p>
 * It provides no-op stub methods for the given Lienzo's overlay types. Delegates most of the method stubbing to parent
 * and takes about some concrete methods such as <code>make</code>, <code>create</code>, <code>makeXXX</code>
 * or <code>createXXX</code>.
 *
 * @author Roger Martinez
 * @since 1.0
 */
public class LienzoJSOStubTranslatorInterceptor extends AbstractLienzoJSOTranslatorInterceptor implements HasSettings {

    private final Set<String> jsos = new LinkedHashSet<>();

    public LienzoJSOStubTranslatorInterceptor() {
    }

    @Override
    protected Set<String> getJSOClasses() {
        return jsos;
    }

    @Override
    protected void setMakeMethodBody(final String fqcn, final CtClass ctClass, final CtMethod ctMethod) throws NotFoundException, CannotCompileException {
        ctMethod.setBody(String.format("{ return new %s(); }", fqcn));
    }

    @Override
    public void useSettings(final Settings settings) {
        assert null != settings;

        this.jsos.addAll(settings.getJSOStubs());
    }
}
