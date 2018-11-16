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

package org.kie.workbench.common.stunner.core.definition.adapter.bootstrap;

import java.util.Optional;
import java.util.Set;

import org.kie.workbench.common.stunner.core.definition.adapter.PropertySetAdapter;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;

class BootstrapPropertySetAdapter implements PropertySetAdapter<Object> {

    private final AdapterRegistry adapterRegistry;

    BootstrapPropertySetAdapter(final AdapterRegistry adapterRegistry) {
        this.adapterRegistry = adapterRegistry;
    }

    @Override
    public String getId(final Object pojo) {
        return getWrapped(pojo).getId(pojo);
    }

    @Override
    public String getName(final Object pojo) {
        return getWrapped(pojo).getName(pojo);
    }

    @Override
    public Set<?> getProperties(final Object pojo) {
        return getWrapped(pojo).getProperties(pojo);
    }

    @Override
    public Optional<?> getProperty(Object pojo, String propertyName) {
        return getWrapped(pojo).getProperty(pojo, propertyName);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean accepts(final Class<?> type) {
        return null != getWrapped(type);
    }

    @Override
    public boolean isPojoModel() {
        return false;
    }

    private PropertySetAdapter<Object> getWrapped(final Object pojo) {
        return getWrapped(pojo.getClass());
    }

    private PropertySetAdapter<Object> getWrapped(final Class<?> type) {
        return adapterRegistry.getPropertySetAdapter(type);
    }
}
