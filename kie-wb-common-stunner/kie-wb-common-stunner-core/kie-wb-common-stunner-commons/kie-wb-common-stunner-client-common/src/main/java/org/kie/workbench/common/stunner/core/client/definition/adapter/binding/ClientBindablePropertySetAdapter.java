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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindablePropertySetAdapter;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;

class ClientBindablePropertySetAdapter extends AbstractClientBindableAdapter<Object> implements BindablePropertySetAdapter<Object> {

    private Map<Class, String> propertyNameFieldNames;
    private Map<Class, Set<String>> propertiesFieldNames;

    public ClientBindablePropertySetAdapter(StunnerTranslationService translationService) {
        super(translationService);
    }

    @Override
    public void setBindings(final Map<Class, String> propertyNameFieldNames,
                            final Map<Class, Set<String>> propertiesFieldNames) {
        this.propertyNameFieldNames = propertyNameFieldNames;
        this.propertiesFieldNames = propertiesFieldNames;
    }

    @Override
    public String getId(final Object pojo) {
        return BindableAdapterUtils.getPropertySetId(pojo.getClass());
    }

    @Override
    public String getName(final Object pojo) {
        String name = translationService.getPropertySetName(pojo.getClass().getName());
        if (name != null) {
            return name;
        }
        return getProxiedValue(pojo,
                               getPropertyNameFieldNames().get(pojo.getClass()));
    }

    @Override
    public Set<?> getProperties(final Object pojo) {
        return getProxiedSet(pojo,
                             getPropertiesFieldNames().get(pojo.getClass()));
    }

    @Override
    public Optional<?> getProperty(Object pojo, String propertyName) {
        return getPropertiesFieldNames().get(pojo.getClass()).stream()
                .filter(name -> Objects.equals(name, propertyName))
                .findFirst()
                .map(prop -> getProxiedValue(pojo, prop));
    }

    @Override
    public boolean accepts(final Class<?> pojoClass) {
        return getPropertiesFieldNames().containsKey(pojoClass);
    }

    private Map<Class, String> getPropertyNameFieldNames() {
        return propertyNameFieldNames;
    }

    private Map<Class, Set<String>> getPropertiesFieldNames() {
        return propertiesFieldNames;
    }
}
