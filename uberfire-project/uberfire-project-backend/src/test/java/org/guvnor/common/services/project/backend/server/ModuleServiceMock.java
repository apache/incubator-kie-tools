/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.guvnor.common.services.project.backend.server;

import java.util.Set;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.structure.repositories.Branch;
import org.uberfire.backend.vfs.Path;

public class ModuleServiceMock
        implements ModuleService<MockModule> {

    @Override
    public MockModule resolveModule(Path resource) {
        return null;
    }

    @Override
    public MockModule resolveModule(Path resource, boolean loadPOM) {
        return null;
    }

    @Override
    public Module resolveParentModule(Path resource) {
        return null;
    }

    @Override
    public Module resolveToParentModule(Path resource) {
        return null;
    }

    @Override
    public Set<Package> resolvePackages(Module module) {
        return null;
    }

    @Override
    public Set<Package> resolvePackages(Package pkg) {
        return null;
    }

    @Override
    public Package resolveDefaultPackage(Module module) {
        return null;
    }

    @Override
    public Package resolveDefaultWorkspacePackage(Module module) {
        return null;
    }

    @Override
    public Package resolveParentPackage(Package pkg) {
        return null;
    }

    @Override
    public Path resolveDefaultPath(Package pkg, String resourceType) {
        return null;
    }

    @Override
    public boolean isPom(Path resource) {
        return false;
    }

    @Override
    public Package resolvePackage(Path resource) {
        return null;
    }

    @Override
    public Set<Module> getAllModules(Branch branch) {
        return null;
    }

    @Override
    public MockModule newModule(Path repositoryRoot, POM pom) {
        return null;
    }

    @Override
    public MockModule newModule(Path repositoryRoot, POM pom, DeploymentMode mode) {
        return null;
    }

    @Override
    public Package newPackage(Package pkg, String packageName) {
        return null;
    }

    @Override
    public Path rename(Path pathToPomXML, String newName, String comment) {
        return null;
    }

    @Override
    public void delete(Path pathToPomXML, String comment) {

    }

    @Override
    public void copy(Path pathToPomXML, String newName, String comment) {

    }

    @Override
    public void reImport(Path pathToPomXML) {

    }

    @Override
    public void createModuleDirectories(Path repositoryRoot) {

    }
}
