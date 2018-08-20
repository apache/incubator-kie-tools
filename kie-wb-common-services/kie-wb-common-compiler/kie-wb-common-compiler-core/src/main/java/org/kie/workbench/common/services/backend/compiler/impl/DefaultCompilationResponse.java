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
package org.kie.workbench.common.services.backend.compiler.impl;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.classloader.CompilerClassloaderUtils;
import org.uberfire.java.nio.file.Path;

/***
 * Default implementation of a basic (Non Kie) Compilation response,
 * it contains a boolean flag as a result of the build, an optional String error message,
 * and an optional List of String with the maven output
 */
public class DefaultCompilationResponse implements CompilationResponse,
                                                   Serializable {

    private Boolean successful;
    private List<String> mavenOutput;
    private Path workingDir;
    private String requestUUID;

    private List<String> projectDependencies = Collections.emptyList();
    private List<URI> projectDependenciesAsURI = Collections.emptyList();
    private List<URL> projectDependenciesAsURL = Collections.emptyList();

    private List<String> targetContent = Collections.emptyList();
    private List<URI> targetContentAsURI = Collections.emptyList();
    private List<URL> targetContentAsURL = Collections.emptyList();

    public DefaultCompilationResponse(final Boolean successful,
                                      final List<String> mavenOutput,
                                      final Path workingDir,
                                      final String requestUUID) {

        this(successful,mavenOutput,workingDir, Collections.emptyList(), Collections.emptyList(), requestUUID);
    }

    public DefaultCompilationResponse(final Boolean successful,
                                      final List<String> mavenOutput,
                                      final Path workingDir,
                                      final List<String> projectDependencies,
                                      final String requestUUID) {
        this(successful,mavenOutput,workingDir, Collections.emptyList(), projectDependencies, requestUUID);
    }

    public DefaultCompilationResponse(final Boolean successful,
                                      final List<String> mavenOutput,
                                      final Path workingDir,
                                      final List<String> targetContent,
                                      final List<String> projectDependencies,
                                      final String requestUUID) {
        this.successful = successful;
        this.mavenOutput = nullToEmpty(mavenOutput);

        this.workingDir = workingDir;
        this.targetContent = nullToEmpty(targetContent);
        this.projectDependencies = nullToEmpty(projectDependencies);
        this.requestUUID = requestUUID;
    }

    private <T> List<T> nullToEmpty(List<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(list);
    }

    public Boolean isSuccessful() {
        return successful;
    }

    public List<String> getMavenOutput() {
        return mavenOutput;
    }

    public Optional<Path> getWorkingDir() {
        return Optional.ofNullable(workingDir);
    }

    @Override
    public List<String> getTargetContent() {
        return targetContent;
    }

    @Override
    public List<URI> getTargetContentAsURI() {
        if (targetContentAsURI.isEmpty()) {
            targetContentAsURI = getRawAsURIs(targetContent);
        }
        return targetContentAsURI;
    }

    @Override
    public List<URL> getTargetContentAsURL() {
        if (targetContentAsURL.isEmpty()) {
            targetContentAsURL = getRawAsURLs(targetContent);
        }
        return targetContentAsURL;
    }

    @Override
    public List<String> getDependencies() {
        return projectDependencies;
    }

    @Override
    public List<URI> getDependenciesAsURI() {
        if (projectDependenciesAsURI.isEmpty()) {
            projectDependenciesAsURI = getProjectDependenciesAsURIs();
        }
        return projectDependenciesAsURI;
    }

    private List<URI> getProjectDependenciesAsURIs() {
        if (projectDependencies != null && !projectDependencies.isEmpty()) {
            return CompilerClassloaderUtils.readAllDepsAsUris(projectDependencies);
        }
        return Collections.emptyList();
    }

    @Override
    public List<URL> getDependenciesAsURL() {
        if (projectDependenciesAsURL.isEmpty()) {
            projectDependenciesAsURL = getProjectDependenciesAsURLs();
        }
        return projectDependenciesAsURL;
    }

    public String getRequestUUID() {
        return requestUUID;
    }

    private List<URL> getProjectDependenciesAsURLs() {
        if (projectDependencies != null && !projectDependencies.isEmpty()) {
            return CompilerClassloaderUtils.readAllDepsAsUrls(projectDependencies);
        }
        return Collections.emptyList();
    }

    private List<URL> getRawAsURLs(final List<String> targetContent) {
        if (targetContent != null && !targetContent.isEmpty()) {
            return CompilerClassloaderUtils.processScannedFilesAsURLs(targetContent);
        }
        return Collections.emptyList();
    }

    private List<URI> getRawAsURIs(final List<String> targetContent) {
        if (targetContent != null && !targetContent.isEmpty()) {
            return CompilerClassloaderUtils.processScannedFilesAsURIs(targetContent);
        }
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultCompilationResponse{");
        sb.append("successful=").append(successful);
        sb.append(", mavenOutput=").append(mavenOutput);
        sb.append(", workingDir=").append(workingDir);
        sb.append(", requestUUID='").append(requestUUID).append('\'');
        sb.append(", projectDependencies=").append(projectDependencies);
        sb.append(", projectDependenciesAsURI=").append(projectDependenciesAsURI);
        sb.append(", projectDependenciesAsURL=").append(projectDependenciesAsURL);
        sb.append(", targetContent=").append(targetContent);
        sb.append(", targetContentAsURI=").append(targetContentAsURI);
        sb.append(", targetContentAsURL=").append(targetContentAsURL);
        sb.append('}');
        return sb.toString();
    }
}