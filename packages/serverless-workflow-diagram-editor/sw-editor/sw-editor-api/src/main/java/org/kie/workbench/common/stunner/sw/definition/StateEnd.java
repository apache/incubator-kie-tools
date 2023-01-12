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
import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.client.json.mapper.annotation.JSONMapper;
import org.kie.workbench.common.stunner.sw.definition.custom.ContinueAsJsonbTypeDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.ContinueAsJsonbTypeSerializer;

@JSONMapper
@JsType
public class StateEnd {

    private Boolean terminate;
    private Boolean compensate;

    @JsonbTypeSerializer(ContinueAsJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(ContinueAsJsonbTypeDeserializer.class)
    private Object continueAs;

    private ProducedEvent[] produceEvents;

    public final Boolean getTerminate() {
        return terminate;
    }

    public final void setTerminate(Boolean terminate) {
        this.terminate = terminate;
    }

    public final Boolean getCompensate() {
        return compensate;
    }

    public final void setCompensate(Boolean compensate) {
        this.compensate = compensate;
    }

    public final Object getContinueAs() {
        return continueAs;
    }

    public final void setContinueAs(Object continueAs) {
        this.continueAs = continueAs;
    }

    public final ProducedEvent[] getProduceEvents() {
        return produceEvents;
    }

    public final void setProduceEvents(ProducedEvent[] produceEvents) {
        this.produceEvents = produceEvents;
    }
}
