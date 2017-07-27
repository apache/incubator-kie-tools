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
package org.kie.workbench.common.services.backend.compiler.nio.decorators;

import java.util.List;

import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.LogUtils;
import org.kie.workbench.common.services.backend.compiler.nio.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.nio.CompilationRequest;

/***
 * After decorator to read and store the maven output into a List<String> in the CompilationResponse
 */
public class OutputLogAfterDecorator<T extends CompilationResponse, C extends AFCompiler<T>> implements CompilerDecorator {

    private C compiler;

    public OutputLogAfterDecorator(C compiler) {
        this.compiler = compiler;
    }

    @Override
    public T compileSync(CompilationRequest req) {
        T res = compiler.compileSync(req);

        return compiler.buildDefaultCompilationResponse(res.isSuccessful(),
                                                        LogUtils.getOutput(req.getInfo().getPrjPath().toAbsolutePath().toString(),
                                                                           req.getKieCliRequest().getRequestUUID()));
    }

    @Override
    public T buildDefaultCompilationResponse(final Boolean value) {
        return compiler.buildDefaultCompilationResponse(value);
    }

    @Override
    public T buildDefaultCompilationResponse(final Boolean value,
                                             final List output) {
        return compiler.buildDefaultCompilationResponse(value);
    }
}
