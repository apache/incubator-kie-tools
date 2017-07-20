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
package org.kie.workbench.common.services.backend.compiler.impl;

import java.util.List;
import java.util.Optional;

import org.kie.workbench.common.services.backend.compiler.CompilationResponse;

/***
 * Default implementation of a basic (Non Kie) Compilation response,
 * it contains a boolean flag as a result of the build, an optional String error message,
 *  and an optional List of String with the maven output
 *
 */
public class DefaultCompilationResponse implements CompilationResponse {

    private Boolean successful;
    private Optional<String> errorMessage;
    private Optional<List<String>> mavenOutput;

    public DefaultCompilationResponse(Boolean successful) {
        this.successful = successful;
        this.errorMessage = Optional.empty();
        this.mavenOutput = Optional.empty();
    }

    public DefaultCompilationResponse(Boolean successful,
                                      List<String> mavenOutput) {
        this.successful = successful;
        this.errorMessage = Optional.empty();
        this.mavenOutput = Optional.ofNullable(mavenOutput);
    }

    public DefaultCompilationResponse(Boolean successful,
                                      String errorMessage,
                                      List<String> mavenOutput) {
        this.successful = successful;
        this.errorMessage = Optional.ofNullable(errorMessage);
        this.mavenOutput = Optional.ofNullable(mavenOutput);
    }

    public Boolean isSuccessful() {
        return successful;
    }

    public Optional<String> getErrorMessage() {
        return errorMessage;
    }

    public Optional<List<String>> getMavenOutput() {
        return mavenOutput;
    }
}
