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

import javax.enterprise.inject.Instance;

import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class SpaceConfigStorageRegistryImplTest {

    @Mock
    private Instance<SpaceConfigStorage> spaceConfigStorages;

    private SpaceConfigStorageRegistryImpl spaceConfigStorageRegistry;

    @Mock
    private SpaceConfigStorage mySpaceConfigStorage;

    @Mock
    private SpaceConfigStorage otherSpaceConfigStorage;

    @Before
    public void setup() {
        spaceConfigStorageRegistry = new SpaceConfigStorageRegistryImpl(spaceConfigStorages);
    }

    @Test
    public void getTest() {
        doReturn(mySpaceConfigStorage).when(spaceConfigStorages).get();
        final SpaceConfigStorage spaceConfigStorage1 = spaceConfigStorageRegistry.get("mySpace");
        assertSame(mySpaceConfigStorage, spaceConfigStorage1);

        doReturn(otherSpaceConfigStorage).when(spaceConfigStorages).get();
        final SpaceConfigStorage spaceConfigStorage2 = spaceConfigStorageRegistry.get("mySpace");
        assertSame(mySpaceConfigStorage, spaceConfigStorage2);
    }
}
