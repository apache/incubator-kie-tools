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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.annotation.DiagramEditor;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.workbench.events.AbstractPlaceEvent;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;

/**
 * Observes screen events and publish Stunner related events, e.g.
 * {@link ScreenMaximizedEvent}, {@link ScreenMinimizedEvent}.
 */
@ApplicationScoped
public class ScreenEventPublisher {

    private Event<ScreenMaximizedEvent> diagramEditorMaximizedEventEvent;
    private Event<ScreenMinimizedEvent> diagramEditorMinimizedEventEvent;
    private ActivityBeansCache activityBeansCache;

    @Inject
    public ScreenEventPublisher(Event<ScreenMaximizedEvent> diagramEditorMaximizedEventEvent,
                                Event<ScreenMinimizedEvent> diagramEditorMinimizedEventEvent,
                                ActivityBeansCache activityBeansCache) {
        this.diagramEditorMaximizedEventEvent = diagramEditorMaximizedEventEvent;
        this.diagramEditorMinimizedEventEvent = diagramEditorMinimizedEventEvent;
        this.activityBeansCache = activityBeansCache;
    }

    protected void onPlaceMaximizedEvent(@Observes PlaceMaximizedEvent event) {
        diagramEditorMaximizedEventEvent.fire(new ScreenMaximizedEvent(verifyEventIdentifier(event)));
    }

    protected void onPlaceMinimizedEvent(@Observes PlaceMinimizedEvent event) {
        diagramEditorMinimizedEventEvent.fire(new ScreenMinimizedEvent(verifyEventIdentifier(event)));
    }

    private boolean verifyEventIdentifier(AbstractPlaceEvent event) {
        return activityBeansCache.getActivity(event.getPlace().getIdentifier()).getQualifiers().stream()
                .anyMatch(a -> a instanceof DiagramEditor);
    }
}
