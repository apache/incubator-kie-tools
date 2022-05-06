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

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;

/**
 * Defines events that trigger action execution.
 *
 * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#Event-State"> Events state </a>
 */
@Bindable
@Definition
@Morph(base = State.class)
@JsType
public class EventState extends State {

    @JsIgnore
    public static final String TYPE_EVENT = "event";

    /**
     * Determines if the state waits for any of
     * the event defined in onEvents array.
     */
    @Property
    public boolean exclusive;

    /**
     * Define the events to be consumed and optional actions to be performed.
     */
    public OnEvent[] onEvents;

    public EventState() {
        this.type = TYPE_EVENT;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public OnEvent[] getOnEvents() {
        return onEvents;
    }

    public void setOnEvents(OnEvent[] onEvents) {
        this.onEvents = onEvents;
    }
}
