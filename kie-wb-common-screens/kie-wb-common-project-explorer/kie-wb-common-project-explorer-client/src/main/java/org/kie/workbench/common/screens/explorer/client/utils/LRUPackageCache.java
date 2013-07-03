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
import org.guvnor.common.services.project.model.Project;
import org.kie.commons.validation.PortablePreconditions;

/**
 * A client-side cache of Packages per project
 */
public class LRUPackageCache {

    private static final int MAX_ENTRIES = 10;

    private Map<Project, Collection<Package>> cache;

    public LRUPackageCache() {
        cache = new LinkedHashMap<Project, Collection<Package>>( MAX_ENTRIES + 1,
                                                                 0.75f,
                                                                 true ) {
            public boolean removeEldestEntry( Map.Entry eldest ) {
                return size() > MAX_ENTRIES;
            }
        };
    }

    public Collection<Package> getEntry( final Project project ) {
        PortablePreconditions.checkNotNull( "project",
                                            project );
        return cache.get( project );
    }

    public void setEntry( final Project project,
                          final Collection<Package> packages ) {
        PortablePreconditions.checkNotNull( "project",
                                            project );
        PortablePreconditions.checkNotNull( "packages",
                                            packages );
        cache.put( project,
                   packages );
    }

    public void invalidateCache() {
        this.cache.clear();
    }

    public void invalidateCache( final Project project ) {
        PortablePreconditions.checkNotNull( "project",
                                            project );
        this.cache.remove( project );
    }

    public Set<Project> getKeys() {
        return cache.keySet();
    }

}
