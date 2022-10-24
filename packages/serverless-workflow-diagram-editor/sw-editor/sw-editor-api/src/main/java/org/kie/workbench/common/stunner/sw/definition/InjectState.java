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
 * Inject state can be used to inject static data into state data input.
 * Inject state does not perform any actions.
 *
 * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#Inject-State"> Inject state </a>
 */
@Bindable
@Definition
@Morph(base = State.class)
@JSONMapper
@JsType
public class InjectState extends State {

    public static final String TYPE_INJECT = "inject";

    /**
     * JSON object as String which can be set as state's data input and can be manipulated via filter.
     */
    //TODO it's a JSON object in examples
    private Data data;

    /**
     * Whether the state is used to compensate for another state.
     * Defaults to false.
     */
    private Boolean usedForCompensation;

    public InjectState() {
        this.type = TYPE_INJECT;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Boolean getUsedForCompensation() {
        return usedForCompensation;
    }

    public void setUsedForCompensation(Boolean usedForCompensation) {
        this.usedForCompensation = usedForCompensation;
    }
}
