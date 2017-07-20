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

package org.kie.workbench.common.services.backend.compiler.nio.decorators.kie;

import org.kie.workbench.common.services.backend.compiler.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultKieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.JGitUtils;
import org.kie.workbench.common.services.backend.compiler.nio.NIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.NIOKieMavenCompiler;

/***
 * Before decorator to update a git repo before the build on a Kie Project with NIO2
 */
public class NIOKieJGITCompilerBeforeDecorator implements NIOKieCompilerDecorator {

    private NIOKieMavenCompiler compiler;

    public NIOKieJGITCompilerBeforeDecorator(NIOKieMavenCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public KieCompilationResponse compileSync(NIOCompilationRequest req) {
        if (JGitUtils.applyBefore(req.getInfo().getGitRepo())) {
            KieCompilationResponse res = compiler.compileSync(req);
            return res;
        } else {
            return new DefaultKieCompilationResponse(Boolean.FALSE);
        }
    }
}
