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
public class ForEachState extends State {

    public static final String TYPE_FOR_EACH = "foreach";

    public String inputCollection;

    public String outputCollection;

    public String iterationParam;

    public ForEachState() {
        this.type = TYPE_FOR_EACH;
    }

    public String getInputCollection() {
        return inputCollection;
    }

    public void setInputCollection(String inputCollection) {
        this.inputCollection = inputCollection;
    }

    public String getOutputCollection() {
        return outputCollection;
    }

    public void setOutputCollection(String outputCollection) {
        this.outputCollection = outputCollection;
    }

    public String getIterationParam() {
        return iterationParam;
    }

    public void setIterationParam(String iterationParam) {
        this.iterationParam = iterationParam;
    }
}
