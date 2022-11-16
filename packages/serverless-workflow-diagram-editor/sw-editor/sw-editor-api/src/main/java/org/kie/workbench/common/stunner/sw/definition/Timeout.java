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

import jakarta.json.bind.annotation.JsonbTransient;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase;

// TODO: Timeouts are not being used for now, consider dropping all related classes and usages.
@MorphBase(defaultType = EventTimeout.class)
public class Timeout {

    public static final String LABEL_TIMEOUT = "timeout";

    @Category
    public static final transient String category = Categories.TIMEOUTS;

    @Labels
    @JsonbTransient
    private final Set<String> labels = Stream.of(Workflow.LABEL_ROOT_NODE,
                                                 LABEL_TIMEOUT).collect(Collectors.toSet());

    public Timeout() {
    }

    public Set<String> getLabels() {
        return labels;
    }

    public String getCategory() {
        return category;
    }
}
