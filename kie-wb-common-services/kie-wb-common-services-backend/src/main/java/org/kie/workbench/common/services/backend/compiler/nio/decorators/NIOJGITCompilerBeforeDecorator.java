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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RebaseResult;
import org.kie.workbench.common.services.backend.compiler.CompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.nio.NIOCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.NIOMavenCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Before decorator to update a git repo before the build on a Project with NIO2
 */
public class NIOJGITCompilerBeforeDecorator implements NIOCompilerDecorator {

    private static final Logger logger = LoggerFactory.getLogger(NIOJGITCompilerBeforeDecorator.class);
    private final String COMPILED_EXTENSION = ".class";
    private final String REMOTE = "origin";
    private final String REMOTE_BRANCH = "master";
    private NIOMavenCompiler compiler;

    public NIOJGITCompilerBeforeDecorator(NIOMavenCompiler compiler) {
        this.compiler = compiler;
    }

    @Override
    public CompilationResponse compileSync(NIOCompilationRequest req) {
        if (applyBefore(req)) {
            CompilationResponse res = compiler.compileSync(req);
            return res;
        } else {
            return new DefaultCompilationResponse(Boolean.FALSE);
        }
    }

    private Boolean applyBefore(NIOCompilationRequest req) {
        Boolean result = Boolean.FALSE;
        if (req.getInfo().getGitRepo().isPresent()) {
            try {
                Git git = req.getInfo().getGitRepo().get();
                PullCommand pc = git.pull().setRemote(REMOTE).setRebase(Boolean.TRUE);
                PullResult pullRes = pc.call();
                RebaseResult rr = pullRes.getRebaseResult();

                if (rr.getStatus().equals(RebaseResult.Status.UP_TO_DATE) || rr.getStatus().equals(RebaseResult.Status.FAST_FORWARD)) {
                    result = Boolean.TRUE;
                }
                if (rr.getStatus().equals(RebaseResult.Status.UNCOMMITTED_CHANGES)) {
                    PullResult pr = git.pull().call();
                    if (pr.isSuccessful()) {
                        result = Boolean.TRUE;
                    } else {
                        result = Boolean.FALSE;
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            return result;
        }
        return result;
    }
}
