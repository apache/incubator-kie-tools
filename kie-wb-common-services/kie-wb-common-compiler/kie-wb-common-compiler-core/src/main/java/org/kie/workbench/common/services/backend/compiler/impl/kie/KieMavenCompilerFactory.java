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
package org.kie.workbench.common.services.backend.compiler.impl.kie;

import java.util.Set;

import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.configuration.KieDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.BaseMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.ClasspathDepsAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.JGITCompilerBeforeDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.KieAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.OutputLogAfterDecorator;

/***
 * Factory to create compilers with correct order of decorators to build Kie Projects
 * working with the kie takari plugin
 */
public class KieMavenCompilerFactory {

    private KieMavenCompilerFactory() {
    }

    /**
     * Provides a Maven compiler decorated with a Decorator Behaviour
     */
    public static <T extends CompilationResponse> AFCompiler<T> getCompiler(Set<KieDecorator> decorators) {
        return createAndAddNewCompiler(decorators);
    }

    private static <T extends CompilationResponse> AFCompiler<T> createAndAddNewCompiler(Set<KieDecorator> decorators) {

        boolean enableIncremental = decorators.contains(KieDecorator.ENABLE_INCREMENTAL_BUILD);
        boolean enableLogging = decorators.contains(KieDecorator.ENABLE_LOGGING);

        //Order of the construction of the decorators matters, DO not change the order.
        AFCompiler compiler = new BaseMavenCompiler(enableIncremental, enableLogging);

        if (decorators.contains(KieDecorator.STORE_BUILD_CLASSPATH)) {
            compiler = new ClasspathDepsAfterDecorator(compiler);
        }
        if (decorators.contains(KieDecorator.ENABLE_LOGGING)) {
            compiler = new OutputLogAfterDecorator(compiler);
        }
        if (decorators.contains(KieDecorator.STORE_KIE_OBJECTS)) {
            compiler = new KieAfterDecorator(compiler);
        }
        if (decorators.contains(KieDecorator.UPDATE_JGIT_BEFORE_BUILD)) {
            compiler = new JGITCompilerBeforeDecorator(compiler);
        }
        return compiler;
    }
}