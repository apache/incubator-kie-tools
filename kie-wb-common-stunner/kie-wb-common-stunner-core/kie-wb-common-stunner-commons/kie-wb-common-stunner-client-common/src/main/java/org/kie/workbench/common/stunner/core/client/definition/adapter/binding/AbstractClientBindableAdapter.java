/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collection;
import java.util.Set;

import org.kie.workbench.common.stunner.core.definition.adapter.PriorityAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;

public abstract class AbstractClientBindableAdapter<T> implements PriorityAdapter {

    protected StunnerTranslationService translationService;

    public AbstractClientBindableAdapter(StunnerTranslationService translationService) {
        this.translationService = translationService;
    }

    @SuppressWarnings("unchecked")
    protected <R> R getProxiedValue(final T pojo,
                                    final String fieldName) {
        return ClientBindingUtils.getProxiedValue(pojo,
                                                  fieldName);
    }

    @SuppressWarnings("unchecked")
    protected <R> Set<R> getProxiedSet(final T pojo,
                                       final Collection<String> fieldNames) {
        return ClientBindingUtils.getProxiedSet(pojo,
                                                fieldNames);
    }

    @SuppressWarnings("unchecked")
    protected <V> void setProxiedValue(final T pojo,
                                       final String fieldName,
                                       final V value) {
        ClientBindingUtils.setProxiedValue(pojo,
                                           fieldName,
                                           value);
    }

    protected String getDefinitionId(final Class<?> type) {
        return BindableAdapterUtils.getDefinitionId(type);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
