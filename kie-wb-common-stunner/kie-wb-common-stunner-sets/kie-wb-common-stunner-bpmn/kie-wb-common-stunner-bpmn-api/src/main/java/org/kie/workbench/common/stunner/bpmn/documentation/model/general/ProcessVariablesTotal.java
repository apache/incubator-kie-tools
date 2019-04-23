/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.documentation.model.general;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.core.client.util.js.KeyValue;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ProcessVariablesTotal {

    private Integer total;
    private Integer totalVariables;
    private KeyValue[] variables;

    private ProcessVariablesTotal() {

    }

    @JsOverlay
    public static final ProcessVariablesTotal create(Integer total, Integer totalVariables, KeyValue[] variables) {
        final ProcessVariablesTotal instance = new ProcessVariablesTotal();
        instance.total = total;
        instance.totalVariables = totalVariables;
        instance.variables = variables;
        return instance;
    }

    @JsOverlay
    public final Integer getTotal() {
        return total;
    }

    @JsOverlay
    public final Integer getTotalVariables() {
        return totalVariables;
    }

    @JsOverlay
    public final KeyValue[] getVariables() {
        return variables;
    }
}