/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.jgit.util.commands;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.fs.jgit.util.GitImpl;

public class DeleteBranch {

    private final GitImpl git;
    private final Ref branch;

    public DeleteBranch(final GitImpl git,
                        final Ref branch) {
        this.git = git;
        this.branch = branch;
    }

    public void execute() {
        try {
            git._branchDelete().setBranchNames(branch.getName()).setForce(true).call();
        } catch (final GitAPIException e) {
            throw new IOException(e);
        }
    }
}
