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
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.sw.definition.custom.FunctionRefJsonDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.FunctionRefJsonSerializer;
import org.kie.workbench.common.stunner.sw.definition.custom.SubFlowRefJsonDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.SubFlowRefJsonSerializer;

/**
 * Actions specify invocations of services or other workflows during workflow execution.
 * Note that functionRef, eventRef, and subFlowRef are mutually exclusive, meaning that only one of them can be specified
 * in a single action definition.
 *
 * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#Action-Definition"> Action definition </a>
 */
@Bindable
@Definition
@MorphBase(defaultType = CallFunctionAction.class)
@JsType
public class ActionNode {

    public static final String LABEL_ACTION = "action";

    @Category
    public static final transient String category = Categories.ACTIONS;

    @Labels
    private static final Set<String> labels = Stream.of(Workflow.LABEL_ROOT_NODE,
                                                        LABEL_ACTION).collect(Collectors.toSet());

    @Property
    private String id;

    /**
     * Unique action name.
     */
    @Property(meta = PropertyMetaTypes.NAME)
    private String name;

    /**
     * References to a reusable function definition.
     */

    @JsonbTypeSerializer(FunctionRefJsonSerializer.class)
    @JsonbTypeDeserializer(FunctionRefJsonDeserializer.class)
    private Object functionRef;

    /**
     * Reference to a trigger and result reusable event definition.
     */
    private ActionEventRef eventRef;

    /**
     * Reference to a workflow to be invoked.
     */
    @JsonbTypeSerializer(SubFlowRefJsonSerializer.class)
    @JsonbTypeDeserializer(SubFlowRefJsonDeserializer.class)
    private Object subFlowRef;

    private String retryRef;

    private Sleep sleep;

    private String[] retryableErrors;

    private String[] nonRetryableErrors;

    private ActionDataFilters actionDataFilter;

    private String condition;

    public ActionNode() {
    }

    public ActionNode setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public ActionNode setId(String id) {
        this.id = id;
        return this;
    }

    public Object getFunctionRef() {
        return functionRef;
    }

    public ActionNode setFunctionRef(Object functionRef) {
        this.functionRef = functionRef;
        return this;
    }

    public ActionEventRef getEventRef() {
        return eventRef;
    }

    public ActionNode setEventRef(ActionEventRef eventRef) {
        this.eventRef = eventRef;
        return this;
    }

    public Object getSubFlowRef() {
        return subFlowRef;
    }

    public ActionNode setSubFlowRef(Object subFlowRef) {
        this.subFlowRef = subFlowRef;
        return this;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public String getCategory() {
        return category;
    }

    public String getRetryRef() {
        return retryRef;
    }

    public void setRetryRef(String retryRef) {
        this.retryRef = retryRef;
    }

    public Sleep getSleep() {
        return sleep;
    }

    public void setSleep(Sleep sleep) {
        this.sleep = sleep;
    }

    public String[] getRetryableErrors() {
        return retryableErrors;
    }

    public void setRetryableErrors(String[] retryableErrors) {
        this.retryableErrors = retryableErrors;
    }

    public String[] getNonRetryableErrors() {
        return nonRetryableErrors;
    }

    public void setNonRetryableErrors(String[] nonRetryableErrors) {
        this.nonRetryableErrors = nonRetryableErrors;
    }

    public ActionDataFilters getActionDataFilter() {
        return actionDataFilter;
    }

    public void setActionDataFilter(ActionDataFilters actionDataFilter) {
        this.actionDataFilter = actionDataFilter;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
