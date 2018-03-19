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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskType;
import org.kie.workbench.common.stunner.core.definition.morph.BindableMorphProperty;
import org.kie.workbench.common.stunner.core.definition.morph.BindablePropertyMorphDefinition;
import org.kie.workbench.common.stunner.core.definition.morph.MorphProperty;

// TODO: This class describes the morphing for Task types. Morphing definitions are generated at compile time by
// annotation processing, so until not introspecting this info via runtime annotation processing on test scope, this
// morphing property adapter is necessary to make the marshallers work on test scope.
public class TaskTypeMorphDefinition extends BindablePropertyMorphDefinition {

    private static final Map<Class<?>, Collection<MorphProperty>> PROPERTY_MORPH_DEFINITIONS =
            new HashMap<Class<?>, Collection<MorphProperty>>(1) {{
                put(BaseTask.class,
                    new ArrayList<MorphProperty>(1) {{
                        add(new TaskTypeMorphProperty());
                    }});
            }};

    @Override
    protected Map<Class<?>, Collection<MorphProperty>> getBindableMorphProperties() {
        return PROPERTY_MORPH_DEFINITIONS;
    }

    @Override
    protected Class<?> getDefaultType() {
        return NoneTask.class;
    }

    private static class TaskTypeMorphProperty extends BindableMorphProperty<TaskType, Object> {

        private final static BaseTask.TaskTypeMorphPropertyBinding BINDER = new BaseTask.TaskTypeMorphPropertyBinding();

        @Override
        public Class<?> getPropertyClass() {
            return TaskType.class;
        }

        @Override
        public Map getMorphTargetClasses() {
            return BINDER.getMorphTargets();
        }

        @Override
        public Object getValue(final TaskType property) {
            return BINDER.getValue(property);
        }
    }
}
