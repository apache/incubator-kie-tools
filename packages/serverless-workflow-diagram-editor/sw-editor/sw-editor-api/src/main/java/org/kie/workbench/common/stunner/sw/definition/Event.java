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
import org.kie.workbench.common.stunner.sw.definition.custom.WorkflowFunctionsJsonDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.WorkflowFunctionsJsonSerializer;

/**
 * Used to define events and their correlations.
 */
@JSONMapper
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class Event {

    /**
     * Unique event name.
     */
    private String name;

    /**
     * {@link CloudEvent } source
     */
    private String source;

    /**
     * @link CloudEvent type
     */
    private String type;

    private Kind kind;

    private Boolean dataOnly;

    private Correlation[] correlation;

    @JsonbTypeSerializer(WorkflowFunctionsJsonSerializer.class)
    @JsonbTypeDeserializer(WorkflowFunctionsJsonDeserializer.class)
    private Object functions;

    @JsOverlay
    public final String getName() {
        return name;
    }

    @JsOverlay
    public final void setName(String name) {
        this.name = name;
    }

    @JsOverlay
    public final String getSource() {
        return source;
    }

    @JsOverlay
    public final void setSource(String source) {
        this.source = source;
    }

    @JsOverlay
    public final String getType() {
        return type;
    }

    @JsOverlay
    public final void setType(String type) {
        this.type = type;
    }

    @JsOverlay
    public final Boolean getDataOnly() {
        return dataOnly;
    }

    @JsOverlay
    public final void setDataOnly(Boolean dataOnly) {
        this.dataOnly = dataOnly;
    }

    @JsOverlay
    public final Kind getKind() {
        return kind;
    }

    @JsOverlay
    public final void setKind(Kind kind) {
        this.kind = kind;
    }

    @JsOverlay
    public final Correlation[] getCorrelation() {
        return correlation;
    }

    @JsOverlay
    public final void setCorrelation(Correlation[] correlation) {
        this.correlation = correlation;
    }

    @JsOverlay
    public final Object getFunctions() {
        return functions;
    }

    @JsOverlay
    public final void setFunctions(Object functions) {
        this.functions = functions;
    }
}
