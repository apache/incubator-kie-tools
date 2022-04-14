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

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;

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
    @JsIgnore
    public static final transient String category = Categories.ACTIONS;

    @Labels
    @JsIgnore
    private static final Set<String> labels = Stream.of(Workflow.LABEL_ROOT_NODE,
                                                        LABEL_ACTION).collect(Collectors.toSet());

    @Property
    public String id;

    /**
     * Unique action name.
     */
    @Property(meta = PropertyMetaTypes.NAME)
    public String name;

    /**
     * References to a reusable function definition.
     */
    public String functionRef;

    /**
     * Reference to a trigger and result reusable event definition.
     */
    public String eventRef;

    /**
     * Reference to a workflow to be invoked.
     */
    public String subFlowRef;

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

    public String getFunctionRef() {
        return functionRef;
    }

    public ActionNode setFunctionRef(String functionRef) {
        this.functionRef = functionRef;
        return this;
    }

    public String getEventRef() {
        return eventRef;
    }

    public ActionNode setEventRef(String eventRef) {
        this.eventRef = eventRef;
        return this;
    }

    public String getSubFlowRef() {
        return subFlowRef;
    }

    public ActionNode setSubFlowRef(String subFlowRef) {
        this.subFlowRef = subFlowRef;
        return this;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public String getCategory() {
        return category;
    }
}
