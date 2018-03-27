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

package org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard;

import java.util.Arrays;
import java.util.HashSet;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.mvp.PlaceRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClipboardControlCleanerTest {

    public static final String DIAGRAM_EDITOR_ID = UUID.uuid();
    private ClipboardControlCleaner clipboardControlCleaner;

    @Mock
    private ManagedInstance<ClipboardControl> clipboardControls;

    @Mock
    private ActivityBeansCache activityBeansCache;

    @Mock
    private PlaceLostFocusEvent placeLostFocusEvent;

    @Mock
    private ClipboardControl clipboardControl;

    @Mock
    private PlaceRequest placeRequest;

    @Mock
    private SyncBeanDef<Activity> syncBeanDef;

    @Before
    public void setUp() throws Exception {
        clipboardControlCleaner = new ClipboardControlCleaner(clipboardControls, activityBeansCache);

        when(placeLostFocusEvent.getPlace()).thenReturn(placeRequest);
        when(placeRequest.getIdentifier()).thenReturn(DIAGRAM_EDITOR_ID);
        when(activityBeansCache.getActivity(DIAGRAM_EDITOR_ID)).thenReturn(syncBeanDef);
        when(syncBeanDef.getQualifiers()).thenReturn(new HashSet<>(Arrays.asList(mock(DiagramEditor.class))));
        when(clipboardControls.spliterator()).thenReturn(Arrays.asList(clipboardControl).spliterator());
    }

    @Test
    public void onPlaceGainFocusEvent() {
        clipboardControlCleaner.onPlaceGainFocusEvent(placeLostFocusEvent);
        verify(clipboardControl, times(1)).clear();
    }
}