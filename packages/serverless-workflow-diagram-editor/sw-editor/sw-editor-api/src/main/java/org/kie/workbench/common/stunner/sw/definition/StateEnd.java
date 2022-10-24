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
import org.kie.workbench.common.stunner.sw.definition.custom.ContinueAsJsonbTypeDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.ContinueAsJsonbTypeSerializer;

@JSONMapper
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class StateEnd {

    private Boolean terminate;
    private Boolean compensate;

    @JsonbTypeSerializer(ContinueAsJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(ContinueAsJsonbTypeDeserializer.class)
    private Object continueAs;

    private ProducedEvent[] produceEvents;

    @JsOverlay
    public final Boolean getTerminate() {
        return terminate;
    }

    @JsOverlay
    public final void setTerminate(Boolean terminate) {
        this.terminate = terminate;
    }

    @JsOverlay
    public final Boolean getCompensate() {
        return compensate;
    }

    @JsOverlay
    public final void setCompensate(Boolean compensate) {
        this.compensate = compensate;
    }

    @JsOverlay
    public final Object getContinueAs() {
        return continueAs;
    }

    @JsOverlay
    public final void setContinueAs(Object continueAs) {
        this.continueAs = continueAs;
    }

    @JsOverlay
    public final ProducedEvent[] getProduceEvents() {
        return produceEvents;
    }

    @JsOverlay
    public final void setProduceEvents(ProducedEvent[] produceEvents) {
        this.produceEvents = produceEvents;
    }
}
