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

import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jsinterop.annotations.JsType;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.client.json.mapper.annotation.JSONMapper;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;
import org.kie.workbench.common.stunner.sw.definition.custom.ConstantsValueHolderJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.definition.custom.ErrorJsonDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.ErrorJsonSerializer;
import org.kie.workbench.common.stunner.sw.definition.custom.EventJsonbTypeDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.EventJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.definition.custom.StartDefinitionJsonbTypeDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.StartDefinitionJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.definition.custom.StateJsonDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.StateJsonSerializer;
import org.kie.workbench.common.stunner.sw.definition.custom.ValueHolderJsonbTypeDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.WorkflowFunctionsJsonDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.WorkflowFunctionsJsonSerializer;
import org.kie.workbench.common.stunner.sw.definition.custom.WorkflowTimeoutsJsonDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.WorkflowTimeoutsJsonSerializer;

/**
 * Represents a workflow instance. A single workflow execution corresponding to the instructions provided by a workflow definition.
 *
 * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#Workflow-definition"> Workflow definition </a>
 */
@Bindable
@Definition
@CanContain(roles = {Workflow.LABEL_ROOT_NODE})
@JSONMapper
@JsType
// TODO: Missing to create a custom GraphFactory, so when creating a new graph it just adds the parent Workflow node by default?
public class Workflow {

    public static final String LABEL_WORKFLOW = "workflow";
    public static final String LABEL_ROOT_NODE = "rootNode";

    @Category
    public static final transient String category = Categories.STATES;

    @Labels
    public static final Set<String> labels = Stream.of(LABEL_WORKFLOW).collect(Collectors.toSet());

    /**
     *  Workflow unique identifier.
     */
    @Property
    private String id;

    /**
     *  Domain-specific workflow identifier
     */
    @Property
    private String key;

    /**
     * Workflow name.
     */
    @Property(meta = PropertyMetaTypes.NAME)
    private String name;

    private String description;

    private String specVersion;

    private String version;

    @JsonbTypeSerializer(ConstantsValueHolderJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(ValueHolderJsonbTypeDeserializer.class)
    private ValueHolder constants;

    /**
     * Workflow start definition.
     */
    @JsonbTypeSerializer(StartDefinitionJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(StartDefinitionJsonbTypeDeserializer.class)
    private Object start;

    /**
     * Workflow event definitions.
     */
    @JsonbTypeSerializer(EventJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(EventJsonbTypeDeserializer.class)
    private Object events; //TODO array or string

    /**
     * Workflow state definitions.
     */
    @JsonbTypeSerializer(StateJsonSerializer.class)
    @JsonbTypeDeserializer(StateJsonDeserializer.class)
    private State[] states;

    private Boolean keepActive;

    @JsonbTypeSerializer(WorkflowFunctionsJsonSerializer.class)
    @JsonbTypeDeserializer(WorkflowFunctionsJsonDeserializer.class)
    private Object functions;

    private Boolean autoRetries;

    @JsonbTypeSerializer(WorkflowTimeoutsJsonSerializer.class)
    @JsonbTypeDeserializer(WorkflowTimeoutsJsonDeserializer.class)
    private Object timeouts;

    @JsonbTypeSerializer(ErrorJsonSerializer.class)
    @JsonbTypeDeserializer(ErrorJsonDeserializer.class)
    private Object errors;

    private Retry[] retries;

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

    public Set<String> getLabels() {
        return labels;
    }

    public String getCategory() {
        return category;
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
