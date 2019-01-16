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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.uberfire.commons.data.Pair;
import org.uberfire.java.nio.fs.jgit.util.GitImpl;

public class SyncRemote {

    private final GitImpl git;
    private final Pair<String, String> remote;

    public SyncRemote(final GitImpl git,
                      final Pair<String, String> remote) {
        this.git = git;
        this.remote = remote;
    }

    public Optional execute() throws InvalidRemoteException {
        try {
            final List<Ref> branches = git._branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
            final Set<String> remoteBranches = new HashSet<>();
            final Set<String> localBranches = new HashSet<>();
            fillBranches(branches, remoteBranches, localBranches);

            /*
             * We filter out HEAD below because otherwise it appears
             * as a branch in the UI importing repositories.
             *
             * We may need to revisit this in the future when we support
             * mirror repositories.
             */

            for (final String localBranch : localBranches) {
                if (localBranch.equals(Constants.HEAD)) {
                    continue;
                }
                if (remoteBranches.contains(localBranch)) {
                    try {
                        git._branchCreate()
                        .setName(localBranch)
                        .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
                        .setStartPoint(remote.getK1() + "/" + localBranch)
                        .setForce(true)
                        .call();
                    } catch (Throwable t) {
                        throw new RuntimeException("Error creating branch [" + localBranch + "].");
                    }
                }
            }

            remoteBranches.removeAll(localBranches);

            for (final String branch : remoteBranches) {
                if (branch.equals(Constants.HEAD)) {
                    continue;
                }
                try {
                    git._branchCreate()
                    .setName(branch)
                    .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
                    .setStartPoint(remote.getK1() + "/" + branch)
                    .setForce(true)
                    .call();
                } catch (Throwable t) {
                    throw new RuntimeException("Error creating branch [" + branch + "].");
                }
            }
            return null;
        } catch (final InvalidRemoteException e) {
            throw e;
        } catch (final RuntimeException re) {
            throw re;
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    void fillBranches(final List<Ref> branches,
                      final Collection<String> remoteBranches,
                      final Collection<String> localBranches) {
        for (final Ref branch : branches) {
            final String branchFullName = branch.getName();
            final String remotePrefix = "refs/remotes/" + remote.getK1() + "/";
            final String localPrefix = "refs/heads/";

            if (branchFullName.startsWith(remotePrefix)) {
                remoteBranches.add(branchFullName.replaceFirst(remotePrefix, ""));
            } else if (branchFullName.startsWith(localPrefix)) {
                localBranches.add(branchFullName.replaceFirst(localPrefix, ""));
            } else {
                localBranches.add(branchFullName.substring(branchFullName.lastIndexOf("/") + 1));
            }
        }
    }
}
