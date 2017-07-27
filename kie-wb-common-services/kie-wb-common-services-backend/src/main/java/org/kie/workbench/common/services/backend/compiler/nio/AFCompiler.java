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
package org.kie.workbench.common.services.backend.compiler.nio;

import java.util.List;

import org.kie.workbench.common.services.backend.compiler.CompilationResponse;

/***
 * Define the behaviour of a NIO compiler
 */
public interface AFCompiler<T extends CompilationResponse> {

    /**
     * Compile a project starting from the main POM in a sync way
     */
    T compileSync(final CompilationRequest req);

    T buildDefaultCompilationResponse(final Boolean value);

    T buildDefaultCompilationResponse(final Boolean successful,
                                      final List<String> output);
}
