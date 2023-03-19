/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.definition;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jsinterop.annotations.JsType;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;

@Bindable
@Definition
@CanContain(roles = {EventRef.LABEL_EVENT, ActionNode.LABEL_ACTION})
@JsType
public class OnEvent {

    public static final String LABEL_ONEVENTS = "on_events";

    @Category
    public static final transient String category = Categories.EVENTS;

    @Labels
    private static final Set<String> labels = Stream.of(Workflow.LABEL_ROOT_NODE,
                                                        LABEL_ONEVENTS).collect(Collectors.toSet());

    private String[] eventRefs;

    private ActionNode[] actions;

    private EventDataFilter eventDataFilter;

    public OnEvent() {
    }

    public Set<String> getLabels() {
        return labels;
    }

    public String getCategory() {
        return category;
    }

    public String[] getEventRefs() {
        return eventRefs;
    }

    public void setEventRefs(String[] eventRefs) {
        this.eventRefs = eventRefs;
    }

    public ActionNode[] getActions() {
        return actions;
    }

    public void setActions(ActionNode[] actions) {
        this.actions = actions;
    }

    public EventDataFilter getEventDataFilter() {
        return eventDataFilter;
    }

    public void setEventDataFilter(EventDataFilter eventDataFilter) {
        this.eventDataFilter = eventDataFilter;
    }
}
