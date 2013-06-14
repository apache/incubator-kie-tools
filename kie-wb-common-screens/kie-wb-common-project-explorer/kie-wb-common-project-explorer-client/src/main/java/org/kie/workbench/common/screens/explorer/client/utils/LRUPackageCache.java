package org.kie.workbench.common.screens.explorer.client.utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.enterprise.event.Observes;

import org.kie.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.shared.context.Package;
import org.kie.workbench.common.services.shared.context.PackageAddedEvent;
import org.kie.workbench.common.services.shared.context.Project;
import org.uberfire.backend.vfs.Path;

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

    public void onPackageAdded( @Observes final PackageAddedEvent event ) {
        final Path projectRoot = event.getPackage().getProjectRootPath();
        Project projectToInvalidate = null;
        for ( Project project : getKeys() ) {
            if ( project.getRootPath().equals( projectRoot ) ) {
                projectToInvalidate = project;
            }
        }
        if ( projectToInvalidate != null ) {
            invalidateCache( projectToInvalidate );
        }
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

    public void invalidateCache( final Project project ) {
        PortablePreconditions.checkNotNull( "project",
                                            project );
        this.cache.remove( project );
    }

    public Set<Project> getKeys() {
        return cache.keySet();
    }

}
