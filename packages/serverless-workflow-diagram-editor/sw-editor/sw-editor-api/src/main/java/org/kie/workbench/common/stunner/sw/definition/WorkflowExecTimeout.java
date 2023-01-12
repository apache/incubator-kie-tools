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

@JSONMapper
@JsType
public class WorkflowExecTimeout {

    private String duration;
    private Boolean interrupt;
    private String runBefore;

    public final String getDuration() {
        return duration;
    }

    public final void setDuration(String duration) {
        this.duration = duration;
    }

    public final Boolean getInterrupt() {
        return interrupt;
    }

    public final void setInterrupt(Boolean interrupt) {
        this.interrupt = interrupt;
    }

    public final String getRunBefore() {
        return runBefore;
    }

    public final void setRunBefore(String runBefore) {
        this.runBefore = runBefore;
    }
}
