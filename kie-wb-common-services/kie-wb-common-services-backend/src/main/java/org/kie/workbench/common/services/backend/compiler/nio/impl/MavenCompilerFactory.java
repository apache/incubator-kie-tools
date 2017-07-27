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

package org.kie.workbench.common.services.backend.compiler.nio.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.workbench.common.services.backend.compiler.configuration.Decorator;
import org.kie.workbench.common.services.backend.compiler.nio.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.nio.decorators.JGITCompilerBeforeDecorator;
import org.kie.workbench.common.services.backend.compiler.nio.decorators.OutputLogAfterDecorator;

/***
 * Factory to create compilers with correct order of decorators
 */
public class MavenCompilerFactory {

    private static Map<String, AFCompiler> compilers = new ConcurrentHashMap<>();

    private MavenCompilerFactory() {
    }

    /**
     * Provides a Maven compiler decorated with a Decorator Behaviour
     */
    public static AFCompiler getCompiler(Decorator decorator) {
        AFCompiler compiler = compilers.get(decorator.name());
        if (compiler == null) {
            compiler = createAndAddNewCompiler(decorator);
        }
        return compiler;
    }

    private static AFCompiler createAndAddNewCompiler(Decorator decorator) {
        AFCompiler<?> compiler;
        switch (decorator) {
            case NONE:
                compiler = new DefaultMavenCompiler();
                break;

            case JGIT_BEFORE:
                compiler = new JGITCompilerBeforeDecorator(new DefaultMavenCompiler());
                break;

            case LOG_OUTPUT_AFTER:
                compiler = new OutputLogAfterDecorator(new DefaultMavenCompiler());
                break;

            case JGIT_BEFORE_AND_LOG_AFTER:
                compiler = new JGITCompilerBeforeDecorator(new OutputLogAfterDecorator(new DefaultMavenCompiler()));
                break;

            default:
                compiler = new DefaultMavenCompiler();
        }
        compilers.put(Decorator.NONE.name(),
                      compiler);
        return compiler;
    }

    /**
     * Delete the compilers creating a new data structure
     */
    public static void deleteCompilers() {
        compilers = new ConcurrentHashMap<>();
    }

    /**
     * Clear the internal data structure
     */
    public static void clearCompilers() {
        compilers.clear();
    }
}
