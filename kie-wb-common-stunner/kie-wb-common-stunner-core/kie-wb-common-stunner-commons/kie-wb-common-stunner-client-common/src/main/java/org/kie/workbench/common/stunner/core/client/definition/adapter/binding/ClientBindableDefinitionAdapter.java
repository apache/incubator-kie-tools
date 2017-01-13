/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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

import org.kie.workbench.common.stunner.core.definition.adapter.binding.AbstractBindableDefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableDefinitionAdapter;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

class ClientBindableDefinitionAdapter extends AbstractBindableDefinitionAdapter<Object>
        implements BindableDefinitionAdapter<Object> {

    ClientBindableDefinitionAdapter(final DefinitionUtils definitionUtils) {
        super(definitionUtils);
    }

    public String getCategory(final Object pojo) {
        return getProxiedValue(pojo,
                               getPropertyCategoryFieldNames().get(pojo.getClass()));
    }

    public String getTitle(final Object pojo) {
        return getProxiedValue(pojo,
                               getPropertyTitleFieldNames().get(pojo.getClass()));
    }

    public String getDescription(final Object pojo) {
        return getProxiedValue(pojo,
                               getPropertyDescriptionFieldNames().get(pojo.getClass()));
    }

    public Set<String> getLabels(final Object pojo) {
        final String fName = getPropertyLabelsFieldNames().get(pojo.getClass());
        return getProxiedValue(pojo,
                               fName);
    }

    public Set<?> getPropertySets(final Object pojo) {
        return getProxiedSet(pojo,
                             getPropertySetsFieldNames().get(pojo.getClass()));
    }

    @Override
    protected Set<?> getBindProperties(final Object pojo) {
        return getProxiedSet(pojo,
                             getPropertiesFieldNames().get(pojo.getClass()));
    }

    private <T, R> R getProxiedValue(final T pojo,
                                     final String fieldName) {
        return ClientBindingUtils.getProxiedValue(pojo,
                                                  fieldName);
    }

    private <T, R> Set<R> getProxiedSet(final T pojo,
                                        final Collection<String> fieldNames) {
        return ClientBindingUtils.getProxiedSet(pojo,
                                                fieldNames);
    }

    private <T, V> void setProxiedValue(final T pojo,
                                        final String fieldName,
                                        final V value) {
        ClientBindingUtils.setProxiedValue(pojo,
                                           fieldName,
                                           value);
    }
}
