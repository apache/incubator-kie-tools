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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class Ids {

    public static String fromString(String myString) {
        try {
            return "_" + UUID.nameUUIDFromBytes(myString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            return "_" + UUID.nameUUIDFromBytes(myString.getBytes());
        }
    }

    public static String item(String itemId) {
        return "_" + itemId + "Item";
    }

    public static String typedIdentifier(String parentScopeId, String identifier) {
        return "var_" + parentScopeId + "_" + identifier;
    }

    public static String dataInput(String parentId, String inputId) {
        return parentId + "_" + inputId + "InputX";
    }

    public static String dataInputItem(String parentId, String inputId) {
        return "_" + dataInput(parentId, inputId) + "Item";
    }

    public static String dataOutput(String parentId, String outputId) {
        return parentId + "_" + outputId + "OutputX";
    }

    public static String dataOutputItem(String parentId, String outputId) {
        return "_" + Ids.dataOutput(parentId, outputId) + "Item";
    }

    public static String bpmnShape(String parentId) {
        return "shape_" + parentId;
    }

    public static String bpmnEdge(String source, String target) {
        return "edge_" + source + "_to_" + target;
    }
}
