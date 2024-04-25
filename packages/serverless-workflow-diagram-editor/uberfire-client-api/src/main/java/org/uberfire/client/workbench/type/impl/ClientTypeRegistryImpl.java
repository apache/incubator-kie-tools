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


package org.uberfire.client.workbench.type.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.kie.j2cl.tools.di.core.BeanManager;
import org.kie.j2cl.tools.di.core.SyncBeanDef;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.type.ClientTypeRegistry;

import static java.util.Collections.sort;
import static java.util.Collections.unmodifiableList;

@ApplicationScoped
public class ClientTypeRegistryImpl implements ClientTypeRegistry {

    protected final BeanManager iocManager;
    private List<ClientResourceType> localResourceTypes = new ArrayList<ClientResourceType>();

    @Inject
    public ClientTypeRegistryImpl(final BeanManager iocManager) {
        this.iocManager = iocManager;
    }

    @PostConstruct
    public void init() {
        final Collection<SyncBeanDef<ClientResourceType>> availableTypes = iocManager.lookupBeans(ClientResourceType.class);

        for (final SyncBeanDef<ClientResourceType> availableType : availableTypes) {
            localResourceTypes.add(availableType.getInstance());
        }

        sort(localResourceTypes,
             new Comparator<ClientResourceType>() {
                 @Override
                 public int compare(final ClientResourceType o1,
                                    final ClientResourceType o2) {
                     if (o1.getPriority() < o2.getPriority()) {
                         return 1;
                     } else if (o1.getPriority() > o2.getPriority()) {
                         return -1;
                     } else {
                         return 0;
                     }
                 }
             });
    }

    @Override
    public Collection<ClientResourceType> getRegisteredTypes() {
        return unmodifiableList(localResourceTypes);
    }

    @Override
    public boolean isEnabled(ClientResourceType resourceType) {
        return localResourceTypes.contains(resourceType);
    }

    @Override
    public ClientResourceType resolve(final Path path) {
        for (final ClientResourceType resourceType : localResourceTypes) {
            if (resourceType.accept(path)) {
                return resourceType;
            }
        }
        return null;
    }

    @Override
    public String resolveWildcardPattern(final String shortName) {
        for (final ClientResourceType resourceType : localResourceTypes) {
            if (resourceType.getShortName().equalsIgnoreCase(shortName)) {
                return resourceType.getSimpleWildcardPattern();
            }
        }
        return shortName;
    }
}
