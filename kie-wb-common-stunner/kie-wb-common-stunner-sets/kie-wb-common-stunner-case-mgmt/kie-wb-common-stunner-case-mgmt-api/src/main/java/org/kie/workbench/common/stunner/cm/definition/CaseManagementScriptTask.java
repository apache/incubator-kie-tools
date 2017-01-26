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
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskType;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = CaseManagementScriptTask.ScriptTaskBuilder.class)
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "BPMNProperties"),
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED
)
public class CaseManagementScriptTask extends CaseManagementBaseTask {

    @Title
    public static final transient String title = "Script Task";

    @PropertySet
    @FormField(
            labelKey = "executionSet",
            afterElement = "general"
    )
    @Valid
    protected ScriptTaskExecutionSet executionSet;

    @NonPortable
    public static class ScriptTaskBuilder extends BaseTaskBuilder<CaseManagementScriptTask> {

        @Override
        public CaseManagementScriptTask build() {
            return new CaseManagementScriptTask(new TaskGeneralSet(new Name("Task"),
                                                                   new Documentation("")),
                                                new ScriptTaskExecutionSet(),
                                                new BackgroundSet(COLOR,
                                                                  BORDER_COLOR,
                                                                  BORDER_SIZE),
                                                new FontSet(),
                                                new RectangleDimensionsSet(WIDTH,
                                                                           HEIGHT),
                                                new SimulationSet(),
                                                new TaskType(TaskTypes.SCRIPT));
        }
    }

    public CaseManagementScriptTask() {
        super(TaskTypes.SCRIPT);
    }

    public CaseManagementScriptTask(final @MapsTo("general") TaskGeneralSet general,
                                    final @MapsTo("executionSet") ScriptTaskExecutionSet executionSet,
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

    public ScriptTaskExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(final ScriptTaskExecutionSet executionSet) {
        this.executionSet = executionSet;
    }
}
