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
import org.kie.workbench.common.stunner.client.json.mapper.annotation.JSONMapper;
import org.kie.workbench.common.stunner.sw.definition.custom.WorkflowExecTimeoutJsonDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.WorkflowExecTimeoutJsonSerializer;

@JSONMapper
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class WorkflowTimeouts {

    @JsonbTypeSerializer(WorkflowExecTimeoutJsonSerializer.class)
    @JsonbTypeDeserializer(WorkflowExecTimeoutJsonDeserializer.class)
    private Object workflowExecTimeout;
    private String stateExecTimeout;
    private String actionExecTimeout;
    private String branchExecTimeout;
    private String eventTimeout;

    @JsOverlay
    public final Object getWorkflowExecTimeout() {
        return workflowExecTimeout;
    }

    @JsOverlay
    public final void setWorkflowExecTimeout(Object workflowExecTimeout) {
        this.workflowExecTimeout = workflowExecTimeout;
    }

    @JsOverlay
    public final String getStateExecTimeout() {
        return stateExecTimeout;
    }

    @JsOverlay
    public final void setStateExecTimeout(String stateExecTimeout) {
        this.stateExecTimeout = stateExecTimeout;
    }

    @JsOverlay
    public final String getActionExecTimeout() {
        return actionExecTimeout;
    }

    @JsOverlay
    public final void setActionExecTimeout(String actionExecTimeout) {
        this.actionExecTimeout = actionExecTimeout;
    }

    @JsOverlay
    public final String getBranchExecTimeout() {
        return branchExecTimeout;
    }

    @JsOverlay
    public final void setBranchExecTimeout(String branchExecTimeout) {
        this.branchExecTimeout = branchExecTimeout;
    }

    @JsOverlay
    public final String getEventTimeout() {
        return eventTimeout;
    }

    @JsOverlay
    public final void setEventTimeout(String eventTimeout) {
        this.eventTimeout = eventTimeout;
    }
}
