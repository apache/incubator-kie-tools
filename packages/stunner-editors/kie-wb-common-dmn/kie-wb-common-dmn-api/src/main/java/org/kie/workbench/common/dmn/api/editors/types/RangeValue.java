/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.api.editors.types;

import java.util.Objects;

public class RangeValue {

    private boolean includeStartValue;
    private String startValue;
    private String endValue;
    private boolean includeEndValue;

    public RangeValue() {
        this.startValue = "";
        this.endValue = "";
        this.includeStartValue = true;
        this.includeEndValue = true;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RangeValue that = (RangeValue) o;
        return includeStartValue == that.includeStartValue &&
                includeEndValue == that.includeEndValue &&
                Objects.equals(startValue, that.startValue) &&
                Objects.equals(endValue, that.endValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(includeStartValue, startValue, endValue, includeEndValue);
    }
}