/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.DefinitionBindableProperty;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.DefinitionBindablePropertyAdapter;

@ApplicationScoped
public class ClientDefinitionBindablePropertyAdapter implements DefinitionBindablePropertyAdapter<Object, Object> {

    private final ClientTranslationService translationService;

    @Inject
    public ClientDefinitionBindablePropertyAdapter(ClientTranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public String getCaption(DefinitionBindableProperty<Object> property) {
        return translationService.getPropertyCaption(getId(property));
    }

    @Override
    public Object getValue(DefinitionBindableProperty<Object> property) {
        return ClientBindingUtils.getProxiedValue(property.getPojo(), property.getField());
    }

    @Override
    public void setValue(DefinitionBindableProperty<Object> property, Object value) {
        ClientBindingUtils.setProxiedValue(property.getPojo(), property.getField(), value);
    }
}
