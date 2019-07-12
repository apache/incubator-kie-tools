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

import java.util.function.Function;

import javax.enterprise.inject.Instance;

import org.assertj.core.api.Assertions;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageBatch;
import org.guvnor.structure.organizationalunit.config.SpaceInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SpaceConfigStorageRegistryImplTest {

    private static final String SPACE_NAME = "mySpace";

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
        final SpaceConfigStorage spaceConfigStorage1 = spaceConfigStorageRegistry.get(SPACE_NAME);
        assertSame(mySpaceConfigStorage, spaceConfigStorage1);

        doReturn(otherSpaceConfigStorage).when(spaceConfigStorages).get();
        final SpaceConfigStorage spaceConfigStorage2 = spaceConfigStorageRegistry.get(SPACE_NAME);
        assertSame(mySpaceConfigStorage, spaceConfigStorage2);
    }

    @Test
    public void getBatchTest() {
        doReturn(mySpaceConfigStorage).when(spaceConfigStorages).get();

        final SpaceInfo spaceInfo = mock(SpaceInfo.class);

        when(mySpaceConfigStorage.loadSpaceInfo()).thenReturn(spaceInfo);

        SpaceConfigStorageBatch batch = spy(spaceConfigStorageRegistry.getBatch(SPACE_NAME));

        Assertions.assertThatThrownBy(() -> batch.run(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'function' should be not null!");

        Function<SpaceConfigStorageBatch.SpaceConfigStorageBatchContext, Void> function = (context) -> {
            context.saveSpaceInfo();
            return null;
        };

        batch.run(function);

        verify(mySpaceConfigStorage).saveSpaceInfo(any());
    }
}
