/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.drools.core.rule.KieModuleMetaInfo;
import org.kie.api.builder.KieModule;

/**
 * Compilation response with benefits of Kie
 */
public interface KieCompilationResponse extends CompilationResponse {

    /**
     * Provides the list of all dependencies used by the project, included transitive
     */
    Optional<List<URI>> getProjectDependencies();

    /**
     * Provides a KieModuleMetaInfo if a kie maven plugin is used in the project
     */
    Optional<KieModuleMetaInfo> getKieModuleMetaInfo();

    /**
     * Provides a KieModule if a kie maven plugin is used in the project
     */
    Optional<KieModule> getKieModule();
}
