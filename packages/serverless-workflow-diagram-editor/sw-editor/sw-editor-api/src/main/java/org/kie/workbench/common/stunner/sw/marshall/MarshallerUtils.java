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

package org.kie.workbench.common.stunner.sw.marshall;

import java.util.Stack;

import elemental2.core.Global;
import elemental2.core.JsObject;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.sw.definition.State;
import org.kie.workbench.common.stunner.sw.definition.Workflow;
import org.uberfire.commons.Pair;

public class MarshallerUtils {

    @SuppressWarnings("all")
    public static String getStateNodeName(Node node) {
        if (null != node) {
            Object definition = ((View) node.getContent()).getDefinition();
            if (definition instanceof State) {
                String name = ((State) definition).getName();
                return name;
            }
        }
        return null;
    }

    @SuppressWarnings("all")
    public static <T> T getElementDefinition(Element node) {
        return null != node ? (T) ((Definition) node.getContent()).getDefinition() : null;
    }

    public static boolean isValidString(String s) {
        return null != s && s.trim().length() > 0;
    }

    public static <T> T parse(FactoryManager factoryManager, Class<? extends T> type, T jso) {
        T instance = factoryManager.newDefinition(type.getName());
        return parse(instance, jso);
    }

    public static <T> T parse(T instance, T jso) {
        return (T) JsObject.assign(instance, jso);
    }

    private static <T> T stunnerMerge(DefinitionManager definitionManager, Object instance, Object jso) {
        JsPropertyMap<Object> instanceMap = Js.asPropertyMap(instance);
        JsPropertyMap<Object> jsoMap = Js.asPropertyMap(jso);
        String[] propertyFields = definitionManager.adapters().forDefinition().getPropertyFields(instance);
        for (String propertyField : propertyFields) {
            Object value = jsoMap.get(propertyField);
            if (null != value) {
                instanceMap.set(propertyField, value);
            }
        }
        return Js.uncheckedCast(instance);
    }

    /**
     * The original JSON could have the properties, that are not defined in Java based models.
     * But we still need to have them in the JSON after the serialization. So we preserve them
     * in the "__original__" field of the definition after deserialization.
     * <p>
     * After we finished the serialization of the workflow, we merge the original JSON with the JSON, that
     * is generated from the definition. We can skip the check for the "function" property, because initially
     * it's a JSON object, that has no "function" properties. So it's safe to merge it with the resulted JSON.
     *
     * @param json     - the original JSON
     * @param workflow - the definition of the workflow
     */
    static void onPostDeserialize(String json, Workflow workflow, DocType docType) {
        if(docType == DocType.JSON) {
            Object parsed = Global.JSON.parse(json);
            Js.asPropertyMap(workflow).set("__original__", parsed);
        } else {
            Js.asPropertyMap(workflow).set("__original__", "empty");

        }
    }

    static String onPostSerialize(String model, Workflow workflow, DocType docType) {
        if(docType == DocType.YAML) {
            return model;
        }
        Object parsed = Global.JSON.parse(model);
        merge(Js.asPropertyMap(workflow).get("__original__"), parsed);
        return Global.JSON.stringify(parsed);
    }

    private static void merge(Object o1, Object o2) {
        Pair<Object, Object> pair = new Pair<>(o1, o2);
        Stack<Pair<Object, Object>> stack = new Stack<>();
        stack.push(pair);
        while (!stack.isEmpty()) {
            Pair<Object, Object> current = stack.pop();
            JsPropertyMap<Object> old = Js.asPropertyMap(current.getK1());
            JsPropertyMap<Object> _new = Js.asPropertyMap(current.getK2());
            old.forEach(key -> {
                if (Js.typeof(old.get(key)).equals("object")) {
                    if (_new.has(key)) {
                        stack.push(new Pair<>(old.get(key), _new.get(key)));
                    } else {
                        _new.set(key, old.get(key));
                    }
                } else {
                    if (!_new.has(key)) {
                        _new.set(key, old.get(key));
                    }
                }
            });
        }
    }

}
