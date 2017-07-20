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
import org.kie.workbench.common.services.backend.compiler.nio.NIOMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.nio.decorators.NIOJGITCompilerBeforeDecorator;
import org.kie.workbench.common.services.backend.compiler.nio.decorators.NIOOutputLogAfterDecorator;

/***
 * Factory to create compilers based on the NIO2 implementation with correct order of decorators
 */
public class NIOMavenCompilerFactory {

    private static Map<String, NIOMavenCompiler> compilers = new ConcurrentHashMap<>();

    private NIOMavenCompilerFactory() {
    }

    /**
     * Provides a Maven compiler decorated with a Decorator Behaviour
     */
    public static NIOMavenCompiler getCompiler(Decorator decorator) {
        NIOMavenCompiler compiler = compilers.get(decorator.name());
        if (compiler == null) {
            compiler = createAndAddNewCompiler(decorator);
        }
        return compiler;
    }

    private static NIOMavenCompiler createAndAddNewCompiler(Decorator decorator) {
        NIOMavenCompiler compiler;
        switch (decorator) {
            case NONE:
                compiler = new NIODefaultMavenCompiler();
                break;

            case JGIT_BEFORE:
                compiler = new NIOJGITCompilerBeforeDecorator(new NIODefaultMavenCompiler());
                break;

            case LOG_OUTPUT_AFTER:
                compiler = new NIOOutputLogAfterDecorator(new NIODefaultMavenCompiler());
                break;

            case JGIT_BEFORE_AND_LOG_AFTER:
                compiler = new NIOJGITCompilerBeforeDecorator(new NIOOutputLogAfterDecorator(new NIODefaultMavenCompiler()));
                break;

            default:
                compiler = new NIODefaultMavenCompiler();
        }
        compilers.put(decorator.name(),
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
