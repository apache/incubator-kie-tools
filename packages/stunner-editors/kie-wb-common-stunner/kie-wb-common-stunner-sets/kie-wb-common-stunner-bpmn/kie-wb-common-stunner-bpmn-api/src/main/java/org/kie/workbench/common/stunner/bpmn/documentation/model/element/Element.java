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

package org.kie.workbench.common.stunner.bpmn.documentation.model.element;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.core.client.util.js.JsConverter;
import org.kie.workbench.common.stunner.core.client.util.js.KeyValue;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class Element {

    private String name;
    private String type;
    private String title;
    private String icon;
    private KeyValue[] properties;

    private Element() {

    }

    @JsOverlay
    public static final Element create(String name, String type, String title, String icon,
                                       Map<String, String> properties) {
        final Element instance = new Element();
        instance.name = name;
        instance.type = type;
        instance.title = title;
        instance.icon = icon;
        instance.properties = JsConverter.fromMap(properties);
        Arrays.sort(instance.properties, Comparator.comparing(k -> String.valueOf(k.getKey())));
        return instance;
    }

    @JsOverlay
    public final String getType() {
        return type;
    }

    @JsOverlay
    public final String getIcon() {
        return icon;
    }

    @JsOverlay
    public final String getTitle() {
        return title;
    }

    @JsOverlay
    public final String getName() {
        return name;
    }

    @JsOverlay
    public final KeyValue[] getProperties() {
        return properties;
    }
}
