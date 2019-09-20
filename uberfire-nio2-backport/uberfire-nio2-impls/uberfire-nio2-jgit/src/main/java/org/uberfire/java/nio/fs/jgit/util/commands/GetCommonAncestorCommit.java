/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.java.nio.fs.jgit.util.commands;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class GetCommonAncestorCommit {

    private final Git git;
    private final RevCommit commitA;
    private final RevCommit commitB;

    public GetCommonAncestorCommit(final Git git,
                                   final RevCommit commitA,
                                   final RevCommit commitB) {
        this.git = checkNotNull("git",
                                git);
        this.commitA = checkNotNull("commitA",
                                    commitA);
        this.commitB = checkNotNull("commitB",
                                    commitB);
    }

    public RevCommit execute() {
        try (final RevWalk revWalk = new RevWalk(git.getRepository())) {
            final RevCommit validatedCommitA = revWalk.lookupCommit(this.commitA);
            final RevCommit validatedCommitB = revWalk.lookupCommit(this.commitB);

            revWalk.setRevFilter(RevFilter.MERGE_BASE);
            revWalk.markStart(validatedCommitA);
            revWalk.markStart(validatedCommitB);
            return revWalk.next();
        } catch (Exception e) {
            throw new GitException("Error when trying to get common ancestor",
                                   e);
        }
    }
}
