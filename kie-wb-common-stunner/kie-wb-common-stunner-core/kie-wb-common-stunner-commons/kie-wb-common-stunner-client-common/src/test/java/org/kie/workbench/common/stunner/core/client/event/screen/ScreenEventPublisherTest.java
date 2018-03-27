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

package org.kie.workbench.common.stunner.core.client.event.screen;

import java.util.HashMap;
import java.util.stream.Stream;

import javax.enterprise.event.Event;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static java.util.stream.Collectors.toSet;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static sun.reflect.annotation.AnnotationParser.annotationForMap;

@RunWith(MockitoJUnitRunner.class)
public class ScreenEventPublisherTest {

    private ScreenEventPublisher screenEventPublisher;

    @Mock
    private Event<ScreenMaximizedEvent> diagramEditorMaximizedEventEvent;

    @Mock
    private Event<ScreenMinimizedEvent> diagramEditorMinimizedEventEvent;

    @Mock
    private ActivityBeansCache activityBeansCache;

    @Mock
    private PlaceMaximizedEvent placeMaximizedEvent;

    @Mock
    private PlaceMinimizedEvent placeMinimizedEvent;

    @Mock
    private SyncBeanDef syncBeanDef;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {

        screenEventPublisher = new ScreenEventPublisher(diagramEditorMaximizedEventEvent,
                                                        diagramEditorMinimizedEventEvent, activityBeansCache);

        String screenId = "editor";
        PlaceRequest placeRequest = new DefaultPlaceRequest(screenId);
        when(placeMaximizedEvent.getPlace()).thenReturn(placeRequest);
        when(placeMinimizedEvent.getPlace()).thenReturn(placeRequest);
        when(activityBeansCache.getActivity(screenId)).thenReturn(syncBeanDef);

        when(syncBeanDef.getQualifiers()).thenReturn(Stream.of(annotationForMap(
                DiagramEditor.class, new HashMap<>())).collect(toSet()));
    }

    @Test
    public void onPlaceMaximizedEventTest() {
        screenEventPublisher.onPlaceMaximizedEvent(placeMaximizedEvent);
        verify(diagramEditorMaximizedEventEvent, Mockito.times(1)).fire(new ScreenMaximizedEvent(true));

        reset(syncBeanDef);

        screenEventPublisher.onPlaceMaximizedEvent(placeMaximizedEvent);
        verify(diagramEditorMaximizedEventEvent, Mockito.times(1)).fire(new ScreenMaximizedEvent(false));
    }

    @Test
    public void onPlaceMinimizedEventTest() {
        screenEventPublisher.onPlaceMinimizedEvent(placeMinimizedEvent);
        verify(diagramEditorMinimizedEventEvent, Mockito.times(1)).fire(new ScreenMinimizedEvent(true));

        reset(syncBeanDef);

        screenEventPublisher.onPlaceMinimizedEvent(placeMinimizedEvent);
        verify(diagramEditorMinimizedEventEvent, Mockito.times(1)).fire(new ScreenMinimizedEvent(false));
    }
}
