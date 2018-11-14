/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.common.services.project.backend.server.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PathUtilTest {

    private PathUtil pathUtil;

    @Before
    public void setup() {
        pathUtil = new PathUtil();
    }

    @Test
    public void stripGitProtocolAndBranch() throws Exception {
        final String result = pathUtil.stripProtocolAndBranch("git://master@space/repo/some/path");
        assertEquals("space/repo/some/path", result);
    }

    @Test
    public void stripDefaultProtocolAndBranch() throws Exception {
        final String result = pathUtil.stripProtocolAndBranch("default://master@space/repo/some/path");
        assertEquals("space/repo/some/path", result);
    }

    @Test
    public void stripProtocolMissingBranch() throws Exception {
        final String result = pathUtil.stripProtocolAndBranch("default://space/repo/some/path");
        assertEquals("space/repo/some/path", result);
    }

    @Test
    public void stripRepoNameAndSpace() throws Exception {
        final String result = pathUtil.stripRepoNameAndSpace("space/repo/some/path");
        assertEquals("some/path", result);
    }

    @Test
    public void replaceBranchName() {
        final String newUri = pathUtil.replaceBranch("new-branch", "git://current-branch@space/repo/some/path");
        assertEquals("git://new-branch@space/repo/some/path", newUri);
    }

    @Test
    public void replaceMissingBranchName() {
        final String newUri = pathUtil.replaceBranch("new-branch", "git://space/repo/some/path");
        assertEquals("git://space/repo/some/path", newUri);
    }

    @Test
    public void replaceBranchNameWith$() {
        final String newUri = pathUtil.replaceBranch("new$branch", "git://current-branch@space/repo/some/path");
        assertEquals("git://new$branch@space/repo/some/path", newUri);
    }

}
