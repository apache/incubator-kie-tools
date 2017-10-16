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
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.cache.LRUDataModelOracleCache;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleIncrementalPayload;
import org.kie.workbench.common.services.datamodel.service.IncrementalDataModelService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.vfs.Path;

/**
 *
 */
@Service
@ApplicationScoped
public class IncrementalDataModelServiceImpl implements IncrementalDataModelService {

    private LRUDataModelOracleCache cachePackages;

    private KieProjectService projectService;

    @Inject
    public IncrementalDataModelServiceImpl(@Named("PackageDataModelOracleCache") final LRUDataModelOracleCache cachePackages,
                                           final KieProjectService projectService) {
        this.cachePackages = PortablePreconditions.checkNotNull("cachePackages",
                                                                cachePackages);
        this.projectService = PortablePreconditions.checkNotNull("projectService",
                                                                 projectService);
    }

    public IncrementalDataModelServiceImpl() {
    }

    @Override
    public PackageDataModelOracleIncrementalPayload getUpdates(final Path resourcePath,
                                                               final Imports imports,
                                                               final String factType) {
        PortablePreconditions.checkNotNull("resourcePath",
                                           resourcePath);
        PortablePreconditions.checkNotNull("imports",
                                           imports);
        PortablePreconditions.checkNotNull("factType",
                                           factType);

        final PackageDataModelOracleIncrementalPayload dataModel = new PackageDataModelOracleIncrementalPayload();

        try {
            //Check resource was within a Project structure
            final KieProject project = resolveProject(resourcePath);
            if (project == null) {
                return dataModel;
            }
            //Check resource was within a Package structure
            final Package pkg = resolvePackage(resourcePath);
            if (pkg == null) {
                return dataModel;
            }

            //Get the fully qualified class name of the fact type
            String fullyQualifiedClassName = factType;

            //Retrieve (or build) oracle and populate incremental content
            final PackageDataModelOracle oracle = cachePackages.assertPackageDataModelOracle(project,
                                                                                             pkg);

            // Check if the FactType is already known to the DataModelOracle, otherwise we need to find the FQCN
            if (oracle.getProjectModelFields().get(fullyQualifiedClassName) == null) {
                for (Import imp : imports.getImports()) {
                    if (imp.getType().endsWith(factType)) {
                        fullyQualifiedClassName = imp.getType();
                        break;
                    }
                }
            }

            //If the FactType isn't recognised try using the Package Name
            if (oracle.getProjectModelFields().get(fullyQualifiedClassName) == null) {
                fullyQualifiedClassName = pkg.getPackageName() + "." + factType;
            }

            //If the FactType still isn't recognised return an empty payload
            if (oracle.getProjectModelFields().get(fullyQualifiedClassName) == null) {
                return dataModel;
            }

            DataModelOracleUtilities.populateDataModel(oracle,
                                                       dataModel,
                                                       fullyQualifiedClassName);
            return dataModel;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    private KieProject resolveProject(final Path resourcePath) {
        return projectService.resolveProject(resourcePath);
    }

    private Package resolvePackage(final Path resourcePath) {
        return projectService.resolvePackage(resourcePath);
    }
}
