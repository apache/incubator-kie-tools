/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jgit.lib.Ref;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.commons.data.Pair;
import org.uberfire.java.nio.fs.jgit.util.GitImpl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class SyncRemoteTest {

    private SyncRemote syncRemote;

    @Before
    public void setup() {
        syncRemote = new SyncRemote(mock(GitImpl.class), new Pair<>("upstream", "b"));
    }

    @Test
    public void fillBranchesTest() {
        final List<Ref> branches = Arrays.asList(createBranch("refs/heads/local/branch1"),
                                                 createBranch("refs/heads/localBranch2"),
                                                 createBranch("refs/remotes/upstream/remote/branch1"),
                                                 createBranch("refs/remotes/upstream/remoteBranch2"));

        final List<String> remoteBranches = new ArrayList<>();
        final List<String> localBranches = new ArrayList<>();

        syncRemote.fillBranches(branches, remoteBranches, localBranches);

        assertEquals(2, remoteBranches.size());
        assertEquals("remote/branch1", remoteBranches.get(0));
        assertEquals("remoteBranch2", remoteBranches.get(1));

        assertEquals(2, localBranches.size());
        assertEquals("local/branch1", localBranches.get(0));
        assertEquals("localBranch2", localBranches.get(1));
    }

    private Ref createBranch(String branchName) {
        final Ref branch = mock(Ref.class);
        doReturn(branchName).when(branch).getName();

        return branch;
    }
}
