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

import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.sw.definition.custom.StateEndDefinitionJsonbTypeDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.StateEndDefinitionJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.definition.custom.StateTransitionDefinitionJsonbTypeDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.StateTransitionDefinitionJsonbTypeSerializer;

@JsType
public class State {

    public String name;

    public String type;

    public Metadata metadata;

    // TODO: Not all states supports this (eg: switch state)
    @JsonbTypeSerializer(StateTransitionDefinitionJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(StateTransitionDefinitionJsonbTypeDeserializer.class)
    public Object transition;

    // TODO: Not all states supports this (eg: switch state)
    @JsonbTypeSerializer(StateEndDefinitionJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(StateEndDefinitionJsonbTypeDeserializer.class)
    public Object end;

    public ErrorTransition[] onErrors;

    public String compensatedBy;

    public StateDataFilter stateDataFilter;

    public WorkflowTimeouts timeouts;

    public State() {
        this.name = "State";
    }

    public State setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public State setType(String type) {
        this.type = type;
        return this;
    }

    public Object getTransition() {
        return transition;
    }

    public State setTransition(Object transition) {
        this.transition = transition;
        return this;
    }

    public Object getEnd() {
        return end;
    }

    public State setEnd(Object end) {
        this.end = end;
        return this;
    }

    public ErrorTransition[] getOnErrors() {
        return onErrors;
    }

    public State setOnErrors(ErrorTransition[] onErrors) {
        this.onErrors = onErrors;
        return this;
    }

    public String getCompensatedBy() {
        return compensatedBy;
    }

    public State setCompensatedBy(String compensatedBy) {
        this.compensatedBy = compensatedBy;
        return this;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public StateDataFilter getStateDataFilter() {
        return stateDataFilter;
    }

    public void setStateDataFilter(StateDataFilter stateDataFilter) {
        this.stateDataFilter = stateDataFilter;
    }

    public WorkflowTimeouts getTimeouts() {
        return timeouts;
    }

    public void setTimeouts(WorkflowTimeouts timeouts) {
        this.timeouts = timeouts;
    }
}
