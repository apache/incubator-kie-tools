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
package org.kie.workbench.common.stunner.core.backend.definition.adapter.bind;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.kie.workbench.common.stunner.core.backend.definition.adapter.AbstractReflectAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindablePropertySetAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Handle i18n bundle.

class BackendBindablePropertySetAdapter<T> extends AbstractReflectAdapter<T>
        implements BindablePropertySetAdapter<T> {

    private static final Logger LOG = LoggerFactory.getLogger(BackendBindablePropertySetAdapter.class);

    private Map<Class, String> propertyNameFieldNames;
    private Map<Class, Set<String>> propertiesFieldNames;

    @Override
    public void setBindings(final Map<Class, String> propertyNameFieldNames,
                            final Map<Class, Set<String>> propertiesFieldNames) {
        this.propertyNameFieldNames = propertyNameFieldNames;
        this.propertiesFieldNames = propertiesFieldNames;
    }

    @Override
    public String getId(final T propertySet) {
        return BindableAdapterUtils.getPropertySetId(propertySet.getClass());
    }

    @Override
    public String getName(final T propertySet) {
        Class<?> type = propertySet.getClass();
        try {
            return getFieldValue(propertySet,
                                 propertyNameFieldNames.get(type));
        } catch (IllegalAccessException e) {
            LOG.error("Error obtaining name for Property Set with id " + getId(propertySet));
        }
        return null;
    }

    @Override
    public Set<?> getProperties(final T propertySet) {
        Class<?> type = propertySet.getClass();
        Set<String> fields = propertiesFieldNames.get(type);
        try {
            return getFieldValues(propertySet,
                                  fields);
        } catch (IllegalAccessException e) {
            LOG.error("Error obtaining properties for Property Set with id " + getId(propertySet));
        }
        return null;
    }

    @Override
    public <P> P getProperty(T pojo, String propertyName) {
        return (P) propertiesFieldNames.get(pojo.getClass()).stream()
                .filter(name -> Objects.equals(name, propertyName))
                .findFirst()
                .map(prop -> {
                    try {
                        return getFieldValue(pojo, prop);
                    } catch (IllegalAccessException e) {
                        LOG.error("Error obtaining properties for Property Set with id " + getId(pojo));
                        return null;
                    }
                })
                .orElse(null);
    }

    @Override
    public boolean accepts(final Class<?> type) {
        return null != propertyNameFieldNames && propertyNameFieldNames.containsKey(type);
    }
}
