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

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class GetCommit {

    private final Git git;
    private final String commitId;

    public GetCommit(final Git git,
                     final String commitId) {
        this.git = checkNotNull("git",
                                git);
        this.commitId = checkNotEmpty("commitId",
                                      commitId);
    }

    public RevCommit execute() {
        final Repository repository = git.getRepository();

        try (final RevWalk revWalk = new RevWalk(repository)) {
            final ObjectId id = repository.resolve(this.commitId);
            return id != null ? revWalk.parseCommit(id) : null;
        } catch (Exception e) {
            throw new GitException("Error when trying to get commit", e);
        }
    }
}
