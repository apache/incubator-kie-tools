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

import java.io.IOException;

import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.uberfire.java.nio.fs.jgit.util.Git;

public class GetLastCommit {

    private final Git git;
    private final Ref ref;

    public GetLastCommit(final Git git,
                         final String branchName) {
        this(git,
             git.getRef(branchName));
    }

    public GetLastCommit(final Git git,
                         final Ref ref) {
        this.git = git;
        this.ref = ref;
    }

    public RevCommit execute() throws IOException {
        if (ref == null) {
            return null;
        }
        return git.resolveRevCommit(ref.getTarget().getObjectId());
    }
}