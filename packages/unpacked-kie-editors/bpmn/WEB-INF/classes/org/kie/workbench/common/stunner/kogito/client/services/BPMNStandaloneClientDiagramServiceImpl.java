/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.kogito.client.services;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.marshalling.client.Marshalling;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.submarine.api.diagram.SubmarineDiagram;
import org.kie.workbench.common.stunner.submarine.api.diagram.SubmarineMetadata;
import org.kie.workbench.common.stunner.submarine.api.diagram.impl.SubmarineMetadataImpl;
import org.kie.workbench.common.stunner.submarine.api.editor.impl.SubmarineDiagramResourceImpl;
import org.kie.workbench.common.stunner.submarine.api.service.SubmarineDiagramService;
import org.kie.workbench.common.stunner.submarine.client.service.SubmarineClientDiagramService;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.promise.Promises;
import org.uberfire.commons.uuid.UUID;

@ApplicationScoped
public class BPMNStandaloneClientDiagramServiceImpl implements SubmarineClientDiagramService {

    private ShapeManager shapeManager;
    private Caller<VFSService> vfsServiceCaller;
    private Caller<SubmarineDiagramService> submarineDiagramServiceCaller;
    private Promises promises;

    @Inject
    private FactoryManager factoryManager;

    @Inject
    private DefinitionManager definitionManager;

    public BPMNStandaloneClientDiagramServiceImpl() {
        //CDI proxy
    }

    @Inject
    public BPMNStandaloneClientDiagramServiceImpl(final ShapeManager shapeManager,
                                                  final Caller<VFSService> vfsServiceCaller,
                                                  final Caller<SubmarineDiagramService> submarineDiagramServiceCaller,
                                                  final Promises promises) {
        this.shapeManager = shapeManager;
        this.vfsServiceCaller = vfsServiceCaller;
        this.submarineDiagramServiceCaller = submarineDiagramServiceCaller;
        this.promises = promises;
    }

    //Submarine requirements

