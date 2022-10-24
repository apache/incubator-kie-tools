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
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.client.json.mapper.annotation.JSONMapper;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;

/**
 * The Callback state allows you to explicitly model manual decision steps during workflow execution.
 *
 * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#Callback-State"> Callback state </a>
 */
@Bindable
@Definition
@Morph(base = State.class)
@JSONMapper
@JsType
public class CallbackState extends State {

    public static final String TYPE_CALLBACK = "callback";

    /**
     * Reference to an unique callback event name in the defined workflow events.
     */
    private String eventRef;

    /**
     * The action to be executed.
     */
    private ActionNode action;

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
}
