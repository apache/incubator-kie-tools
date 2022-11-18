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

import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.sw.definition.State;

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

    public static <T> T getElementDefinition(Element node) {
        return null != node ? (T) ((Definition) node.getContent()).getDefinition() : null;
    }

    public static boolean isValidString(String s) {
        return null != s && s.trim().length() > 0;
    }

    public static <T> T parse(FactoryManager factoryManager, Class<? extends T> type, T jso) {
        T instance = factoryManager.newDefinition(type.getName());
        instance = nativeMerge(instance, jso);
        return (T) instance;
    }

    public static <T> T parse(T instance, T jso) {
        return nativeMerge(instance, jso);
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

    public static native <T> T nativeMerge(Object o1, Object o2) /*-{
        if (typeof Object.assign != 'function') {
            Object.assign = function (target) {
                'use strict';
                if (target == null) {
                    throw new TypeError('Cannot convert undefined or null to object');
                }

                target = Object(target);
                for (var index = 1; index < arguments.length; index++) {
                    var source = arguments[index];
                    if (source != null) {
                        for (var key in source) {
                            if (Object.prototype.hasOwnProperty.call(source, key)) {
                                target[key] = source[key];
                            }
                        }
                    }
                }
                return target;
            };
        }
        // return Object.assign({}, o1, o2);
        return Object.assign(o1, o2);
    }-*/;
}
