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

import jsinterop.annotations.JsType;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.client.json.mapper.annotation.JSONMapper;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;

/**
 * Parallel state defines a collection of branches that are executed in parallel.
 * A parallel state can be seen a state which splits up the current workflow instance execution path into multiple ones,
 * one for each branch. These execution paths are performed in parallel and are joined back into the current execution
 * path depending on the defined completionType parameter value.
 *
 * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#Parallel-State"> Parallel state </a>
 */
@Bindable
@Definition
@Morph(base = State.class)
@JSONMapper
@JsType
public class ParallelState extends State {

    public static final String TYPE_PARALLEL = "parallel";

    public ParallelState() {
        this.type = TYPE_PARALLEL;
    }

    private String completionType;

    private ParallelStateBranch[] branches;

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
}
