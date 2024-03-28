/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.sw.definition;

import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jsinterop.annotations.JsType;
import org.kie.j2cl.tools.json.mapper.annotation.JSONMapper;
import org.kie.j2cl.tools.processors.annotations.GWT3Export;
import org.kie.j2cl.tools.yaml.mapper.api.annotation.YAMLMapper;
import org.kie.j2cl.tools.yaml.mapper.api.annotation.YamlPropertyOrder;
import org.kie.j2cl.tools.yaml.mapper.api.annotation.YamlTypeDeserializer;
import org.kie.j2cl.tools.yaml.mapper.api.annotation.YamlTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.BatchSizeJsonTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.StateEndDefinitionJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.StateTransitionDefinitionJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.WorkflowTimeoutsJsonSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.StateEndDefinitionYamlTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.StateTransitionDefinitionYamlTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.StringNumberYamlTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.WorkflowTimeoutsYamlSerializer;

@JSONMapper
@YAMLMapper
@JsType
@GWT3Export
@YamlPropertyOrder({"name", "type", "actions", "inputCollection", "outputCollection", "iterationParam", "transition", "stateDataFilter", "eventTimeout", "compensatedBy", "timeouts", "onErrors", "end", "metadata"})
public class ForEachState extends State<ForEachState> implements HasErrors<ForEachState>, HasEnd<ForEachState>, HasTransition<ForEachState>, HasCompensatedBy<ForEachState>, HasMetadata<ForEachState> {

    public static final String TYPE_FOR_EACH = "foreach";

    public ActionNode[] actions;

    public String inputCollection;

    public String outputCollection;

    public String iterationParam;

    public String mode;

    @JsonbTypeSerializer(BatchSizeJsonTypeSerializer.class)
    @JsonbTypeDeserializer(BatchSizeJsonTypeSerializer.class)
    @YamlTypeSerializer(StringNumberYamlTypeSerializer.class)
    @YamlTypeDeserializer(StringNumberYamlTypeSerializer.class)
    public Object batchSize;

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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Object getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Object batchSize) {
        this.batchSize = batchSize;
    }

    public ForEachState() {
        this.type = TYPE_FOR_EACH;
    }

    public ActionNode[] getActions() {
        return actions;
    }

    public String getInputCollection() {
        return inputCollection;
    }

    public void setInputCollection(String inputCollection) {
        this.inputCollection = inputCollection;
    }

    public String getOutputCollection() {
        return outputCollection;
    }

    public void setOutputCollection(String outputCollection) {
        this.outputCollection = outputCollection;
    }

    public String getIterationParam() {
        return iterationParam;
    }

    public void setIterationParam(String iterationParam) {
        this.iterationParam = iterationParam;
    }

    public void setActions(ActionNode[] actions) {
        this.actions = actions;
    }

    public StateDataFilter getStateDataFilter() {
        return stateDataFilter;
    }

    public void setStateDataFilter(StateDataFilter stateDataFilter) {
        this.stateDataFilter = stateDataFilter;
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public ForEachState setMetadata(Metadata metadata) {
        this.metadata = metadata;
        return this;
    }

    @Override
    public Object getTransition() {
        return transition;
    }

    @Override
    public ForEachState setTransition(Object transition) {
        this.transition = transition;
        return this;
    }

    @Override
    public Object getEnd() {
        return end;
    }

    @Override
    public ForEachState setEnd(Object end) {
        this.end = end;
        return this;
    }

    @Override
    public ErrorTransition[] getOnErrors() {
        return onErrors;
    }

    @Override
    public ForEachState setOnErrors(ErrorTransition[] onErrors) {
        this.onErrors = onErrors;
        return this;
    }

    public Object getTimeouts() {
        return timeouts;
    }

    public void setTimeouts(Object timeouts) {
        this.timeouts = timeouts;
    }

    @Override
    public String getCompensatedBy() {
        return compensatedBy;
    }

    @Override
    public ForEachState setCompensatedBy(String compensatedBy) {
        this.compensatedBy = compensatedBy;
        return this;
    }
}
