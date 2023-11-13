/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.sw.definition;

import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.sw.marshall.json.WorkflowExecTimeoutJsonSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.WorkflowExecTimeoutYamlSerializer;
import org.treblereel.gwt.json.mapper.annotation.JSONMapper;
import org.treblereel.gwt.yaml.api.annotation.YAMLMapper;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeDeserializer;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeSerializer;
import org.treblereel.j2cl.processors.annotations.GWT3Export;

@JSONMapper
@YAMLMapper
@JsType
@GWT3Export
public class WorkflowTimeouts {

    @JsonbTypeSerializer(WorkflowExecTimeoutJsonSerializer.class)
    @JsonbTypeDeserializer(WorkflowExecTimeoutJsonSerializer.class)
    @YamlTypeSerializer(WorkflowExecTimeoutYamlSerializer.class)
    @YamlTypeDeserializer(WorkflowExecTimeoutYamlSerializer.class)
    private Object workflowExecTimeout;
    private String stateExecTimeout;
    private String actionExecTimeout;
    private String branchExecTimeout;
    private String eventTimeout;

    public final Object getWorkflowExecTimeout() {
        return workflowExecTimeout;
    }

    public final void setWorkflowExecTimeout(Object workflowExecTimeout) {
        this.workflowExecTimeout = workflowExecTimeout;
    }

    public final String getStateExecTimeout() {
        return stateExecTimeout;
    }

    public final void setStateExecTimeout(String stateExecTimeout) {
        this.stateExecTimeout = stateExecTimeout;
    }

    public final String getActionExecTimeout() {
        return actionExecTimeout;
    }

    public final void setActionExecTimeout(String actionExecTimeout) {
        this.actionExecTimeout = actionExecTimeout;
    }

    public final String getBranchExecTimeout() {
        return branchExecTimeout;
    }

    public final void setBranchExecTimeout(String branchExecTimeout) {
        this.branchExecTimeout = branchExecTimeout;
    }

    public final String getEventTimeout() {
        return eventTimeout;
    }

    public final void setEventTimeout(String eventTimeout) {
        this.eventTimeout = eventTimeout;
    }
}
