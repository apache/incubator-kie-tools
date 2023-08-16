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


package org.kie.workbench.common.stunner.bpmn.client.components.palette;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNImageResources;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionProviders;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph;

@Singleton
public class BPMNCategoryDefinitionProvider extends DefaultPaletteDefinitionProviders.CategoryDefinitionProvider {

    public BPMNCategoryDefinitionProvider() {
        super(BPMNCategories.class);
    }

    @PostConstruct
    protected void init() {
        this.put(BPMNCategories.START_EVENTS,
                 category -> category
                         .bindToDefinition(StartNoneEvent.class)
                         .useGlyph(SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.categoryStartEvents().getSafeUri())))
                .put(BPMNCategories.INTERMEDIATE_EVENTS,
                     category -> category
                             .bindToDefinition(IntermediateTimerEvent.class)
                             .useGlyph(SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.categoryIntermediateEvents().getSafeUri())))
                .put(BPMNCategories.END_EVENTS,
                     category -> category
                             .bindToDefinition(EndNoneEvent.class)
                             .useGlyph(SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.categoryEndEvents().getSafeUri())))
                .put(BPMNCategories.ACTIVITIES,
                     category -> category
                             .bindToDefinition(NoneTask.class)
                             .useGlyph(SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.categoryActivity().getSafeUri())))
                .put(BPMNCategories.SUB_PROCESSES,
                     category -> category
                             .bindToDefinition(ReusableSubprocess.class)
                             .useGlyph(SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.categorySubProcess().getSafeUri())))
                .put(BPMNCategories.GATEWAYS,
                     category -> category
                             .bindToDefinition(ParallelGateway.class)
                             .useGlyph(SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.categoryGateway().getSafeUri())))
                .put(BPMNCategories.CONTAINERS,
                     category -> category
                             .bindToDefinition(Lane.class)
                             .useGlyph(SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.categoryContainer().getSafeUri())))
                .put(BPMNCategories.CONNECTING_OBJECTS,
                     category -> category
                             .bindToDefinition(SequenceFlow.class)
                             .useGlyph(SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.categorySequence().getSafeUri())))
                .put(BPMNCategories.CUSTOM_TASKS,
                     category -> category
                             .useGlyph(SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.categoryServiceTasks().getSafeUri())))
                .put(BPMNCategories.ARTIFACTS,
                     category -> category
                             .bindToDefinition(DataObject.class)
                             .useGlyph(SvgDataUriGlyph.Builder.build(BPMNImageResources.INSTANCE.categoryArtifacts().getSafeUri())));
    }
}
