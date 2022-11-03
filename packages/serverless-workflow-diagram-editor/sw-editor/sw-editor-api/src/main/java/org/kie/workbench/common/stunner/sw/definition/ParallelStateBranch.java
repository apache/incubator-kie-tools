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

import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.sw.definition.custom.WorkflowTimeoutsJsonDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.WorkflowTimeoutsJsonSerializer;

@JsType
public class ParallelStateBranch {

    private String name;
    private ActionNode[] actions;

    @JsonbTypeSerializer(WorkflowTimeoutsJsonSerializer.class)
    @JsonbTypeDeserializer(WorkflowTimeoutsJsonDeserializer.class)
    private Object timeouts;

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final ActionNode[] getActions() {
        return actions;
    }

    public final void setActions(ActionNode[] actions) {
        this.actions = actions;
    }

    public final Object getTimeouts() {
        return timeouts;
    }

    public final void setTimeouts(Object timeouts) {
        this.timeouts = timeouts;
    }
}
