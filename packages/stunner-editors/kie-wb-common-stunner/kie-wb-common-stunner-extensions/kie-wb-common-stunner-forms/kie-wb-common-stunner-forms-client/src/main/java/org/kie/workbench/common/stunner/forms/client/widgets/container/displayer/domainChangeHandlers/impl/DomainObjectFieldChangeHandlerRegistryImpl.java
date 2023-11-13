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


package org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.domainChangeHandlers.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.domainChangeHandlers.DomainObjectFieldChangeHandler;
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.domainChangeHandlers.DomainObjectFieldChangeHandlerRegistry;

@ApplicationScoped
public class DomainObjectFieldChangeHandlerRegistryImpl implements DomainObjectFieldChangeHandlerRegistry {

    private final Map<Class, Class<? extends DomainObjectFieldChangeHandler>> registry = new HashMap<>();

    private ManagedInstance<DomainObjectFieldChangeHandler> managedInstance;

    @Inject
    public DomainObjectFieldChangeHandlerRegistryImpl(ManagedInstance<DomainObjectFieldChangeHandler> managedInstance) {
        this.managedInstance = managedInstance;
    }

    @Override
    public void register(Class domainType, Class<? extends DomainObjectFieldChangeHandler> changeHandlerType) {
        this.registry.put(domainType, changeHandlerType);
    }

    @Override
    public Optional<DomainObjectFieldChangeHandler> lookupChangeHandler(Object domainObject) {
        Class<? extends DomainObjectFieldChangeHandler> registryType = this.registry.get(domainObject.getClass());
        if(registryType == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(managedInstance.select(registryType).get());
    }

    @PreDestroy
    public void destroy() {
        registry.clear();
        managedInstance.destroyAll();
    }
}