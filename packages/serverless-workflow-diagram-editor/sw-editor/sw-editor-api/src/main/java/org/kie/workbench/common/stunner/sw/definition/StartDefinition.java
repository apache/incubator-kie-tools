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
import org.kie.workbench.common.stunner.sw.definition.custom.ScheduleJsonbTypeDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.ScheduleJsonbTypeSerializer;

@JSONMapper
@JsType
public class StartDefinition {

    private String stateName;

    @JsonbTypeSerializer(ScheduleJsonbTypeSerializer.class)
    @JsonbTypeDeserializer(ScheduleJsonbTypeDeserializer.class)
    private Object schedule;

    public final String getStateName() {
        return stateName;
    }

    public final void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public final Object getSchedule() {
        return schedule;
    }

    public final void setSchedule(Object schedule) {
        this.schedule = schedule;
    }
}
