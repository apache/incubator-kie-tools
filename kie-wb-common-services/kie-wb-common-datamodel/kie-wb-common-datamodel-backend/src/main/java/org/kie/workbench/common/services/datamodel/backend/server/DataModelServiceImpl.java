/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodel.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.model.Package;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.commons.oracle.ModuleDataModelOracleImpl;
import org.kie.soup.project.datamodel.commons.oracle.PackageDataModelOracleImpl;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.cache.LRUDataModelOracleCache;
import org.kie.workbench.common.services.datamodel.backend.server.cache.LRUModuleDataModelOracleCache;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class DataModelServiceImpl
        implements DataModelService {

    private LRUDataModelOracleCache cachePackages;

    private LRUModuleDataModelOracleCache cacheModules;

    private KieModuleService moduleService;

    @Inject
    public DataModelServiceImpl(final @Named("PackageDataModelOracleCache") LRUDataModelOracleCache cachePackages,
                                final @Named("ModuleDataModelOracleCache") LRUModuleDataModelOracleCache cacheModules,
                                final KieModuleService moduleService) {
        this.cachePackages = cachePackages;
        this.cacheModules = cacheModules;
        this.moduleService = moduleService;
    }

    @Override
    public PackageDataModelOracle getDataModel(final Path resourcePath) {
        try {
            PortablePreconditions.checkNotNull("resourcePath",
                                               resourcePath);
            final KieModule module = resolveModule(resourcePath);
            final Package pkg = resolvePackage(resourcePath);

            //Resource was not within a Module structure
            if (module == null) {
                return new PackageDataModelOracleImpl();
            }

            //Retrieve (or build) oracle
            final PackageDataModelOracle oracle = cachePackages.assertPackageDataModelOracle(module,
                                                                                             pkg);
            return oracle;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public ModuleDataModelOracle getModuleDataModel(final Path resourcePath) {
        try {
            PortablePreconditions.checkNotNull("resourcePath",
                                               resourcePath);
            final KieModule module = resolveModule(resourcePath);

            //Resource was not within a Module structure
            if (module == null) {
                return new ModuleDataModelOracleImpl();
            }

            //Retrieve (or build) oracle
            final ModuleDataModelOracle oracle = cacheModules.assertModuleDataModelOracle(module);
            return oracle;
        } catch (Exception e) {
            e.printStackTrace();
            throw ExceptionUtilities.handleException(e);
        }
    }

    private KieModule resolveModule(final Path resourcePath) {
        return moduleService.resolveModule(resourcePath);
    }

    private Package resolvePackage(final Path resourcePath) {
        return moduleService.resolvePackage(resourcePath);
    }
}
