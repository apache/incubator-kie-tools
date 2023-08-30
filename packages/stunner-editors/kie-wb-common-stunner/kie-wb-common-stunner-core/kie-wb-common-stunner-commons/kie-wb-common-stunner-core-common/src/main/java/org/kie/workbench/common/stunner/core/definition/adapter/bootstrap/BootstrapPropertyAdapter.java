/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.definition.adapter.bootstrap;

import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;

class BootstrapPropertyAdapter implements PropertyAdapter<Object, Object> {

    private final AdapterRegistry adapterRegistry;

    BootstrapPropertyAdapter(final AdapterRegistry adapterRegistry) {
        this.adapterRegistry = adapterRegistry;
    }

    @Override
    public String getId(final Object pojo) {
        return getWrapped(pojo).getId(pojo);
    }

    @Override
    public String getCaption(Object pojo) {
        return getWrapped(pojo).getCaption(pojo);
    }

    @Override
    public Object getValue(final Object pojo) {
        return getWrapped(pojo).getValue(pojo);
    }

    @Override
    public void setValue(final Object pojo,
                         final Object value) {
        getWrapped(pojo).setValue(pojo,
                                  value);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean accepts(final Class<?> type) {
        return null != getWrapped(type);
    }

    private PropertyAdapter<Object, Object> getWrapped(final Object pojo) {
        return getWrapped(pojo.getClass());
    }

    @SuppressWarnings("unchecked")
    private PropertyAdapter<Object, Object> getWrapped(final Class<?> type) {
        return (PropertyAdapter<Object, Object>) adapterRegistry.getPropertyAdapter(type);
    }
}
