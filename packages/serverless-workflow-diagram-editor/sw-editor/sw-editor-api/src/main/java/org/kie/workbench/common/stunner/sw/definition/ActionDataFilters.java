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

@JsType
public class ActionDataFilters {

    private String fromStateData;
    private String results;
    private String toStateData;
    private Boolean useResults;

    public final String getFromStateData() {
        return fromStateData;
    }

    public final void setFromStateData(String fromStateData) {
        this.fromStateData = fromStateData;
    }

    public final String getResults() {
        return results;
    }

    public final void setResults(String results) {
        this.results = results;
    }

    public final String getToStateData() {
        return toStateData;
    }

    public final void setToStateData(String toStateData) {
        this.toStateData = toStateData;
    }

    public final Boolean getUseResults() {
        return useResults;
    }

    public final void setUseResults(Boolean useResults) {
        this.useResults = useResults;
    }
}
