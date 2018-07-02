/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.project.client.docks;

import javax.enterprise.event.Observes;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.event.screen.ScreenMaximizedEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramFocusEvent;
import org.kie.workbench.common.stunner.project.client.editor.event.OnDiagramLoseFocusEvent;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class StunnerDocksHandlerTest {

    private StunnerDocksHandler handler;

    @Mock
    private Command command;

    @Mock
    private ClientSession clientSession;

    @Before
    public void init() {
        handler = new StunnerDocksHandler();

        handler.init(command);

        assertEquals(2,
                     handler.provideDocks("").size());
    }

    @Test
    public void testOnDiagramFocusEvent() {
        handler.onDiagramFocusEvent(new OnDiagramFocusEvent());

        assertTrue(handler.shouldRefreshDocks());

        assertFalse(handler.shouldDisableDocks());

        verify(command).execute();
    }

    @Test
    public void testOnDiagramLoseFocusEvent() {
        handler.onDiagramLoseFocusEvent(new OnDiagramLoseFocusEvent());
    }

    @Test
    public void testOnDiagramEditorMaximized() {
        handler.onDiagramEditorMaximized(new ScreenMaximizedEvent(true));

        assertTrue(handler.shouldRefreshDocks());
        assertFalse(handler.shouldDisableDocks());
    }

    @Test
    public void testOnOtherEditorMaximized() {
        handler.onDiagramEditorMaximized(new ScreenMaximizedEvent(false));

        assertFalse(handler.shouldRefreshDocks());
        assertFalse(handler.shouldDisableDocks());
    }

    public void onDiagramLoseFocusEvent(@Observes OnDiagramLoseFocusEvent event) {
        assertTrue(handler.shouldRefreshDocks());

        assertTrue(handler.shouldDisableDocks());

        verify(command).execute();
    }
}
