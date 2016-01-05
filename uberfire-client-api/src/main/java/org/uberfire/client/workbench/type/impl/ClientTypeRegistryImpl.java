/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.workbench.type.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.type.ClientTypeRegistry;

import static java.util.Collections.*;

@ApplicationScoped
public class ClientTypeRegistryImpl implements ClientTypeRegistry {

    private List<ClientResourceType> localResourceTypes = new ArrayList<ClientResourceType>();

    private final SyncBeanManager iocManager;

    @Inject
    public ClientTypeRegistryImpl( final SyncBeanManager iocManager ) {
        this.iocManager = iocManager;
    }

    @PostConstruct
    public void init() {
        final Collection<IOCBeanDef<ClientResourceType>> availableTypes = iocManager.lookupBeans( ClientResourceType.class );

        for ( final IOCBeanDef<ClientResourceType> availableType : availableTypes ) {
            localResourceTypes.add( availableType.getInstance() );
        }

        sort( localResourceTypes, new Comparator<ClientResourceType>() {
            @Override
            public int compare( final ClientResourceType o1,
                                final ClientResourceType o2 ) {
                if ( o1.getPriority() < o2.getPriority() ) {
                    return 1;
                } else if ( o1.getPriority() > o2.getPriority() ) {
                    return -1;
                } else {
                    return 0;
                }
            }
        } );
    }

    @Override
    public Collection<ClientResourceType> getRegisteredTypes() {
        return unmodifiableList( localResourceTypes );
    }

    @Override
    public ClientResourceType resolve( final Path path ) {
        for ( final ClientResourceType resourceType : localResourceTypes ) {
            if ( resourceType.accept( path ) ) {
                return resourceType;
            }
        }
        return null;
    }

    @Override
    public String resolveWildcardPattern( final String shortName ) {
        for ( final ClientResourceType resourceType : localResourceTypes ) {
            if ( resourceType.getShortName().equalsIgnoreCase( shortName ) ) {
                return resourceType.getSimpleWildcardPattern();
            }
        }
        return shortName;
    }
}
