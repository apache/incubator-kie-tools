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
import org.kie.workbench.common.stunner.sw.marshall.json.StringOrValueHolderJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.StringOrValueHolderYamlTypeSerializer;
import org.treblereel.gwt.json.mapper.annotation.JSONMapper;
import org.treblereel.gwt.yaml.api.annotation.YAMLMapper;
import org.treblereel.gwt.yaml.api.annotation.YamlPropertyOrder;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeDeserializer;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeSerializer;
import org.treblereel.j2cl.processors.annotations.GWT3Export;

@JSONMapper
@YAMLMapper
@JsType
@YamlPropertyOrder({"refName", "selectionSet", "invoke", "arguments"})
@GWT3Export
public class FunctionRef {

    public String refName;

    public String selectionSet;

    public FunctionRefType invoke;

    @JsonbTypeSerializer(StringOrValueHolderJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(StringOrValueHolderJsonbTypeSerializer.class)
    @YamlTypeSerializer(StringOrValueHolderYamlTypeSerializer.class)
    @YamlTypeDeserializer(StringOrValueHolderYamlTypeSerializer.class)
    private Object arguments;

    public final String getRefName() {
        return refName;
    }

    public final void setRefName(String refName) {
        this.refName = refName;
    }

    public final String getSelectionSet() {
        return selectionSet;
    }

    public final void setSelectionSet(String selectionSet) {
        this.selectionSet = selectionSet;
    }

    public final FunctionRefType getInvoke() {
        return invoke;
    }

    public final void setInvoke(FunctionRefType invoke) {
        this.invoke = invoke;
    }

    public final Object getArguments() {
        return arguments;
    }

    public final void setArguments(Object arguments) {
        this.arguments = arguments;
    }
}
