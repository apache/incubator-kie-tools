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

package org.kie.workbench.common.screens.explorer.service;

import java.util.Collection;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.kie.workbench.common.screens.explorer.model.ResourceContext;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;

/**
 * Service definition for Explorer editor
 */
@Remote
public interface ExplorerService {

    Collection<OrganizationalUnit> getOrganizationalUnits();

    Collection<Repository> getRepositories( final OrganizationalUnit organizationalUnit );

    Collection<Project> getProjects( final Repository repository );

    Collection<Package> getPackages( final Project project );

    Collection<FolderItem> getItems( final Package pkg );

    FolderListing getFolderListing( final Path path );

    ResourceContext resolveResourceContext( final Path path );

}
