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

package org.kie.workbench.common.services.backend.compiler.nio.impl.kie;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.nio.NIOKieMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.nio.decorators.kie.NIOKieAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.nio.decorators.kie.NIOKieJGITCompilerBeforeDecorator;
import org.kie.workbench.common.services.backend.compiler.nio.decorators.kie.NIOKieOutputLogAfterDecorator;

/***
 * Factory to create compilers based on NIO2 implementation with correct order of decorators to build Kie Projects
 * working with the kie takari plugin
 */
public class NIOKieMavenCompilerFactory {

    private static Map<String, NIOKieMavenCompiler> compilers = new ConcurrentHashMap<>();

    private NIOKieMavenCompilerFactory() {
    }

    /**
     * Provides a Maven compiler decorated with a Decorator Behaviour
     */
    public static NIOKieMavenCompiler getCompiler(KieDecorator decorator) {
        NIOKieMavenCompiler compiler = compilers.get(decorator.name());
        if (compiler == null) {
            compiler = createAndAddNewCompiler(decorator);
        }
        return compiler;
    }

    private static NIOKieMavenCompiler createAndAddNewCompiler(KieDecorator decorator) {
        NIOKieMavenCompiler compiler;
        switch (decorator) {
            case NONE:
                compiler = new NIOKieDefaultMavenCompiler();
                break;

            case KIE_AFTER:
                compiler = new NIOKieAfterDecorator(new NIOKieDefaultMavenCompiler());
                break;

            case KIE_AND_LOG_AFTER:
                compiler = new NIOKieAfterDecorator(new NIOKieOutputLogAfterDecorator(new NIOKieDefaultMavenCompiler()));
                break;

            case JGIT_BEFORE:
                compiler = new NIOKieJGITCompilerBeforeDecorator(new NIOKieDefaultMavenCompiler());
                break;

            case LOG_OUTPUT_AFTER:
                compiler = new NIOKieOutputLogAfterDecorator(new NIOKieDefaultMavenCompiler());
                break;

            case JGIT_BEFORE_AND_LOG_AFTER:
                compiler = new NIOKieJGITCompilerBeforeDecorator(new NIOKieOutputLogAfterDecorator(new NIOKieDefaultMavenCompiler()));
                break;

            case JGIT_BEFORE_AND_KIE_AND_LOG_AFTER:
                compiler = new NIOKieJGITCompilerBeforeDecorator(new NIOKieAfterDecorator(new NIOKieOutputLogAfterDecorator(new NIOKieDefaultMavenCompiler())));
                break;

            default:
                compiler = new NIOKieDefaultMavenCompiler();
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
