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

package org.kie.workbench.common.stunner.cm.definition;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOModel;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = ReusableSubprocess.ReusableSubprocessBuilder.class)
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED
)
// This is a clone of org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess with different labels.
// Unfortunately extending the foregoing and providing a new set of labels leads to errai-data-binding to barf
// presumably because there are two fields called "labels" (although I've also tried with a different name field
// and it leads to the same errors).
public class ReusableSubprocess extends BaseSubprocess implements DataIOModel {

    @Title
    public static final transient String title = "Reusable Sub-Process";

    @Description
    public static final transient String description = "A reusable Sub-Process. It can be used to invoke another process.";

    @PropertySet
    @FormField(
            afterElement = "general"
    )
    @Valid
    protected ReusableSubprocessTaskExecutionSet executionSet;

    @PropertySet
    @FormField(
            afterElement = "executionSet"
    )
    @Valid
    protected DataIOSet dataIOSet;

    @NonPortable
    public static class ReusableSubprocessBuilder extends BaseSubprocessBuilder<ReusableSubprocess> {

        @Override
        public ReusableSubprocess build() {
            return new ReusableSubprocess(
                    new BPMNGeneralSet("Subprocess"),
                    new ReusableSubprocessTaskExecutionSet(),
                    new DataIOSet(),
                    new BackgroundSet(COLOR,
                                      BORDER_COLOR,
                                      BORDER_SIZE),
                    new FontSet(),
                    new RectangleDimensionsSet(WIDTH,
                                               HEIGHT),
                    new SimulationSet());
        }
    }

    public ReusableSubprocess() {
        super();
    }

    public ReusableSubprocess(final @MapsTo("general") BPMNGeneralSet general,
                              final @MapsTo("executionSet") ReusableSubprocessTaskExecutionSet executionSet,
                              final @MapsTo("dataIOSet") DataIOSet dataIOSet,
                              final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                              final @MapsTo("fontSet") FontSet fontSet,
                              final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet,
                              final @MapsTo("simulationSet") SimulationSet simulationSet) {
        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet,
              simulationSet);
        this.executionSet = executionSet;
        this.dataIOSet = dataIOSet;
    }

    @Override
    protected Set<String> makeLabels() {
        return new HashSet<String>() {{
            add("all");
            add("sequence_start");
            add("sequence_end");
            add("messageflow_start");
            add("messageflow_end");
            add("to_task_event");
            add("from_task_event");
            add("fromtoall");
            add("ActivitiesMorph");
            add("cm_activity");
        }};
    }

    @Override
    public boolean hasInputVars() {
        return true;
    }

    @Override
    public boolean isSingleInputVar() {
        return false;
    }

    @Override
    public boolean hasOutputVars() {
        return true;
    }

    @Override
    public boolean isSingleOutputVar() {
        return false;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public ReusableSubprocessTaskExecutionSet getExecutionSet() {
        return executionSet;
    }

    public DataIOSet getDataIOSet() {
        return dataIOSet;
    }

    public void setExecutionSet(final ReusableSubprocessTaskExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    public void setDataIOSet(final DataIOSet dataIOSet) {
        this.dataIOSet = dataIOSet;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         executionSet.hashCode(),
                                         dataIOSet.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ReusableSubprocess) {
            ReusableSubprocess other = (ReusableSubprocess) o;

            return super.equals(other) &&
                    executionSet.equals(other.executionSet) &&
                    dataIOSet.equals(other.dataIOSet);
        }
        return false;
    }
}
