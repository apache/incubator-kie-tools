/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.services.shared.project;

import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;

@Portable
public class KieProject
        extends Project {

    private Path kmoduleXMLPath;
    private Path importsPath;

    public KieProject() {
        //For Errai-marshalling
    }

    public KieProject(final Path rootPath,
            final Path pomXMLPath,
            final Path kmoduleXMLPath,
            final Path importsPath,
            final String projectName) {
        super(rootPath, pomXMLPath, projectName);
        this.kmoduleXMLPath = PortablePreconditions.checkNotNull("kmoduleXMLPath",
                kmoduleXMLPath);
        this.importsPath = PortablePreconditions.checkNotNull("importsPath",
                importsPath);
    }

    public Path getKModuleXMLPath() {
        return this.kmoduleXMLPath;
    }

    public Path getImportsPath() {
        return this.importsPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KieProject)) {
            return false;
        }

        KieProject project = (KieProject) o;

        if (!rootPath.equals(project.rootPath)) {
            return false;
        }
        if (!pomXMLPath.equals(project.pomXMLPath)) {
            return false;
        }
        if (!kmoduleXMLPath.equals(project.kmoduleXMLPath)) {
            return false;
        }
        if (!importsPath.equals(project.importsPath)) {
            return false;
        }
        if (!projectName.equals(project.projectName)) {
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
        result = 31 * result + projectName.hashCode();
        return result;
    }
}
