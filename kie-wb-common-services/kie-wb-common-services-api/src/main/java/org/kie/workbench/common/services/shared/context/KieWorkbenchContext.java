/*
 * Copyright 2013 JBoss Inc
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
package org.kie.workbench.common.services.shared.context;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.GroupChangeEvent;
import org.uberfire.workbench.events.PathChangeEvent;
import org.uberfire.workbench.events.RepositoryChangeEvent;

/**
 * A specialized implementation that also has Project and Package scope
 */
@ApplicationScoped
public class KieWorkbenchContext {

    private Group activeGroup;
    private Repository activeRepository;
    private Project activeProject;
    private Package activePackage;

    public void setActiveGroup( @Observes final GroupChangeEvent event ) {
        final Group activeGroup = event.getGroup();
        setActiveGroup( activeGroup );
        setActiveRepository( (Repository) null );
        setActiveProject( (Project) null );
        setActivePackage( (Package) null );
    }

    public void setActiveRepository( @Observes final RepositoryChangeEvent event ) {
        final Repository activeRepository = event.getRepository();
        setActiveRepository( activeRepository );
        setActiveProject( (Project) null );
        setActivePackage( (Package) null );
    }

    public void setActiveProject( @Observes final ProjectChangeEvent event ) {
        final Project activeProject = event.getProject();
        setActiveProject( activeProject );
        setActivePackage( (Package) null );
    }

    public void setActivePackage( @Observes final PackageChangeEvent event ) {
        final Package activePackage = event.getPackage();
        setActivePackage( activePackage );
    }

    public void setActivePath( @Observes final PathChangeEvent event ) {
        final Path activePath = event.getPath();
    }

    public void setActiveGroup( final Group activeGroup ) {
        this.activeGroup = activeGroup;
    }

    public Group getActiveGroup() {
        return this.activeGroup;
    }

    public void setActiveRepository( final Repository activeRepository ) {
        this.activeRepository = activeRepository;
    }

    public Repository getActiveRepository() {
        return this.activeRepository;
    }

    public Project getActiveProject() {
        return this.activeProject;
    }

    public void setActiveProject( final Project activeProject ) {
        this.activeProject = activeProject;
    }

    public Package getActivePackage() {
        return this.activePackage;
    }

    public void setActivePackage( final Package activePackage ) {
        this.activePackage = activePackage;
    }

}
