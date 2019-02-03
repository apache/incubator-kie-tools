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
package org.kie.workbench.common.stunner.bpmn.definition;

import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseAdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.BaseProcessData;

public abstract class BaseAdHocSubprocess<P extends BaseProcessData, S extends BaseAdHocSubprocessTaskExecutionSet>
        extends BaseSubprocess {

    public BaseAdHocSubprocess(BPMNGeneralSet general,
                               BackgroundSet backgroundSet,
                               FontSet fontSet,
                               RectangleDimensionsSet dimensionsSet,
                               SimulationSet simulationSet) {
        super(general, backgroundSet, fontSet, dimensionsSet, simulationSet);
    }

    public abstract S getExecutionSet();

    public abstract void setExecutionSet(final S executionSet);

    public abstract P getProcessData();

    public abstract void setProcessData(final P processData);
}
