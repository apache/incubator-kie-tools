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
import org.kie.workbench.common.stunner.sw.marshall.json.StateEndDefinitionJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.json.StateTransitionDefinitionJsonbTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.StateEndDefinitionYamlTypeSerializer;
import org.kie.workbench.common.stunner.sw.marshall.yaml.StateTransitionDefinitionYamlTypeSerializer;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeDeserializer;
import org.treblereel.gwt.yaml.api.annotation.YamlTypeSerializer;
import org.treblereel.j2cl.processors.annotations.GWT3Export;

@JsType
@GWT3Export
public class EventConditionTransition {

    public String name;

    public String eventRef;

    @JsonbTypeSerializer(StateTransitionDefinitionJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(StateTransitionDefinitionJsonbTypeSerializer.class)
    @YamlTypeSerializer(StateTransitionDefinitionYamlTypeSerializer.class)
    @YamlTypeDeserializer(StateTransitionDefinitionYamlTypeSerializer.class)
    public Object transition;

    @JsonbTypeSerializer(StateEndDefinitionJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(StateEndDefinitionJsonbTypeSerializer.class)
    @YamlTypeSerializer(StateEndDefinitionYamlTypeSerializer.class)
    @YamlTypeDeserializer(StateEndDefinitionYamlTypeSerializer.class)
    public Object end;

    public EventConditionTransition() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventRef() {
        return eventRef;
    }

    public void setEventRef(String eventRef) {
        this.eventRef = eventRef;
    }

    public Object getTransition() {
        return transition;
    }

    public EventConditionTransition setTransition(Object transition) {
        this.transition = transition;
        return this;
    }

    public Object getEnd() {
        return end;
    }

    public EventConditionTransition setEnd(Object end) {
        this.end = end;
        return this;
    }
}
