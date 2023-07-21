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
import org.kie.workbench.common.stunner.client.json.mapper.annotation.JSONMapper;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YAMLMapper;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlPropertyOrder;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.StateEndDefinitionJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.StateTransitionDefinitionJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.WorkflowTimeoutsJsonSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.StateEndDefinitionYamlTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.StateTransitionDefinitionYamlTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.WorkflowTimeoutsYamlSerializer;

@JSONMapper
@YAMLMapper
@JsType
@YamlPropertyOrder({"name", "type", "actions", "usedForCompensation", "transition", "end", "onErrors", "compensatedBy", "stateDataFilter", "timeouts", "eventTimeout", "actionMode", "metadata"})
public class OperationState extends State<OperationState> {

    public static final String TYPE_OPERATION = "operation";

    public String actionMode;

    public ActionNode[] actions;

    public Boolean usedForCompensation;

    public StateDataFilter stateDataFilter;

    public Metadata metadata;

    @JsonbTypeSerializer(StateTransitionDefinitionJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(StateTransitionDefinitionJsonbTypeSerializer.class)
    @YamlTypeSerializer(StateTransitionDefinitionYamlTypeSerializer.class)
    @YamlTypeDeserializer(StateTransitionDefinitionYamlTypeSerializer.class)
    public Object transition;

    @JsonbTypeSerializer(StateEndDefinitionJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(StateEndDefinitionJsonbTypeSerializer.class)
    @YamlTypeSerializer(StateEndDefinitionYamlTypeSerializer.class)
    @YamlTypeDeserializer(StateEndDefinitionYamlTypeSerializer.class)
    public Object end;

    public ErrorTransition[] onErrors;

    @JsonbTypeSerializer(WorkflowTimeoutsJsonSerializer.class)
    @JsonbTypeDeserializer(WorkflowTimeoutsJsonSerializer.class)
    @YamlTypeSerializer(WorkflowTimeoutsYamlSerializer.class)
    @YamlTypeDeserializer(WorkflowTimeoutsYamlSerializer.class)
    public Object timeouts;

    public String compensatedBy;

    public OperationState() {
        this.type = TYPE_OPERATION;
    }

    public String getActionMode() {
        return actionMode;
    }

    public OperationState setActionMode(String actionMode) {
        this.actionMode = actionMode;
        return this;
    }

    public ActionNode[] getActions() {
        return actions;
    }

    public OperationState setActions(ActionNode[] actions) {
        this.actions = actions;
        return this;
    }

    public Boolean isUsedForCompensation() {
        return usedForCompensation;
    }

    public OperationState setUsedForCompensation(Boolean usedForCompensation) {
        this.usedForCompensation = usedForCompensation;
        return this;
    }

    public StateDataFilter getStateDataFilter() {
        return stateDataFilter;
    }

    public void setStateDataFilter(StateDataFilter stateDataFilter) {
        this.stateDataFilter = stateDataFilter;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public OperationState setMetadata(Metadata metadata) {
        this.metadata = metadata;
        return this;
    }

    public Object getTransition() {
        return transition;
    }

    public OperationState setTransition(Object transition) {
        this.transition = transition;
        return this;
    }

    public Object getEnd() {
        return end;
    }

    public OperationState setEnd(Object end) {
        this.end = end;
        return this;
    }

    public ErrorTransition[] getOnErrors() {
        return onErrors;
    }

    public OperationState setOnErrors(ErrorTransition[] onErrors) {
        this.onErrors = onErrors;
        return this;
    }

    public Object getTimeouts() {
        return timeouts;
    }

    public void setTimeouts(Object timeouts) {
        this.timeouts = timeouts;
    }

    public String getCompensatedBy() {
        return compensatedBy;
    }

    public OperationState setCompensatedBy(String compensatedBy) {
        this.compensatedBy = compensatedBy;
        return this;
    }
}
