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
 * Sleep state suspends workflow execution for a given time duration.
 * The delay is defined in its duration property using the ISO 8601 duration format.
 *
 * Note that transition and end properties are mutually exclusive, meaning that you cannot define both of them at the same time.
 *
 * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#Sleep-State"> Sleep state </a>
 */
@Bindable
@Definition
@Morph(base = State.class)
@JSONMapper
@JsType
public class SleepState extends State {

    public static final String TYPE_SLEEP = "sleep";

    /**
     * Duration (ISO 8601 duration format) to sleep.
     *
     * For example: "PT15M" (sleep 15 minutes), or "P2DT3H4M" (sleep 2 days, 3 hours and 4 minutes)
     */
    private String duration;

    public SleepState() {
        this.type = TYPE_SLEEP;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