    @Override
    public void transform(final String xml,
                          final ServiceCallback<SubmarineDiagram> callback) {

        SubmarineDiagram d = (SubmarineDiagram) Marshalling.fromJSON("{\"^EncodedType\":\"org.kie.workbench.common.stunner.submarine.api.diagram.impl.SubmarineDiagramImpl\",\"^ObjectID\":\"1\",\"name\":\"C893BDC6-F01A-4ABF-9C21-2638010076DD\",\"metadata\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.submarine.api.diagram.impl.SubmarineMetadataImpl\",\"^ObjectID\":\"2\",\"definitionSetId\":\"org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet\",\"profileId\":null,\"title\":\"C893BDC6-F01A-4ABF-9C21-2638010076DD\",\"shapeSetId\":null,\"canvasRootUUID\":\"_AF55C5D6-DCF3-4BB5-A074-206600493ECB\",\"thumbData\":null,\"root\":{\"^EncodedType\":\"org.uberfire.backend.vfs.PathFactory$PathImpl\",\"^ObjectID\":\"3\",\"uri\":\"default://master@system/stunner/diagrams\",\"fileName\":\".\",\"attributes\":{\"^EncodedType\":\"java.util.HashMap\",\"^ObjectID\":\"4\",\"^Value\":{}},\"hasVersionSupport\":false},\"path\":null},\"graph\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.impl.GraphImpl\",\"^ObjectID\":\"5\",\"nodeStore\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl\",\"^ObjectID\":\"6\",\"nodes\":{\"^EncodedType\":\"java.util.HashMap\",\"^ObjectID\":\"7\",\"^Value\":{\"_ED23CA59-6F5F-4530-B462-5281FC353432\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.impl.NodeImpl\",\"^ObjectID\":\"8\",\"inEdges\":{\"^EncodedType\":\"java.util.ArrayList\",\"^ObjectID\":\"9\",\"^Value\":[{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl\",\"^ObjectID\":\"10\",\"sourceNode\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.impl.NodeImpl\",\"^ObjectID\":\"11\",\"inEdges\":{\"^EncodedType\":\"java.util.ArrayList\",\"^ObjectID\":\"12\",\"^Value\":[]},\"outEdges\":{\"^EncodedType\":\"java.util.ArrayList\",\"^ObjectID\":\"13\",\"^Value\":[{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl\",\"^ObjectID\":\"10\"}]},\"uuid\":\"_AF55C5D6-DCF3-4BB5-A074-206600493ECB\",\"labels\":{\"^EncodedType\":\"java.util.LinkedHashSet\",\"^ObjectID\":\"14\",\"^Value\":[\"diagram\",\"org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl\",\"canContainArtifacts\"]},\"content\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl\",\"^ObjectID\":\"15\",\"definition\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl\",\"^ObjectID\":\"16\",\"diagramSet\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet\",\"^ObjectID\":\"17\",\"name\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.general.Name\",\"^ObjectID\":\"18\",\"value\":\"C893BDC6-F01A-4ABF-9C21-2638010076DD\"},\"documentation\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation\",\"^ObjectID\":\"19\",\"value\":\"\"},\"id\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id\",\"^ObjectID\":\"20\",\"value\":\"C893BDC6-F01A-4ABF-9C21-2638010076DD\"},\"packageProperty\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package\",\"^ObjectID\":\"21\",\"value\":null},\"version\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Version\",\"^ObjectID\":\"22\",\"value\":\"1.0\"},\"adHoc\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc\",\"^ObjectID\":\"23\",\"value\":false},\"processInstanceDescription\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessInstanceDescription\",\"^ObjectID\":\"24\",\"value\":\"\"},\"globalVariables\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.diagram.GlobalVariables\",\"^ObjectID\":\"25\",\"value\":\"\"},\"executable\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Executable\",\"^ObjectID\":\"26\",\"value\":true},\"slaDueDate\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate\",\"^ObjectID\":\"27\",\"value\":\"\"}},\"processData\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData\",\"^ObjectID\":\"28\",\"processVariables\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables\",\"^ObjectID\":\"29\",\"value\":\"\"}},\"caseManagementSet\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseManagementSet\",\"^ObjectID\":\"30\",\"caseIdPrefix\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseIdPrefix\",\"^ObjectID\":\"31\",\"value\":\"\"},\"caseRoles\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseRoles\",\"^ObjectID\":\"32\",\"value\":\"\"},\"caseFileVariables\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseFileVariables\",\"^ObjectID\":\"33\",\"value\":\"\"}},\"backgroundSet\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet\",\"^ObjectID\":\"34\",\"bgColor\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.background.BgColor\",\"^ObjectID\":\"35\",\"value\":null},\"borderColor\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.background.BorderColor\",\"^ObjectID\":\"36\",\"value\":null},\"borderSize\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.background.BorderSize\",\"^ObjectID\":\"37\",\"value\":null}},\"fontSet\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet\",\"^ObjectID\":\"38\",\"fontFamily\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.font.FontFamily\",\"^ObjectID\":\"39\",\"value\":null},\"fontColor\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.font.FontColor\",\"^ObjectID\":\"40\",\"value\":null},\"fontSize\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSize\",\"^ObjectID\":\"41\",\"value\":null},\"fontBorderSize\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.font.FontBorderSize\",\"^ObjectID\":\"42\",\"value\":null},\"fontBorderColor\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.font.FontBorderColor\",\"^ObjectID\":\"43\",\"value\":null}},\"dimensionsSet\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet\",\"^ObjectID\":\"44\",\"width\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Width\",\"^ObjectID\":\"45\",\"value\":950},\"height\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Height\",\"^ObjectID\":\"46\",\"value\":950}},\"labels\":{\"^EncodedType\":\"java.util.HashSet\",\"^ObjectID\":\"47\",\"^Value\":[\"diagram\",\"canContainArtifacts\"]}},\"bounds\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.content.Bounds\",\"^ObjectID\":\"48\",\"lr\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.content.Bound\",\"^ObjectID\":\"49\",\"x\":950,\"y\":950},\"ul\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.content.Bound\",\"^ObjectID\":\"50\",\"x\":0,\"y\":0}}}},\"targetNode\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.impl.NodeImpl\",\"^ObjectID\":\"8\"},\"uuid\":\"_BE54369C-C148-45C2-9D9C-E675DB02EB6C\",\"labels\":{\"^EncodedType\":\"java.util.LinkedHashSet\",\"^ObjectID\":\"51\",\"^Value\":[]},\"content\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.content.relationship.Child\",\"^ObjectID\":\"52\",\"^InstantiateOnly\":true}}]},\"outEdges\":{\"^EncodedType\":\"java.util.ArrayList\",\"^ObjectID\":\"53\",\"^Value\":[]},\"uuid\":\"_ED23CA59-6F5F-4530-B462-5281FC353432\",\"labels\":{\"^EncodedType\":\"java.util.LinkedHashSet\",\"^ObjectID\":\"54\",\"^Value\":[\"all\",\"org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent\",\"sequence_start\",\"choreography_sequence_start\",\"StartEventsMorph\",\"cm_nop\",\"fromtoall\",\"lane_child\",\"to_task_event\",\"Startevents_outgoing_all\",\"Startevents_all\",\"from_task_event\"]},\"content\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl\",\"^ObjectID\":\"55\",\"definition\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent\",\"^ObjectID\":\"56\",\"executionSet\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.event.BaseStartEventExecutionSet\",\"^ObjectID\":\"57\",\"isInterrupting\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting\",\"^ObjectID\":\"58\",\"value\":true},\"slaDueDate\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate\",\"^ObjectID\":\"59\",\"value\":\"\"}},\"labels\":{\"^EncodedType\":\"java.util.HashSet\",\"^ObjectID\":\"60\",\"^Value\":[\"all\",\"sequence_start\",\"choreography_sequence_start\",\"StartEventsMorph\",\"cm_nop\",\"fromtoall\",\"lane_child\",\"to_task_event\",\"Startevents_outgoing_all\",\"Startevents_all\",\"from_task_event\"]},\"general\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet\",\"^ObjectID\":\"61\",\"name\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.general.Name\",\"^ObjectID\":\"62\",\"value\":\"\"},\"documentation\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation\",\"^ObjectID\":\"63\",\"value\":\"\"}},\"backgroundSet\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet\",\"^ObjectID\":\"64\",\"bgColor\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.background.BgColor\",\"^ObjectID\":\"65\",\"value\":null},\"borderColor\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.background.BorderColor\",\"^ObjectID\":\"66\",\"value\":null},\"borderSize\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.background.BorderSize\",\"^ObjectID\":\"67\",\"value\":null}},\"fontSet\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet\",\"^ObjectID\":\"68\",\"fontFamily\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.font.FontFamily\",\"^ObjectID\":\"69\",\"value\":null},\"fontColor\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.font.FontColor\",\"^ObjectID\":\"70\",\"value\":null},\"fontSize\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSize\",\"^ObjectID\":\"71\",\"value\":null},\"fontBorderSize\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.font.FontBorderSize\",\"^ObjectID\":\"72\",\"value\":null},\"fontBorderColor\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.font.FontBorderColor\",\"^ObjectID\":\"73\",\"value\":null}},\"dimensionsSet\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet\",\"^ObjectID\":\"74\",\"radius\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius\",\"^ObjectID\":\"75\",\"value\":null}},\"simulationSet\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet\",\"^ObjectID\":\"76\",\"min\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.simulation.Min\",\"^ObjectID\":\"77\",\"value\":0},\"max\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.simulation.Max\",\"^ObjectID\":\"78\",\"value\":0},\"mean\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.simulation.Mean\",\"^ObjectID\":\"79\",\"value\":0},\"timeUnit\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.simulation.TimeUnit\",\"^ObjectID\":\"80\",\"value\":\"ms\"},\"standardDeviation\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.simulation.StandardDeviation\",\"^ObjectID\":\"81\",\"value\":0},\"distributionType\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.bpmn.definition.property.simulation.DistributionType\",\"^ObjectID\":\"82\",\"value\":\"normal\"}}},\"bounds\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.content.Bounds\",\"^ObjectID\":\"83\",\"lr\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.content.Bound\",\"^ObjectID\":\"84\",\"x\":100,\"y\":100},\"ul\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.content.Bound\",\"^ObjectID\":\"85\",\"x\":100,\"y\":100}}}},\"_AF55C5D6-DCF3-4BB5-A074-206600493ECB\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.impl.NodeImpl\",\"^ObjectID\":\"11\"}}}},\"uuid\":\"_84236423-0D41-4F41-AA5D-F37453D4A17F\",\"labels\":{\"^EncodedType\":\"java.util.LinkedHashSet\",\"^ObjectID\":\"86\",\"^Value\":[\"org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet\"]},\"content\":{\"^EncodedType\":\"org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSetImpl\",\"^ObjectID\":\"87\",\"id\":\"org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet\"}}}");
        //        submarineDiagramServiceCaller.call((SubmarineDiagram d) -> {
            updateClientMetadata(d);
            callback.onSuccess(d);
//        }).transform(xml);

        callback.onSuccess(doNewDiagram());
    }

