/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.openshift.model;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class TemplateParam {

    private String name;

    private String displayName;

    private String description;

    private boolean required;

    private String value;

    public TemplateParam(@MapsTo("name") final String name,
                         @MapsTo("displayName") final String displayName,
                         @MapsTo("description") final String description,
                         @MapsTo("required") final boolean required,
                         @MapsTo("value") final String value) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.required = required;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TemplateParam param = (TemplateParam) o;

        if (required != param.required) {
            return false;
        }
        if (name != null ? !name.equals(param.name) : param.name != null) {
            return false;
        }
        if (displayName != null ? !displayName.equals(param.displayName) : param.displayName != null) {
            return false;
        }
        if (description != null ? !description.equals(param.description) : param.description != null) {
            return false;
        }
        return value != null ? value.equals(param.value) : param.value == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (required ? 1 : 0);
        result = ~~result;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
