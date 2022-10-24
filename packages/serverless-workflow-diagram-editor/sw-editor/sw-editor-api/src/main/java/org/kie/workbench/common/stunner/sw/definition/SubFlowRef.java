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
import org.kie.workbench.common.stunner.sw.definition.custom.SubflowExecutionTypeJsonbTypeDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.SubflowExecutionTypeJsonbTypeSerializer;

@JSONMapper
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class SubFlowRef {

    private String workflowId;
    private String version;
    private FunctionRefType invoke;


    //TODO custom ser/deser because of continue is reserved java lit
    @JsonbTypeSerializer(SubflowExecutionTypeJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(SubflowExecutionTypeJsonbTypeDeserializer.class)
    private SubflowExecutionType onParentComplete;

    @JsOverlay
    public final String getWorkflowId() {
        return workflowId;
    }

    @JsOverlay
    public final void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    @JsOverlay
    public final String getVersion() {
        return version;
    }

    @JsOverlay
    public final void setVersion(String version) {
        this.version = version;
    }

    @JsOverlay
    public final FunctionRefType getInvoke() {
        return invoke;
    }

    @JsOverlay
    public final void setInvoke(FunctionRefType invoke) {
        this.invoke = invoke;
    }

    @JsOverlay
    public final SubflowExecutionType getOnParentComplete() {
        return onParentComplete;
    }

    @JsOverlay
    public final void setOnParentComplete(SubflowExecutionType onParentComplete) {
        this.onParentComplete = onParentComplete;
    }
}
