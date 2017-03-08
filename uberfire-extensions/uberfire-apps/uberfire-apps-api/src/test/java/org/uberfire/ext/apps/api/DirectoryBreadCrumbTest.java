/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.apps.api;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class DirectoryBreadCrumbTest {

    private static final String TOP_DIR_NAME = "top";
    private static final String MIDDLE_DIR_NAME = "middle";
    private static final String LOW_DIR_NAME = "low";
    private static final String OTHER_DIR_NAME = "other";

    @Test
    public void breadCrumbsOrderTest() {
        Directory topDir = new Directory(TOP_DIR_NAME,
                                         "",
                                         "",
                                         new HashMap<String, List<String>>());
        Directory middleDir = new Directory(MIDDLE_DIR_NAME,
                                            "",
                                            "",
                                            topDir);
        Directory lowDir = new Directory(LOW_DIR_NAME,
                                         "",
                                         "",
                                         middleDir);
        Directory otherDir = new Directory(OTHER_DIR_NAME,
                                           "",
                                           "",
                                           topDir);

        assertDirectoryBreadCrumbs(topDir,
                                   TOP_DIR_NAME);
        assertDirectoryBreadCrumbs(middleDir,
                                   TOP_DIR_NAME,
                                   MIDDLE_DIR_NAME);
        assertDirectoryBreadCrumbs(lowDir,
                                   TOP_DIR_NAME,
                                   MIDDLE_DIR_NAME,
                                   LOW_DIR_NAME);
        assertDirectoryBreadCrumbs(otherDir,
                                   TOP_DIR_NAME,
                                   OTHER_DIR_NAME);
    }

    private void assertDirectoryBreadCrumbs(Directory workingDir,
                                            String... expectedNames) {
        List<DirectoryBreadCrumb> breadCrumbs = DirectoryBreadCrumb.getBreadCrumbs(workingDir);
        int breadCrumbsSize = breadCrumbs.size();

        assertEquals(expectedNames.length,
                     breadCrumbsSize);

        for (int i = 0; i < breadCrumbsSize; i++) {
            assertEquals(expectedNames[i],
                         breadCrumbs.get(i).getName());
        }
    }
}
