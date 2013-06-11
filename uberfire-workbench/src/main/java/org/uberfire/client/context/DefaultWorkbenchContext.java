/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.client.context;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.GroupChangeEvent;
import org.uberfire.workbench.events.PathChangeEvent;
import org.uberfire.workbench.events.RepositoryChangeEvent;

/**
 * Container for the context of the Workbench
 */
@ApplicationScoped
public class DefaultWorkbenchContext implements WorkbenchContext {

    private Group activeGroup;
    private Repository activeRepository;
    private Path activePath;

    public void setActiveGroup( @Observes final GroupChangeEvent event ) {
        final Group activeGroup = event.getGroup();
        setActiveGroup( activeGroup );
        setActiveRepository( (Repository) null );
        setActivePath( (Path) null );
    }

    public void setActiveRepository( @Observes final RepositoryChangeEvent event ) {
        final Repository activeRepository = event.getRepository();
        setActiveRepository( activeRepository );
        setActivePath( (Path) null );
    }

    public void setActivePath( @Observes final PathChangeEvent event ) {
        final Path activePath = event.getPath();
        setActivePath( activePath );
    }

    @Override
    public void setActiveGroup( final Group activeGroup ) {
        this.activeGroup = activeGroup;
    }

    @Override
    public Group getActiveGroup() {
        return this.activeGroup;
    }

    @Override
    public void setActiveRepository( final Repository activeRepository ) {
        this.activeRepository = activeRepository;
    }

    @Override
    public Repository getActiveRepository() {
        return this.activeRepository;
    }

    public void setActivePath( final Path activePath ) {
        this.activePath = activePath;
    }

    public Path getActivePath() {
        return this.activePath;
    }

}
