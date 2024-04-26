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
import org.kie.workbench.common.stunner.sw.marshall.json.ErrorJsonSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.EventJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.StartDefinitionJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.StateJsonSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.ValueHolderJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.WorkflowFunctionsJsonSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.WorkflowTimeoutsJsonSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.ErrorYamlSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.EventYamlTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.StartDefinitionYamlTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.StateYamlSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.ValueHolderYamlTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.WorkflowFunctionsYamlSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.WorkflowTimeoutsYamlSerializer;

@JSONMapper
@YAMLMapper
@JsType
@YamlPropertyOrder({"id", "version", "specVersion", "name", "description", "start", "states"})
// TODO: Missing to create a custom GraphFactory, so when creating a new graph it just adds the parent Workflow node by default?
@GWT3Export
public class Workflow {

    public String id;

    public String key;

    public String name;

    public String description;

    public String specVersion;

    public String version;

    @JsonbTypeSerializer(ValueHolderJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(ValueHolderJsonbTypeSerializer.class)
    @YamlTypeSerializer(ValueHolderYamlTypeSerializer.class)
    @YamlTypeDeserializer(ValueHolderYamlTypeSerializer.class)
    private ValueHolder constants;

    @JsonbTypeSerializer(StartDefinitionJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(StartDefinitionJsonbTypeSerializer.class)
    @YamlTypeSerializer(StartDefinitionYamlTypeSerializer.class)
    @YamlTypeDeserializer(StartDefinitionYamlTypeSerializer.class)
    private Object start;

    @JsonbTypeSerializer(EventJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(EventJsonbTypeSerializer.class)
    @YamlTypeSerializer(EventYamlTypeSerializer.class)
    @YamlTypeDeserializer(EventYamlTypeSerializer.class)
    private Object events; //TODO array or string

    @JsonbTypeSerializer(StateJsonSerializer.class)
    @JsonbTypeDeserializer(StateJsonSerializer.class)
    @YamlTypeSerializer(StateYamlSerializer.class)
    @YamlTypeDeserializer(StateYamlSerializer.class)
    private State[] states;

    public Boolean keepActive;

    @JsonbTypeSerializer(WorkflowFunctionsJsonSerializer.class)
    @JsonbTypeDeserializer(WorkflowFunctionsJsonSerializer.class)
    @YamlTypeSerializer(WorkflowFunctionsYamlSerializer.class)
    @YamlTypeDeserializer(WorkflowFunctionsYamlSerializer.class)
    private Object functions;

    public Boolean autoRetries;

    @JsonbTypeSerializer(WorkflowTimeoutsJsonSerializer.class)
    @JsonbTypeDeserializer(WorkflowTimeoutsJsonSerializer.class)
    @YamlTypeSerializer(WorkflowTimeoutsYamlSerializer.class)
    @YamlTypeDeserializer(WorkflowTimeoutsYamlSerializer.class)
    private Object timeouts;

    @JsonbTypeSerializer(ErrorJsonSerializer.class)
    @JsonbTypeDeserializer(ErrorJsonSerializer.class)
    @YamlTypeSerializer(ErrorYamlSerializer.class)
    @YamlTypeDeserializer(ErrorYamlSerializer.class)
    private Object errors;

    public Retry[] retries;

    public Workflow() {
    }

    public String getId() {
        return id;
    }

    public Workflow setId(String id) {
        this.id = id;
        return this;
    }

    public String getKey() {
        return key;
    }

    public Workflow setKey(String key) {
        this.key = key;
        return this;
    }

    public String getName() {
        return name;
    }

    public Workflow setName(String name) {
        this.name = name;
        return this;
    }

    public Object getStart() {
        return start;
    }

    public Workflow setStart(Object start) {
        this.start = start;
        return this;
    }

    public Object getEvents() {
        return events;
    }

    public Workflow setEvents(Object events) {
        this.events = events;
        return this;
    }

    public State[] getStates() {
        return states;
    }

    public Workflow setStates(State[] states) {
        this.states = states;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecVersion() {
        return specVersion;
    }

    public void setSpecVersion(String specVersion) {
        this.specVersion = specVersion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getKeepActive() {
        return keepActive;
    }

    public void setKeepActive(Boolean keepActive) {
        this.keepActive = keepActive;
    }

    public Object getFunctions() {
        return functions;
    }

    public void setFunctions(Object functions) {
        this.functions = functions;
    }

    public Boolean getAutoRetries() {
        return autoRetries;
    }

    public void setAutoRetries(Boolean autoRetries) {
        this.autoRetries = autoRetries;
    }

    public Object getTimeouts() {
        return timeouts;
    }

    public void setTimeouts(Object timeouts) {
        this.timeouts = timeouts;
    }

    public ValueHolder getConstants() {
        return constants;
    }

    public void setConstants(ValueHolder constants) {
        this.constants = constants;
    }

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Object errors) {
        this.errors = errors;
    }

    public Retry[] getRetries() {
        return retries;
    }

    public void setRetries(Retry[] retries) {
        this.retries = retries;
    }
}
