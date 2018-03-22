/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import java.util.ArrayList;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.FieldValue;
import org.kie.workbench.common.forms.adf.definitions.annotations.metaModel.I18nMode;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNProperty;
import org.kie.workbench.common.stunner.bpmn.definition.property.type.TaskPropertyType;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.property.AllowedValues;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Type;
import org.kie.workbench.common.stunner.core.definition.annotation.property.Value;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.kie.workbench.common.stunner.core.util.HashUtil;

/**
 * The TaskType property behaves different from the one in the spec. and in other
 * engines or tools focusing the BPMN domain.
 * In case of service tasks (work items), the Stunner BPMN domain considers
 * all types of services as just "service tasks", this way the task type
 * can be just an Enum type wich has well supported support on other areas
 * as well (like forms).
 * On the other hand, other domains cannot apply the same consideration, this way
 * this property class provides an String member in order to store that information
 * into the model and be able to manage it.
 */
@Portable
@Bindable
@Property
@FieldDefinition(i18nMode = I18nMode.OVERRIDE_I18N_KEY)
public class TaskType implements BPMNProperty {

    @Type
    public static final PropertyType type = new TaskPropertyType();

    @AllowedValues
    public static final Iterable<TaskTypes> allowedValues = new ArrayList<TaskTypes>(5) {{
        add(TaskTypes.NONE);
        add(TaskTypes.USER);
        add(TaskTypes.SCRIPT);
        add(TaskTypes.BUSINESS_RULE);
        add(TaskTypes.SERVICE_TASK);
    }};

    @Value
    @FieldValue
    private TaskTypes value;
    private String rawType;

    public TaskType() {
        this(TaskTypes.NONE);
    }

    public TaskType(final TaskTypes value) {
        this.value = value;
    }

    public PropertyType getType() {
        return type;
    }

    public Iterable<TaskTypes> getAllowedValues() {
        return allowedValues;
    }

    public TaskTypes getValue() {
        return value;
    }

    public void setValue(final TaskTypes value) {
        this.value = value;
    }

    public String getRawType() {
        return rawType;
    }

    public void setRawType(String rawType) {
        this.rawType = rawType;
    }

    @Override
    public int hashCode() {
        if (null != rawType && null != value) {
            return HashUtil.combineHashCodes(value.hashCode(),
                                             rawType.hashCode());
        }
        return (null != value) ? value.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TaskType) {
            TaskType other = (TaskType) o;
            return Objects.equals(value, other.value) &&
                    Objects.equals(rawType, other.rawType);
        }
        return false;
    }
}
