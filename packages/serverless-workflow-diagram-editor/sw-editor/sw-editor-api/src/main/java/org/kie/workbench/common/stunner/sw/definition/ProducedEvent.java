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

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ProducedEvent {

    private String eventRef;
    private String data;

    @JsOverlay
    public final String getEventRef() {
        return eventRef;
    }

    @JsOverlay
    public final void setEventRef(String eventRef) {
        this.eventRef = eventRef;
    }

    @JsOverlay
    public final String getData() {
        return data;
    }

    @JsOverlay
    public final void setData(String data) {
        this.data = data;
    }
}
