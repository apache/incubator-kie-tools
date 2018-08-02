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
import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.uberfire.java.nio.file.Path;

/**
 * Wrapper of the result of a compilation
 */
public interface CompilationResponse {

    Boolean isSuccessful();

    /**
     * Provides Maven output
     */
    List<String> getMavenOutput();

    /**
     * Provides the Path of the working directory
     */
    Optional<Path> getWorkingDir();

    /**
     * Provides the List of project dependencies from target folders as List of String
     * @return
     */
    List<String> getDependencies();

    /**
     * Provides the list of all dependencies used by the project, included transitive
     */
    List<URI> getDependenciesAsURI();

    /**
     * Provides the list of all dependencies used by the project, included transitive
     */
    List<URL> getDependenciesAsURL();

    /**
     * Provides the List of project dependencies from target folders as List of String
     * @return
     */
    List<String> getTargetContent();

    /**
     * Provides the list of all dependencies used by the project, included transitive
     */
    List<URI> getTargetContentAsURI();

    /**
     * Provides the list of all dependencies used by the project, included transitive
     */
    List<URL> getTargetContentAsURL();
}
