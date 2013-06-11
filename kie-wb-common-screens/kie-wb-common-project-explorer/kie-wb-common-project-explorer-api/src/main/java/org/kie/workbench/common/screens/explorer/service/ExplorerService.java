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

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.explorer.model.Item;
import org.kie.workbench.common.screens.explorer.model.Package;
import org.kie.workbench.common.screens.explorer.model.Project;
import org.kie.workbench.common.screens.explorer.model.ProjectPackage;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.vfs.Path;

/**
 * Service definition for Explorer editor
 */
@Remote
public interface ExplorerService {

    Collection<Group> getGroups();

    Collection<Repository> getRepositories( final Group group );

    Collection<Project> getProjects( final Repository repository );

    Collection<Package> getPackages( final Project project );

    Collection<Item> getItems( final Package pkg );

    ProjectPackage resolveProjectPackage( final Path path );

}
