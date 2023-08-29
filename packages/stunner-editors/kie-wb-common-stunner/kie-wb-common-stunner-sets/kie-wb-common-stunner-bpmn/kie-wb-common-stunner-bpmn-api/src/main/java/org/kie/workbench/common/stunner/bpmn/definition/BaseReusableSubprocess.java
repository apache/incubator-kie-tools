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

package org.kie.workbench.common.stunner.bpmn.definition;

import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;

public abstract class BaseReusableSubprocess<E extends BaseReusableSubprocessTaskExecutionSet>
        extends BaseNonContainerSubprocess {

    public BaseReusableSubprocess(BPMNGeneralSet general,
                                  BackgroundSet backgroundSet,
                                  FontSet fontSet,
                                  RectangleDimensionsSet dimensionsSet,
                                  SimulationSet simulationSet,
                                  AdvancedData advancedData) {
        super(general, backgroundSet, fontSet, dimensionsSet, simulationSet, advancedData);
    }

    public abstract DataIOSet getDataIOSet();

    public abstract void setDataIOSet(DataIOSet dataIOSet);

    public abstract E getExecutionSet();

    public abstract void setExecutionSet(E executionSet);
}
