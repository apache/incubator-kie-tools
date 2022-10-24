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
import org.kie.workbench.common.stunner.sw.definition.custom.ArgumentsValueHolderJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.definition.custom.ValueHolderJsonbTypeDeserializer;

@JSONMapper
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class FunctionRef {

    private String refName;
    private String selectionSet;
    private FunctionRefType invoke;

    @JsonbTypeSerializer(ArgumentsValueHolderJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(ValueHolderJsonbTypeDeserializer.class)
    private ValueHolder arguments;

    @JsOverlay
    public final String getRefName() {
        return refName;
    }

    @JsOverlay
    public final void setRefName(String refName) {
        this.refName = refName;
    }

    @JsOverlay
    public final String getSelectionSet() {
        return selectionSet;
    }

    @JsOverlay
    public final void setSelectionSet(String selectionSet) {
        this.selectionSet = selectionSet;
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
    public final ValueHolder getArguments() {
        return arguments;
    }

    @JsOverlay
    public final void setArguments(ValueHolder arguments) {
        this.arguments = arguments;
    }
}
