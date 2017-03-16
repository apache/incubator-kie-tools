/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.builder.service;

import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.builder.core.TypeSourceResolver;

/**
 * Interface for providing access to the information related with a Project build.
 */
public interface BuildInfo {

    /**
     * @return a KieModule for the underlying project. Eventual internal errors are not shadowed.
     * @see KieModule
     * @see KieBuilder#getKieModule()
     */
    KieModule getKieModule();

    /**
     * @return a KieModule for the underlying project. Eventual internal errors are ignored and the KieModule is still returned.
     * @see KieModule
     * @see InternalKieBuilder#getKieModuleIgnoringErrors()
     */
    KieModule getKieModuleIgnoringErrors();

    /**
     * @return the KieModuleMetaData for the KieModule corresponding to the underlying project. Eventual errors are
     * ignored and the KieModuleMetaData is still returned.
     * @see KieModuleMetaData
     * @see KieModuleMetaData.Factory
     */
    KieModuleMetaData getKieModuleMetaDataIgnoringErrors();

    /**
     * @return a TypeResourceResolver based on the kieModuleMetaData for the underlying project.
     */
    TypeSourceResolver getTypeSourceResolver( KieModuleMetaData kieModuleMetaData );

    /**
     * @return a KieContainer for the underlying project.
     * @see KieContainer
     */
    KieContainer getKieContainer();

}