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

import org.guvnor.common.services.project.model.Package;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.workbench.common.screens.explorer.model.FolderItem;

/**
 * A client-side cache of Packages per project
 */
public class LRUItemCache {

    private static final int MAX_ENTRIES = 10;

    private Map<Package, Collection<FolderItem>> cache;

    public LRUItemCache() {
        cache = new LinkedHashMap<Package, Collection<FolderItem>>( MAX_ENTRIES + 1,
                                                                    0.75f,
                                                                    true ) {
            public boolean removeEldestEntry( Map.Entry eldest ) {
                return size() > MAX_ENTRIES;
            }
        };
    }

    public Collection<FolderItem> getEntry( final Package pkg ) {
        PortablePreconditions.checkNotNull( "pkg",
                                            pkg );
        return cache.get( pkg );
    }

    public void setEntry( final Package pkg,
                          final Collection<FolderItem> folderItems ) {
        PortablePreconditions.checkNotNull( "pkg",
                                            pkg );
        PortablePreconditions.checkNotNull( "folderItems",
                                            folderItems );
        cache.put( pkg,
                   folderItems );
    }

    public void invalidateCache() {
        this.cache.clear();
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
