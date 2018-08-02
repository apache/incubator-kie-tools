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

import java.io.InputStream;
import java.util.Map;

import org.uberfire.java.nio.file.Path;

/***
 * Define the behaviour of a Compiler
 */
public interface AFCompiler<T extends CompilationResponse> {

    /**
     * Compile a project starting from the main POM
     */
    T compile(final CompilationRequest req);

    /**
     * Compile a project overriding or creating the elements in the Map and then revert this changes
     */
    T compile(final CompilationRequest req,
              final Map<Path, InputStream> override);

    Boolean cleanInternalCache();
}
