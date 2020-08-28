/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dashbuilder.displayer.external;

import java.util.Map;

import elemental2.core.JsMap;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ExternalComponentMessage {

    JsMap<String, Object> properties;

    @JsOverlay
    public static ExternalComponentMessage create() {
        ExternalComponentMessage message = new ExternalComponentMessage();
        message.properties = new JsMap<>();
        return message;
    }

    @JsOverlay
    public static ExternalComponentMessage create(Map<String, String> properties) {
        ExternalComponentMessage message = new ExternalComponentMessage();
        message.properties = new JsMap<>();
        properties.forEach(message::setProperty);
        return message;
    }

    @JsOverlay
    public final void setProperty(String key, Object value) {
        properties.set(key, value);
    }

    @JsOverlay
    public final Object getProperty(String key) {
        return properties.get(key);
    }

}