/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.kie.workbench.common.screens.examples.backend.validation;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.kie.workbench.common.screens.examples.model.ExampleProjectError;
import org.kie.workbench.common.screens.examples.validation.ImportProjectValidator;
import org.uberfire.backend.vfs.Path;

/**
 * Validates if Project POM contains any module. No modules should be found to be a valid project.
 */
@ApplicationScoped
public class CheckModulesValidator extends ImportProjectValidator {

    private POMService pomService;

    public CheckModulesValidator() {
    }

    @Inject
    public CheckModulesValidator(POMService pomService) {
        this.pomService = pomService;
    }

    @Override
    protected Optional<ExampleProjectError> getError(Path projectPath) {

        POM pom = this.getPom(pomService,
                              projectPath);

        if (pom.getModules() == null || pom.getModules().isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new ExampleProjectError(CheckModulesValidator.class.getCanonicalName(),
                                                       ""));
        }
    }
}
