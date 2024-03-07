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
import org.kie.workbench.common.stunner.sw.marshall.json.WorkflowTimeoutsJsonSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.WorkflowTimeoutsYamlSerializer;

@JSONMapper
@YAMLMapper
@JsType
@GWT3Export
@YamlPropertyOrder({"name", "type", "dataConditions", "eventConditions", "transition", "end", "onErrors", "usedForCompensation", "compensatedBy", "dataConditions", "stateDataFilter", "timeouts", "eventTimeout", "metadata"})
public class SwitchState extends State<SwitchState> implements HasErrors<SwitchState>, HasCompensatedBy<SwitchState>, HasMetadata<SwitchState> {

    public static final String TYPE_SWITCH = "switch";

    public DefaultConditionTransition defaultCondition;

    public EventConditionTransition[] eventConditions;

    public DataConditionTransition[] dataConditions;

    public Boolean usedForCompensation;

    public StateDataFilter stateDataFilter;

    public Metadata metadata;

    public ErrorTransition[] onErrors;

    @JsonbTypeSerializer(WorkflowTimeoutsJsonSerializer.class)
    @JsonbTypeDeserializer(WorkflowTimeoutsJsonSerializer.class)
    @YamlTypeSerializer(WorkflowTimeoutsYamlSerializer.class)
    @YamlTypeDeserializer(WorkflowTimeoutsYamlSerializer.class)
    public Object timeouts;

    public String compensatedBy;

    public SwitchState() {
        this.type = TYPE_SWITCH;
    }

    public DefaultConditionTransition getDefaultCondition() {
        return defaultCondition;
    }

    public SwitchState setDefaultCondition(DefaultConditionTransition defaultCondition) {
        this.defaultCondition = defaultCondition;
        return this;
    }

    public EventConditionTransition[] getEventConditions() {
        return eventConditions;
    }

    public void setEventConditions(EventConditionTransition[] eventConditions) {
        this.eventConditions = eventConditions;
    }

    public DataConditionTransition[] getDataConditions() {
        return dataConditions;
    }

    public SwitchState setDataConditions(DataConditionTransition[] dataConditions) {
        this.dataConditions = dataConditions;
        return this;
    }

    public Boolean getUsedForCompensation() {
        return usedForCompensation;
    }

    public void setUsedForCompensation(Boolean usedForCompensation) {
        this.usedForCompensation = usedForCompensation;
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
    public SwitchState setMetadata(Metadata metadata) {
        this.metadata = metadata;
        return this;
    }

    @Override
    public ErrorTransition[] getOnErrors() {
        return onErrors;
    }

    @Override
    public SwitchState setOnErrors(ErrorTransition[] onErrors) {
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
    public SwitchState setCompensatedBy(String compensatedBy) {
        this.compensatedBy = compensatedBy;
        return this;
    }
}
