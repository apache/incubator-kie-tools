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

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.client.json.mapper.annotation.JSONMapper;

@JSONMapper
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class Function {

    private String name;

    private String operation;

    private FunctionType type;

    private String authRef;

    @JsOverlay
    public final String getName() {
        return name;
    }

    @JsOverlay
    public final void setName(String name) {
        this.name = name;
    }

    @JsOverlay
    public final String getOperation() {
        return operation;
    }

    @JsOverlay
    public final void setOperation(String operation) {
        this.operation = operation;
    }

    @JsOverlay
    public final FunctionType getType() {
        return type;
    }

    @JsOverlay
    public final void setType(FunctionType type) {
        this.type = type;
    }

    @JsOverlay
    public final String getAuthRef() {
        return authRef;
    }

    @JsOverlay
    public final void setAuthRef(String authRef) {
        this.authRef = authRef;
    }
}
