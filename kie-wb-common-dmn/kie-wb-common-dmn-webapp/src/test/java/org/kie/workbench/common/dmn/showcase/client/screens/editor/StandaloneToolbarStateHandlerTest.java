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

package org.kie.workbench.common.dmn.showcase.client.screens.editor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNEditorToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarCommand;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StandaloneToolbarStateHandlerTest {

    @Mock
    private DMNEditorToolbar toolbar;

    private StandaloneToolbarStateHandler toolbarStateHandler;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        this.toolbarStateHandler = new StandaloneToolbarStateHandler(toolbar);
        when(toolbar.isEnabled(any(ToolbarCommand.class))).thenReturn(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEnterGridView() {
        toolbarStateHandler.enterGridView();

        verify(toolbar, atLeast(1)).disable(any(ToolbarCommand.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEnterGraphView() {
        toolbarStateHandler.enterGridView();

        toolbarStateHandler.enterGraphView();

        verify(toolbar, atLeast(1)).enable(any(ToolbarCommand.class));
    }
}
