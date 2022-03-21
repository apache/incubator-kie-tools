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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.observers;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.appformer.kogito.bridge.client.guided.tour.GuidedTourBridge;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourObserver;
import org.appformer.kogito.bridge.client.guided.tour.service.api.UserInteraction;
import org.jboss.errai.ioc.client.api.Disposer;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourUtils;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.AbstractCanvasHandlerElementEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;

import static org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourActions.CREATED;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourActions.REMOVED;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourActions.UPDATED;

@Dependent
public class GuidedTourGraphObserver extends GuidedTourObserver<GuidedTourGraphObserver> {

    private final GuidedTourUtils guidedTourUtils;

    @Inject
    public GuidedTourGraphObserver(final Disposer<GuidedTourGraphObserver> disposer,
                                   final GuidedTourUtils guidedTourUtils) {
        super(disposer);
        this.guidedTourUtils = guidedTourUtils;
    }

    public void onCanvasElementAddedEvent(final @Observes CanvasElementAddedEvent event) {
        onCanvasEvent(CREATED.name(), getNodeName(event));
    }

    public void onCanvasElementUpdatedEvent(final @Observes CanvasElementUpdatedEvent event) {
        onCanvasEvent(UPDATED.name(), getNodeName(event));
    }

    public void onAbstractCanvasElementRemovedEvent(final @Observes CanvasElementRemovedEvent event) {
        onCanvasEvent(REMOVED.name(), getNodeName(event));
    }

    private void onCanvasEvent(final String action,
                               final String target) {
        final Optional<GuidedTourBridge> monitorBridge = getMonitorBridge();
        monitorBridge.ifPresent(bridge -> bridge.refresh(buildUserInteraction(action, target)));
    }

    private String getNodeName(final AbstractCanvasHandlerElementEvent event) {
        return guidedTourUtils.getName(event).orElse("");
    }

    UserInteraction buildUserInteraction(final String action,
                                         final String target) {
        final UserInteraction userInteraction = new UserInteraction();
        userInteraction.setAction(action);
        userInteraction.setTarget(target);
        return userInteraction;
    }
}
