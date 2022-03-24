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

@Bindable
@Definition
@MorphBase(defaultType = InjectState.class)
@JsType
public class State {

    public static final String LABEL_STATE = "state";

    @Category
    @JsIgnore
    public static final transient String category = Categories.STATES;

    @Labels
    @JsIgnore
    private static final Set<String> labels = Stream.of(Workflow.LABEL_ROOT_NODE,
                                                        LABEL_STATE).collect(Collectors.toSet());

    @Property(meta = PropertyMetaTypes.NAME)
    public String name;

    public String type;

    public String transition;

    public boolean end;

    public ErrorTransition[] onErrors;

    public State() {
        this.name = "State";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public ErrorTransition[] getOnErrors() {
        return onErrors;
    }

    public void setOnErrors(ErrorTransition[] onErrors) {
        this.onErrors = onErrors;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public String getCategory() {
        return category;
    }
}
