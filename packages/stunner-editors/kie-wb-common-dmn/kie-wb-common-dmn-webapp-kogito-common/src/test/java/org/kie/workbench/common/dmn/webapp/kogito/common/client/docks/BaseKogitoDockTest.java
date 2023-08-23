/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package org.kie.workbench.common.dmn.webapp.kogito.common.client.docks;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.kie.workbench.common.stunner.client.widgets.editor.DiagramEditorDock;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseKogitoDockTest<DOCK extends DiagramEditorDock> {

    @Mock
    protected UberfireDocks uberfireDocks;

    @Mock
    protected DecisionNavigatorPresenter decisionNavigatorPresenter;

    @Mock
    protected TranslationService translationService;

    @Captor
    protected ArgumentCaptor<UberfireDock> dockArgumentCaptor;

    protected DOCK dock;

    @Before
    public void setup() {
        this.dock = makeDock();

        dock.init();
    }

    protected abstract DOCK makeDock();

    protected abstract UberfireDockPosition position();

    protected abstract String screen();

    @Test
    public void testInit() {
        dock.init();

        verify(uberfireDocks).add(dockArgumentCaptor.capture());
        verify(uberfireDocks).show(position());

        assertEquals(screen(), dockArgumentCaptor.getValue().getIdentifier());
    }

    @Test
    public void testInit2TimeCall() {
        dock.init();

        verify(uberfireDocks, times(1)).add(dockArgumentCaptor.capture());
        verify(uberfireDocks, times(1)).show(position());

        assertEquals(screen(), dockArgumentCaptor.getValue().getIdentifier());

        reset(uberfireDocks);

        dock.init();

        verify(uberfireDocks, never()).add(any());
        verify(uberfireDocks, never()).show(any());
    }

    @Test
    public void testDestroy() {
        dock.destroy();

        verify(uberfireDocks).remove(dockArgumentCaptor.capture());

        assertEquals(screen(), dockArgumentCaptor.getValue().getIdentifier());
    }

    @Test
    public void testDestroy2TimeCall() {
        dock.destroy();

        verify(uberfireDocks, times(1)).remove(dockArgumentCaptor.capture());

        assertEquals(screen(), dockArgumentCaptor.getValue().getIdentifier());

        reset(uberfireDocks);

        dock.destroy();

        verify(uberfireDocks, never()).remove(any());
    }

    @Test
    public void testOpenWhenClosed() {
        dock.open();

        verify(uberfireDocks).open(dockArgumentCaptor.capture());

        assertEquals(screen(), dockArgumentCaptor.getValue().getIdentifier());
    }

    @Test
    public void testOpenWhenAlreadyOpen() {
        dock.open();

        reset(uberfireDocks);

        dock.open();

        verify(uberfireDocks, never()).open(any(UberfireDock.class));
    }

    @Test
    public void testCloseWhenOpen() {
        dock.open();
        dock.close();

        verify(uberfireDocks).close(dockArgumentCaptor.capture());

        assertEquals(screen(), dockArgumentCaptor.getValue().getIdentifier());
    }

    @Test
    public void testCloseWhenAlreadyClosed() {
        dock.close();

        verify(uberfireDocks, never()).close(any(UberfireDock.class));
    }
}
