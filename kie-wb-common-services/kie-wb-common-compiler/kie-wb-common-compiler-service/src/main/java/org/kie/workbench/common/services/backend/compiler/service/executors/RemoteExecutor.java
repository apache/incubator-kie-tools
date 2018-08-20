/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.service.executors;

import java.util.concurrent.CompletableFuture;

import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;

/***
 * This interface provides behaviour and use simple objects from HTTP world to run build requested from a remote client
 */
public interface RemoteExecutor {

    /************************************ Suitable for the REST Builds ************************************************/

    /**
     * Run a mvn compile on the projectPath with mavenRepo specified, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder and maven repo changes
     * between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> build(String projectPath, String mavenRepo);

    /**
     * Run a mvn compile on the projectPath with mavenRepo specified, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder and maven repo changes
     * between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> build(String projectPath, String mavenRepo, Boolean skipPrjDependenciesCreationList);

    /**
     * Run a mvn install on the projectPath, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder and maven repo changes
     * between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> buildAndInstall(String projectPath, String mavenRepo);

    /**
     * Run a mvn install on the projectPath, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder and maven repo changes
     * between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> buildAndInstall(String projectPath, String mavenRepo, Boolean skipPrjDependenciesCreationList);

    /**
     * Run a mvn {args}, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder, maven repo and
     * maven args changes between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> buildSpecialized(String projectPath, String mavenRepo, String[] args);

    /**
     * Run a mvn {args}, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder, maven repo and
     * maven args changes between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> buildSpecialized(String projectPath, String mavenRepo,
                                                               String[] args, Boolean skipPrjDependenciesCreationList);
}
