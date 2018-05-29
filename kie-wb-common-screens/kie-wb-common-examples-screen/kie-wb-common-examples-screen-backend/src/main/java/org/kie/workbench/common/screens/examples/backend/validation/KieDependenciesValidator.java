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

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.maven.model.Dependency;
import org.guvnor.common.services.project.model.Dependencies;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.kie.workbench.common.screens.examples.model.ExampleProjectError;
import org.kie.workbench.common.screens.examples.validation.ExampleProjectValidator;
import org.uberfire.backend.vfs.Path;

import static java.util.stream.Collectors.joining;

/**
 * Validate if Project POM contains all mandatory dependencies.
 */
@ApplicationScoped
public class KieDependenciesValidator extends ExampleProjectValidator {

    public static final String TEST_SCOPE = "test";
    private MandatoryDependencies mandatoryDependencies;
    private POMService pomService;

    public KieDependenciesValidator() {

    }

    @Inject
    public KieDependenciesValidator(POMService pomService,
                                    MandatoryDependencies mandatoryDependencies) {
        this.pomService = pomService;
        this.mandatoryDependencies = mandatoryDependencies;
    }

    @Override
    protected Optional<ExampleProjectError> getError(Path projectPath) {

        POM pom = this.getPom(this.pomService,
                              projectPath);

        Dependencies dependencies = pom.getDependencies();

        List<Dependency> missingDependencies = this.mandatoryDependencies
                .getDependencies()
                .stream()
                .filter(mandatoryDependency -> {
                    Optional<org.guvnor.common.services.project.model.Dependency> dependency = Optional.ofNullable(dependencies.get(new GAV(buildGAV(mandatoryDependency))));
                    return dependency.map(d -> this.isScopeInvalid(mandatoryDependency,
                                                                   d)).orElse(true);
                }).collect(Collectors.toList());

        if (missingDependencies.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(new ExampleProjectError(KieDependenciesValidator.class.getCanonicalName(),
                                                       missingDependencies.stream()
                                                               .map(dependency -> this.buildGAV(dependency))
                                                               .collect(joining(", "))));
        }
    }

    private boolean isScopeInvalid(Dependency mandatoryDependency,
                                   org.guvnor.common.services.project.model.Dependency dependency) {
        if (TEST_SCOPE.equalsIgnoreCase(mandatoryDependency.getScope())) {
            return false;
        } else {
            return !mandatoryDependency.getScope().equalsIgnoreCase(dependency.getScope());
        }
    }

    protected String buildGAV(Dependency dependency) {
        return MessageFormat.format("{0}:{1}:{2}:{3}",
                                    dependency.getGroupId(),
                                    dependency.getArtifactId(),
                                    dependency.getVersion(),
                                    dependency.getScope());
    }
}
