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

import org.dashbuilder.displayer.client.events.DisplayerTypeSelectedEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.dashbuilder.displayer.DisplayerType.*;
import static org.dashbuilder.displayer.DisplayerSubType.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DisplayerTypeSelectorTest {

    @Mock
    DisplayerTypeSelector.View typeView;

    @Mock
    DisplayerSubtypeSelector subtypeSelector;

    @Mock
    EventSourceMock<DisplayerTypeSelectedEvent> typeSelectedEvent;

    DisplayerTypeSelector presenter;

    @Before
    public void init() {
        presenter = new DisplayerTypeSelector(typeView, subtypeSelector, typeSelectedEvent);
    }

    @Test
    public void testInitialization() {
        presenter.init(LINECHART, SMOOTH);
        verify(typeView).select(LINECHART);
    }

    @Test
    public void testOnSelect() {
        presenter.onSelect(BARCHART);
        assertEquals(presenter.getSelectedType(), BARCHART);
        verify(typeSelectedEvent).fire(any(DisplayerTypeSelectedEvent.class));
    }
}