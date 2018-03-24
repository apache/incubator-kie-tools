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
package org.kie.workbench.common.stunner.bpmn.client.forms.filters;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.stunner.bpmn.definition.StartErrorEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

public class StartEventFilterProviderFactory {

    private SessionManager sessionManager;
    private GraphUtils graphUtils;

    private List<StartEventFilterProvider> filterProviders = new ArrayList();

    public StartEventFilterProviderFactory(final SessionManager sessionManager, final GraphUtils graphUtils) {
        this.sessionManager = sessionManager;
        this.graphUtils = graphUtils;
        addStartSignalEventFilter();
        addStartMessageEventFilter();
        addStartErrorEventFilter();
        addStartTimerEventFilter();
    }

    public List<StartEventFilterProvider> getFilterProviders() {
        return filterProviders;
    }

    private void addStartSignalEventFilter() {
        filterProviders.add(new StartEventFilterProvider(sessionManager,
                                                         StartSignalEvent.class));
    }

    private void addStartMessageEventFilter() {
        filterProviders.add(new StartEventFilterProvider(sessionManager,
                                                         StartMessageEvent.class));
    }

    private void addStartErrorEventFilter() {
        filterProviders.add(new StartEventFilterProvider(sessionManager,
                                                         StartErrorEvent.class));
    }

    private void addStartTimerEventFilter() {
        filterProviders.add(new StartEventFilterProvider(sessionManager,
                                                         StartTimerEvent.class));
    }
}
