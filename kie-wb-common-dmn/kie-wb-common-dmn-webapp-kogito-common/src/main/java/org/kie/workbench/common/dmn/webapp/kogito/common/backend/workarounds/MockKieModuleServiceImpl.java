/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.webapp.kogito.common.backend.workarounds;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.structure.repositories.Branch;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;

@Service
@ApplicationScoped
public class MockKieModuleServiceImpl implements KieModuleService {

    @Override
    public KieModule resolveModule(final Path resource) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public KieModule resolveModule(final Path resource,
                                   final boolean loadPOM) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public Module resolveParentModule(final Path resource) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public Module resolveToParentModule(final Path resource) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public Set<Package> resolvePackages(final Module module) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public Set<Package> resolvePackages(final Package pkg) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public Package resolveDefaultPackage(final Module module) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public Package resolveDefaultWorkspacePackage(final Module module) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public Package resolveParentPackage(final Package pkg) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public Path resolveDefaultPath(final Package pkg,
                                   final String resourceType) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public boolean isPom(final Path resource) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public Package resolvePackage(final Path resource) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public Set<Module> getAllModules(final Branch branch) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public KieModule newModule(final Path repositoryRoot,
                               final POM pom) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public KieModule newModule(final Path repositoryRoot,
                               final POM pom,
                               final DeploymentMode mode) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public Package newPackage(final Package pkg,
                              final String packageName) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public Path rename(final Path pathToPomXML,
                       final String newName,
                       final String comment) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public void delete(final Path pathToPomXML,
                       final String comment) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public void copy(final Path pathToPomXML,
                     final String newName,
                     final String comment) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }

    @Override
    public void reImport(final Path pathToPomXML) {
        throw new UnsupportedOperationException("Not available in Kogito");
    }
}
