/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.api.editors.types;

public class RangeValue {

    private boolean includeStartValue;
    private String startValue;
    private String endValue;
    private boolean includeEndValue;

    public RangeValue() {
        this.startValue = "";
        this.endValue = "";
    }

    public boolean getIncludeStartValue() {
        return includeStartValue;
    }

    public String getStartValue() {
        return startValue;
    }

    public String getEndValue() {
        return endValue;
    }

    public boolean getIncludeEndValue() {
        return includeEndValue;
    }

    public void setIncludeStartValue(final boolean includeStartValue) {
        this.includeStartValue = includeStartValue;
    }

    public void setIncludeEndValue(final boolean includeEndValue) {
        this.includeEndValue = includeEndValue;
    }

    public void setStartValue(final String startValue) {
        this.startValue = startValue;
    }

    public void setEndValue(final String endValue) {
        this.endValue = endValue;
    }
}