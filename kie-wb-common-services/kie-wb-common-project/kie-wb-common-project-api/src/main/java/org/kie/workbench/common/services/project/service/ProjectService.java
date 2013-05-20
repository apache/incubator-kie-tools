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

package org.kie.workbench.common.services.project.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.services.project.service.model.POM;
import org.kie.workbench.common.services.project.service.model.ProjectImports;
import org.kie.workbench.common.services.shared.file.SupportsRead;
import org.kie.workbench.common.services.shared.file.SupportsUpdate;
import org.kie.workbench.common.services.workingset.client.model.WorkingSetSettings;
import org.uberfire.backend.vfs.Path;

/**
 *
 */
@Remote
public interface ProjectService extends SupportsRead<ProjectImports>,
                                        SupportsUpdate<ProjectImports> {

    public static final String DEFAULT_PKG = "defaultpkg";

    WorkingSetSettings loadWorkingSetConfig( final Path project );

    /**
     * Given a Resource path resolve it to the containing Project Path. A Project path is the folder containing pom.xml
     * @param resource
     * @return Path to the folder containing the Project's pom.xml file or null if the resource was not in a Project
     */
    Path resolveProject( final Path resource );

    /**
     * Given a Resource path resolve it to the containing Package Path. A Package path is the folder containing the resource.
     * The folder must be within a valid Project structure and at least reference /src/main/java, /src/main/resources,
     * src/test/java or src/test/resources (or deeper).
     * @param resource
     * @return Path to the folder containing the resource file or null if the resource is not in a Package.
     */
    Path resolvePackage( final Path resource );

    /**
     * Given a Resource path resolve it to the containing Source Package Path. A Source Package path is the folder
     * containing the resource. The folder must be within a valid Project structure and at least reference
     * /src/main/java or /src/main/resources (or deeper).
     * @param resource
     * @return Path to the Source folder containing the resource file or null if the resource is not in a Source package.
     */
    Path resolveSrcPackage( final Path resource );

    /**
     * Given a Resource path resolve it to the containing Test Package Path. A Test Package path is the folder
     * containing the resource. The folder must be within a valid Project structure and at least reference
     * /src/test/java or /src/test/resources (or deeper).
     * @param resource
     * @return Path to the Test folder containing the resource file or null if the resource is not in a Test package.
     */
    Path resolveTestPackage( final Path resource );

    /**
     * Given a Package path resolve it to a package name.
     * @param packagePath
     * @return Name of the package.
     */
    String resolvePackageName( final Path packagePath );

    /**
     * Path for the project pom.xml that the given resource belongs to.
     * @param resource to resource
     * @return Path to pom.xml for the given resource
     */
    Path resolvePathToPom( Path resource );

    /**
     * Path for the project project.imports that the given resource belongs to.
     * @param resource to resource
     * @return path to project.imports for the given resource
     */
    Path resolvePathToProjectImports( Path resource );

    /**
     * Return true if the file is the Project's pom.xml file
     * @param resource
     * @return
     */
    boolean isPom( Path resource );

    /**
     * Return true if the file is the Project's kmodule.xml file
     * @param resource
     * @return
     */
    boolean isKModule( Path resource );

    /**
     * Creates a new project to the given path.
     * @param activePath
     * @param name
     * @param pom
     * @param baseURL the base URL where the Guvnor is hosted in web container
     * @return
     */
    Path newProject( final Path activePath,
                     final String name,
                     final POM pom,
                     final String baseURL );

    Path newPackage( final Path contextPath,
                     final String packageName );

    Path newDirectory( final Path contextPath,
                       final String dirName );

}
