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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jgit.merge.MergeResult;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.merge.ResolveMerger;
import org.eclipse.jgit.merge.ThreeWayMerger;
import org.eclipse.jgit.revwalk.RevCommit;
import org.uberfire.java.nio.fs.jgit.util.Git;
import org.uberfire.java.nio.fs.jgit.util.exceptions.GitException;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class ConflictBranchesChecker {

    private final Git git;
    private final String branchA;
    private final String branchB;

    public ConflictBranchesChecker(final Git git,
                                   final String branchA,
                                   final String branchB) {
        this.git = checkNotNull("git",
                                git);
        this.branchA = checkNotEmpty("branchA",
                                     branchA);
        this.branchB = checkNotEmpty("branchB",
                                     branchB);
    }

    public List<String> execute() {
        BranchUtil.existsBranch(this.git,
                                this.branchA);

        BranchUtil.existsBranch(this.git,
                                this.branchB);

        List<String> result = new ArrayList<>();

        try {
            final RevCommit commitA = git.getLastCommit(branchA);
            final RevCommit commitB = git.getLastCommit(branchB);

            final RevCommit commonAncestor = git.getCommonAncestorCommit(branchA,
                                                                         branchB);

            ThreeWayMerger merger = MergeStrategy.RECURSIVE.newMerger(git.getRepository(),
                                                                      true);
            merger.setBase(commonAncestor);

            boolean canMerge = merger.merge(commitA,
                                            commitB);

            if (!canMerge) {
                ResolveMerger resolveMerger = (ResolveMerger) merger;
                Map<String, MergeResult<?>> mergeResults = resolveMerger.getMergeResults();
                result.addAll(mergeResults.keySet()
                                      .stream()
                                      .sorted(String::compareToIgnoreCase)
                                      .collect(Collectors.toList()));
            }
        } catch (IOException e) {
            throw new GitException(
                    String.format("Error when checking for conflicts between branches %s and %s: %s",
                                  this.branchA, this.branchB, e));
        }

        return result;
    }
}
