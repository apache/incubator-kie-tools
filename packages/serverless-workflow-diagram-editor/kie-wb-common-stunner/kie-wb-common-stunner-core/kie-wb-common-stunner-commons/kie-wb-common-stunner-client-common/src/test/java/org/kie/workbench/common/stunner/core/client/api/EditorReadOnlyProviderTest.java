/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.api;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.appformer.client.context.EditorContextProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.appformer.client.context.Channel.DEFAULT;
import static org.appformer.client.context.Channel.EMBEDDED;
import static org.appformer.client.context.Channel.GITHUB;
import static org.appformer.client.context.Channel.ONLINE;
import static org.appformer.client.context.Channel.VSCODE;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class EditorReadOnlyProviderTest {

    @Mock
    private EditorContextProvider contextProvider;

    private EditorReadOnlyProvider readOnlyProvider;

    @Before
    public void setup() {
        readOnlyProvider = new EditorReadOnlyProvider(contextProvider);
    }

    @Test
    public void testIsReadOnlyDiagramWhenIsGithub() {
        when(contextProvider.getChannel()).thenReturn(GITHUB);
        assertFalse(readOnlyProvider.isReadOnlyDiagram());
    }

    @Test
    public void testIsReadOnlyDiagramWhenIsDefault() {
        when(contextProvider.getChannel()).thenReturn(DEFAULT);
        assertFalse(readOnlyProvider.isReadOnlyDiagram());
    }

    @Test
    public void testIsReadOnlyDiagramWhenIsVsCode() {
        when(contextProvider.getChannel()).thenReturn(VSCODE);
        assertFalse(readOnlyProvider.isReadOnlyDiagram());
    }

    @Test
    public void testIsReadOnlyDiagramWhenIsOnline() {
        when(contextProvider.getChannel()).thenReturn(ONLINE);
        assertFalse(readOnlyProvider.isReadOnlyDiagram());
    }

    @Test
    public void testIsReadOnlyDiagramWhenIsEmbedded() {
        when(contextProvider.getChannel()).thenReturn(EMBEDDED);
        assertFalse(readOnlyProvider.isReadOnlyDiagram());
    }
}