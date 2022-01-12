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
import javassist.ClassPool;
import javassist.NotFoundException;

/**
 * It removes the <code>final</code> modifier class declared methods, so those can be further mocked using
 * regular mockito API.
 *
 * @author Roger Martinez
 * @See com.ait.lienzo.test.annotation.Settings#mocks
 * @since 1.0
 */
public class StripFinalModifiersTranslatorInterceptor extends AbstractStripFinalModifiersTranslatorInterceptor implements HasSettings {

    private final Set<String> classNames = new LinkedHashSet<>();

    @Override
    protected Set<String> getClassNames() {
        return classNames;
    }

    @Override
    public void useSettings(final Settings settings) {
        this.classNames.addAll(settings.getMocks());
    }

    @Override
    public void interceptAfterParent(final ClassPool classPool, final String name) throws NotFoundException, CannotCompileException {
        // Nothing required for now.
    }
}
