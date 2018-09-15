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
package org.kie.workbench.common.services.backend.compiler.service;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.uberfire.java.nio.file.Path;

/**
 * Define the Behaviour of a AppFormer Compiler Service
 */
public interface AFCompilerService {

    /************************************ Suitable for the Local Builds ***********************************************/

    /**
     * Run a mvn compile on the projectPath with mavenRepoPath specified, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder and maven repo changes
     * between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> build(Path projectPath, String mavenRepoPath, String settingXML);

    /**
     * Run a mvn compile on the projectPath with mavenRepoPath specified, overriding the content contained in the Map, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder and maven repo changes
     * between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> build(Path projectPath, String mavenRepoPath, String settingXML, Map<Path, InputStream> override);

    /**
     * Run a mvn compile on the projectPath with mavenRepoPath specified, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder and maven repo changes
     * between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> build(Path projectPath, String mavenRepoPath, String settingXML,
                                                    Boolean skipPrjDependenciesCreationList);

    /**
     * Run a mvn install on the projectPath, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder and maven repo changes
     * between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> buildAndInstall(Path projectPath, String mavenRepoPath, String settingXML);

    /**
     * Run a mvn install on the projectPath, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder and maven repo changes
     * between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> buildAndInstall(Path projectPath, String mavenRepoPath, String settingXML,
                                                              Boolean skipPrjDependenciesCreationList);

    /**
     * Run a mvn {args}, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder, maven repo and
     * maven args changes between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> buildSpecialized(Path projectPath, String mavenRepoPath, String[] args);

    /**
     * Run a mvn {args}, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder, maven repo and
     * maven args changes between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> buildSpecialized(Path projectPath,
                                                               String mavenRepoPath,
                                                               String[] args, Boolean skipPrjDependenciesCreationList);

    /************************************ Suitable for the REST Builds ************************************************/

    /**
     * When a HTTP call asks a build this method run a mvn compile on the projectPath with mavenRepoPath specified, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder and maven repo changes
     * between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> build(String projectPath, String mavenRepoPath, String settingXML);

    /**
     * When a HTTP call asks a build this method run a mvn compile on the projectPath with mavenRepoPath specified, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder and maven repo changes
     * between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> build(String projectPath, String mavenRepoPath, String settingXML, Boolean skipPrjDependenciesCreationList);

    /**
     * When a HTTP call asks a build this method run a mvn install on the projectPath, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder and maven repo changes
     * between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> buildAndInstall(String projectPath, String mavenRepoPath, String settingXML);

    /**
     * When a HTTP call asks a build this method run a mvn install on the projectPath, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder and maven repo changes
     * between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> buildAndInstall(String projectPath, String mavenRepoPath, String settingXML, Boolean skipPrjDependenciesCreationList);

    /**
     * When a HTTP call asks a build this method run a mvn {args}, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder, maven repo and
     * maven args changes between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> buildSpecialized(String projectPath, String mavenRepoPath, String settingXML, String[] args);

    /**
     * When a HTTP call asks a build this method run a mvn {args}, maven output provided in the CompilationResponse
     * a new CompilationRequest will be created at every invocation, useful if the project folder, maven repo and
     * maven args changes between compilation Requests
     */
    CompletableFuture<KieCompilationResponse> buildSpecialized(String projectPath, String mavenRepoPath,
                                                               String[] args, Boolean skipPrjDependenciesCreationList);
}