    private SubmarineDiagram doNewDiagram() {
        final String title = UUID.uuid();
        final String defSetId = BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class);
        final SubmarineMetadata metadata = new SubmarineMetadataImpl.SubmarineMetadataBuilder(defSetId,
                                                                                              definitionManager)
                .setRoot(PathFactory.newPath(".", ""))
                .build();
        metadata.setTitle(title);

        try {
            return factoryManager.newDiagram(title,
                                             defSetId,
                                             metadata);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Promise<String> transform(final SubmarineDiagramResourceImpl resource) {
//        if (resource.getType() == DiagramType.PROJECT_DIAGRAM) {
//            return promises.promisify(submarineDiagramServiceCaller,
//                                      s -> {
//                                          return s.transform(resource.projectDiagram().orElseThrow(() -> new IllegalStateException("DiagramType is PROJECT_DIAGRAM however no instance present")));
//                                      });
//        }
//        return promises.resolve(resource.xmlDiagram().orElse("DiagramType is XML_DIAGRAM however no instance present"));
        return promises.resolve();
    }

    private void updateClientMetadata(final SubmarineDiagram diagram) {
        if (null != diagram) {
            final Metadata metadata = diagram.getMetadata();
            if (Objects.nonNull(metadata) && StringUtils.isEmpty(metadata.getShapeSetId())) {
                final String sId = shapeManager.getDefaultShapeSet(metadata.getDefinitionSetId()).getId();
                metadata.setShapeSetId(sId);
            }
        }
    }
}
