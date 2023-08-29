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


package org.kie.workbench.common.stunner.bpmn.documentation.model.general;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariableSerializer;
import org.kie.workbench.common.stunner.core.client.util.js.KeyValue;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ProcessVariablesTotal {

    private Integer total;
    private Integer totalVariables;
    private KeyValue[] variables;
    private VariableTriplets[] tripplets;

    private ProcessVariablesTotal() {

    }

    @JsOverlay
    public static final ProcessVariablesTotal create(Integer total, Integer totalVariables, KeyValue[] variables) {
        final ProcessVariablesTotal instance = new ProcessVariablesTotal();
        instance.total = total;
        instance.totalVariables = totalVariables;
        instance.variables = variables;
        instance.tripplets = instance.getVariablesAsTriplets();
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

    @JsOverlay
    public final VariableTriplets[] getVariablesAsTriplets() {
        final VariableTriplets[] triplets = new VariableTriplets[variables.length];
        for (int i = 0; i < variables.length; i++) {
            final ProcessVariableSerializer.VariableInfo info = (ProcessVariableSerializer.VariableInfo) variables[i].getValue();
            triplets[i] = VariableTriplets.create(variables[i].getKey(),
                                                  info.getType(),
                                                  info.getTags());
        }
        return triplets;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class VariableTriplets {

        private Object name;
        private Object type;
        private Object tags;

        private VariableTriplets() {
        }

        @JsOverlay
        public static final VariableTriplets create(final Object name, final Object type, final Object tags) {
            final VariableTriplets instance = new VariableTriplets();
            instance.name = name;
            instance.type = type;
            instance.tags = tags;
            return instance;
        }

        @JsOverlay
        public final Object getName() {
            return name;
        }

        @JsOverlay
        public final Object getType() {
            return type;
        }

        @JsOverlay
        public final Object getTags() {
            return tags;
        }
    }
}