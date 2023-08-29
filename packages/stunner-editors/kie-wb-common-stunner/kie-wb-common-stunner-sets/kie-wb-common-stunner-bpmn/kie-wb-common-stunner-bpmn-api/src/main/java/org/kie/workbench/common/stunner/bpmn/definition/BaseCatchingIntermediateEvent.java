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
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase;

@MorphBase(defaultType = IntermediateTimerEvent.class)
public abstract class BaseCatchingIntermediateEvent extends BaseIntermediateEvent {

    @Category
    public static final transient String category = BPMNCategories.INTERMEDIATE_EVENTS;

    public BaseCatchingIntermediateEvent() {
        super();
    }

    public BaseCatchingIntermediateEvent(final BPMNGeneralSet general,
                                         final BackgroundSet backgroundSet,
                                         final FontSet fontSet,
                                         final CircleDimensionSet dimensionsSet,
                                         final DataIOSet dataIOSet,
                                         final AdvancedData advancedData) {
        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet,
              dataIOSet,
              advancedData);
    }

    @Override
    protected void initLabels() {
        labels.add("all");
        labels.add("lane_child");
        labels.add("sequence_start");
        labels.add("sequence_end");
        labels.add("to_task_event");
        labels.add("from_task_event");
        labels.add("fromtoall");
        labels.add("IntermediateEventOnSubprocessBoundary");
        labels.add("IntermediateEventOnActivityBoundary");
        labels.add("EventOnChoreographyActivityBoundary");
        labels.add("IntermediateEventsMorph");
        labels.add("cm_nop");
        labels.add("IntermediateEventCatching");
    }

    @Override
    public boolean hasOutputVars() {
        return true;
    }

    @Override
    public boolean isSingleOutputVar() {
        return true;
    }

    public String getCategory() {
        return category;
    }
}
