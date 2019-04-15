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

package org.kie.workbench.common.dmn.backend.editors.common;

import java.util.Optional;

import javax.inject.Inject;

import org.guvnor.common.services.project.model.Package;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.backend.editors.types.exceptions.DMNIncludeModelCouldNotBeCreatedException;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;

public class DMNIncludeModelFactory {

    private static final String DEFAULT_PACKAGE_NAME = "";

    private final DMNDiagramHelper diagramHelper;

    private final KieModuleService moduleService;

    @Inject
    public DMNIncludeModelFactory(final DMNDiagramHelper diagramHelper,
                                  final KieModuleService moduleService) {
        this.diagramHelper = diagramHelper;
        this.moduleService = moduleService;
    }

    public DMNIncludedModel create(final Path path) throws DMNIncludeModelCouldNotBeCreatedException {
        try {

            final String fileName = path.getFileName();
            final String modelPackage = getPackage(path);
            final String pathURI = path.toURI();
            final String namespace = diagramHelper.getNamespace(path);

            return new DMNIncludedModel(fileName, modelPackage, pathURI, namespace);
        } catch (final Exception e) {
            throw new DMNIncludeModelCouldNotBeCreatedException();
        }
    }

    private String getPackage(final Path path) {
        return Optional
                .ofNullable(moduleService.resolvePackage(path))
                .map(Package::getPackageName)
                .orElse(DEFAULT_PACKAGE_NAME);
    }
}
