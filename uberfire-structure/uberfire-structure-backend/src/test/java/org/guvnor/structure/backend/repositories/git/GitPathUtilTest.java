/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.backend.repositories.git;

import java.util.Optional;

import org.junit.Test;

import static org.junit.Assert.*;

public class GitPathUtilTest {

    @Test
    public void extractPresentBranchName() throws Exception {
        final Optional<String> result = GitPathUtil.extractBranch("git://master@space/repo/some/path");
        assertEquals(Optional.of("master"), result);
    }

    @Test
    public void extractBranchNameWithSlashes() throws Exception {
        final Optional<String> result = GitPathUtil.extractBranch("git://my/master/branch@space/repo/some/path");
        assertEquals(Optional.of("my/master/branch"), result);
    }

    @Test
    public void extractMissingBranchName() throws Exception {
        final Optional<String> result = GitPathUtil.extractBranch("git://space/repo/some/path");
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void extractLocalBranchNameFromRef() throws Exception {
        final Optional<String> result = GitPathUtil.extractBranchFromRef("refs/heads/master");
        assertEquals("master", result.get());
    }

    @Test
    public void extractLocalBranchNameWithSlashFromRef() throws Exception {
        final Optional<String> result = GitPathUtil.extractBranchFromRef("refs/heads/my/branch");
        assertEquals("my/branch", result.get());
    }

    @Test
    public void extractRemoteBranchNameFromRef() throws Exception {
        final Optional<String> result = GitPathUtil.extractBranchFromRef("refs/remotes/upstream/master");
        assertEquals("master", result.get());
    }

    @Test
    public void extractRemoteBranchNameWithSlashFromRef() throws Exception {
        final Optional<String> result = GitPathUtil.extractBranchFromRef("refs/remotes/upstream/my/branch");
        assertEquals("my/branch", result.get());
    }
}
