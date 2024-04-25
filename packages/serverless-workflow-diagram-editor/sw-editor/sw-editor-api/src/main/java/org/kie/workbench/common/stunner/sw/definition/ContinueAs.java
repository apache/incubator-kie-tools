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

import jsinterop.annotations.JsType;
import org.kie.j2cl.tools.json.mapper.annotation.JSONMapper;
import org.kie.j2cl.tools.processors.annotations.GWT3Export;
import org.kie.j2cl.tools.yaml.mapper.api.annotation.YAMLMapper;

@JSONMapper
@YAMLMapper
@JsType
@GWT3Export
public class ContinueAs {

    public String workflowId;

    public String version;

    public String data;

    public String workflowExecTimeout;

    public final String getWorkflowId() {
        return workflowId;
    }

    public final ContinueAs setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
        return this;
    }

    public final String getVersion() {
        return version;
    }

    public final ContinueAs setVersion(String version) {
        this.version = version;
        return this;
    }

    public final String getData() {
        return data;
    }

    public final ContinueAs setData(String data) {
        this.data = data;
        return this;
    }

    public final String getWorkflowExecTimeout() {
        return workflowExecTimeout;
    }

    public final void setWorkflowExecTimeout(String workflowExecTimeout) {
        this.workflowExecTimeout = workflowExecTimeout;
    }
}
