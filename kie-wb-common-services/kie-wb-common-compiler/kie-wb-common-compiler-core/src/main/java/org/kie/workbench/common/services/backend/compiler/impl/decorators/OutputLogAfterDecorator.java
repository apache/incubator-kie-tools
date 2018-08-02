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
package org.kie.workbench.common.services.backend.compiler.impl.decorators;

import java.util.Map;

import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationResponse;
import org.kie.workbench.common.services.backend.logback.OutputSharedMap;
import org.slf4j.MDC;

/***
 * After decorator to read and store the maven output as a List<String> in the CompilationResponse
 */
public class OutputLogAfterDecorator<T extends CompilationResponse, C extends AFCompiler<T>> implements CompilerDecorator {

    private C compiler;

    public OutputLogAfterDecorator(C compiler) {
        this.compiler = compiler;
    }

    //for test
    public C getCompiler() {
        return compiler;
    }

    @Override
    public T compile(CompilationRequest req) {
        T res = compiler.compile(req);
        return handleMavenOutput(req, res);
    }

    @Override
    public CompilationResponse compile(CompilationRequest req, Map override) {
        T res = (T) compiler.compile(req, override);
        return handleMavenOutput(req, res);
    }

    private T handleMavenOutput(CompilationRequest req, T res) {
        T t = (T) new DefaultCompilationResponse(res.isSuccessful(),
                                                 OutputSharedMap.getLog(req.getKieCliRequest().getRequestUUID()),
                                                 req.getInfo().getPrjPath(),
                                                 res.getDependencies());
        OutputSharedMap.removeLog(req.getKieCliRequest().getRequestUUID());
        MDC.clear();
        return t;
    }

    @Override
    public Boolean cleanInternalCache() {
        return compiler.cleanInternalCache();
    }
}
