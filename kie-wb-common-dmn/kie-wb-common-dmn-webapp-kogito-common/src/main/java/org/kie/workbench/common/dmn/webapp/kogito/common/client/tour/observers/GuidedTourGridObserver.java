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

import java.util.Objects;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.appformer.kogito.bridge.client.guided.tour.GuidedTourObserver;
import org.appformer.kogito.bridge.client.guided.tour.service.api.UserInteraction;
import org.jboss.errai.ioc.client.api.Disposer;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourUtils;

import static org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourActions.CREATED;
import static org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourActions.UPDATED;

public class GuidedTourGridObserver extends GuidedTourObserver<GuidedTourGridObserver> {

    private static final String BOXED_EXPRESSION = "BOXED_EXPRESSION";

    private final DMNGraphUtils dmnGraphUtils;

    private final GuidedTourUtils guidedTourUtils;

    @Inject
    public GuidedTourGridObserver(final Disposer<GuidedTourGridObserver> disposer,
                                  final DMNGraphUtils dmnGraphUtils,
                                  final GuidedTourUtils guidedTourUtils) {
        super(disposer);
        this.dmnGraphUtils = dmnGraphUtils;
        this.guidedTourUtils = guidedTourUtils;
    }

    public void onEditExpressionEvent(final @Observes EditExpressionEvent event) {
        onBoxedExpressionEvent(CREATED.name(), event.getNodeUUID());
    }

    public void onExpressionEditorChanged(final @Observes ExpressionEditorChanged event) {
        onBoxedExpressionEvent(UPDATED.name(), event.getNodeUUID());
    }

    private void onBoxedExpressionEvent(final String action,
                                        final String nodeUUID) {
        final String name = getName(nodeUUID);
        final String target = BOXED_EXPRESSION + ":::" + name;
        getMonitorBridge()
                .ifPresent(bridge -> bridge.refresh(buildUserInteraction(action, target)));
    }

    private String getName(final String nodeUUID) {
        return dmnGraphUtils
                .getNodeStream()
                .filter(node -> Objects.equals(node.getUUID(), nodeUUID))
                .findFirst()
                .map(guidedTourUtils::getName)
                .orElse("");
    }

    UserInteraction buildUserInteraction(final String action,
                                         final String target) {
        final UserInteraction userInteraction = new UserInteraction();
        userInteraction.setAction(action);
        userInteraction.setTarget(target);
        return userInteraction;
    }
}
