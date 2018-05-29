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

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.kie.workbench.common.screens.examples.model.ExampleProjectError;
import org.kie.workbench.common.screens.examples.validation.ExampleProjectValidator;
import org.uberfire.backend.vfs.Path;

/**
 * Validates if Project POM contains org.kie:kie-maven-plugin. That plugin is mandatory
 */
@ApplicationScoped
public class KieMavenPluginValidator extends ExampleProjectValidator {

    protected static final String KIE_VERSION_PROPERTIES = "kie.properties";

    protected static final String GROUP_ID = "kie.groupId";
    protected static final String ARTIFACT_ID = "kie.artifactId";
    protected static final String KIE_DEPENDENCY = "kie.dependency";

    private POMService pomService;
    protected String kiePluginExample;
    protected String kieArtifactId;
    protected String kieGroupId;

    public KieMavenPluginValidator() {
    }

    @Inject
    public KieMavenPluginValidator(POMService pomService) {
        this.pomService = pomService;
    }

    @PostConstruct
    public void initialize() {
        this.loadKieProperties();
    }

    private void loadKieProperties() {
        try {
            Properties props = new Properties();
            props.load(CheckModulesValidator.class.getClassLoader().getResourceAsStream(KIE_VERSION_PROPERTIES));
            this.kiePluginExample = props.getProperty(KIE_DEPENDENCY);
            this.kieArtifactId = props.getProperty(ARTIFACT_ID);
            this.kieGroupId = props.getProperty(GROUP_ID);
        } catch (IOException e) {
            throw new RuntimeException("Can't load kie.properties",
                                       e);
        }
    }

    @Override
    protected Optional<ExampleProjectError> getError(Path projectPath) {
        POM pom = getPom(this.pomService,
                         projectPath);

        boolean containsPlugin = pom.getBuild() != null &&
                pom.getBuild()
                        .getPlugins()
                        .stream()
                        .anyMatch(plugin -> this.kieArtifactId.equalsIgnoreCase(plugin.getArtifactId()) &&
                                this.kieGroupId.equalsIgnoreCase(plugin.getGroupId()));

        if (containsPlugin) {
            return Optional.empty();
        } else {
            return Optional.of(new ExampleProjectError(KieMavenPluginValidator.class.getCanonicalName(),
                                                       this.kiePluginExample));
        }
    }
}
