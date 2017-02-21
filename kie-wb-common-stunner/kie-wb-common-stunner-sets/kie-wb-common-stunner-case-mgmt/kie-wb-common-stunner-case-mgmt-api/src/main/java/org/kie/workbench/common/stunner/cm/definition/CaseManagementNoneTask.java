/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.definition;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.EmptyTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskType;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = CaseManagementNoneTask.NoneTaskBuilder.class)
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED
)
public class CaseManagementNoneTask extends CaseManagementBaseTask {

    @Title
    public static final transient String title = "None Task";

    @PropertySet
    @FormField(
            afterElement = "general"
    )
    @Valid
    protected EmptyTaskExecutionSet executionSet;

    @NonPortable
    public static class NoneTaskBuilder extends BaseTaskBuilder<CaseManagementNoneTask> {

        @Override
        public CaseManagementNoneTask build() {
            return new CaseManagementNoneTask(new TaskGeneralSet(new Name("Task"),
                                                                 new Documentation("")),
                                              new EmptyTaskExecutionSet(),
                                              new BackgroundSet(COLOR,
                                                                BORDER_COLOR,
                                                                BORDER_SIZE),
                                              new FontSet(),
                                              new RectangleDimensionsSet(WIDTH,
                                                                         HEIGHT),
                                              new SimulationSet(),
                                              new TaskType(TaskTypes.NONE));
        }
    }

    public CaseManagementNoneTask() {
        super(TaskTypes.NONE);
    }

    public CaseManagementNoneTask(final @MapsTo("general") TaskGeneralSet general,
                                  final @MapsTo("executionSet") EmptyTaskExecutionSet executionSet,
                                  final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                                  final @MapsTo("fontSet") FontSet fontSet,
                                  final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet,
                                  final @MapsTo("simulationSet") SimulationSet simulationSet,
                                  final @MapsTo("taskType") TaskType taskType) {
        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet,
              simulationSet,
              taskType);
        this.executionSet = executionSet;
    }

    public String getTitle() {
        return title;
    }

    public EmptyTaskExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(final EmptyTaskExecutionSet executionSet) {
        this.executionSet = executionSet;
    }
}
