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

import jsinterop.annotations.JsType;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;

/**
 * Start definition explicitly defines how/when workflow instances should be created and what the workflow starting state is.
 * Can be either string or object type. If type string, it defines the name of the workflow starting state.
 *
 * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#Start-definition"> Start defintion </a>
 */
@Bindable
@Definition
@JsType
public class Start {

    public static final String LABEL_START = "start";

    @Property(meta = PropertyMetaTypes.NAME)
    private String name;

    @Category
    public static final transient String category = Categories.START;

    @Labels
    private static final Set<String> labels = Stream.of(Workflow.LABEL_ROOT_NODE,
                                                        LABEL_START).collect(Collectors.toSet());

    public Start() {
        this.name = "Start";
    }

    public Set<String> getLabels() {
        return labels;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
