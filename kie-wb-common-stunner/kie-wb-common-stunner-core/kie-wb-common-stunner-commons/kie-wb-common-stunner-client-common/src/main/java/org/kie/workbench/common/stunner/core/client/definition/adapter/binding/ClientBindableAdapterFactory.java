/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.definition.adapter.binding;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterFactory;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindablePropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindablePropertySetAdapter;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@ApplicationScoped
public class ClientBindableAdapterFactory implements BindableAdapterFactory {

    DefinitionUtils definitionUtils;

    StunnerTranslationService translationService;

    protected ClientBindableAdapterFactory() {
        this(null,
             null);
    }

    @Inject
    public ClientBindableAdapterFactory(final DefinitionUtils definitionUtils,
                                        StunnerTranslationService translationService) {
        this.definitionUtils = definitionUtils;
        this.translationService = translationService;
    }

    public BindableDefinitionAdapter newBindableDefinitionAdapter() {
        return new ClientBindableDefinitionAdapter(definitionUtils,
                                                   translationService);
    }

    public BindableDefinitionSetAdapter newBindableDefinitionSetAdapter() {
        return new ClientBindableDefinitionSetAdapter(translationService);
    }

    public BindablePropertyAdapter newBindablePropertyAdapter() {
        return new ClientBindablePropertyAdapter(translationService);
    }

    public BindablePropertySetAdapter<Object> newBindablePropertySetAdapter() {
        return new ClientBindablePropertySetAdapter(translationService);
    }
}
