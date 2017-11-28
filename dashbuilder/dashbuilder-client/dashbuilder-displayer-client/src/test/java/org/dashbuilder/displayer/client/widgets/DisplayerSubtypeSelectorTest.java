/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.client.widgets;

import java.util.Arrays;

import org.dashbuilder.displayer.DisplayerSubType;
import org.dashbuilder.displayer.DisplayerType;
import org.dashbuilder.displayer.client.RendererLibrary;
import org.dashbuilder.displayer.client.RendererManager;
import org.dashbuilder.displayer.client.events.DisplayerSubtypeSelectedEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.dashbuilder.displayer.DisplayerSubType.*;
import static org.dashbuilder.displayer.DisplayerType.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DisplayerSubtypeSelectorTest {

    @Mock
    DisplayerSubtypeSelector.View subtypeView;

    @Mock
    RendererManager rendererManager;

    @Mock
    RendererLibrary rendererLibrary;

    @Mock
    EventSourceMock<DisplayerSubtypeSelectedEvent> subtypeSelectedEvent;

    DisplayerSubtypeSelector presenter;

    @Before
    public void init() {
        when(rendererManager.getRendererForType(any(DisplayerType.class))).thenReturn(rendererLibrary);

        when(rendererLibrary.getSupportedTypes()).thenReturn(Arrays.asList(BARCHART, LINECHART, BUBBLECHART, MAP));
        when(rendererLibrary.getSupportedSubtypes(BARCHART)).thenReturn(Arrays.asList(BAR, BAR_STACKED));
        when(rendererLibrary.getSupportedSubtypes(LINECHART)).thenReturn(Arrays.asList(LINE, SMOOTH));
        when(rendererLibrary.getSupportedSubtypes(BUBBLECHART)).thenReturn(null);

        presenter = new DisplayerSubtypeSelector(subtypeView, rendererManager, subtypeSelectedEvent);
    }

    @Test
    public void testInitialization1() {
        presenter.init(BARCHART, BAR);
        verify(subtypeView).show(BARCHART, BAR);
        verify(subtypeView).show(BARCHART, BAR_STACKED);
        verify(subtypeView).select(BAR);
    }

    @Test
    public void testInitialization2() {
        presenter.init(LINECHART, null);
        verify(subtypeView).show(LINECHART, LINE);
        verify(subtypeView).show(LINECHART, SMOOTH);
        verify(subtypeView).select(LINE);
    }

    @Test
    public void testInitialization3() {
        presenter.init(BUBBLECHART, null);
        verify(subtypeView, never()).show(any(DisplayerType.class), any(DisplayerSubType.class));
        verify(subtypeView).showDefault(BUBBLECHART);
    }

    @Test
    public void testOnSelect() {
        presenter.onSelect(LINE);
        assertEquals(presenter.getSelectedSubtype(), LINE);
        verify(subtypeSelectedEvent).fire(any(DisplayerSubtypeSelectedEvent.class));
    }
}