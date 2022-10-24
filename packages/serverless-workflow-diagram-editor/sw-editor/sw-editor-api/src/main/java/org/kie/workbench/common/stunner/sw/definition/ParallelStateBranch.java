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
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.sw.definition.custom.WorkflowTimeoutsJsonDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.WorkflowTimeoutsJsonSerializer;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ParallelStateBranch {

    private String name;
    private ActionNode[] actions;

    @JsonbTypeSerializer(WorkflowTimeoutsJsonSerializer.class)
    @JsonbTypeDeserializer(WorkflowTimeoutsJsonDeserializer.class)
    private Object timeouts;

    @JsOverlay
    public final String getName() {
        return name;
    }

    @JsOverlay
    public final void setName(String name) {
        this.name = name;
    }

    @JsOverlay
    public final ActionNode[] getActions() {
        return actions;
    }

    @JsOverlay
    public final void setActions(ActionNode[] actions) {
        this.actions = actions;
    }

    @JsOverlay
    public final Object getTimeouts() {
        return timeouts;
    }

    @JsOverlay
    public final void setTimeouts(Object timeouts) {
        this.timeouts = timeouts;
    }
}
