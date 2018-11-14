/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.backend.server.spaces;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SpacesAPIImplTest {

    SpacesAPIImpl spaces;

    @Before
    public void setup() {
        spaces = new SpacesAPIImpl();
    }

    @Test
    public void resolveFileSystemURITest() {

        assertEquals("default://system/system",
                     spaces.resolveFileSystemURI(SpacesAPI.Scheme.DEFAULT,
                                                 SpacesAPI.DEFAULT_SPACE,
                                                 "system").toString());
    }

    @Test
    public void resolveSpaceNameTest() throws Exception {
        Optional<Space> space = spaces.resolveSpace("default://master@myteam/mortgages/");

        assertTrue(space.isPresent());
        assertEquals("myteam", space.get().getName());
    }

    @Test
    public void resolveSpaceNameWhenBranchNameHasSlashesTest() throws Exception {
        Optional<Space> space = spaces.resolveSpace("default://my/master/branch@myteam/mortgages/");

        assertTrue(space.isPresent());
        assertEquals("myteam", space.get().getName());
    }
}