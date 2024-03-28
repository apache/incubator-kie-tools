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


package org.kie.workbench.common.stunner.sw.definition;

import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jsinterop.annotations.JsType;
import org.kie.j2cl.tools.json.mapper.annotation.JSONMapper;
import org.kie.j2cl.tools.processors.annotations.GWT3Export;
import org.kie.j2cl.tools.yaml.mapper.api.annotation.YAMLMapper;
import org.kie.j2cl.tools.yaml.mapper.api.annotation.YamlTypeDeserializer;
import org.kie.j2cl.tools.yaml.mapper.api.annotation.YamlTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.ContinueAsJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.ContinueAsYamlTypeSerializer;

@JSONMapper
@YAMLMapper
@JsType
@GWT3Export
public class StateEnd {

    public Boolean terminate;

    public Boolean compensate;

    @JsonbTypeSerializer(ContinueAsJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(ContinueAsJsonbTypeSerializer.class)
    @YamlTypeSerializer(ContinueAsYamlTypeSerializer.class)
    @YamlTypeDeserializer(ContinueAsYamlTypeSerializer.class)
    public Object continueAs;

    public ProducedEvent[] produceEvents;

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

    public final StateEnd setContinueAs(Object continueAs) {
        this.continueAs = continueAs;
        return this;
    }

    public final ProducedEvent[] getProduceEvents() {
        return produceEvents;
    }

    public final void setProduceEvents(ProducedEvent[] produceEvents) {
        this.produceEvents = produceEvents;
    }
}
