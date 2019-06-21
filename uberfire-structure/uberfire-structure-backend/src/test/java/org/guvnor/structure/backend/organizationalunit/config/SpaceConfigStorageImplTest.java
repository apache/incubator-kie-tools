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

package org.guvnor.structure.backend.organizationalunit.config;

import org.guvnor.structure.organizationalunit.config.BranchPermissions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.io.object.ObjectStorage;
import org.uberfire.io.IOService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SpaceConfigStorageImplTest {

    @Mock
    private ObjectStorage objectStorage;

    @Mock
    private IOService ioService;

    private SpaceConfigStorageImpl spaceConfigStorage;

    @Before
    public void setup() {
        spaceConfigStorage = spy(new SpaceConfigStorageImpl(objectStorage,
                                                            ioService));
    }

    @Test
    public void loadCustomBranchPermissionsTest() {
        final BranchPermissions customBranchPermissions = mock(BranchPermissions.class);
        doReturn(customBranchPermissions).when(objectStorage).read("/config/myProject/myBranch/BranchPermissions.json");

        final BranchPermissions branchPermissions = spaceConfigStorage.loadBranchPermissions("myBranch",
                                                                                             "myProject");

        assertSame(customBranchPermissions,
                   branchPermissions);
    }

    @Test
    public void loadDefaultBranchPermissionsTest() {
        final BranchPermissions defaultBranchPermissions = mock(BranchPermissions.class);
        doReturn(defaultBranchPermissions).when(spaceConfigStorage).getDefaultBranchPermissions("myBranch");

        final BranchPermissions branchPermissions = spaceConfigStorage.loadBranchPermissions("myBranch",
                                                                                             "myProject");

        assertSame(defaultBranchPermissions,
                   branchPermissions);
    }

    @Test
    public void saveBranchPermissionsTest() {
        final BranchPermissions customBranchPermissions = mock(BranchPermissions.class);

        spaceConfigStorage.saveBranchPermissions("myBranch",
                                                 "myProject",
                                                 customBranchPermissions);

        verify(objectStorage).write(eq("/config/myProject/myBranch/BranchPermissions.json"),
                                    same(customBranchPermissions));
    }

    @Test
    public void deleteBranchPermissionsTest() {
        spaceConfigStorage.deleteBranchPermissions("myBranch",
                                                   "myProject");

        verify(objectStorage).delete(eq("/config/myProject/myBranch/BranchPermissions.json"));
    }
}
