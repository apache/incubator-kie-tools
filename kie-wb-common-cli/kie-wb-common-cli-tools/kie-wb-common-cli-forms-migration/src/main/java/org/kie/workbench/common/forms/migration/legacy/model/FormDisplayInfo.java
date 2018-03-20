/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.forms.migration.legacy.model;

import java.io.Serializable;

public class FormDisplayInfo implements Comparable,
                                        Serializable {

    private String displayData = "";

    private String displayModifier = "";

    private String displayMode = "";

    public FormDisplayInfo(String displayMode, String displayData, String displayModifier) {
        this.displayMode = displayMode;
        this.displayData = displayData;
        this.displayModifier = displayModifier;
    }

    public FormDisplayInfo() {
    }

    public String getDisplayData() {
        return this.displayData;
    }

    public void setDisplayData(String displayData) {
        this.displayData = displayData;
    }

    public String getDisplayModifier() {
        return this.displayModifier;
    }

    public void setDisplayModifier(String displayModifier) {
        this.displayModifier = displayModifier;
    }

    public String getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }

    public String toString() {
        return displayMode;
    }

    public boolean equals(Object other) {
        if (!(other instanceof FormDisplayInfo)) {
            return false;
        }
        FormDisplayInfo castOther = (FormDisplayInfo) other;
        return displayMode.equals(castOther.getDisplayMode());
    }

    public int hashCode() {
        return displayMode.hashCode();
    }

    @Override
    public int compareTo(Object o) {
        try {
            return displayMode.compareTo(((FormDisplayInfo) o).getDisplayMode());
        } catch (Exception e) {
            return -1;
        }
    }
}
