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

package org.kie.workbench.common.screens.archetype.mgmt.backend.config;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.archetype.mgmt.shared.model.Archetype;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.io.object.ObjectStorage;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ArchetypeConfigStorageTest {

    private static final String ARCHETYPE_ALIAS = "myArchetype";
    private static final String ARCHETYPE_PATH = "/config/myArchetype.json";

    private ArchetypeConfigStorage archetypeConfigStorage;

    @Mock
    private ObjectStorage objectStorage;

    @Before
    public void setup() {
        archetypeConfigStorage = new ArchetypeConfigStorageImpl(objectStorage);
    }

    @Test
    public void setupTest() {
        archetypeConfigStorage.setup();

        verify(objectStorage).init(any(URI.class));
    }

    @Test
    public void loadArchetypeTest() {
        final Archetype expectedArchetype = mock(Archetype.class);
        doReturn(expectedArchetype).when(objectStorage).read(ARCHETYPE_PATH);

        final Archetype archetype = archetypeConfigStorage.loadArchetype(ARCHETYPE_ALIAS);

        assertSame(expectedArchetype,
                   archetype);
    }

    @Test
    public void saveArchetypeTest() {
        final Archetype archetype = mock(Archetype.class);
        doReturn(ARCHETYPE_ALIAS).when(archetype).getAlias();

        archetypeConfigStorage.saveArchetype(archetype);

        verify(objectStorage).write(eq(ARCHETYPE_PATH),
                                    same(archetype));
    }

    @Test
    public void deleteArchetypeTest() {
        archetypeConfigStorage.deleteArchetype(ARCHETYPE_ALIAS);

        verify(objectStorage).delete(eq(ARCHETYPE_PATH));
    }
}
