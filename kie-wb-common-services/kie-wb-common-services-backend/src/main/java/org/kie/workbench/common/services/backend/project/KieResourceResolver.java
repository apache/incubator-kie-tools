/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.project;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.ModuleResourcePathResolver;
import org.guvnor.common.services.project.backend.server.ResourceResolver;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Files;

import static org.guvnor.common.services.project.utils.ModuleResourcePaths.POM_PATH;
import static org.kie.workbench.common.services.backend.project.KieModuleResourcePaths.KMODULE_PATH;
import static org.kie.workbench.common.services.backend.project.KieModuleResourcePaths.PACKAGE_NAME_WHITE_LIST;
import static org.kie.workbench.common.services.backend.project.KieModuleResourcePaths.PROJECT_IMPORTS_PATH;
import static org.kie.workbench.common.services.backend.project.KieModuleResourcePaths.PROJECT_REPOSITORIES_PATH;

public class KieResourceResolver
        extends ResourceResolver<KieModule> {

    private KModuleService kModuleService;

    public KieResourceResolver() {

    }

    @Inject
    public KieResourceResolver(final @Named("ioStrategy") IOService ioService,
                               final POMService pomService,
                               final CommentedOptionFactory commentedOptionFactory,
                               final KModuleService kModuleService,
                               final Instance<ModuleResourcePathResolver> resourcePathResolversInstance) {
        super(ioService,
              pomService,
              commentedOptionFactory,
              resourcePathResolversInstance);
        this.kModuleService = kModuleService;
    }

    @Override
    public KieModule resolveModule(Path resource, boolean loadPOM) {
        try {
            //Null resource paths cannot resolve to a Module
            if (resource == null) {
                return null;
            }

            //Check if resource is the module root
            org.uberfire.java.nio.file.Path path = Paths.convert(resource).normalize();

            //A module root is the folder containing the pom.xml file. This will be the parent of the "src" folder
            if (Files.isRegularFile(path)) {
                path = path.getParent();
            }
            while (!hasPom(path)) {
                path = path.getParent();
                if (path == null) {
                    return null;
                }
            }

            if (!hasKModule(path)) {
                return null;
            }

            if (loadPOM) {
                return makeModule(path);
            } else {
                return simpleModuleInstance(path);
            }
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    protected KieModule makeModule(final org.uberfire.java.nio.file.Path nioModuleRootPath) {
        try {

            final KieModule module = simpleModuleInstance(nioModuleRootPath);
            final POM pom = pomService.load(module.getPomXMLPath());
            module.setPom(pom);

            return module;

        } catch (final Exception e) {
            return null;
        }
    }

    @Override
    public org.guvnor.common.services.project.model.Package resolvePackage(final Path resource) {
        try {
            //Null resource paths cannot resolve to a Module
            if (resource == null) {
                return null;
            }

            //If Path is not within a Module we cannot resolve a package
            final Module module = resolveModule(resource, false);
            if (module == null) {
                return null;
            }

            //pom.xml and kmodule.xml are not inside packages
            if (isPom(resource) || kModuleService.isKModule(resource)) {
                return null;
            }

            return makePackage(module,
                               resource);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public KieModule simpleModuleInstance(final org.uberfire.java.nio.file.Path nioModuleRootPath) {
        final Path moduleRootPath = Paths.convert(nioModuleRootPath);
        final Path pomXMLPath = Paths.convert(nioModuleRootPath.resolve(POM_PATH));

        return new KieModule(moduleRootPath,
                             pomXMLPath,
                             Paths.convert(nioModuleRootPath.resolve(KMODULE_PATH)),
                             Paths.convert(nioModuleRootPath.resolve(PROJECT_IMPORTS_PATH)),
                             Paths.convert(nioModuleRootPath.resolve(PROJECT_REPOSITORIES_PATH)),
                             Paths.convert(nioModuleRootPath.resolve(PACKAGE_NAME_WHITE_LIST)));
    }

    protected boolean hasKModule(final org.uberfire.java.nio.file.Path path) {
        final org.uberfire.java.nio.file.Path kmodulePath = path.resolve(KMODULE_PATH);
        return Files.exists(kmodulePath);
    }
}
