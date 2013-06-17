/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.workbench.common.screens.explorer.model.Item;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.shared.context.Package;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;

/**
 * A client-side cache of Packages per project
 */
public class LRUItemCache {

    private static final int MAX_ENTRIES = 10;

    private Map<Package, Collection<Item>> cache;

    @Inject
    private Caller<ProjectService> projectService;

    public LRUItemCache() {
        cache = new LinkedHashMap<Package, Collection<Item>>( MAX_ENTRIES + 1,
                                                              0.75f,
                                                              true ) {
            public boolean removeEldestEntry( Map.Entry eldest ) {
                return size() > MAX_ENTRIES;
            }
        };
    }

    // Invalidate the corresponding Package cache entry when a Resource has been added
    public void onResourceAdded( @Observes final ResourceAddedEvent event ) {
        final Path resource = event.getPath();
        handleResourceChangeEvent( resource );
    }

    // Invalidate the corresponding Package cache entry when a Resource has been deleted
    public void onResourceDeleted( @Observes final ResourceDeletedEvent event ) {
        final Path resource = event.getPath();
        handleResourceChangeEvent( resource );
    }

    // Invalidate the corresponding Package cache entry when a Resource has been copied
    public void onResourceCopied( @Observes final ResourceCopiedEvent event ) {
        final Path resource = event.getDestinationPath();
        handleResourceChangeEvent( resource );
    }

    // Invalidate the corresponding Package cache entry when a Resource has been renamed
    public void onResourceRenamed( @Observes final ResourceRenamedEvent event ) {
        final Path resource = event.getDestinationPath();
        handleResourceChangeEvent( resource );
    }

    private void handleResourceChangeEvent( final Path resource ) {
        if ( resource == null ) {
            return;
        }
        projectService.call( new RemoteCallback<Package>() {

            @Override
            public void callback( final Package resourcePackage ) {
                Package packageToInvalidate = null;
                for ( Package pkg : getKeys() ) {
                    if ( pkg.equals( resourcePackage ) ) {
                        packageToInvalidate = pkg;
                    }
                }
                if ( packageToInvalidate != null ) {
                    invalidateCache( packageToInvalidate );
                }
            }
        } ).resolvePackage( resource );
    }

    public Collection<Item> getEntry( final Package pkg ) {
        PortablePreconditions.checkNotNull( "pkg",
                                            pkg );
        return cache.get( pkg );
    }

    public void setEntry( final Package pkg,
                          final Collection<Item> items ) {
        PortablePreconditions.checkNotNull( "pkg",
                                            pkg );
        PortablePreconditions.checkNotNull( "items",
                                            items );
        cache.put( pkg,
                   items );
    }

    public void invalidateCache( final Package pkg ) {
        PortablePreconditions.checkNotNull( "pkg",
                                            pkg );
        this.cache.remove( pkg );
    }

    public Set<Package> getKeys() {
        return cache.keySet();
    }

}
