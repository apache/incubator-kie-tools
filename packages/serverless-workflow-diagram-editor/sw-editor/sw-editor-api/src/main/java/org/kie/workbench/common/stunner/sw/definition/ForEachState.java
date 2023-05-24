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
import org.kie.workbench.common.stunner.client.json.mapper.annotation.JSONMapper;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YAMLMapper;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlPropertyOrder;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeDeserializer;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlTypeSerializer;
import org.kie.workbench.common.stunner.sw.definition.custom.json.BatchSizeJsonTypeSerializer;
import org.kie.workbench.common.stunner.sw.definition.custom.yaml.StringNumberYamlTypeSerializer;

@JSONMapper
@YAMLMapper
@JsType
@YamlPropertyOrder({"name", "type", "actions", "inputCollection", "outputCollection", "iterationParam", "transition", "stateDataFilter", "eventTimeout", "compensatedBy", "timeouts", "onErrors", "end", "metadata"})
public class ForEachState extends State {

    public static final String TYPE_FOR_EACH = "foreach";

    public ActionNode[] actions;

    public String inputCollection;

    public String outputCollection;

    public String iterationParam;

    public String mode;

    @JsonbTypeSerializer(BatchSizeJsonTypeSerializer.class)
    @JsonbTypeDeserializer(BatchSizeJsonTypeSerializer.class)
    @YamlTypeSerializer(StringNumberYamlTypeSerializer.class)
    @YamlTypeDeserializer(StringNumberYamlTypeSerializer.class)
    public Object batchSize;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Object getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Object batchSize) {
        this.batchSize = batchSize;
    }

    public ForEachState() {
        this.type = TYPE_FOR_EACH;
    }

    public ActionNode[] getActions() {
        return actions;
    }

    public String getInputCollection() {
        return inputCollection;
    }

    public void setInputCollection(String inputCollection) {
        this.inputCollection = inputCollection;
    }

    public String getOutputCollection() {
        return outputCollection;
    }

    public void setOutputCollection(String outputCollection) {
        this.outputCollection = outputCollection;
    }

    public String getIterationParam() {
        return iterationParam;
    }

    public void setIterationParam(String iterationParam) {
        this.iterationParam = iterationParam;
    }

    public void setActions(ActionNode[] actions) {
        this.actions = actions;
    }
}
