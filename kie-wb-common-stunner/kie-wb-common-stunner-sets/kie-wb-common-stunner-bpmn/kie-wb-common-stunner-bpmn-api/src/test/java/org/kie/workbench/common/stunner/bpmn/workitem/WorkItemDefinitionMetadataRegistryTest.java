/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.workitem;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkItemDefinitionMetadataRegistryTest {

    @Mock
    private BiConsumer<Path, Consumer<Collection<WorkItemDefinition>>> workItemsByPathSupplier;

    @Mock
    private WorkItemDefinitionCacheRegistry registry;

    private WorkItemDefinitionMetadataRegistry tested;

    @Before
    public void init() {
        this.tested = new WorkItemDefinitionMetadataRegistry()
                .setRegistrySupplier(() -> registry)
                .setWorkItemsByPathSupplier(workItemsByPathSupplier);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoad() {
        Path path = mock(Path.class);
        Metadata metadata = mock(Metadata.class);
        Command callback = mock(Command.class);
        when(metadata.getRoot()).thenReturn(path);
        tested.load(metadata,
                    callback);
        verify(workItemsByPathSupplier, times(1)).accept(eq(path),
                                                         any(Consumer.class));
    }

    @Test
    public void testGetItems() {
        tested.items();
        verify(registry, times(1)).items();
    }

    @Test
    public void testGetItem() {
        tested.get("name1");
        verify(registry, times(1)).get(eq("name1"));
    }
}
