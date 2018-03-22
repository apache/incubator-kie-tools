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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskType;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.bpmn.definition.property.type.TaskPropertyType;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

/**
 * The serializer for TaskType in Oryx differs from the Stunner default as in Oryx
 * the taskType is set to a String, which in case of service tasks corresponds to the
 * work item declared name.
 * In order to apply the right value conversions between the Stunner and the Oryx domain,
 * this property serializer handles the special case for work items / service tasks.
 * NOTE: This will be no longer necessary once moving to the new BPMN un/mashaller implementations.
 */
@ApplicationScoped
public class TaskTypeSerializer implements Bpmn2OryxPropertySerializer<Object> {

    private final DefinitionUtils definitionUtils;
    private final EnumTypeSerializer enumTypeSerializer;

    protected TaskTypeSerializer() {
        this(null,
             null);
    }

    @Inject
    public TaskTypeSerializer(final DefinitionUtils definitionUtils,
                              final EnumTypeSerializer enumTypeSerializer) {
        this.definitionUtils = definitionUtils;
        this.enumTypeSerializer = enumTypeSerializer;
    }

    @Override
    public boolean accepts(final PropertyType type) {
        return TaskPropertyType.name.equals(type.getName());
    }

    @Override
    public Object parse(final Object property,
                        final String value) {
        if (null == value) {
            return null;
        }
        final Object parsed = enumTypeSerializer.parse(property, value);
        if (null == parsed) {
            ((TaskType) property).setRawType(value);
            return TaskTypes.SERVICE_TASK;
        }
        return parsed;
    }

    @Override
    public String serialize(final Object property,
                            final Object value) {
        if (TaskTypes.SERVICE_TASK.equals(value)) {
            return ((TaskType) property).getRawType();
        }
        return StringUtils.capitalize(value.toString().toLowerCase());
    }
}
