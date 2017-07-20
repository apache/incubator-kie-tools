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
package org.kie.workbench.common.services.backend.compiler.impl;

import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.RebaseResult;
import org.kie.workbench.common.services.backend.compiler.internalNIO.decorators.InternalNIOJGITCompilerBeforeDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Class used to provides JGit functionalities to the JGIT compiler decorators
 */
public class JGitUtils {

    private static final Logger logger = LoggerFactory.getLogger(InternalNIOJGITCompilerBeforeDecorator.class);
    private static String REMOTE = "origin";
    private final String COMPILED_EXTENSION = ".class";
    private final String REMOTE_BRANCH = "master";

    public static Boolean applyBefore(Optional<Git> repo) {
        Boolean result = Boolean.FALSE;
        if (repo.isPresent()) {
            try {
                Git git = repo.get();
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
