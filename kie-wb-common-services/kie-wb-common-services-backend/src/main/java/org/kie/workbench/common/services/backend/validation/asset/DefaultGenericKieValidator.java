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
package org.kie.workbench.common.services.backend.validation.asset;

import java.io.InputStream;
import java.util.List;
import javax.inject.Inject;

import org.guvnor.common.services.backend.validation.GenericValidator;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.uberfire.backend.vfs.Path;

/**
 * Validator capable of validating generic Kie assets (i.e those that are handled by KieBuilder)
 */
public class DefaultGenericKieValidator implements GenericValidator {

    private BuildService buildService;

    private KieProjectService projectService;

    public DefaultGenericKieValidator() {
    }

    @Inject
    public DefaultGenericKieValidator( final KieProjectService projectService,
                                       final BuildService buildService ) {
        this.projectService = projectService;
        this.buildService = buildService;
    }

    @Override
    public List<ValidationMessage> validate( final Path resourcePath,
                                             final InputStream inputStream ) {

        return validator().validate( resourcePath, inputStream );
    }

    private Validator validator() {
        return new Validator( projectService, buildService );
    }
}
