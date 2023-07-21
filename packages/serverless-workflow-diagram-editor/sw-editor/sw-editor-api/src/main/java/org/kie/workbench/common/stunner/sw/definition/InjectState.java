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
import org.kie.workbench.common.stunner.sw.marshall.json.StateEndDefinitionJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.StateTransitionDefinitionJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.StringOrValueHolderJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.StateEndDefinitionYamlTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.StateTransitionDefinitionYamlTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.StringOrValueHolderYamlTypeSerializer;

@JSONMapper
@YAMLMapper
@JsType
@YamlPropertyOrder({"name", "type", "transition", "usedForCompensation", "stateDataFilter", "eventTimeout", "compensatedBy", "timeouts", "onErrors", "end", "data", "metadata"})
public class InjectState extends State<InjectState> {

    public static final String TYPE_INJECT = "inject";

    /**
     * JSON object as String which can be set as state's data input and can be manipulated via filter.
     */
    @JsonbTypeSerializer(StringOrValueHolderJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(StringOrValueHolderJsonbTypeSerializer.class)
    @YamlTypeSerializer(StringOrValueHolderYamlTypeSerializer.class)
    @YamlTypeDeserializer(StringOrValueHolderYamlTypeSerializer.class)
    public Object data;

    public Boolean usedForCompensation;

    public StateDataFilter stateDataFilter;

    public Metadata metadata;

    @JsonbTypeSerializer(StateTransitionDefinitionJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(StateTransitionDefinitionJsonbTypeSerializer.class)
    @YamlTypeSerializer(StateTransitionDefinitionYamlTypeSerializer.class)
    @YamlTypeDeserializer(StateTransitionDefinitionYamlTypeSerializer.class)
    public Object transition;

    @JsonbTypeSerializer(StateEndDefinitionJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(StateEndDefinitionJsonbTypeSerializer.class)
    @YamlTypeSerializer(StateEndDefinitionYamlTypeSerializer.class)
    @YamlTypeDeserializer(StateEndDefinitionYamlTypeSerializer.class)
    public Object end;

    public String compensatedBy;

    public InjectState() {
        this.type = TYPE_INJECT;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Boolean getUsedForCompensation() {
        return usedForCompensation;
    }

    public void setUsedForCompensation(Boolean usedForCompensation) {
        this.usedForCompensation = usedForCompensation;
    }

    public StateDataFilter getStateDataFilter() {
        return stateDataFilter;
    }

    public void setStateDataFilter(StateDataFilter stateDataFilter) {
        this.stateDataFilter = stateDataFilter;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public InjectState setMetadata(Metadata metadata) {
        this.metadata = metadata;
        return this;
    }

    public Object getTransition() {
        return transition;
    }

    public InjectState setTransition(Object transition) {
        this.transition = transition;
        return this;
    }

    public Object getEnd() {
        return end;
    }

    public InjectState setEnd(Object end) {
        this.end = end;
        return this;
    }

    public String getCompensatedBy() {
        return compensatedBy;
    }

    public InjectState setCompensatedBy(String compensatedBy) {
        this.compensatedBy = compensatedBy;
        return this;
    }
}
