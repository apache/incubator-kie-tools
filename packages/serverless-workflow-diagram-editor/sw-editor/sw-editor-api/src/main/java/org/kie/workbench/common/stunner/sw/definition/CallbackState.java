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

import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.client.json.mapper.annotation.JSONMapper;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YAMLMapper;
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YamlPropertyOrder;

@JSONMapper
@YAMLMapper
@JsType
@YamlPropertyOrder({"name", "type", "transition", "action", "eventRef", "stateDataFilter", "eventTimeout", "compensatedBy", "timeouts", "onErrors", "end",  "metadata"})
public class CallbackState extends State {

    public static final String TYPE_CALLBACK = "callback";

    public String eventRef;

    public ActionNode action;

    EventDataFilter eventDataFilter;

    public CallbackState() {
        this.type = TYPE_CALLBACK;
    }

    public String getEventRef() {
        return eventRef;
    }

    public void setEventRef(String eventRef) {
        this.eventRef = eventRef;
    }

    public ActionNode getAction() {
        return action;
    }

    public void setAction(ActionNode action) {
        this.action = action;
    }

    public EventDataFilter getEventDataFilter() {
        return eventDataFilter;
    }

    public void setEventDataFilter(EventDataFilter eventDataFilter) {
        this.eventDataFilter = eventDataFilter;
    }
}
