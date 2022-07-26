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
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;

/**
 * Represents a workflow instance. A single workflow execution corresponding to the instructions provided by a workflow definition.
 *
 * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#Workflow-definition"> Workflow definition </a>
 */
@Bindable
@Definition
@CanContain(roles = {Workflow.LABEL_ROOT_NODE})
@JsType
// TODO: Missing to create a custom GraphFactory, so when creating a new graph it just adds the parent Workflow node by default?
public class Workflow {

    public static final String LABEL_WORKFLOW = "workflow";
    public static final String LABEL_ROOT_NODE = "rootNode";

    @Category
    @JsIgnore
    public static final transient String category = Categories.STATES;

    @Labels
    @JsIgnore
    public static final Set<String> labels = Stream.of(LABEL_WORKFLOW).collect(Collectors.toSet());

    /**
     *  Workflow unique identifier.
     */
    @Property
    public String id;

    /**
     *  Domain-specific workflow identifier
     */
    @Property
    public String key;

    /**
     * Workflow name.
     */
    @Property(meta = PropertyMetaTypes.NAME)
    public String name;

    /**
     * Workflow start definition.
     */
    public Object start;

    /**
     * Workflow event definitions.
     */
    public Event[] events;

    /**
     * Workflow state definitions.
     */
    public State[] states;

    // missing specVersion, functions

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

    public Event[] getEvents() {
        return events;
    }

    public Workflow setEvents(Event[] events) {
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
}
