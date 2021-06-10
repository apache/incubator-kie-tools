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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ait.lienzo.test.translator.LienzoMockitoClassTranslator;

/**
 * The runtime settings.
 *
 * @author Roger Martinez
 * @since 1.0
 */
public final class Settings {

    private final Map<String, String> stubs;

    private final Collection<String> jsoStubs;

    private final Collection<String> jsoMocks;

    private final Collection<String> mocks;

    private final Collection<LienzoMockitoClassTranslator.TranslatorInterceptor> translators;

    Settings(final Collection<LienzoMockitoClassTranslator.TranslatorInterceptor> translators) {
        this.stubs = new LinkedHashMap<>();
        this.jsoStubs = new LinkedHashSet<>();
        this.jsoMocks = new LinkedHashSet<>();
        this.mocks = new LinkedHashSet<>();
        this.translators = translators;
    }

    Settings(final Map<String, String> stubs, final Set<String> jsoStubs, final Set<String> jsoMocks, final Set<String> mocks, final List<LienzoMockitoClassTranslator.TranslatorInterceptor> translators) {
        this.stubs = stubs;
        this.jsoStubs = jsoStubs;
        this.jsoMocks = jsoMocks;
        this.mocks = mocks;
        this.translators = translators;
    }

    public Map<String, String> getStubs() {
        return stubs;
    }

    public Collection<String> getJSOStubs() {
        return jsoStubs;
    }

    public Collection<String> getJSOMocks() {
        return jsoMocks;
    }

    public Collection<String> getMocks() {
        return mocks;
    }

    public Collection<LienzoMockitoClassTranslator.TranslatorInterceptor> getAdditionalTranslators() {
        return translators;
    }
}
