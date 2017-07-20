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
package org.kie.workbench.common.services.backend.compiler.internalNIO.impl.kie;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.workbench.common.services.backend.compiler.configuration.Decorator;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.internalNIO.InternalNIOKieMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.internalNIO.decorators.kie.InternalNIOKieAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.internalNIO.decorators.kie.InternalNIOKieJGITCompilerBeforeDecorator;
import org.kie.workbench.common.services.backend.compiler.internalNIO.decorators.kie.InternalNIOKieOutputLogAfterDecorator;

/***
 * Factory to create compilers based on the Internal Nio implementation with correct order of decorators to build Kie Projects
 * working with the kie takari plugin
 */
public class InternalNIOKieMavenCompilerFactory {

    private static Map<String, InternalNIOKieMavenCompiler> compilers = new ConcurrentHashMap<>();

    private InternalNIOKieMavenCompilerFactory() {
    }

    /**
     * Provides a Maven compiler decorated with a Decorator Behaviour
     */
    public static InternalNIOKieMavenCompiler getCompiler(KieDecorator decorator) {
        InternalNIOKieMavenCompiler compiler = compilers.get(decorator.name());
        if (compiler == null) {
            compiler = createAndAddNewCompiler(decorator);
        }
        return compiler;
    }

    private static InternalNIOKieMavenCompiler createAndAddNewCompiler(KieDecorator decorator) {

        InternalNIOKieMavenCompiler compiler;
        switch (decorator) {
            case NONE:
                compiler = new InternalNIOKieDefaultMavenCompiler();
                break;

            case KIE_AFTER:
                compiler = new InternalNIOKieAfterDecorator(new InternalNIOKieDefaultMavenCompiler());
                break;

            case KIE_AND_LOG_AFTER:
                compiler = new InternalNIOKieAfterDecorator(new InternalNIOKieOutputLogAfterDecorator(new InternalNIOKieDefaultMavenCompiler()));
                break;

            case JGIT_BEFORE:
                compiler = new InternalNIOKieJGITCompilerBeforeDecorator(new InternalNIOKieDefaultMavenCompiler());
                break;

            case JGIT_BEFORE_AND_LOG_AFTER:
                compiler = new InternalNIOKieJGITCompilerBeforeDecorator(new InternalNIOKieOutputLogAfterDecorator(new InternalNIOKieDefaultMavenCompiler()));
                break;

            case JGIT_BEFORE_AND_KIE_AFTER:
                compiler = new InternalNIOKieJGITCompilerBeforeDecorator(new InternalNIOKieAfterDecorator(new InternalNIOKieDefaultMavenCompiler()));
                break;

            case LOG_OUTPUT_AFTER:
                compiler = new InternalNIOKieOutputLogAfterDecorator(new InternalNIOKieDefaultMavenCompiler());
                break;

            case JGIT_BEFORE_AND_KIE_AND_LOG_AFTER:
                compiler = new InternalNIOKieJGITCompilerBeforeDecorator(new InternalNIOKieAfterDecorator(new InternalNIOKieOutputLogAfterDecorator(new InternalNIOKieDefaultMavenCompiler())));
                break;

            default:
                compiler = new InternalNIOKieDefaultMavenCompiler();
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
