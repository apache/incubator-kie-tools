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

package org.kie.workbench.common.services.backend.compiler.internalNIO.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.workbench.common.services.backend.compiler.configuration.Decorator;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.internalNIO.decorators.InternalNIOJGITCompilerBeforeDecorator;
import org.kie.workbench.common.services.backend.compiler.internalNIO.decorators.InternalNIOOutputLogAfterDecorator;

/***
 * Factory to create compilers based on the Internal Nio implementation with correct order of decorators
 */
public class InternalNIOMavenCompilerFactory {

    private static Map<String, InternalNIOMavenCompiler> compilers = new ConcurrentHashMap<>();

    private InternalNIOMavenCompilerFactory() {
    }

    /**
     * Provides a Maven compiler decorated with a Decorator Behaviour
     */
    public static InternalNIOMavenCompiler getCompiler(Decorator decorator) {
        InternalNIOMavenCompiler compiler = compilers.get(decorator.name());
        if (compiler == null) {
            compiler = createAndAddNewCompiler(decorator);
        }
        return compiler;
    }

    private static InternalNIOMavenCompiler createAndAddNewCompiler(Decorator decorator) {
        InternalNIOMavenCompiler compiler;
        switch (decorator) {
            case NONE:
                compiler = new InternalNIODefaultMavenCompiler();
                break;

            case JGIT_BEFORE:
                compiler = new InternalNIOJGITCompilerBeforeDecorator(new InternalNIODefaultMavenCompiler());
                break;

            case LOG_OUTPUT_AFTER:
                compiler = new InternalNIOOutputLogAfterDecorator(new InternalNIODefaultMavenCompiler());
                break;

            case JGIT_BEFORE_AND_LOG_AFTER:
                compiler = new InternalNIOJGITCompilerBeforeDecorator(new InternalNIOOutputLogAfterDecorator(new InternalNIODefaultMavenCompiler()));
                break;

            default:
                compiler = new InternalNIODefaultMavenCompiler();
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
