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
import org.kie.workbench.common.stunner.client.json.mapper.annotation.JSONMapper;

@JSONMapper
@JsType
public class ContinueAs {

    private String workflowId;
    private String version;
    private String data;
    private String workflowExecTimeout;

    public final String getWorkflowId() {
        return workflowId;
    }

    public final void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public final String getVersion() {
        return version;
    }

    public final void setVersion(String version) {
        this.version = version;
    }

    public final String getData() {
        return data;
    }

    public final void setData(String data) {
        this.data = data;
    }

    public final String getWorkflowExecTimeout() {
        return workflowExecTimeout;
    }

    public final void setWorkflowExecTimeout(String workflowExecTimeout) {
        this.workflowExecTimeout = workflowExecTimeout;
    }
}
