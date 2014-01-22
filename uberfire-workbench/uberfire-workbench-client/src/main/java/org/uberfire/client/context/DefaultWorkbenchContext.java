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

import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.context.WorkbenchContext;
import org.uberfire.workbench.events.OrganizationalUnitChangeEvent;
import org.uberfire.workbench.events.PanelFocusEvent;
import org.uberfire.workbench.events.PathChangeEvent;
import org.uberfire.workbench.events.RepositoryChangeEvent;
import org.uberfire.workbench.model.PanelDefinition;

/**
 * Container for the context of the Workbench
 */
@ApplicationScoped
public class DefaultWorkbenchContext implements WorkbenchContext {

    private OrganizationalUnit activeOrganizationalUnit;
    private Repository activeRepository;
    private Path activePath;
    private PanelDefinition activePanel;

    public void setActiveOrganizationalUnit( @Observes final OrganizationalUnitChangeEvent event ) {
        final OrganizationalUnit activeOrganizationalUnit = event.getOrganizationalUnit();
        setActiveOrganizationalUnit( activeOrganizationalUnit );
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

    private void setActiveOrganizationalUnit( final OrganizationalUnit activeOrganizationalUnit ) {
        this.activeOrganizationalUnit = activeOrganizationalUnit;
    }

    @Override
    public OrganizationalUnit getActiveOrganizationalUnit() {
        return this.activeOrganizationalUnit;
    }

    private void setActiveRepository( final Repository activeRepository ) {
        this.activeRepository = activeRepository;
    }

    @Override
    public Repository getActiveRepository() {
        return this.activeRepository;
    }

    private void setActivePath( final Path activePath ) {
        this.activePath = activePath;
    }

    @Override
    public Path getActivePath() {
        return this.activePath;
    }

    @Override
    public PanelDefinition getActivePanel() {
        return activePanel;
    }

    private void setActivePanel( @Observes final PanelFocusEvent panelFocusEvent ) {
        this.activePanel = panelFocusEvent.getPanel();
    }
}
