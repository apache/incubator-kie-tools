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

import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import org.apache.maven.model.Dependency;
import org.kie.workbench.common.services.backend.pom.PomJsonReader;

@ApplicationScoped
public class MandatoryDependencies {

    private static final String JSON_POM_MANDATORY_DEPS = "pom-mandatory.json";
    private List<org.apache.maven.model.Dependency> dependencies;

    public MandatoryDependencies() {
        PomJsonReader mandatoryReader = new PomJsonReader(PomJsonReader.class
                                                                  .getClassLoader()
                                                                  .getResourceAsStream(JSON_POM_MANDATORY_DEPS));

        dependencies = mandatoryReader.readDeps().getDependencies();
    }

    public List<Dependency> getDependencies() {
        return this.dependencies;
    }
}
