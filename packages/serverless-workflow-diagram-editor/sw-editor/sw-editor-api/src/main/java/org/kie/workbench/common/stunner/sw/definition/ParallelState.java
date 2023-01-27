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
import org.kie.workbench.common.stunner.client.yaml.mapper.api.annotation.YAMLMapper;
import org.kie.workbench.common.stunner.sw.definition.custom.NumCompletedJsonTypeDeserializer;
import org.kie.workbench.common.stunner.sw.definition.custom.NumCompletedJsonTypeSerializer;

@JSONMapper
@YAMLMapper
@JsType
public class ParallelState extends State {

    public static final String TYPE_PARALLEL = "parallel";

    public ParallelState() {
        this.type = TYPE_PARALLEL;
    }

    public String completionType;

    @JsonbTypeSerializer(NumCompletedJsonTypeSerializer.class)
    @JsonbTypeDeserializer(NumCompletedJsonTypeDeserializer.class)
    public Object numCompleted;

    public ParallelStateBranch[] branches;

    public String getCompletionType() {
        return completionType;
    }

    public void setCompletionType(String completionType) {
        this.completionType = completionType;
    }

    public ParallelStateBranch[] getBranches() {
        return branches;
    }

    public void setBranches(ParallelStateBranch[] branches) {
        this.branches = branches;
    }

    public Object getNumCompleted() {
        return numCompleted;
    }

    public void setNumCompleted(Object numCompleted) {
        this.numCompleted = numCompleted;
    }
}
