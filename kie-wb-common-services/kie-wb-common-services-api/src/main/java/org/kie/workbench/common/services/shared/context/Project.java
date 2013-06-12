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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.backend.vfs.Path;

/**
 * An item representing a project
 */
@Portable
public class Project {

    private Path rootPath;
    private Path pomXMLPath;
    private Path kmoduleXMLPath;
    private Path importsPath;
    private String title;

    public Project() {
        //For Errai-marshalling
    }

    public Project( final Path rootPath,
                    final Path pomXMLPath,
                    final Path kmoduleXMLPath,
                    final Path importsPath,
                    final String title ) {
        this.rootPath = PortablePreconditions.checkNotNull( "rootPath",
                                                            rootPath );
        this.pomXMLPath = PortablePreconditions.checkNotNull( "pomXMLPath",
                                                              pomXMLPath );
        this.kmoduleXMLPath = PortablePreconditions.checkNotNull( "kmoduleXMLPath",
                                                                  kmoduleXMLPath );
        this.importsPath = PortablePreconditions.checkNotNull( "importsPath",
                                                               importsPath );
        this.title = PortablePreconditions.checkNotNull( "title",
                                                         title );
    }

    public Path getRootPath() {
        return this.rootPath;
    }

    public Path getPomXMLPath() {
        return this.pomXMLPath;
    }

    public Path getKModuleXMLPath() {
        return this.kmoduleXMLPath;
    }

    public Path getImportsPath() {
        return this.importsPath;
    }

    public String getTitle() {
        return this.title;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Project ) ) {
            return false;
        }

        Project project = (Project) o;

        if ( !rootPath.equals( project.rootPath ) ) {
            return false;
        }
        if ( !pomXMLPath.equals( project.pomXMLPath ) ) {
            return false;
        }
        if ( !kmoduleXMLPath.equals( project.kmoduleXMLPath ) ) {
            return false;
        }
        if ( !importsPath.equals( project.importsPath ) ) {
            return false;
        }
        if ( !title.equals( project.title ) ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = rootPath.hashCode();
        result = 31 * result + pomXMLPath.hashCode();
        result = 31 * result + kmoduleXMLPath.hashCode();
        result = 31 * result + importsPath.hashCode();
        result = 31 * result + title.hashCode();
        return result;
    }

}
