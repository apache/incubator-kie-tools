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
import org.kie.workbench.common.stunner.sw.definition.custom.WorkflowFunctionsJsonDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.WorkflowFunctionsJsonSerializer;

/**
 * Used to define events and their correlations.
 */
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getDataOnly() {
        return dataOnly;
    }

    public void setDataOnly(Boolean dataOnly) {
        this.dataOnly = dataOnly;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public Correlation[] getCorrelation() {
        return correlation;
    }

    public void setCorrelation(Correlation[] correlation) {
        this.correlation = correlation;
    }

    public Object getFunctions() {
        return functions;
    }

    public void setFunctions(Object functions) {
        this.functions = functions;
    }
}
