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
import org.kie.workbench.common.stunner.sw.marshall.json.NumCompletedJsonTypeSerializer;
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
@YamlPropertyOrder({"name", "type", "transition", "stateDataFilter", "compensatedBy", "branches", "timeouts", "eventTimeout", "onErrors", "end", "metadata"})
@GWT3Export
public class ParallelState extends State<ParallelState> implements HasErrors<ParallelState>, HasEnd<ParallelState>, HasTransition<ParallelState>, HasCompensatedBy<ParallelState>, HasMetadata<ParallelState> {

    public static final String TYPE_PARALLEL = "parallel";

    public ParallelState() {
        this.type = TYPE_PARALLEL;
    }

    public String completionType;

    @JsonbTypeSerializer(NumCompletedJsonTypeSerializer.class)
    @JsonbTypeDeserializer(NumCompletedJsonTypeSerializer.class)
    @YamlTypeSerializer(StringNumberYamlTypeSerializer.class)
    @YamlTypeDeserializer(StringNumberYamlTypeSerializer.class)
    public Object numCompleted;

    public ParallelStateBranch[] branches;

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

    public String getCompletionType() {
        return completionType;
    }

    public void setCompletionType(String completionType) {
        this.completionType = completionType;
    }

    public ParallelStateBranch[] getBranches() {
        return branches;
    }

    public void setBranches(ParallelStateBranch[] branches) {
        this.branches = branches;
    }

    public Object getNumCompleted() {
        return numCompleted;
    }

    public void setNumCompleted(Object numCompleted) {
        this.numCompleted = numCompleted;
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
    public ParallelState setMetadata(Metadata metadata) {
        this.metadata = metadata;
        return this;
    }

    @Override
    public Object getTransition() {
        return transition;
    }

    @Override
    public ParallelState setTransition(Object transition) {
        this.transition = transition;
        return this;
    }

    @Override
    public Object getEnd() {
        return end;
    }

    @Override
    public ParallelState setEnd(Object end) {
        this.end = end;
        return this;
    }

    @Override
    public ErrorTransition[] getOnErrors() {
        return onErrors;
    }

    @Override
    public ParallelState setOnErrors(ErrorTransition[] onErrors) {
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
    public ParallelState setCompensatedBy(String compensatedBy) {
        this.compensatedBy = compensatedBy;
        return this;
    }
}
