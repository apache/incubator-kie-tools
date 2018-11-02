/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.bpmn.backend.legacy;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import bpsim.BPSimDataType;
import bpsim.BpsimFactory;
import bpsim.BpsimPackage;
import bpsim.ControlParameters;
import bpsim.CostParameters;
import bpsim.ElementParameters;
import bpsim.FloatingParameterType;
import bpsim.NormalDistributionType;
import bpsim.Parameter;
import bpsim.PoissonDistributionType;
import bpsim.PriorityParameters;
import bpsim.ResourceParameters;
import bpsim.Scenario;
import bpsim.ScenarioParameters;
import bpsim.TimeParameters;
import bpsim.TimeUnit;
import bpsim.UniformDistributionType;
import bpsim.impl.BpsimPackageImpl;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.AdHocOrdering;
import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.Artifact;
import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.Auditing;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.Category;
import org.eclipse.bpmn2.CategoryValue;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.DataStore;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.Error;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.Escalation;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.EventSubprocess;
import org.eclipse.bpmn2.ExclusiveGateway;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.GatewayDirection;
import org.eclipse.bpmn2.GlobalTask;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.Import;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.Monitoring;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.PotentialOwner;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ProcessType;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.Relationship;
import org.eclipse.bpmn2.ResourceAssignmentExpression;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.Signal;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TextAnnotation;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.di.BpmnDiFactory;
import org.eclipse.bpmn2.util.Bpmn2Resource;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.DcFactory;
import org.eclipse.dd.dc.Point;
import org.eclipse.dd.di.DiagramElement;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Internal;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl.SimpleFeatureMapEntry;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsFactory;
import org.jboss.drools.DroolsPackage;
import org.jboss.drools.GlobalType;
import org.jboss.drools.ImportType;
import org.jboss.drools.MetaDataType;
import org.jboss.drools.OnEntryScriptType;
import org.jboss.drools.OnExitScriptType;
import org.jboss.drools.impl.DroolsPackageImpl;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.resource.JBPMBpmn2ResourceFactoryImpl;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.util.Utils;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.Bpmn2OryxManager;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.ScriptTypeListTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.ScriptTypeTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.TimerSettingsTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.workbench.common.stunner.bpmn.backend.legacy.Bpmn2JsonPropertyIds.TIMECYCLE;
import static org.kie.workbench.common.stunner.bpmn.backend.legacy.Bpmn2JsonPropertyIds.TIMECYCLELANGUAGE;
import static org.kie.workbench.common.stunner.bpmn.backend.legacy.Bpmn2JsonPropertyIds.TIMEDATE;
import static org.kie.workbench.common.stunner.bpmn.backend.legacy.Bpmn2JsonPropertyIds.TIMEDURATION;
import static org.kie.workbench.common.stunner.bpmn.backend.legacy.Bpmn2JsonPropertyIds.TIMERSETTINGS;

/**
 * @author Antoine Toulme
 * @author Tihomir Surdilovic
 *         <p>
 *         an unmarshaller to transform JSON into BPMN 2.0 elements.
 */
public class Bpmn2JsonUnmarshaller {

    public static final String defaultBgColor = "#fafad2";
    public static final String defaultBrColor = "#000000";
    public static final String defaultFontColor = "#000000";
    public static final String defaultSequenceflowColor = "#000000";

    public static final String exporterName = "jBPM Process Modeler";
    public static final String exporterVersion = "1.0";

    public static final String defaultRelationshipType = "BPSimData";

    // Variables for setting UserTask name to "Task_n" if empty
    public static final String DEFAULT_USERTASK_NAME_PREFIX = "Task_";
    private static final Logger _logger = LoggerFactory.getLogger(Bpmn2JsonUnmarshaller.class);
    private int LastUserTaskID = 0;
    // a list of the objects created, kept in memory with their original id for
    // fast lookup.
    private Map<Object, String> _objMap = new HashMap<Object, String>();
    private Map<String, Object> _idMap = new HashMap<String, Object>();
    // the collection of outgoing ids.
    // we reconnect the edges with the shapes as a last step of the construction
    // of our graph from json, as we miss elements before.
    private Map<Object, List<String>> _outgoingFlows = new HashMap<Object, List<String>>();
    private Set<String> _sequenceFlowTargets = new HashSet<String>();
    private Map<String, Bounds> _bounds = new HashMap<String, Bounds>();
    private Map<String, List<Point>> _dockers = new HashMap<String, List<Point>>();
    private List<Lane> _lanes = new ArrayList<Lane>();
    private List<Artifact> _artifacts = new ArrayList<Artifact>();
    private Map<String, ItemDefinition> _subprocessItemDefs = new HashMap<String, ItemDefinition>();
    private List<Import> _wsdlImports = new ArrayList<Import>();
    private Map<String, List<String>> _elementColors = new HashMap<String, List<String>>();
    private List<BpmnMarshallerHelper> _helpers;
    private String processDocs;
    private Bpmn2Resource _currentResource;
    private Map<String, Escalation> _escalations = new HashMap<String, Escalation>();
    private Map<String, Error> _errors = new HashMap<String, Error>();
    private Map<String, Message> _messages = new HashMap<String, Message>();
    private Map<String, ItemDefinition> _itemDefinitions = new HashMap<String, ItemDefinition>();
    private Map<String, List<EObject>> _simulationElementParameters = new HashMap<String, List<EObject>>();
    private ScenarioParameters _simulationScenarioParameters = BpsimFactory.eINSTANCE.createScenarioParameters();
    private boolean zOrderEnabled;

    public Bpmn2JsonUnmarshaller() {
        _helpers = new ArrayList<BpmnMarshallerHelper>();
        DroolsPackageImpl.init();
        BpsimPackageImpl.init();
        // load the helpers to place them in field
        if (getClass().getClassLoader() instanceof BundleReference) {
            BundleContext context = ((BundleReference) getClass().getClassLoader()).
                    getBundle().getBundleContext();
            try {
                ServiceReference[] refs = context.getAllServiceReferences(
                        BpmnMarshallerHelper.class.getName(),
                        null);
                for (ServiceReference ref : refs) {
                    BpmnMarshallerHelper helper = (BpmnMarshallerHelper) context.getService(ref);
                    _helpers.add(helper);
                }
            } catch (InvalidSyntaxException e) {
            }
        }
    }

    /*
        private static String escapeXmlString(String string) {
            StringBuffer sb = new StringBuffer(string.length());
            // true if last char was blank
            boolean lastWasBlankChar = false;
            int len = string.length();
            char c;
            for (int i = 0; i < len; i++) {
                c = string.charAt(i);
                if (c == ' ') {
                    sb.append(' ');
                } else {
                    lastWasBlankChar = false;
                    //
                    // HTML Special Chars
                    if (c == '"') {
                        sb.append("&quot;");
                    } else if (c == '&') {
                        sb.append("&amp;");
                    } else if (c == '<') {
                        sb.append("&lt;");
                    } else if (c == '>') {
                        sb.append("&gt;");
                    } else {
                        int ci = 0xffff & c;
                        if (ci < 160)
                        // nothing special only 7 Bit
                        {
                            sb.append(c);
                        } else {
                            // Not 7 Bit use the unicode system
                            sb.append("&#");
                            sb.append(Integer.toString(ci));
                            sb.append(';');
                        }
                    }
                }
            }
            return sb.toString();
        }
    */
    public Bpmn2Resource unmarshall(String json,
                                    String preProcessingData) throws JsonParseException, IOException {
        return unmarshall(new JsonFactory().createJsonParser(json),
                          preProcessingData);
    }

    public Bpmn2Resource unmarshall(File file,
                                    String preProcessingData) throws JsonParseException, IOException {
        return unmarshall(new JsonFactory().createJsonParser(file),
                          preProcessingData);
    }

    public void setZOrderEnabled(boolean zOrderEnabled) {
        this.zOrderEnabled = zOrderEnabled;
    }

    /**
     * NOTE: This method has been set protected for Stunner support. Stunner bpmn implementation provides a custom JsonParser that
     * is used instead of the one used in jbpm-designer-backend.
     * <p>
     * Start unmarshalling using the parser.
     * @param parser
     * @param preProcessingData
     * @return the root element of a bpmn2 document.
     * @throws JsonParseException
     * @throws IOException
     */
    protected Bpmn2Resource unmarshall(JsonParser parser,
                                       String preProcessingData) throws JsonParseException, IOException {
        try {
            parser.nextToken(); // open the object
            ResourceSet rSet = new ResourceSetImpl();
            rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("bpmn2",
                                                                             new JBPMBpmn2ResourceFactoryImpl());
            Bpmn2Resource bpmn2 = (Bpmn2Resource) rSet.createResource(URI.createURI("virtual.bpmn2"));
            rSet.getResources().add(bpmn2);
            _currentResource = bpmn2;
            if (preProcessingData == null || preProcessingData.length() < 1) {
                preProcessingData = "ReadOnlyService";
            }
            // do the unmarshalling now:
            Definitions def = (Definitions) unmarshallItem(parser,
                                                           preProcessingData);
            def.setExporter(exporterName);
            def.setExporterVersion(exporterVersion);
            revisitUserTasks(def);
            revisitServiceTasks(def);
            revisitMessages(def);
            revisitCatchEvents(def);
            revisitThrowEvents(def);
            revisitLanes(def);
            revisitSubProcessItemDefs(def);
            revisitArtifacts(def);
            revisitGroups(def);
            revisitTaskAssociations(def);
            revisitSendReceiveTasks(def);
            reconnectFlows();
            revisitGateways(def);
            revisitCatchEventsConvertToBoundary(def);
            revisitBoundaryEventsPositions(def);
            createDiagram(def);
            updateIDs(def);
            revisitDataObjects(def);
            revisitAssociationsIoSpec(def);
            revisitWsdlImports(def);
            revisitMultiInstanceTasks(def);
            addSimulation(def);
            revisitItemDefinitions(def);
            revisitProcessDoc(def);
            revisitDI(def);
            revisitSignalRef(def);
            orderDiagramElements(def);
            // return def;
            _currentResource.getContents().add(def);
            return _currentResource;
        } catch (Exception e) {
            _logger.error(e.getMessage());
            return _currentResource;
        } finally {
            parser.close();
            _objMap.clear();
            _idMap.clear();
            _outgoingFlows.clear();
            _sequenceFlowTargets.clear();
            _bounds.clear();
            _currentResource = null;
        }
    }

    private void orderDiagramElements(Definitions def) {
        if (zOrderEnabled) {
            if (def.getDiagrams() != null) {
                for (BPMNDiagram diagram : def.getDiagrams()) {
                    if (diagram != null) {
                        _logger.debug("Sorting diagram elements using DIZorderComparator");
                        BPMNPlane plane = diagram.getPlane();
                        List<DiagramElement> unsortedElements = new ArrayList<DiagramElement>(plane.getPlaneElement());
                        plane.getPlaneElement().clear();
                        Collections.sort(unsortedElements,
                                         new DIZorderComparator());
                        plane.getPlaneElement().addAll(unsortedElements);
                        diagram.setPlane(plane);
                    }
                }
            }
        }
    }

    public void revisitUserTasks(Definitions def) {
        List<RootElement> rootElements = def.getRootElements();
        for (RootElement root : rootElements) {
            if (root instanceof Process) {
                setUserTaskInfo((Process) root);
            }
        }
    }

    private void setUserTaskInfo(FlowElementsContainer container) {
        List<FlowElement> flowElements = container.getFlowElements();
        for (FlowElement fe : flowElements) {
            // Set name and metaData "elementname" to "Task_n" if empty
            if (fe instanceof UserTask) {
                UserTask task = (UserTask) fe;
                String name = task.getName();
                if (name == null || name.length() == 0) {
                    LastUserTaskID++;
                    String newName = DEFAULT_USERTASK_NAME_PREFIX + LastUserTaskID;
                    task.setName(newName);
                    if (task.getExtensionValues() != null && task.getExtensionValues().size() > 0) {
                        for (ExtensionAttributeValue extattrval : task.getExtensionValues()) {
                            FeatureMap extensionElements = extattrval.getValue();
                            List<MetaDataType> metadataExtensions = (List<MetaDataType>) extensionElements
                                    .get(DroolsPackage.Literals.DOCUMENT_ROOT__META_DATA,
                                         true);
                            for (MetaDataType eleMetadata : metadataExtensions) {
                                if (eleMetadata.getName() != null && eleMetadata.getName().equals("elementname")) {
                                    eleMetadata.setMetaValue(wrapInCDATABlock(newName));
                                }
                            }
                        }
                    }
                }
            }
            if (fe instanceof FlowElementsContainer) {
                setUserTaskInfo((FlowElementsContainer) fe);
            }
        }
    }

    public void revisitItemDefinitions(Definitions def) {
        List<String> itemIds = new ArrayList<String>();
        for (RootElement root : def.getRootElements()) {
            if (root instanceof ItemDefinition) {
                if (!itemIds.contains(root.getId())) {
                    itemIds.add(root.getId());
                } else {
                    ItemDefinition idef = (ItemDefinition) root;
                    Random rand = new Random();
                    int randomNum = rand.nextInt((1000 - 10) + 1) + 10;
                    idef.setId(idef.getId() + randomNum);
                }
            }
        }
    }

    protected Map<Object, List<String>> getOutgoingFlowsMap() {
        return _outgoingFlows;
    }

    public void revisitDI(Definitions def) {
        revisitDIColors(def);
        BPMNPlane plane = def.getDiagrams().get(0).getPlane();
        List<DiagramElement> diagramElements = plane.getPlaneElement();
        for (DiagramElement dia : diagramElements) {
            if (dia instanceof BPMNShape) {
                BPMNShape shape = (BPMNShape) dia;
                updateShapeBounds(def,
                                  plane,
                                  shape.getBpmnElement());
            }
        }
        revisitEdgeBoundsInLanes(def);
        revisitEdgeBoundsInContainers(def);
    }

    public BPMNShape getBPMNShapeForElement(BaseElement be,
                                            BPMNPlane plane) {
        List<DiagramElement> diagramElements = plane.getPlaneElement();
        for (DiagramElement dia : diagramElements) {
            if (dia instanceof BPMNShape) {
                BPMNShape shape = (BPMNShape) dia;
                if (shape.getBpmnElement().getId().equals(be.getId())) {
                    return shape;
                }
            }
        }
        return null;
    }

    public void revisitEdgeBoundsInContainers(Definitions def) {
        BPMNPlane plane = def.getDiagrams().get(0).getPlane();
        List<DiagramElement> diagramElements = plane.getPlaneElement();
        for (DiagramElement dia : diagramElements) {
            if (dia instanceof BPMNEdge) {
                BPMNEdge edge = (BPMNEdge) dia;
                if (edge.getBpmnElement() instanceof SequenceFlow) {
                    SequenceFlow sq = (SequenceFlow) edge.getBpmnElement();
                    List<RootElement> rootElements = def.getRootElements();
                    for (RootElement root : rootElements) {
                        if (root instanceof Process) {
                            Process process = (Process) root;
                            updateEdgeBoundsInContainers(process,
                                                         sq,
                                                         plane,
                                                         edge);
                        }
                    }
                    // update the source and target on BPMNEdge
                    if (sq.getSourceRef() != null) {
                        edge.setSourceElement(getBPMNShapeForElement(sq.getSourceRef(),
                                                                     plane));
                    }
                    if (sq.getTargetRef() != null) {
                        edge.setTargetElement(getBPMNShapeForElement(sq.getTargetRef(),
                                                                     plane));
                    }
                }
            }
        }
    }

    public void updateEdgeBoundsInContainers(FlowElementsContainer container,
                                             SequenceFlow sq,
                                             BPMNPlane plane,
                                             BPMNEdge edge) {
        for (FlowElement fele : container.getFlowElements()) {
            // dont do this if its on process level
            if (!(container instanceof Process)) {
                if (fele.getId().equals(sq.getSourceRef().getId())) {
                    Bounds sourceBounds = getBoundsForElement(sq.getSourceRef(),
                                                              plane);
                    List<Point> edgePoints = edge.getWaypoint();
                    if (edgePoints != null && edgePoints.size() > 1) {
                        if (sourceBounds != null) {
                            Point first = edgePoints.get(0);
                            first.setX(first.getX() + getBoundsForElement(container,
                                                                          plane).getX());
                            first.setY(first.getY() + getBoundsForElement(container,
                                                                          plane).getY());
                        }
                    }
                } else if (fele.getId().equals(sq.getTargetRef().getId())) {
                    Bounds targetBounds = getBoundsForElement(sq.getTargetRef(),
                                                              plane);
                    List<Point> edgePoints = edge.getWaypoint();
                    if (edgePoints != null && edgePoints.size() > 1) {
                        if (targetBounds != null) {
                            Point last = edgePoints.get(edgePoints.size() - 1);
                            last.setX(last.getX() + getBoundsForElement(container,
                                                                        plane).getX());
                            last.setY(last.getY() + getBoundsForElement(container,
                                                                        plane).getY());
                        }
                    }
                }
            }
            if (fele instanceof FlowElementsContainer) {
                updateEdgeBoundsInContainers((FlowElementsContainer) fele,
                                             sq,
                                             plane,
                                             edge);
            }
        }
    }

    public void revisitEdgeBoundsInLanes(Definitions def) {
        BPMNPlane plane = def.getDiagrams().get(0).getPlane();
        List<DiagramElement> diagramElements = plane.getPlaneElement();
        for (DiagramElement dia : diagramElements) {
            if (dia instanceof BPMNEdge) {
                BPMNEdge edge = (BPMNEdge) dia;
                updateEdgeBoundsInLanes(def,
                                        plane,
                                        edge,
                                        edge.getBpmnElement());
            }
        }
    }

    public void revisitDIColors(Definitions def) {
        BPMNPlane plane = def.getDiagrams().get(0).getPlane();
        List<DiagramElement> diagramElements = plane.getPlaneElement();
        for (DiagramElement dia : diagramElements) {
            if (dia instanceof BPMNShape) {
                BPMNShape shape = (BPMNShape) dia;
                updateShapeColors(shape);
            }
            if (dia instanceof BPMNEdge) {
                BPMNEdge edge = (BPMNEdge) dia;
                updateEdgeColors(edge);
            }
        }
    }

    public void revisitProcessDoc(Definitions def) {
        List<RootElement> rootElements = def.getRootElements();
        for (RootElement root : rootElements) {
            if (root instanceof Process) {
                Process process = (Process) root;
                if (this.processDocs != null && this.processDocs.length() > 0) {
                    process.getDocumentation().add(createDocumentation(wrapInCDATABlock(this.processDocs)));
                }
            }
        }
    }

    public void updateShapeColors(BPMNShape shape) {
        List<String> eleColorsForShape = _elementColors.get(shape.getBpmnElement().getId());
        if (eleColorsForShape != null) {
            String backgroundColor = "";
            String borderColor = "";
            String fontColor = "";
            for (String shapeColor : eleColorsForShape) {
                String[] shapeColorParts = shapeColor.split(":");
                if (shapeColorParts[0].equals("bgcolor")) {
                    backgroundColor = shapeColorParts[1];
                }
                if (shapeColorParts[0].equals("bordercolor")) {
                    borderColor = shapeColorParts[1];
                }
                if (shapeColorParts[0].equals("fontcolor")) {
                    fontColor = shapeColorParts[1];
                }
            }
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttributeBgColor = (EAttributeImpl) metadata.demandFeature(
                    "http://www.omg.org/spec/BPMN/non-normative/color",
                    "background-color",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntryBgColor = new SimpleFeatureMapEntry(extensionAttributeBgColor,
                                                                                    backgroundColor);
            shape.getBpmnElement().getAnyAttribute().add(extensionEntryBgColor);
            EAttributeImpl extensionAttributeBorderColor = (EAttributeImpl) metadata.demandFeature(
                    "http://www.omg.org/spec/BPMN/non-normative/color",
                    "border-color",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntryBorderColor = new SimpleFeatureMapEntry(extensionAttributeBorderColor,
                                                                                        borderColor);
            shape.getBpmnElement().getAnyAttribute().add(extensionEntryBorderColor);
            EAttributeImpl extensionAttributeColor = (EAttributeImpl) metadata.demandFeature(
                    "http://www.omg.org/spec/BPMN/non-normative/color",
                    "color",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntryColor = new SimpleFeatureMapEntry(extensionAttributeColor,
                                                                                  fontColor);
            shape.getBpmnElement().getAnyAttribute().add(extensionEntryColor);
        } else {
            _logger.debug("Unable to find color information for shape: " + shape.getBpmnElement().getId());
        }
    }

    public void updateEdgeColors(BPMNEdge edge) {
        List<String> eleColorsForEdge = _elementColors.get(edge.getBpmnElement().getId());
        if (eleColorsForEdge != null) {
            String backgroundColor = "";
            String borderColor = "";
            String fontColor = "";
            for (String edgeColor : eleColorsForEdge) {
                String[] shapeColorParts = edgeColor.split(":");
                if (shapeColorParts[0].equals("bgcolor")) {
                    backgroundColor = shapeColorParts[1];
                }
                if (shapeColorParts[0].equals("bordercolor")) {
                    borderColor = shapeColorParts[1];
                }
                if (shapeColorParts[0].equals("fontcolor")) {
                    fontColor = shapeColorParts[1];
                }
            }
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttributeBgColor = (EAttributeImpl) metadata.demandFeature(
                    "http://www.omg.org/spec/BPMN/non-normative/color",
                    "background-color",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntryBgColor = new SimpleFeatureMapEntry(extensionAttributeBgColor,
                                                                                    backgroundColor);
            edge.getBpmnElement().getAnyAttribute().add(extensionEntryBgColor);
            EAttributeImpl extensionAttributeBorderColor = (EAttributeImpl) metadata.demandFeature(
                    "http://www.omg.org/spec/BPMN/non-normative/color",
                    "border-color",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntryBorderColor = new SimpleFeatureMapEntry(extensionAttributeBorderColor,
                                                                                        borderColor);
            edge.getBpmnElement().getAnyAttribute().add(extensionEntryBorderColor);
            EAttributeImpl extensionAttributeColor = (EAttributeImpl) metadata.demandFeature(
                    "http://www.omg.org/spec/BPMN/non-normative/color",
                    "color",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntryColor = new SimpleFeatureMapEntry(extensionAttributeColor,
                                                                                  fontColor);
            edge.getBpmnElement().getAnyAttribute().add(extensionEntryColor);
        } else {
            _logger.debug("Unable to find color information for shape: " + edge.getBpmnElement().getId());
        }
    }

    public void updateEdgeBoundsInLanes(Definitions def,
                                        BPMNPlane plane,
                                        BPMNEdge edge,
                                        BaseElement ele) {
        if (ele instanceof SequenceFlow) {
            SequenceFlow sq = (SequenceFlow) ele;
            // update the source and target on BPMNEdge
            if (sq.getSourceRef() != null) {
                edge.setSourceElement(getBPMNShapeForElement(sq.getSourceRef(),
                                                             plane));
            }
            if (sq.getTargetRef() != null) {
                edge.setTargetElement(getBPMNShapeForElement(sq.getTargetRef(),
                                                             plane));
            }
            List<RootElement> rootElements = def.getRootElements();
            for (RootElement root : rootElements) {
                if (root instanceof Process) {
                    Process process = (Process) root;
                    if (sq.getSourceRef() != null && sq.getTargetRef() != null) {
                        if (process.getLaneSets() != null && process.getLaneSets().size() > 0) {
                            for (LaneSet ls : process.getLaneSets()) {
                                for (Lane newLane : ls.getLanes()) {
                                    List<FlowNode> laneFlowNodes = newLane.getFlowNodeRefs();
                                    Bounds laneBounds = getBoundsForElement(newLane,
                                                                            plane);
                                    for (FlowNode newFlowNode : laneFlowNodes) {
                                        if (newFlowNode.getId().equals(sq.getSourceRef().getId())) {
                                            List<DiagramElement> diagramElements = plane.getPlaneElement();
                                            for (DiagramElement dia : diagramElements) {
                                                if (dia instanceof BPMNShape) {
                                                    BPMNShape shape = (BPMNShape) dia;
                                                    if (shape.getBpmnElement().getId().equals(sq.getSourceRef().getId())) {
                                                        Bounds eleBounds = shape.getBounds();
                                                        List<Point> edgePoints = edge.getWaypoint();
                                                        if (edgePoints != null && edgePoints.size() > 1) {
                                                            if (eleBounds != null) {
                                                                Point first = edgePoints.get(0);
                                                                first.setX(first.getX() + laneBounds.getX());
                                                                first.setY(first.getY() + laneBounds.getY());
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else if (newFlowNode.getId().equals(sq.getTargetRef().getId())) {
                                            List<DiagramElement> diagramElements = plane.getPlaneElement();
                                            for (DiagramElement dia : diagramElements) {
                                                if (dia instanceof BPMNShape) {
                                                    BPMNShape shape = (BPMNShape) dia;
                                                    if (shape.getBpmnElement().getId().equals(sq.getTargetRef().getId())) {
                                                        Bounds eleBounds = shape.getBounds();
                                                        List<Point> edgePoints = edge.getWaypoint();
                                                        if (edgePoints != null && edgePoints.size() > 1) {
                                                            if (eleBounds != null) {
                                                                Point last = edgePoints.get(edgePoints.size() - 1);
                                                                last.setX(last.getX() + laneBounds.getX());
                                                                last.setY(last.getY() + laneBounds.getY());
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateShapeBounds(Definitions def,
                                  BPMNPlane plane,
                                  BaseElement ele) {
        if (ele instanceof Lane) {
            Lane nextLane = (Lane) ele;
            Bounds laneBounds = getBoundsForElement(nextLane,
                                                    plane);
            updateShapeBoundsInLanes(plane,
                                     ele,
                                     nextLane,
                                     laneBounds.getX(),
                                     laneBounds.getY());
        } else {
            List<RootElement> rootElements = def.getRootElements();
            for (RootElement root : rootElements) {
                if (root instanceof Process) {
                    Process process = (Process) root;
                    List<FlowElement> flowElements = process.getFlowElements();
                    boolean foundAsTopLevel = false;
                    for (FlowElement fe : flowElements) {
                        if (fe.getId().equals(ele.getId())) {
                            foundAsTopLevel = true;
                            break;
                        }
                    }
                    if (!foundAsTopLevel) {
                        for (FlowElement fe : flowElements) {
                            if (fe instanceof SubProcess) {
                                SubProcess sp = (SubProcess) fe;
                                // process if this subprocess is not in a lane already. otherwise we already updated it
                                if (sp.getLanes().size() < 1) {
                                    // find the subprocess bounds
                                    Bounds subprocessBounds = getBoundsForElement(fe,
                                                                                  plane);
                                    if (subprocessBounds != null) {
                                        updateShapeBoundsInSubprocess(plane,
                                                                      ele,
                                                                      (SubProcess) fe,
                                                                      subprocessBounds.getX(),
                                                                      subprocessBounds.getY());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateShapeBoundsInLanes(BPMNPlane plane,
                                         BaseElement ele,
                                         Lane lane,
                                         float parentX,
                                         float parentY) {
        for (FlowNode fn : lane.getFlowNodeRefs()) {
            Bounds fnBounds = getBoundsForElement(fn,
                                                  plane);
            if (fnBounds != null) {
                fnBounds.setX(fnBounds.getX() + parentX);
                fnBounds.setY(fnBounds.getY() + parentY);
                // if flownode is a subprocess update it too
                if (fn instanceof SubProcess) {
                    updateShapeBoundsInSubprocessInLanes(plane,
                                                         ele,
                                                         (SubProcess) fn,
                                                         fnBounds.getX(),
                                                         fnBounds.getY());
                } else if (fn instanceof Lane) {
                    updateShapeBoundsInLanes(plane,
                                             ele,
                                             (Lane) fn,
                                             fnBounds.getX(),
                                             fnBounds.getY());
                }
            }
        }
    }

    public void updateShapeBoundsInSubprocessInLanes(BPMNPlane plane,
                                                     BaseElement ele,
                                                     SubProcess sub,
                                                     float parentX,
                                                     float parentY) {
        for (FlowElement subEle : sub.getFlowElements()) {
            Bounds subEleBounds = getBoundsForElement(subEle,
                                                      plane);
            if (subEleBounds != null) {
                subEleBounds.setX(subEleBounds.getX() + parentX);
                subEleBounds.setY(subEleBounds.getY() + parentY);
            }
            if (subEle instanceof SubProcess) {
                updateShapeBoundsInSubprocessInLanes(plane,
                                                     ele,
                                                     (SubProcess) subEle,
                                                     subEleBounds.getX(),
                                                     subEleBounds.getY());
            }
        }
    }

    public void updateShapeBoundsInSubprocess(BPMNPlane plane,
                                              BaseElement ele,
                                              SubProcess sub,
                                              float parentX,
                                              float parentY) {
        boolean foundInSubprocess = false;
        for (FlowElement subEle : sub.getFlowElements()) {
            if (subEle.getId().equals(ele.getId())) {
                foundInSubprocess = true;
                Bounds subEleBounds = getBoundsForElement(subEle,
                                                          plane);
                if (subEleBounds != null) {
                    subEleBounds.setX(subEleBounds.getX() + parentX);
                    subEleBounds.setY(subEleBounds.getY() + parentY);
                }
            }
        }
        if (!foundInSubprocess) {
            for (FlowElement subEle : sub.getFlowElements()) {
                if (subEle instanceof SubProcess) {
                    Bounds subEleBounds = getBoundsForElement(subEle,
                                                              plane);
                    updateShapeBoundsInSubprocess(plane,
                                                  ele,
                                                  (SubProcess) subEle,
                                                  subEleBounds.getX(),
                                                  subEleBounds.getY());
                }
            }
        }
    }

    private Bounds getBoundsForElement(BaseElement ele,
                                       BPMNPlane plane) {
        BPMNShape elementShape = getBPMNShapeForElement(ele,
                                                        plane);
        if (elementShape != null) {
            return elementShape.getBounds();
        } else {
            return null;
        }
    }

    public void revisitMultiInstanceTasks(Definitions def) {
        try {
            List<RootElement> rootElements = def.getRootElements();
            for (RootElement root : rootElements) {
                if (root instanceof Process) {
                    Process process = (Process) root;
                    List<FlowElement> flowElements = process.getFlowElements();
                    for (FlowElement fe : flowElements) {
                        if (fe instanceof Task) {
                            Task task = (Task) fe;
                            Iterator<FeatureMap.Entry> iter = task.getAnyAttribute().iterator();
                            while (iter.hasNext()) {
                                FeatureMap.Entry entry = iter.next();
                                if (entry.getEStructuralFeature().getName().equals("mitask")) {
                                    String multiValue = (String) entry.getValue();
                                    String[] multiValueParts = multiValue.split("@");
                                    if (multiValueParts != null && multiValueParts.length == 5) {
                                        String miCollectionInput = (multiValueParts[0].equals(" ") ? "" : multiValueParts[0]);
                                        String miCollectionOutput = (multiValueParts[1].equals(" ") ? "" : multiValueParts[1]);
                                        String miDataInput = (multiValueParts[2].equals(" ") ? "" : multiValueParts[2]);
                                        String miDataOutput = (multiValueParts[3].equals(" ") ? "" : multiValueParts[3]);
                                        String miCompletionCondition = (multiValueParts[4].equals(" ") ? "" : multiValueParts[4]);
                                        MultiInstanceLoopCharacteristics loopCharacteristics = Bpmn2Factory.eINSTANCE.createMultiInstanceLoopCharacteristics();
                                        if (miCollectionInput != null && miCollectionInput.length() > 0) {
                                            List<Property> properties = process.getProperties();
                                            for (Property prop : properties) {
                                                if (prop.getId() != null && prop.getId().equals(miCollectionInput)) {
                                                    DataInput miCollectionInputDI = Bpmn2Factory.eINSTANCE.createDataInput();
                                                    miCollectionInputDI.setName("miinputCollection");
                                                    ItemDefinition miCollectionInputDIItemDefinition = this.getMessageItemDefinition(def.getRootElements(),
                                                                                                                                     prop.getId());
                                                    miCollectionInputDI.setItemSubjectRef(miCollectionInputDIItemDefinition);
                                                    task.getIoSpecification().getDataInputs().add(miCollectionInputDI);
                                                    if (task.getIoSpecification().getInputSets() == null || task.getIoSpecification().getInputSets().size() < 1) {
                                                        InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                                                        task.getIoSpecification().getInputSets().add(inset);
                                                    }
                                                    task.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(miCollectionInputDI);
                                                    loopCharacteristics.setLoopDataInputRef(miCollectionInputDI);
                                                    DataInputAssociation miCollectionInputDataInputAssociation = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                                                    miCollectionInputDataInputAssociation.getSourceRef().add(prop);
                                                    miCollectionInputDataInputAssociation.setTargetRef(miCollectionInputDI);
                                                    task.getDataInputAssociations().add(miCollectionInputDataInputAssociation);
                                                    break;
                                                }
                                            }
                                        }
                                        if (miCollectionOutput != null && miCollectionOutput.length() > 0) {
                                            List<Property> properties = process.getProperties();
                                            for (Property prop : properties) {
                                                if (prop.getId() != null && prop.getId().equals(miCollectionOutput)) {
                                                    DataOutput miCollectionOutputDI = Bpmn2Factory.eINSTANCE.createDataOutput();
                                                    miCollectionOutputDI.setName("mioutputCollection");
                                                    ItemDefinition miCollectionOutputDIItemDefinition = this.getMessageItemDefinition(def.getRootElements(),
                                                                                                                                      prop.getId());
                                                    miCollectionOutputDI.setItemSubjectRef(miCollectionOutputDIItemDefinition);
                                                    task.getIoSpecification().getDataOutputs().add(miCollectionOutputDI);
                                                    if (task.getIoSpecification().getOutputSets() == null || task.getIoSpecification().getOutputSets().size() < 1) {
                                                        OutputSet outset = Bpmn2Factory.eINSTANCE.createOutputSet();
                                                        task.getIoSpecification().getOutputSets().add(outset);
                                                    }
                                                    task.getIoSpecification().getOutputSets().get(0).getDataOutputRefs().add(miCollectionOutputDI);
                                                    loopCharacteristics.setLoopDataOutputRef(miCollectionOutputDI);
                                                    DataOutputAssociation miCollectionInputDataOutputAssociation = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
                                                    miCollectionInputDataOutputAssociation.setTargetRef(prop);
                                                    miCollectionInputDataOutputAssociation.getSourceRef().add(miCollectionOutputDI);
                                                    task.getDataOutputAssociations().add(miCollectionInputDataOutputAssociation);
                                                    break;
                                                }
                                            }
                                        }
                                        if (miDataInput != null && miDataInput.length() > 0) {
                                            List<DataInput> dins = task.getIoSpecification().getDataInputs();
                                            for (DataInput di : dins) {
                                                if (di.getName().equals(miDataInput)) {
                                                    DataInput inputDataItemObj = Bpmn2Factory.eINSTANCE.createDataInput();
                                                    inputDataItemObj.setId("miDataInputX");
                                                    inputDataItemObj.setItemSubjectRef(di.getItemSubjectRef());
                                                    loopCharacteristics.setInputDataItem(inputDataItemObj);
                                                    break;
                                                }
                                            }
                                        }
                                        if (miDataOutput != null && miDataOutput.length() > 0) {
                                            List<DataOutput> douts = task.getIoSpecification().getDataOutputs();
                                            for (DataOutput dout : douts) {
                                                if (dout.getName().equals(miDataOutput)) {
                                                    DataOutput outputDataItemObj = Bpmn2Factory.eINSTANCE.createDataOutput();
                                                    outputDataItemObj.setId("miDataOutputX");
                                                    outputDataItemObj.setItemSubjectRef(dout.getItemSubjectRef());
                                                    loopCharacteristics.setOutputDataItem(outputDataItemObj);
                                                    break;
                                                }
                                            }
                                        }
                                        if (miCompletionCondition != null && !miCompletionCondition.isEmpty()) {
                                            FormalExpression expr = Bpmn2Factory.eINSTANCE.createFormalExpression();
                                            expr.setBody(miCompletionCondition);
                                            loopCharacteristics.setCompletionCondition(expr);
                                        }
                                        task.setLoopCharacteristics(loopCharacteristics);
                                        if (miDataInput != null && miDataInput.length() > 0 && ((MultiInstanceLoopCharacteristics) task.getLoopCharacteristics()).getInputDataItem() != null) {
                                            DataInputAssociation dias = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                                            dias.getSourceRef().add(((MultiInstanceLoopCharacteristics) task.getLoopCharacteristics()).getInputDataItem());
                                            List<DataInput> dins = task.getIoSpecification().getDataInputs();
                                            for (DataInput di : dins) {
                                                if (di.getName().equals(miDataInput)) {
                                                    dias.setTargetRef(di);
                                                    task.getDataInputAssociations().add(dias);
                                                    break;
                                                }
                                            }
                                        }
                                        if (miDataOutput != null && miDataOutput.length() > 0 && ((MultiInstanceLoopCharacteristics) task.getLoopCharacteristics()).getOutputDataItem() != null) {
                                            DataOutputAssociation dout = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
                                            dout.setTargetRef(((MultiInstanceLoopCharacteristics) task.getLoopCharacteristics()).getOutputDataItem());
                                            List<DataOutput> douts = task.getIoSpecification().getDataOutputs();
                                            for (DataOutput dou : douts) {
                                                if (dou.getName().equals(miDataOutput)) {
                                                    dout.getSourceRef().add(dou);
                                                    task.getDataOutputAssociations().add(dout);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void revisitWsdlImports(Definitions def) {
        for (Import imp : _wsdlImports) {
            def.getImports().add(imp);
        }
        _wsdlImports.clear();
    }

    public void revisitSubProcessItemDefs(Definitions def) {
        Iterator<String> iter = _subprocessItemDefs.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            def.getRootElements().add(_subprocessItemDefs.get(key));
        }
        _subprocessItemDefs.clear();
    }

    public void updateIDs(Definitions def) {
        // data object id update
        List<RootElement> rootElements = def.getRootElements();
        for (RootElement root : rootElements) {
            if (root instanceof Process) {
                Process process = (Process) root;
                if (process.getId() != null) {
                    String processId = process.getId().trim();
                    processId = processId.replaceAll("\\s",
                                                     "");
                    process.setId(processId);
                }
                List<FlowElement> flowElements = process.getFlowElements();
                for (FlowElement fe : flowElements) {
                    if (fe instanceof DataObject) {
                        DataObject da = (DataObject) fe;
                        if (da.getName() != null) {
                            String daId = da.getName().trim();
                            daId = daId.replaceAll("\\W",
                                                   "");
                            da.setId(daId);
                        }
                    }
                }
            }
        }
    }

    public void addSimulation(Definitions def) {
        Relationship relationship = Bpmn2Factory.eINSTANCE.createRelationship();
        relationship.getSources().add(def);
        relationship.getTargets().add(def);
        relationship.setType(defaultRelationshipType);
        BPSimDataType simDataType = BpsimFactory.eINSTANCE.createBPSimDataType();
        // currently support single scenario
        Scenario defaultScenario = BpsimFactory.eINSTANCE.createScenario();
        defaultScenario.setId("default"); // single scenario suppoert
        defaultScenario.setName("Simulationscenario"); // single scenario support
        defaultScenario.setScenarioParameters(_simulationScenarioParameters);
        if (_simulationElementParameters.size() > 0) {
            Iterator<String> iter = _simulationElementParameters.keySet().iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                ElementParameters etype = BpsimFactory.eINSTANCE.createElementParameters();
                etype.setElementRef(key);
                List<EObject> params = _simulationElementParameters.get(key);
                for (EObject np : params) {
                    if (np instanceof ControlParameters) {
                        etype.setControlParameters((ControlParameters) np);
                    } else if (np instanceof CostParameters) {
                        etype.setCostParameters((CostParameters) np);
                    } else if (np instanceof PriorityParameters) {
                        etype.setPriorityParameters((PriorityParameters) np);
                    } else if (np instanceof ResourceParameters) {
                        etype.setResourceParameters((ResourceParameters) np);
                    } else if (np instanceof TimeParameters) {
                        etype.setTimeParameters((TimeParameters) np);
                    }
                }
                defaultScenario.getElementParameters().add(etype);
            }
        }
        simDataType.getScenario().add(defaultScenario);
        ExtensionAttributeValue extensionElement = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
        relationship.getExtensionValues().add(extensionElement);
        FeatureMap.Entry extensionElementEntry = new SimpleFeatureMapEntry(
                (Internal) BpsimPackage.Literals.DOCUMENT_ROOT__BP_SIM_DATA,
                simDataType);
        relationship.getExtensionValues().get(0).getValue().add(extensionElementEntry);
        def.getRelationships().add(relationship);
    }

    public void revisitDataObjects(Definitions def) {
        List<RootElement> rootElements = def.getRootElements();
        List<ItemDefinition> itemDefinitionsToAddUnfiltered = new ArrayList<ItemDefinition>();
        List<ItemDefinition> itemDefinitionsToAddFiltered = new ArrayList<ItemDefinition>();
        for (RootElement root : rootElements) {
            if (root instanceof Process) {
                Process process = (Process) root;
                List<FlowElement> flowElements = process.getFlowElements();
                for (FlowElement fe : flowElements) {
                    if (fe instanceof DataObject) {
                        DataObject da = (DataObject) fe;
                        ItemDefinition itemdef = Bpmn2Factory.eINSTANCE.createItemDefinition();
                        itemdef.setId("_" + da.getId() + "Item");
                        Iterator<FeatureMap.Entry> iter = da.getAnyAttribute().iterator();
                        while (iter.hasNext()) {
                            FeatureMap.Entry entry = iter.next();
                            if (entry.getEStructuralFeature().getName().equals("datype")) {
                                String typeValue = (String) entry.getValue();
                                if (typeValue != null && !typeValue.equals("None")) {
                                    itemdef.setStructureRef((String) entry.getValue());
                                }
                            }
                        }
                        da.setItemSubjectRef(itemdef);
                        itemDefinitionsToAddUnfiltered.add(itemdef);
                    }
                }
            }
        }
        for (ItemDefinition itemDef : itemDefinitionsToAddUnfiltered) {
            boolean foundItemDef = false;
            for (RootElement ele : rootElements) {
                if (ele instanceof ItemDefinition) {
                    ItemDefinition idef = (ItemDefinition) ele;
                    if (idef.getId().equals(itemDef.getId())) {
                        foundItemDef = true;
                        break;
                    }
                }
            }
            if (!foundItemDef) {
                itemDefinitionsToAddFiltered.add(itemDef);
            }
        }
        for (ItemDefinition itemDefFil : itemDefinitionsToAddFiltered) {
            def.getRootElements().add(itemDefFil);
        }
        for (RootElement root : rootElements) {
            if (root instanceof Process) {
                Process process = (Process) root;
                List<Artifact> artifactElements = process.getArtifacts();
                for (Artifact af : artifactElements) {
                    if (af instanceof Association) {
                        Association as = (Association) af;
                        if (as.getSourceRef() != null && as.getSourceRef() instanceof DataObject
                                && as.getTargetRef() != null && (as.getTargetRef() instanceof Task || as.getTargetRef() instanceof ThrowEvent)) {
                            DataObject da = (DataObject) as.getSourceRef();
                            if (as.getTargetRef() instanceof Task) {
                                Task task = (Task) as.getTargetRef();
                                if (task.getIoSpecification() == null) {
                                    InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                                    task.setIoSpecification(iospec);
                                }
                                if (task.getIoSpecification().getInputSets() == null || task.getIoSpecification().getInputSets().size() < 1) {
                                    InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                                    task.getIoSpecification().getInputSets().add(inset);
                                }
                                InputSet inSet = task.getIoSpecification().getInputSets().get(0);
                                boolean foundDataInput = false;
                                for (DataInput dataInput : inSet.getDataInputRefs()) {
                                    if (dataInput.getId().equals(task.getId() + "_" + da.getId() + "InputX")) {
                                        foundDataInput = true;
                                    }
                                }
                                if (!foundDataInput) {
                                    DataInput d = Bpmn2Factory.eINSTANCE.createDataInput();
                                    d.setId(task.getId() + "_" + da.getId() + "InputX");
                                    d.setName(da.getId() + "InputX");
                                    task.getIoSpecification().getDataInputs().add(d);
                                    task.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(d);
                                    DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                                    dia.setTargetRef(d);
                                    dia.getSourceRef().add(da);
                                    task.getDataInputAssociations().add(dia);
                                }
                            } else if (as.getTargetRef() instanceof ThrowEvent) {
                                ThrowEvent te = (ThrowEvent) as.getTargetRef();
                                // update throw event data input and add data input association
                                boolean foundDataInput = false;
                                List<DataInput> dataInputs = te.getDataInputs();
                                for (DataInput din : dataInputs) {
                                    if (din.getId().equals(te.getId() + "_" + da.getId() + "InputX")) {
                                        foundDataInput = true;
                                    }
                                }
                                if (!foundDataInput) {
                                    DataInput datain = Bpmn2Factory.eINSTANCE.createDataInput();
                                    datain.setId(te.getId() + "_" + da.getId() + "InputX");
                                    datain.setName(da.getId() + "InputX");
                                    te.getDataInputs().add(datain);
                                    if (te.getInputSet() == null) {
                                        InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                                        te.setInputSet(inset);
                                    }
                                    te.getInputSet().getDataInputRefs().add(datain);
                                    DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                                    dia.setTargetRef(datain);
                                    dia.getSourceRef().add(da);
                                    te.getDataInputAssociation().add(dia);
                                }
                            }
                        }
                        if (as.getTargetRef() != null && as.getTargetRef() instanceof DataObject
                                && as.getSourceRef() != null && (as.getSourceRef() instanceof Task || as.getSourceRef() instanceof CatchEvent)) {
                            DataObject da = (DataObject) as.getTargetRef();
                            if (as.getSourceRef() instanceof Task) {
                                Task task = (Task) as.getSourceRef();
                                if (task.getIoSpecification() == null) {
                                    InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                                    task.setIoSpecification(iospec);
                                }
                                if (task.getIoSpecification().getOutputSets() == null || task.getIoSpecification().getOutputSets().size() < 1) {
                                    OutputSet outSet = Bpmn2Factory.eINSTANCE.createOutputSet();
                                    task.getIoSpecification().getOutputSets().add(outSet);
                                }
                                boolean foundDataOutput = false;
                                OutputSet outSet = task.getIoSpecification().getOutputSets().get(0);
                                for (DataOutput dataOut : outSet.getDataOutputRefs()) {
                                    if (dataOut.getId().equals(task.getId() + "_" + da.getId() + "OutputX")) {
                                        foundDataOutput = true;
                                    }
                                }
                                if (!foundDataOutput) {
                                    DataOutput d = Bpmn2Factory.eINSTANCE.createDataOutput();
                                    d.setId(task.getId() + "_" + da.getId() + "OutputX");
                                    d.setName(da.getId() + "OutputX");
                                    task.getIoSpecification().getDataOutputs().add(d);
                                    task.getIoSpecification().getOutputSets().get(0).getDataOutputRefs().add(d);
                                    DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
                                    doa.getSourceRef().add(d);
                                    doa.setTargetRef(da);
                                    task.getDataOutputAssociations().add(doa);
                                }
                            } else if (as.getSourceRef() instanceof CatchEvent) {
                                CatchEvent ce = (CatchEvent) as.getSourceRef();
                                // update catch event data output and add data output association
                                boolean foundDataOutput = false;
                                List<DataOutput> dataOutputs = ce.getDataOutputs();
                                for (DataOutput dout : dataOutputs) {
                                    if (dout.getId().equals(ce.getId() + "_" + da.getId() + "OutputX")) {
                                        foundDataOutput = true;
                                    }
                                }
                                if (!foundDataOutput) {
                                    DataOutput dataout = Bpmn2Factory.eINSTANCE.createDataOutput();
                                    dataout.setId(ce.getId() + "_" + da.getId() + "OutputX");
                                    dataout.setName(da.getId() + "OutputX");
                                    ce.getDataOutputs().add(dataout);
                                    if (ce.getOutputSet() == null) {
                                        OutputSet outset = Bpmn2Factory.eINSTANCE.createOutputSet();
                                        ce.setOutputSet(outset);
                                    }
                                    ce.getOutputSet().getDataOutputRefs().add(dataout);
                                    DataOutputAssociation dia = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
                                    dia.setTargetRef(da);
                                    dia.getSourceRef().add(dataout);
                                    ce.getDataOutputAssociation().add(dia);
                                }
                            }
                        }
                        if (as.getSourceRef() != null && as.getSourceRef() instanceof DataObject
                                && as.getTargetRef() != null && (as.getTargetRef() instanceof SequenceFlow)) {
                            SequenceFlow sf = (SequenceFlow) as.getTargetRef();
                            if (sf.getSourceRef() != null && sf.getSourceRef() instanceof Activity && sf.getTargetRef() != null && sf.getTargetRef() instanceof Activity) {
                                Activity sourceElement = (Activity) sf.getSourceRef();
                                Activity targetElement = (Activity) sf.getTargetRef();
                                DataObject da = (DataObject) as.getSourceRef();
                                if (targetElement.getIoSpecification() == null) {
                                    InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                                    targetElement.setIoSpecification(iospec);
                                }
                                if (targetElement.getIoSpecification().getInputSets() == null || targetElement.getIoSpecification().getInputSets().size() < 1) {
                                    InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                                    targetElement.getIoSpecification().getInputSets().add(inset);
                                }
                                InputSet inSet = targetElement.getIoSpecification().getInputSets().get(0);
                                boolean foundDataInput = false;
                                for (DataInput dataInput : inSet.getDataInputRefs()) {
                                    if (dataInput.getId().equals(targetElement.getId() + "_" + da.getId() + "InputX")) {
                                        foundDataInput = true;
                                    }
                                }
                                if (!foundDataInput) {
                                    DataInput d = Bpmn2Factory.eINSTANCE.createDataInput();
                                    d.setId(targetElement.getId() + "_" + da.getId() + "InputX");
                                    d.setName(da.getId() + "InputX");
                                    targetElement.getIoSpecification().getDataInputs().add(d);
                                    targetElement.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(d);
                                    DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                                    dia.setTargetRef(d);
                                    dia.getSourceRef().add(da);
                                    targetElement.getDataInputAssociations().add(dia);
                                }
                                if (sourceElement.getIoSpecification() == null) {
                                    InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                                    sourceElement.setIoSpecification(iospec);
                                }
                                if (sourceElement.getIoSpecification().getOutputSets() == null || sourceElement.getIoSpecification().getOutputSets().size() < 1) {
                                    OutputSet outSet = Bpmn2Factory.eINSTANCE.createOutputSet();
                                    sourceElement.getIoSpecification().getOutputSets().add(outSet);
                                }
                                boolean foundDataOutput = false;
                                OutputSet outSet = sourceElement.getIoSpecification().getOutputSets().get(0);
                                for (DataOutput dataOut : outSet.getDataOutputRefs()) {
                                    if (dataOut.getId().equals(sourceElement.getId() + "_" + da.getId() + "OutputX")) {
                                        foundDataOutput = true;
                                    }
                                }
                                if (!foundDataOutput) {
                                    DataOutput d = Bpmn2Factory.eINSTANCE.createDataOutput();
                                    d.setId(sourceElement.getId() + "_" + da.getId() + "OutputX");
                                    d.setName(da.getId() + "OutputX");
                                    sourceElement.getIoSpecification().getDataOutputs().add(d);
                                    sourceElement.getIoSpecification().getOutputSets().get(0).getDataOutputRefs().add(d);
                                    DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
                                    doa.getSourceRef().add(d);
                                    doa.setTargetRef(da);
                                    sourceElement.getDataOutputAssociations().add(doa);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void revisitTaskAssociations(Definitions def) {
        List<RootElement> rootElements = def.getRootElements();
        for (RootElement root : rootElements) {
            if (root instanceof Process) {
                Process process = (Process) root;
                List<FlowElement> flowElements = process.getFlowElements();
                for (FlowElement fe : flowElements) {
                    if (fe instanceof Task) {
                        Task t = (Task) fe;
                        if (t.getDataInputAssociations() != null) {
                            List<DataInputAssociation> inputList = t.getDataInputAssociations();
                            if (inputList != null) {
                                for (DataInputAssociation input : inputList) {
                                    List<ItemAwareElement> sourceRef = input.getSourceRef();
                                    if (sourceRef != null) {
                                        for (ItemAwareElement iae : sourceRef) {
                                            String[] iaeParts = iae.getId().split("\\.");
                                            if (iaeParts.length > 1) {
//                                                     FormalExpression dataInputTransformationExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
//                                                     dataInputTransformationExpression.setBody(iae.getId());
//                                                     input.setTransformation(dataInputTransformationExpression);
//                                                iae.setId(iaeParts[0]);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (t.getDataOutputAssociations() != null) {
                            List<DataOutputAssociation> outputList = t.getDataOutputAssociations();
                            if (outputList != null) {
                                for (DataOutputAssociation output : outputList) {
                                    ItemAwareElement targetEle = output.getTargetRef();
                                    if (targetEle != null) {
                                        String[] targetEleParts = targetEle.getId().split("\\.");
                                        if (targetEleParts.length > 1) {
//                                                FormalExpression dataOutputTransformationExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
//                                                dataOutputTransformationExpression.setBody(targetEle.getId());
//                                                output.setTransformation(dataOutputTransformationExpression);
//                                           targetEle.setId(targetEleParts[0]);
                                        }
                                    }
                                }
                            }
                        }
                        if (t.getIoSpecification() != null) {
                            InputOutputSpecification ios = t.getIoSpecification();
                            if (ios.getInputSets() == null || ios.getInputSets().size() < 1) {
                                InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                                ios.getInputSets().add(inset);
                            }
                            if (ios.getOutputSets() == null) {
                                if (ios.getOutputSets() == null || ios.getOutputSets().size() < 1) {
                                    OutputSet outset = Bpmn2Factory.eINSTANCE.createOutputSet();
                                    ios.getOutputSets().add(outset);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void revisitSendReceiveTasks(Definitions def) {
        List<Message> toAddMessages = new ArrayList<Message>();
        List<ItemDefinition> toAddItemDefinitions = new ArrayList<ItemDefinition>();
        List<RootElement> rootElements = def.getRootElements();
        for (RootElement root : rootElements) {
            if (root instanceof Process) {
                setSendReceiveTasksInfo((Process) root,
                                        def,
                                        toAddMessages,
                                        toAddItemDefinitions);
            }
        }
        for (ItemDefinition idef : toAddItemDefinitions) {
            def.getRootElements().add(idef);
        }
        for (Message msg : toAddMessages) {
            def.getRootElements().add(msg);
        }
    }

    public void setSendReceiveTasksInfo(FlowElementsContainer container,
                                        Definitions def,
                                        List<Message> toAddMessages,
                                        List<ItemDefinition> toAddItemDefinitions) {
        List<FlowElement> flowElements = container.getFlowElements();
        for (FlowElement fe : flowElements) {
            if (fe instanceof ReceiveTask) {
                ReceiveTask rt = (ReceiveTask) fe;
                rt.setMessageRef(extractMessage(rt,
                                                toAddMessages,
                                                toAddItemDefinitions));
            } else if (fe instanceof SendTask) {
                SendTask st = (SendTask) fe;
                st.setMessageRef(extractMessage(st,
                                                toAddMessages,
                                                toAddItemDefinitions));
            } else if (fe instanceof FlowElementsContainer) {
                setSendReceiveTasksInfo((FlowElementsContainer) fe,
                                        def,
                                        toAddMessages,
                                        toAddItemDefinitions);
            }
        }
    }

    private Message extractMessage(BaseElement element,
                                   Collection<Message> toAddMessages,
                                   Collection<ItemDefinition> toAddItemDefinitions) {
        String idefId = null;
        String msgName = null;
        Iterator<FeatureMap.Entry> iter = element.getAnyAttribute().iterator();
        while (iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if (entry.getEStructuralFeature().getName().equals("msgref")) {
                msgName = (String) entry.getValue();
                idefId = (String) entry.getValue() + "Type";
            }
        }
        if (msgName != null && !msgName.isEmpty() && idefId != null && !idefId.isEmpty()) {
            ItemDefinition idef = _itemDefinitions.get(idefId);
            if (idef == null) {
                idef = Bpmn2Factory.eINSTANCE.createItemDefinition();
                idef.setId(idefId);
                _itemDefinitions.put(idefId,
                                     idef);
            }
            Message msg = _messages.get(msgName);
            if (msg == null) {
                msg = Bpmn2Factory.eINSTANCE.createMessage();
                msg.setName(msgName);
                msg.setItemRef(idef);
                _messages.put(msgName,
                              msg);
            }
            msg.setName(msgName);
            toAddMessages.add(msg);
            toAddItemDefinitions.add(idef);
            return msg;
        }
        return null;
    }

    public void revisitLanes(Definitions def) {
        List<RootElement> rootElements = def.getRootElements();
        for (RootElement root : rootElements) {
            if (root instanceof Process) {
                Process process = (Process) root;
                if ((process.getLaneSets() == null || process.getLaneSets().size() < 1) && _lanes.size() > 0) {
                    LaneSet ls = Bpmn2Factory.eINSTANCE.createLaneSet();
                    for (Lane lane : _lanes) {
                        ls.getLanes().add(lane);
                        List<FlowNode> laneFlowNodes = lane.getFlowNodeRefs();
                        for (FlowNode fl : laneFlowNodes) {
                            process.getFlowElements().add(fl);
                        }
                    }
                    process.getLaneSets().add(ls);
                }
            }
        }
    }

    public void revisitArtifacts(Definitions def) {
        List<RootElement> rootElements = def.getRootElements();
        for (RootElement root : rootElements) {
            if (root instanceof Process) {
                Process process = (Process) root;
                for (Artifact a : _artifacts) {
                    process.getArtifacts().add(a);
                }
            }
        }
    }

    public void revisitGroups(Definitions def) {
        List<RootElement> rootElements = def.getRootElements();
        Category defaultCat = Bpmn2Factory.eINSTANCE.createCategory();
        defaultCat.setName("default");
        for (RootElement root : rootElements) {
            if (root instanceof Process) {
                Process process = (Process) root;
                List<Artifact> processArtifacts = process.getArtifacts();
                if (processArtifacts != null) {
                    for (Artifact ar : processArtifacts) {
                        if (ar instanceof Group) {
                            Group group = (Group) ar;
                            Iterator<FeatureMap.Entry> iter = group.getAnyAttribute().iterator();
                            while (iter.hasNext()) {
                                FeatureMap.Entry entry = iter.next();
                                if (entry.getEStructuralFeature().getName().equals("categoryval")) {
                                    CategoryValue catval = Bpmn2Factory.eINSTANCE.createCategoryValue();
                                    catval.setValue((String) entry.getValue());
                                    defaultCat.getCategoryValue().add(catval);
                                    group.setCategoryValueRef(catval);
                                }
                            }
                        }
                    }
                }
            }
        }
        // only add category if it includes at least one categoryvalue
        if (defaultCat.getCategoryValue() != null && defaultCat.getCategoryValue().size() > 0) {
            rootElements.add(defaultCat);
        }
    }

    /**
     * Updates event definitions for all throwing events.
     * @param def Definitions
     */
    public void revisitThrowEvents(Definitions def) {
        List<RootElement> rootElements = def.getRootElements();
        List<Signal> toAddSignals = new ArrayList<Signal>();
        Set<Error> toAddErrors = new HashSet<Error>();
        Set<Escalation> toAddEscalations = new HashSet<Escalation>();
        Set<Message> toAddMessages = new HashSet<Message>();
        Set<ItemDefinition> toAddItemDefinitions = new HashSet<ItemDefinition>();
        for (RootElement root : rootElements) {
            if (root instanceof Process) {
                setThrowEventsInfo((Process) root,
                                   def,
                                   rootElements,
                                   toAddSignals,
                                   toAddErrors,
                                   toAddEscalations,
                                   toAddMessages,
                                   toAddItemDefinitions);
            }
        }
        for (Lane lane : _lanes) {
            setThrowEventsInfoForLanes(lane,
                                       def,
                                       rootElements,
                                       toAddSignals,
                                       toAddErrors,
                                       toAddEscalations,
                                       toAddMessages,
                                       toAddItemDefinitions);
        }
        for (Signal s : toAddSignals) {
            def.getRootElements().add(s);
        }
        for (Error er : toAddErrors) {
            def.getRootElements().add(er);
        }
        for (Escalation es : toAddEscalations) {
            def.getRootElements().add(es);
        }
        for (ItemDefinition idef : toAddItemDefinitions) {
            def.getRootElements().add(idef);
        }
        for (Message msg : toAddMessages) {
            def.getRootElements().add(msg);
        }
    }

    public void setThrowEventsInfo(FlowElementsContainer container,
                                   Definitions def,
                                   List<RootElement> rootElements,
                                   List<Signal> toAddSignals,
                                   Set<Error> toAddErrors,
                                   Set<Escalation> toAddEscalations,
                                   Set<Message> toAddMessages,
                                   Set<ItemDefinition> toAddItemDefinitions) {
        List<FlowElement> flowElements = container.getFlowElements();
        for (FlowElement fe : flowElements) {
            if (fe instanceof ThrowEvent) {
                if (((ThrowEvent) fe).getEventDefinitions().size() > 0) {
                    EventDefinition ed = ((ThrowEvent) fe)
                            .getEventDefinitions().get(0);
                    if (ed instanceof SignalEventDefinition) {
                        SignalEventDefinition sed = (SignalEventDefinition) ed;
                        if (sed.getSignalRef() != null && sed.getSignalRef().length() > 0) {
                            String signalRef = sed.getSignalRef();
                            boolean shouldAddSignal = true;
                            for (RootElement re : rootElements) {
                                if (re instanceof Signal) {
                                    if (((Signal) re).getName().equals(signalRef)) {
                                        shouldAddSignal = false;
                                        break;
                                    }
                                }
                            }
                            if (toAddSignals != null) {
                                for (Signal s : toAddSignals) {
                                    if (s.getName().equals(signalRef)) {
                                        shouldAddSignal = false;
                                        break;
                                    }
                                }
                            }
                            if (shouldAddSignal) {
                                Signal signal = Bpmn2Factory.eINSTANCE.createSignal();
                                signal.setName(signalRef);
                                toAddSignals.add(signal);
                            }
                        }
                    } else if (ed instanceof ErrorEventDefinition) {
                        String errorCode = null;
                        String errorId = null;
                        Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute()
                                .iterator();
                        while (iter.hasNext()) {
                            FeatureMap.Entry entry = iter.next();
                            if (entry.getEStructuralFeature().getName()
                                    .equals("erefname")) {
                                errorId = (String) entry.getValue();
                                errorCode = (String) entry.getValue();
                            }
                        }
                        Error err = this._errors.get(errorCode);
                        if (err == null) {
                            err = Bpmn2Factory.eINSTANCE.createError();
                            err.setId(errorId);
                            err.setErrorCode(errorCode);
                            this._errors.put(errorCode,
                                             err);
                        }
                        toAddErrors.add(err);
                        ((ErrorEventDefinition) ed).setErrorRef(err);
                    } else if (ed instanceof EscalationEventDefinition) {
                        String escalationCode = null;
                        Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute().iterator();
                        while (iter.hasNext()) {
                            FeatureMap.Entry entry = iter.next();
                            if (entry.getEStructuralFeature().getName().equals("esccode")) {
                                escalationCode = (String) entry.getValue();
                                break;
                            }
                        }
                        Escalation escalation = this._escalations.get(escalationCode);
                        if (escalation == null) {
                            escalation = Bpmn2Factory.eINSTANCE.createEscalation();
                            escalation.setEscalationCode(escalationCode);
                            this._escalations.put(escalationCode,
                                                  escalation);
                        }
                        toAddEscalations.add(escalation);
                        ((EscalationEventDefinition) ed).setEscalationRef(escalation);
                    } else if (ed instanceof MessageEventDefinition) {
                        ((MessageEventDefinition) ed).setMessageRef(extractMessage(ed,
                                                                                   toAddMessages,
                                                                                   toAddItemDefinitions));
                    } else if (ed instanceof CompensateEventDefinition) {
                        Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute()
                                .iterator();
                        while (iter.hasNext()) {
                            FeatureMap.Entry entry = iter.next();
                            if (entry.getEStructuralFeature().getName()
                                    .equals("actrefname")) {
                                String activityNameRef = (String) entry
                                        .getValue();
                                // we have to iterate again through all flow
                                // elements
                                // in order to find our activity name
                                List<RootElement> re = def.getRootElements();
                                for (RootElement r : re) {
                                    if (r instanceof Process) {
                                        Process p = (Process) r;
                                        List<FlowElement> fes = p
                                                .getFlowElements();
                                        for (FlowElement f : fes) {
                                            if (f instanceof Activity
                                                    && ((Activity) f)
                                                    .getName()
                                                    .equals(activityNameRef)) {
                                                ((CompensateEventDefinition) ed)
                                                        .setActivityRef((Activity) f);
                                                ((Activity) f).setIsForCompensation(true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (fe instanceof FlowElementsContainer) {
                setThrowEventsInfo((FlowElementsContainer) fe,
                                   def,
                                   rootElements,
                                   toAddSignals,
                                   toAddErrors,
                                   toAddEscalations,
                                   toAddMessages,
                                   toAddItemDefinitions);
            }
        }
    }

    public void setThrowEventsInfoForLanes(Lane lane,
                                           Definitions def,
                                           List<RootElement> rootElements,
                                           List<Signal> toAddSignals,
                                           Set<Error> toAddErrors,
                                           Set<Escalation> toAddEscalations,
                                           Set<Message> toAddMessages,
                                           Set<ItemDefinition> toAddItemDefinitions) {
        List<FlowNode> laneFlowNodes = lane.getFlowNodeRefs();
        for (FlowNode fe : laneFlowNodes) {
            if (fe instanceof ThrowEvent) {
                if (((ThrowEvent) fe).getEventDefinitions().size() > 0) {
                    EventDefinition ed = ((ThrowEvent) fe)
                            .getEventDefinitions().get(0);
                    if (ed instanceof SignalEventDefinition) {
                        SignalEventDefinition sed = (SignalEventDefinition) ed;
                        if (sed.getSignalRef() != null && sed.getSignalRef().length() > 0) {
                            String signalRef = sed.getSignalRef();
                            boolean shouldAddSignal = true;
                            for (RootElement re : rootElements) {
                                if (re instanceof Signal) {
                                    if (((Signal) re).getName().equals(signalRef)) {
                                        shouldAddSignal = false;
                                        break;
                                    }
                                }
                            }
                            if (toAddSignals != null) {
                                for (Signal s : toAddSignals) {
                                    if (s.getName().equals(signalRef)) {
                                        shouldAddSignal = false;
                                        break;
                                    }
                                }
                            }
                            if (shouldAddSignal) {
                                Signal signal = Bpmn2Factory.eINSTANCE.createSignal();
                                signal.setName(signalRef);
                                toAddSignals.add(signal);
                            }
                        }
                    } else if (ed instanceof ErrorEventDefinition) {
                        String errorCode = null;
                        String errorId = null;
                        Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute()
                                .iterator();
                        while (iter.hasNext()) {
                            FeatureMap.Entry entry = iter.next();
                            if (entry.getEStructuralFeature().getName()
                                    .equals("erefname")) {
                                errorId = (String) entry.getValue();
                                errorCode = (String) entry.getValue();
                            }
                        }
                        Error err = this._errors.get(errorCode);
                        if (err == null) {
                            err = Bpmn2Factory.eINSTANCE.createError();
                            err.setId(errorId);
                            err.setErrorCode(errorCode);
                            this._errors.put(errorCode,
                                             err);
                        }
                        toAddErrors.add(err);
                        ((ErrorEventDefinition) ed).setErrorRef(err);
                    } else if (ed instanceof EscalationEventDefinition) {
                        String escalationCode = null;
                        Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute().iterator();
                        while (iter.hasNext()) {
                            FeatureMap.Entry entry = iter.next();
                            if (entry.getEStructuralFeature().getName().equals("esccode")) {
                                escalationCode = (String) entry.getValue();
                                break;
                            }
                        }
                        Escalation escalation = this._escalations.get(escalationCode);
                        if (escalation == null) {
                            escalation = Bpmn2Factory.eINSTANCE.createEscalation();
                            escalation.setEscalationCode(escalationCode);
                            this._escalations.put(escalationCode,
                                                  escalation);
                        }
                        toAddEscalations.add(escalation);
                        ((EscalationEventDefinition) ed).setEscalationRef(escalation);
                    } else if (ed instanceof MessageEventDefinition) {
                        ((MessageEventDefinition) ed).setMessageRef(extractMessage(ed,
                                                                                   toAddMessages,
                                                                                   toAddItemDefinitions));
                    } else if (ed instanceof CompensateEventDefinition) {
                        Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute()
                                .iterator();
                        while (iter.hasNext()) {
                            FeatureMap.Entry entry = iter.next();
                            if (entry.getEStructuralFeature().getName()
                                    .equals("actrefname")) {
                                String activityNameRef = (String) entry
                                        .getValue();
                                // we have to iterate again through all flow
                                // elements
                                // in order to find our activity name
                                List<RootElement> re = def.getRootElements();
                                for (RootElement r : re) {
                                    if (r instanceof Process) {
                                        Process p = (Process) r;
                                        List<FlowElement> fes = p
                                                .getFlowElements();
                                        for (FlowElement f : fes) {
                                            if (f instanceof Activity
                                                    && ((Activity) f)
                                                    .getName()
                                                    .equals(activityNameRef)) {
                                                ((CompensateEventDefinition) ed)
                                                        .setActivityRef((Activity) f);
                                                ((Activity) f).setIsForCompensation(true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (fe instanceof FlowElementsContainer) {
                setThrowEventsInfo((FlowElementsContainer) fe,
                                   def,
                                   rootElements,
                                   toAddSignals,
                                   toAddErrors,
                                   toAddEscalations,
                                   toAddMessages,
                                   toAddItemDefinitions);
            }
        }
    }

    protected FlowElementsContainer findContainerForBoundaryEvent(FlowElementsContainer container,
                                                                  BoundaryEvent be) {
        for (FlowElement flowElement : container.getFlowElements()) {
            if (flowElement.getId().equals(be.getAttachedToRef().getId())) {
                return container;
            }
            if (flowElement instanceof FlowElementsContainer) {
                FlowElementsContainer result = findContainerForBoundaryEvent((FlowElementsContainer) flowElement,
                                                                             be);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private FlowElementsContainer findContanerForFlowElement(FlowElementsContainer container,
                                                             FlowElement fl) {
        for (FlowElement flowElement : container.getFlowElements()) {
            if (flowElement.getId().equals(fl.getId())) {
                return container;
            }
            if (flowElement instanceof FlowElementsContainer) {
                return findContanerForFlowElement((FlowElementsContainer) flowElement,
                                                  fl);
            }
        }
        return null;
    }

    protected void revisitBoundaryEventsPositions(Definitions def) {
        for (RootElement root : def.getRootElements()) {
            if (root instanceof Process) {
                Process process = (Process) root;
                List<BoundaryEvent> toRemove = new ArrayList();
                for (FlowElement fe : process.getFlowElements()) {
                    if (fe instanceof BoundaryEvent) {
                        BoundaryEvent be = (BoundaryEvent) fe;
                        FlowElementsContainer container = findContainerForBoundaryEvent(process,
                                                                                        be);
                        if (container != null && !(container instanceof Process)) {
                            BoundaryEvent beCopy = copyBoundaryEvent(be);
                            container.getFlowElements().add(beCopy);
                            _outgoingFlows.put(beCopy,
                                               _outgoingFlows.get(be));
                            toRemove.add(be);
                            _outgoingFlows.remove(be);
                        }
                    }
                }
                for (BoundaryEvent be : toRemove) {
                    process.getFlowElements().remove(be);
                }
            }
        }
        reconnectFlows();
    }

    private BoundaryEvent copyBoundaryEvent(BoundaryEvent beEntry) {
        EventDefinition ed = null;
        if (beEntry.getEventDefinitions() != null && beEntry.getEventDefinitions().size() > 0) {
            ed = beEntry.getEventDefinitions().get(0);
        }
        BoundaryEvent be = Bpmn2Factory.eINSTANCE.createBoundaryEvent();
        if (ed instanceof ErrorEventDefinition) {
            be.setCancelActivity(true);
        } else {
            Iterator<FeatureMap.Entry> iter = beEntry.getAnyAttribute().iterator();
            while (iter.hasNext()) {
                FeatureMap.Entry entry2 = iter.next();
                if (entry2.getEStructuralFeature().getName().equals("boundaryca")) {
                    String boundaryceVal = (String) entry2.getValue();
                    be.setCancelActivity(Boolean.parseBoolean(boundaryceVal));
                }
            }
        }
        if (beEntry.getDataOutputs() != null) {
            be.getDataOutputs().addAll(beEntry.getDataOutputs());
        }
        if (beEntry.getDataOutputAssociation() != null) {
            be.getDataOutputAssociation().addAll(beEntry.getDataOutputAssociation());
        }
        if (beEntry.getOutputSet() != null) {
            be.setOutputSet(beEntry.getOutputSet());
        }
        if (beEntry.getEventDefinitions() != null) {
            be.getEventDefinitions().addAll(beEntry.getEventDefinitions());
        }
        if (beEntry.getEventDefinitionRefs() != null) {
            be.getEventDefinitionRefs().addAll(beEntry.getEventDefinitionRefs());
        }
        if (beEntry.getProperties() != null) {
            be.getProperties().addAll(beEntry.getProperties());
        }
        if (beEntry.getAnyAttribute() != null) {
            be.getAnyAttribute().addAll(beEntry.getAnyAttribute());
        }
        if (beEntry.getOutgoing() != null) {
            be.getOutgoing().addAll(beEntry.getOutgoing());
        }
        if (beEntry.getIncoming() != null) {
            be.getIncoming().addAll(beEntry.getIncoming());
        }
        if (beEntry.getExtensionValues() != null) {
            be.getExtensionValues().addAll(beEntry.getExtensionValues());
        }
        be.getDocumentation().addAll(beEntry.getDocumentation());
        be.setName(beEntry.getName());
        be.setId(beEntry.getId());
        be.setAttachedToRef(beEntry.getAttachedToRef());
        return be;
    }

    protected void revisitCatchEventsConvertToBoundary(Definitions def) {
        List<CatchEvent> catchEventsToRemove = new ArrayList<>();
        Map<BoundaryEvent, List<String>> boundaryEventsToAdd = new HashMap<>();
        List<RootElement> rootElements = def.getRootElements();
        for (RootElement root : rootElements) {
            if (root instanceof Process) {
                Process process = (Process) root;
                revisitCatchEVentsConvertToBoundaryExecute(process,
                                                           null,
                                                           catchEventsToRemove,
                                                           boundaryEventsToAdd);
            }
        }
        reconnectFlows();
    }

    private void revisitCatchEVentsConvertToBoundaryExecute(Process process,
                                                            FlowElementsContainer subContainer,
                                                            List<CatchEvent> catchEventsToRemove,
                                                            Map<BoundaryEvent, List<String>> boundaryEventsToAdd) {
        FlowElementsContainer container = subContainer != null ? subContainer : process;
        List<FlowElement> flowElements = container.getFlowElements();
        for (FlowElement fe : flowElements) {
            if (fe instanceof CatchEvent) {
                // check if we have an outgoing connection to this catch event from an activity
                for (Entry<Object, List<String>> entry : _outgoingFlows.entrySet()) {
                    for (String flowId : entry.getValue()) {
                        if (entry.getKey() instanceof Activity && flowId.equals(fe.getId())) {
                            CatchEvent ce = (CatchEvent) fe;
                            EventDefinition ed = null;
                            if (ce.getEventDefinitions() != null && ce.getEventDefinitions().size() > 0) {
                                ed = ce.getEventDefinitions().get(0);
                            }
                            BoundaryEvent be = Bpmn2Factory.eINSTANCE.createBoundaryEvent();
                            if (ed != null && ed instanceof ErrorEventDefinition) {
                                be.setCancelActivity(true);
                            } else {
                                Iterator<FeatureMap.Entry> iter = ce.getAnyAttribute().iterator();
                                while (iter.hasNext()) {
                                    FeatureMap.Entry entry2 = iter.next();
                                    if (entry2.getEStructuralFeature().getName().equals("boundaryca")) {
                                        String boundaryceVal = (String) entry2.getValue();
                                        be.setCancelActivity(Boolean.parseBoolean(boundaryceVal));
                                    }
                                }
                            }
                            if (ce.getDataOutputs() != null) {
                                be.getDataOutputs().addAll(ce.getDataOutputs());
                            }
                            if (ce.getDataOutputAssociation() != null) {
                                be.getDataOutputAssociation().addAll(ce.getDataOutputAssociation());
                            }
                            if (ce.getOutputSet() != null) {
                                be.setOutputSet(ce.getOutputSet());
                            }
                            if (ce.getEventDefinitions() != null) {
                                be.getEventDefinitions().addAll(ce.getEventDefinitions());
                            }
                            if (ce.getEventDefinitionRefs() != null) {
                                be.getEventDefinitionRefs().addAll(ce.getEventDefinitionRefs());
                            }
                            if (ce.getProperties() != null) {
                                be.getProperties().addAll(ce.getProperties());
                            }
                            if (ce.getAnyAttribute() != null) {
                                be.getAnyAttribute().addAll(ce.getAnyAttribute());
                            }
                            if (ce.getOutgoing() != null) {
                                be.getOutgoing().addAll(ce.getOutgoing());
                            }
                            if (ce.getIncoming() != null) {
                                be.getIncoming().addAll(ce.getIncoming());
                            }
                            if (ce.getProperties() != null) {
                                be.getProperties().addAll(ce.getProperties());
                            }
                            be.getDocumentation().addAll(ce.getDocumentation());
                            be.setName(ce.getName());
                            String ceElementName = Utils.getMetaDataValue(ce.getExtensionValues(),
                                                                          "elementname");
                            if (ceElementName != null) {
                                Utils.setMetaDataExtensionValue(be,
                                                                "elementname",
                                                                ceElementName);
                            }
                            be.setId(ce.getId());
                            be.setAttachedToRef(((Activity) entry.getKey()));
                            ((Activity) entry.getKey()).getBoundaryEventRefs().add(be);
                            catchEventsToRemove.add(ce);
                            boundaryEventsToAdd.put(be,
                                                    _outgoingFlows.get(ce));
                        }
                    }
                }
            } else if (fe instanceof FlowElementsContainer) {
                revisitCatchEVentsConvertToBoundaryExecute(process,
                                                           (FlowElementsContainer) fe,
                                                           catchEventsToRemove,
                                                           boundaryEventsToAdd);
            }
        }
        if (catchEventsToRemove.size() > 0) {
            for (CatchEvent ce : catchEventsToRemove) {
                boolean removed = container.getFlowElements().remove(ce);
                _outgoingFlows.remove(ce);
            }
        }
        if (boundaryEventsToAdd.size() > 0) {
            Iterator<BoundaryEvent> boundaryToAddIterator = boundaryEventsToAdd.keySet().iterator();
            while (boundaryToAddIterator.hasNext()) {
                BoundaryEvent bToAdd = boundaryToAddIterator.next();
                container.getFlowElements().add(bToAdd);
                _outgoingFlows.put(bToAdd,
                                   boundaryEventsToAdd.get(bToAdd));
            }
        }
    }

    public void revisitAssociationsIoSpec(Definitions def) {
        List<RootElement> rootElements = def.getRootElements();
        List<ItemDefinition> toAddItemDefinitions = new ArrayList<ItemDefinition>();
        for (RootElement root : rootElements) {
            if (root instanceof Process) {
                setItemDefinitionsForActivitiesIoSpec((Process) root,
                                                      def,
                                                      toAddItemDefinitions);
            }
        }
        for (ItemDefinition itemDef : toAddItemDefinitions) {
            def.getRootElements().add(itemDef);
        }
    }

    public void setItemDefinitionsForActivitiesIoSpec(FlowElementsContainer container,
                                                      Definitions def,
                                                      List<ItemDefinition> toAddItemDefinitions) {
        List<FlowElement> flowElements = container.getFlowElements();
        for (FlowElement fe : flowElements) {
            if (fe instanceof Activity) {
                Activity ac = (Activity) fe;
                if (ac.getIoSpecification() != null) {
                    if (ac.getIoSpecification().getDataInputs() != null) {
                        List<DataInput> dataInputs = ac.getIoSpecification().getDataInputs();
                        for (DataInput din : dataInputs) {
                            Iterator<FeatureMap.Entry> iter = din.getAnyAttribute().iterator();
                            while (iter.hasNext()) {
                                FeatureMap.Entry entry = iter.next();
                                if (entry.getEStructuralFeature().getName().equals("dtype")) {
                                    String dinType = (String) entry.getValue();
                                    if (dinType != null && dinType.length() > 0) {
                                        ItemDefinition itemdef = Bpmn2Factory.eINSTANCE.createItemDefinition();
                                        itemdef.setId("_" + din.getId() + "Item");
                                        itemdef.setStructureRef(dinType);
                                        toAddItemDefinitions.add(itemdef);
                                        din.setItemSubjectRef(itemdef);
                                    }
                                }
                            }
                        }
                    }
                    if (ac.getIoSpecification().getDataOutputs() != null) {
                        List<DataOutput> dataOutputs = ac.getIoSpecification().getDataOutputs();
                        for (DataOutput dout : dataOutputs) {
                            Iterator<FeatureMap.Entry> iter = dout.getAnyAttribute().iterator();
                            while (iter.hasNext()) {
                                FeatureMap.Entry entry = iter.next();
                                if (entry.getEStructuralFeature().getName().equals("dtype")) {
                                    String doutType = (String) entry.getValue();
                                    if (doutType != null && doutType.length() > 0) {
                                        ItemDefinition itemdef = Bpmn2Factory.eINSTANCE.createItemDefinition();
                                        itemdef.setId("_" + dout.getId() + "Item");
                                        itemdef.setStructureRef(doutType);
                                        toAddItemDefinitions.add(itemdef);
                                        dout.setItemSubjectRef(itemdef);
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (fe instanceof FlowElementsContainer) {
                setItemDefinitionsForActivitiesIoSpec((FlowElementsContainer) fe,
                                                      def,
                                                      toAddItemDefinitions);
            }
        }
    }

    /**
     * Updates the signal ref on catch and throw event definitions (including boundary)
     * @param def Definitions
     */
    public void revisitSignalRef(Definitions def) {
        revisitSignalIds(def);
        List<RootElement> rootElements = def.getRootElements();
        for (RootElement root : rootElements) {
            if (root instanceof Process) {
                setSignalRefForCatchEvents((Process) root,
                                           def);
                setSignalRefForThrowEvents((Process) root,
                                           def);
                setSignalRefForBoundaryEvents((Process) root,
                                              def);
            }
        }
    }

    public void revisitSignalIds(Definitions def) {
        List<RootElement> rootElements = def.getRootElements();
        for (RootElement re : rootElements) {
            if (re instanceof Signal) {
                Signal signal = (Signal) re;
                if (signal.getName() != null) {
                    try {
                        signal.setId("_" + UUID.nameUUIDFromBytes(signal.getName().getBytes("UTF-8")));
                    } catch (UnsupportedEncodingException e) {
                        signal.setId("_" + UUID.nameUUIDFromBytes(signal.getName().getBytes()));
                    }
                }
            }
        }
    }

    public void setSignalRefForCatchEvents(FlowElementsContainer container,
                                           Definitions def) {
        List<FlowElement> flowElements = container.getFlowElements();
        for (FlowElement fe : flowElements) {
            if (fe instanceof CatchEvent) {
                if (((CatchEvent) fe).getEventDefinitions().size() > 0) {
                    EventDefinition ed = ((CatchEvent) fe).getEventDefinitions().get(0);
                    if (ed instanceof SignalEventDefinition) {
                        SignalEventDefinition sed = (SignalEventDefinition) ed;
                        if (sed.getSignalRef() != null) {
                            Signal signal = findSignalWithName(sed.getSignalRef(),
                                                               def);
                            if (signal != null) {
                                sed.setSignalRef(signal.getId());
                            }
                        }
                    }
                }
            } else if (fe instanceof FlowElementsContainer) {
                setSignalRefForCatchEvents((FlowElementsContainer) fe,
                                           def);
            }
        }
    }

    public void setSignalRefForThrowEvents(FlowElementsContainer container,
                                           Definitions def) {
        List<FlowElement> flowElements = container.getFlowElements();
        for (FlowElement fe : flowElements) {
            if (fe instanceof ThrowEvent) {
                if (((ThrowEvent) fe).getEventDefinitions().size() > 0) {
                    EventDefinition ed = ((ThrowEvent) fe).getEventDefinitions().get(0);
                    if (ed instanceof SignalEventDefinition) {
                        SignalEventDefinition sed = (SignalEventDefinition) ed;
                        if (sed.getSignalRef() != null) {
                            Signal signal = findSignalWithName(sed.getSignalRef(),
                                                               def);
                            if (signal != null) {
                                sed.setSignalRef(signal.getId());
                            }
                        }
                    }
                }
            } else if (fe instanceof FlowElementsContainer) {
                setSignalRefForThrowEvents((FlowElementsContainer) fe,
                                           def);
            }
        }
    }

    public void setSignalRefForBoundaryEvents(FlowElementsContainer container,
                                              Definitions def) {
        List<FlowElement> flowElements = container.getFlowElements();
        for (FlowElement fe : flowElements) {
            if (fe instanceof BoundaryEvent) {
                if (((BoundaryEvent) fe).getEventDefinitions().size() > 0) {
                    EventDefinition ed = ((BoundaryEvent) fe).getEventDefinitions().get(0);
                    if (ed instanceof SignalEventDefinition) {
                        SignalEventDefinition sed = (SignalEventDefinition) ed;
                        if (sed.getSignalRef() != null) {
                            Signal signal = findSignalWithName(sed.getSignalRef(),
                                                               def);
                            if (signal != null) {
                                sed.setSignalRef(signal.getId());
                            }
                        }
                    }
                }
            } else if (fe instanceof FlowElementsContainer) {
                setSignalRefForBoundaryEvents((FlowElementsContainer) fe,
                                              def);
            }
        }
    }

    public Signal findSignalWithName(String signalRef,
                                     Definitions def) {
        List<RootElement> rootElements = def.getRootElements();
        for (RootElement re : rootElements) {
            if (re instanceof Signal) {
                if (((Signal) re).getName().equals(signalRef)) {
                    return (Signal) re;
                }
            }
        }
        return null;
    }

    /**
     * Updates event definitions for all catch events.
     * @param def Definitions
     */
    public void revisitCatchEvents(Definitions def) {
        List<RootElement> rootElements = def.getRootElements();
        List<Signal> toAddSignals = new ArrayList<Signal>();
        Set<Error> toAddErrors = new HashSet<Error>();
        Set<Escalation> toAddEscalations = new HashSet<Escalation>();
        Set<Message> toAddMessages = new HashSet<Message>();
        Set<ItemDefinition> toAddItemDefinitions = new HashSet<ItemDefinition>();
        for (RootElement root : rootElements) {
            if (root instanceof Process) {
                setCatchEventsInfo((Process) root,
                                   def,
                                   toAddSignals,
                                   toAddErrors,
                                   toAddEscalations,
                                   toAddMessages,
                                   toAddItemDefinitions);
            }
        }
        for (Lane lane : _lanes) {
            setCatchEventsInfoForLanes(lane,
                                       def,
                                       toAddSignals,
                                       toAddErrors,
                                       toAddEscalations,
                                       toAddMessages,
                                       toAddItemDefinitions);
        }
        for (Signal s : toAddSignals) {
            def.getRootElements().add(s);
        }
        for (Error er : toAddErrors) {
            def.getRootElements().add(er);
        }
        for (Escalation es : toAddEscalations) {
            def.getRootElements().add(es);
        }
        for (ItemDefinition idef : toAddItemDefinitions) {
            def.getRootElements().add(idef);
        }
        for (Message msg : toAddMessages) {
            def.getRootElements().add(msg);
        }
    }

    public void setCatchEventsInfo(FlowElementsContainer container,
                                   Definitions def,
                                   List<Signal> toAddSignals,
                                   Set<Error> toAddErrors,
                                   Set<Escalation> toAddEscalations,
                                   Set<Message> toAddMessages,
                                   Set<ItemDefinition> toAddItemDefinitions) {
        List<FlowElement> flowElements = container.getFlowElements();
        for (FlowElement fe : flowElements) {
            if (fe instanceof CatchEvent) {
                if (((CatchEvent) fe).getEventDefinitions().size() > 0) {
                    EventDefinition ed = ((CatchEvent) fe).getEventDefinitions().get(0);
                    if (ed instanceof SignalEventDefinition) {
                        SignalEventDefinition sed = (SignalEventDefinition) ed;
                        if (sed.getSignalRef() != null && sed.getSignalRef().length() > 0) {
                            String signalRef = sed.getSignalRef();
                            boolean shouldAddSignal = true;
                            List<RootElement> rootElements = def.getRootElements();
                            for (RootElement re : rootElements) {
                                if (re instanceof Signal) {
                                    if (((Signal) re).getName().equals(signalRef)) {
                                        shouldAddSignal = false;
                                        break;
                                    }
                                }
                            }
                            if (toAddSignals != null) {
                                for (Signal s : toAddSignals) {
                                    if (s.getName().equals(signalRef)) {
                                        shouldAddSignal = false;
                                        break;
                                    }
                                }
                            }
                            if (shouldAddSignal) {
                                Signal signal = Bpmn2Factory.eINSTANCE.createSignal();
                                signal.setName(signalRef);
                                toAddSignals.add(signal);
                            }
                        }
                    } else if (ed instanceof ErrorEventDefinition) {
                        String errorCode = null;
                        String errorId = null;
                        Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute()
                                .iterator();
                        while (iter.hasNext()) {
                            FeatureMap.Entry entry = iter.next();
                            if (entry.getEStructuralFeature().getName()
                                    .equals("erefname")) {
                                errorId = (String) entry.getValue();
                                errorCode = (String) entry.getValue();
                            }
                        }
                        Error err = this._errors.get(errorCode);
                        if (err == null) {
                            err = Bpmn2Factory.eINSTANCE.createError();
                            err.setId(errorId);
                            err.setErrorCode(errorCode);
                            this._errors.put(errorCode,
                                             err);
                        }
                        toAddErrors.add(err);
                        ((ErrorEventDefinition) ed).setErrorRef(err);
                    } else if (ed instanceof EscalationEventDefinition) {
                        String escalationCode = null;
                        Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute().iterator();
                        while (iter.hasNext()) {
                            FeatureMap.Entry entry = iter.next();
                            if (entry.getEStructuralFeature().getName().equals("esccode")) {
                                escalationCode = (String) entry.getValue();
                                break;
                            }
                        }
                        Escalation escalation = this._escalations.get(escalationCode);
                        if (escalation == null) {
                            escalation = Bpmn2Factory.eINSTANCE.createEscalation();
                            escalation.setEscalationCode(escalationCode);
                            this._escalations.put(escalationCode,
                                                  escalation);
                        }
                        toAddEscalations.add(escalation);
                        ((EscalationEventDefinition) ed).setEscalationRef(escalation);
                    } else if (ed instanceof MessageEventDefinition) {
                        ((MessageEventDefinition) ed).setMessageRef(extractMessage(ed,
                                                                                   toAddMessages,
                                                                                   toAddItemDefinitions));
                    } else if (ed instanceof CompensateEventDefinition) {
                        Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute().iterator();
                        while (iter.hasNext()) {
                            FeatureMap.Entry entry = iter.next();
                            if (entry.getEStructuralFeature().getName().equals("actrefname")) {
                                String activityNameRef = (String) entry.getValue();
                                // we have to iterate again through all flow elements
                                // in order to find our activity name
                                List<RootElement> re = def.getRootElements();
                                for (RootElement r : re) {
                                    if (r instanceof Process) {
                                        Process p = (Process) r;
                                        List<FlowElement> fes = p.getFlowElements();
                                        for (FlowElement f : fes) {
                                            if (f instanceof Activity && ((Activity) f).getName().equals(activityNameRef)) {
                                                ((CompensateEventDefinition) ed).setActivityRef((Activity) f);
                                                ((Activity) f).setIsForCompensation(true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (fe instanceof FlowElementsContainer) {
                setCatchEventsInfo((FlowElementsContainer) fe,
                                   def,
                                   toAddSignals,
                                   toAddErrors,
                                   toAddEscalations,
                                   toAddMessages,
                                   toAddItemDefinitions);
            }
        }
    }

    public void setCatchEventsInfoForLanes(Lane lane,
                                           Definitions def,
                                           List<Signal> toAddSignals,
                                           Set<Error> toAddErrors,
                                           Set<Escalation> toAddEscalations,
                                           Set<Message> toAddMessages,
                                           Set<ItemDefinition> toAddItemDefinitions) {
        List<FlowNode> laneFlowNodes = lane.getFlowNodeRefs();
        for (FlowElement fe : laneFlowNodes) {
            if (fe instanceof CatchEvent) {
                if (((CatchEvent) fe).getEventDefinitions().size() > 0) {
                    EventDefinition ed = ((CatchEvent) fe).getEventDefinitions().get(0);
                    if (ed instanceof SignalEventDefinition) {
                        SignalEventDefinition sed = (SignalEventDefinition) ed;
                        if (sed.getSignalRef() != null && sed.getSignalRef().length() > 0) {
                            String signalRef = sed.getSignalRef();
                            boolean shouldAddSignal = true;
                            List<RootElement> rootElements = def.getRootElements();
                            for (RootElement re : rootElements) {
                                if (re instanceof Signal) {
                                    if (((Signal) re).getName().equals(signalRef)) {
                                        shouldAddSignal = false;
                                        break;
                                    }
                                }
                            }
                            if (toAddSignals != null) {
                                for (Signal s : toAddSignals) {
                                    if (s.getName().equals(signalRef)) {
                                        shouldAddSignal = false;
                                        break;
                                    }
                                }
                            }
                            if (shouldAddSignal) {
                                Signal signal = Bpmn2Factory.eINSTANCE.createSignal();
                                signal.setName(signalRef);
                                toAddSignals.add(signal);
                            }
                        }
                    } else if (ed instanceof ErrorEventDefinition) {
                        String errorCode = null;
                        String errorId = null;
                        Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute()
                                .iterator();
                        while (iter.hasNext()) {
                            FeatureMap.Entry entry = iter.next();
                            if (entry.getEStructuralFeature().getName()
                                    .equals("erefname")) {
                                errorId = (String) entry.getValue();
                                errorCode = (String) entry.getValue();
                            }
                        }
                        Error err = this._errors.get(errorCode);
                        if (err == null) {
                            err = Bpmn2Factory.eINSTANCE.createError();
                            err.setId(errorId);
                            err.setErrorCode(errorCode);
                            this._errors.put(errorCode,
                                             err);
                        }
                        toAddErrors.add(err);
                        ((ErrorEventDefinition) ed).setErrorRef(err);
                    } else if (ed instanceof EscalationEventDefinition) {
                        String escalationCode = null;
                        Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute().iterator();
                        while (iter.hasNext()) {
                            FeatureMap.Entry entry = iter.next();
                            if (entry.getEStructuralFeature().getName().equals("esccode")) {
                                escalationCode = (String) entry.getValue();
                                break;
                            }
                        }
                        Escalation escalation = this._escalations.get(escalationCode);
                        if (escalation == null) {
                            escalation = Bpmn2Factory.eINSTANCE.createEscalation();
                            escalation.setEscalationCode(escalationCode);
                            this._escalations.put(escalationCode,
                                                  escalation);
                        }
                        toAddEscalations.add(escalation);
                        ((EscalationEventDefinition) ed).setEscalationRef(escalation);
                    } else if (ed instanceof MessageEventDefinition) {
                        ((MessageEventDefinition) ed).setMessageRef(extractMessage(ed,
                                                                                   toAddMessages,
                                                                                   toAddItemDefinitions));
                    } else if (ed instanceof CompensateEventDefinition) {
                        Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute().iterator();
                        while (iter.hasNext()) {
                            FeatureMap.Entry entry = iter.next();
                            if (entry.getEStructuralFeature().getName().equals("actrefname")) {
                                String activityNameRef = (String) entry.getValue();
                                // we have to iterate again through all flow elements
                                // in order to find our activity name
                                List<RootElement> re = def.getRootElements();
                                for (RootElement r : re) {
                                    if (r instanceof Process) {
                                        Process p = (Process) r;
                                        List<FlowElement> fes = p.getFlowElements();
                                        for (FlowElement f : fes) {
                                            if (f instanceof Activity && ((Activity) f).getName().equals(activityNameRef)) {
                                                ((CompensateEventDefinition) ed).setActivityRef((Activity) f);
                                                ((Activity) f).setIsForCompensation(true);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (fe instanceof FlowElementsContainer) {
                setCatchEventsInfo((FlowElementsContainer) fe,
                                   def,
                                   toAddSignals,
                                   toAddErrors,
                                   toAddEscalations,
                                   toAddMessages,
                                   toAddItemDefinitions);
            }
        }
    }

    /**
     * Updates the gatewayDirection attributes of all gateways.
     * @param def
     */
    private void revisitGateways(Definitions def) {
        List<RootElement> rootElements = def.getRootElements();
        for (RootElement root : rootElements) {
            if (root instanceof Process) {
                setGatewayInfo((Process) root);
            }
        }
    }

    private void setGatewayInfo(FlowElementsContainer container) {
        List<FlowElement> flowElements = container.getFlowElements();
        for (FlowElement fe : flowElements) {
            if (fe instanceof Gateway) {
                Gateway gateway = (Gateway) fe;
                int incoming = gateway.getIncoming() == null ? 0 : gateway.getIncoming().size();
                int outgoing = gateway.getOutgoing() == null ? 0 : gateway.getOutgoing().size();
                if (incoming <= 1 && outgoing > 1) {
                    gateway.setGatewayDirection(GatewayDirection.DIVERGING);
                } else if (incoming > 1 && outgoing <= 1) {
                    gateway.setGatewayDirection(GatewayDirection.CONVERGING);
                }
                // temp. removing support for mixed gateway direction (not supported by runtime yet)
//                else if (incoming > 1 && outgoing > 1) {
//                    gateway.setGatewayDirection(GatewayDirection.MIXED);
//                }
//                else if (incoming == 1 && outgoing == 1) {
//                    // this handles the 1:1 case of the diverging gateways
//                }
                else {
                    gateway.setGatewayDirection(GatewayDirection.UNSPECIFIED);
                }
            }
            if (fe instanceof InclusiveGateway) {
                InclusiveGateway ig = (InclusiveGateway) fe;
                List<SequenceFlow> sqList = new ArrayList<SequenceFlow>();
                if (ig.getIncoming() != null) {
                    sqList.addAll(ig.getIncoming());
                }
                if (ig.getOutgoing() != null) {
                    sqList.addAll(ig.getOutgoing());
                }
                setDefaultGateway(fe,
                                  sqList);
            }
            if (fe instanceof ExclusiveGateway) {
                ExclusiveGateway eg = (ExclusiveGateway) fe;
                List<SequenceFlow> sqList = new ArrayList<SequenceFlow>();
                if (eg.getIncoming() != null) {
                    sqList.addAll(eg.getIncoming());
                }
                if (eg.getOutgoing() != null) {
                    sqList.addAll(eg.getOutgoing());
                }
                setDefaultGateway(fe,
                                  sqList);
            }
            if (fe instanceof FlowElementsContainer) {
                setGatewayInfo((FlowElementsContainer) fe);
            }
        }
    }

    private void setDefaultGateway(FlowElement fe,
                                   List<SequenceFlow> sqList) {
        Iterator<FeatureMap.Entry> iter = fe.getAnyAttribute().iterator();
        while (iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if (entry.getEStructuralFeature().getName().equals("dg")) {
                for (SequenceFlow newFlow : sqList) {
                    String entryValue = (String) entry.getValue();
                    String entryValueId = "";
                    String[] entryValueParts = entryValue.split(" : ");
                    if (entryValueParts.length == 1) {
                        entryValueId = entryValueParts[0];
                    } else if (entryValueParts.length > 1) {
                        entryValueId = entryValueParts[1];
                    }
                    if (newFlow.getId().equals(entryValueId)) {
                        if (fe instanceof ExclusiveGateway) {
                            ((ExclusiveGateway) fe).setDefault(newFlow);
                        } else if (fe instanceof InclusiveGateway) {
                            ((InclusiveGateway) fe).setDefault(newFlow);
                        }
                        if (newFlow.getConditionExpression() == null) {
                            FormalExpression expr = Bpmn2Factory.eINSTANCE.createFormalExpression();
                            expr.setBody("");
                            newFlow.setConditionExpression(expr);
                        }
                    }
                }
            }
        }
    }

    private void revisitServiceTasks(Definitions def) {
        List<RootElement> rootElements = def.getRootElements();
        List<Interface> toAddInterfaces = new ArrayList<Interface>();
        List<Message> toAddMessages = new ArrayList<Message>();
        List<ItemDefinition> toAddDefinitions = new ArrayList<ItemDefinition>();
        for (RootElement root : rootElements) {
            if (root instanceof Process) {
                revisitServiceTasksExecute((Process) root,
                                           rootElements,
                                           toAddInterfaces,
                                           toAddMessages,
                                           toAddDefinitions);
            }
        }
        for (Lane lane : _lanes) {
            revisitServiceTasksExecuteForLanes(lane,
                                               def,
                                               rootElements,
                                               toAddInterfaces,
                                               toAddMessages,
                                               toAddDefinitions);
        }
        for (ItemDefinition id : toAddDefinitions) {
            def.getRootElements().add(id);
        }
        for (Message m : toAddMessages) {
            def.getRootElements().add(m);
        }
        for (Interface i : toAddInterfaces) {
            def.getRootElements().add(i);
        }
    }

    private void revisitServiceTasksExecuteForLanes(Lane lane,
                                                    Definitions def,
                                                    List<RootElement> rootElements,
                                                    List<Interface> toAddInterfaces,
                                                    List<Message> toAddMessages,
                                                    List<ItemDefinition> toAddDefinitions) {
        List<FlowNode> laneFlowNodes = lane.getFlowNodeRefs();
        for (FlowElement fe : laneFlowNodes) {
            if (fe instanceof ServiceTask) {
                Iterator<FeatureMap.Entry> iter = fe.getAnyAttribute().iterator();
                String serviceImplementation = null;
                String serviceInterface = null;
                String serviceOperation = null;
                EStructuralFeature serviceInterfaceFeature = null;
                EStructuralFeature serviceOperationFeature = null;
                while (iter.hasNext()) {
                    FeatureMap.Entry entry = iter.next();
                    if (entry.getEStructuralFeature().getName().equals("serviceimplementation")) {
                        serviceImplementation = (String) entry.getValue();
                    }
                    if (entry.getEStructuralFeature().getName().equals("serviceoperation")) {
                        serviceOperation = (String) entry.getValue();
                        serviceOperationFeature = entry.getEStructuralFeature();
                    }
                    if (entry.getEStructuralFeature().getName().equals("serviceinterface")) {
                        serviceInterface = (String) entry.getValue();
                        serviceInterfaceFeature = entry.getEStructuralFeature();
                    }
                }
                boolean foundInterface = false;
                Interface touseInterface = null;
                if (serviceImplementation != null && serviceImplementation.equals("Java")) {
                    for (RootElement iroot : rootElements) {
                        if (iroot instanceof Interface && ((Interface) iroot).getName().equals(serviceInterface)) {
                            foundInterface = true;
                            touseInterface = (Interface) iroot;
                            break;
                        }
                    }
                    if (!foundInterface) {
                        for (Interface toadd : toAddInterfaces) {
                            if (toadd.getName() != null && toadd.getName().equals(serviceInterface)) {
                                foundInterface = true;
                                touseInterface = toadd;
                                break;
                            }
                        }
                    }
                } else if (serviceImplementation != null && serviceImplementation.equals("##WebService")) {
                    for (RootElement iroot : rootElements) {
                        if (iroot instanceof Interface && ((Interface) iroot).getImplementationRef().equals(serviceInterface)) {
                            foundInterface = true;
                            touseInterface = (Interface) iroot;
                            break;
                        }
                    }
                    if (!foundInterface) {
                        for (Interface toadd : toAddInterfaces) {
                            if (toadd.getImplementationRef().equals(serviceInterface)) {
                                foundInterface = true;
                                touseInterface = toadd;
                                break;
                            }
                        }
                    }
                }
                if (!foundInterface) {
                    touseInterface = Bpmn2Factory.eINSTANCE.createInterface();
                    if (serviceInterface == null || serviceInterface.length() == 0) {
                        serviceInterface = fe.getId() + "_ServiceInterface";
                        if (serviceInterfaceFeature != null) {
                            fe.getAnyAttribute().set(serviceInterfaceFeature,
                                                     serviceInterface);
                        }
                    }
                    touseInterface.setName(serviceInterface);
                    touseInterface.setImplementationRef(serviceInterface);
                    touseInterface.setId(fe.getId() + "_ServiceInterface");
                    toAddInterfaces.add(touseInterface);
                }
                if (serviceOperation != null) {
                    boolean foundOperation = false;
                    for (Operation oper : touseInterface.getOperations()) {
                        if (serviceImplementation != null && serviceImplementation.equals("Java")) {
                            if (oper.getName().equals(serviceOperation)) {
                                foundOperation = true;
                                break;
                            }
                        } else if (serviceImplementation != null && serviceImplementation.equals("##WebService")) {
                            if (oper.getImplementationRef().equals(serviceOperation)) {
                                foundOperation = true;
                                break;
                            }
                        }
                    }
                    if (!foundOperation) {
                        Operation touseOperation = Bpmn2Factory.eINSTANCE.createOperation();
                        if (serviceOperation == null || serviceOperation.length() == 0) {
                            serviceOperation = fe.getId() + "_ServiceOperation";
                            if (serviceOperationFeature != null) {
                                fe.getAnyAttribute().set(serviceOperationFeature,
                                                         serviceOperation);
                            }
                        }
                        touseOperation.setId(fe.getId() + "_ServiceOperation");
                        touseOperation.setName(serviceOperation);
                        touseOperation.setImplementationRef(serviceOperation);
                        Message message = Bpmn2Factory.eINSTANCE.createMessage();
                        message.setId(fe.getId() + "_InMessage");
                        ItemDefinition itemdef = Bpmn2Factory.eINSTANCE.createItemDefinition();
                        itemdef.setId(message.getId() + "Type");
                        message.setItemRef(itemdef);
                        toAddDefinitions.add(itemdef);
                        toAddMessages.add(message);
                        touseOperation.setInMessageRef(message);
                        touseInterface.getOperations().add(touseOperation);
                        ((ServiceTask) fe).setOperationRef(touseOperation);
                    }
                }
            } else if (fe instanceof FlowElementsContainer) {
                revisitServiceTasksExecute((FlowElementsContainer) fe,
                                           rootElements,
                                           toAddInterfaces,
                                           toAddMessages,
                                           toAddDefinitions);
            }
        }
    }

    private void revisitServiceTasksExecute(FlowElementsContainer container,
                                            List<RootElement> rootElements,
                                            List<Interface> toAddInterfaces,
                                            List<Message> toAddMessages,
                                            List<ItemDefinition> toAddDefinitions) {
        List<FlowElement> flowElements = container.getFlowElements();
        for (FlowElement fe : flowElements) {
            if (fe instanceof ServiceTask) {
                Iterator<FeatureMap.Entry> iter = fe.getAnyAttribute().iterator();
                String serviceImplementation = null;
                String serviceInterface = null;
                String serviceOperation = null;
                EStructuralFeature serviceInterfaceFeature = null;
                EStructuralFeature serviceOperationFeature = null;
                while (iter.hasNext()) {
                    FeatureMap.Entry entry = iter.next();
                    if (entry.getEStructuralFeature().getName().equals("serviceimplementation")) {
                        serviceImplementation = (String) entry.getValue();
                    }
                    if (entry.getEStructuralFeature().getName().equals("serviceoperation")) {
                        serviceOperation = (String) entry.getValue();
                        serviceOperationFeature = entry.getEStructuralFeature();
                    }
                    if (entry.getEStructuralFeature().getName().equals("serviceinterface")) {
                        serviceInterface = (String) entry.getValue();
                        serviceInterfaceFeature = entry.getEStructuralFeature();
                    }
                }
                boolean foundInterface = false;
                Interface touseInterface = null;
                if (serviceImplementation != null && serviceImplementation.equals("Java")) {
                    for (RootElement iroot : rootElements) {
                        if (iroot instanceof Interface && ((Interface) iroot).getName().equals(serviceInterface)) {
                            foundInterface = true;
                            touseInterface = (Interface) iroot;
                            break;
                        }
                    }
                    if (!foundInterface) {
                        for (Interface toadd : toAddInterfaces) {
                            if (toadd.getName() != null && toadd.getName().equals(serviceInterface)) {
                                foundInterface = true;
                                touseInterface = toadd;
                                break;
                            }
                        }
                    }
                } else if (serviceImplementation != null && serviceImplementation.equals("##WebService")) {
                    for (RootElement iroot : rootElements) {
                        if (iroot instanceof Interface && ((Interface) iroot).getImplementationRef().equals(serviceInterface)) {
                            foundInterface = true;
                            touseInterface = (Interface) iroot;
                            break;
                        }
                    }
                    if (!foundInterface) {
                        for (Interface toadd : toAddInterfaces) {
                            if (toadd.getImplementationRef().equals(serviceInterface)) {
                                foundInterface = true;
                                touseInterface = toadd;
                                break;
                            }
                        }
                    }
                }
                if (!foundInterface) {
                    touseInterface = Bpmn2Factory.eINSTANCE.createInterface();
                    if (serviceInterface == null || serviceInterface.length() == 0) {
                        serviceInterface = fe.getId() + "_ServiceInterface";
                        if (serviceInterfaceFeature != null) {
                            fe.getAnyAttribute().set(serviceInterfaceFeature,
                                                     serviceInterface);
                        }
                    }
                    touseInterface.setName(serviceInterface);
                    touseInterface.setImplementationRef(serviceInterface);
                    touseInterface.setId(fe.getId() + "_ServiceInterface");
                    toAddInterfaces.add(touseInterface);
                }
                if (serviceOperation != null) {
                    boolean foundOperation = false;
                    for (Operation oper : touseInterface.getOperations()) {
                        if (serviceImplementation != null && serviceImplementation.equals("Java")) {
                            if (oper.getName().equals(serviceOperation)) {
                                foundOperation = true;
                                break;
                            }
                        } else if (serviceImplementation != null && serviceImplementation.equals("##WebService")) {
                            if (oper.getImplementationRef().equals(serviceOperation)) {
                                foundOperation = true;
                                break;
                            }
                        }
                    }
                    if (!foundOperation) {
                        Operation touseOperation = Bpmn2Factory.eINSTANCE.createOperation();
                        if (serviceOperation == null || serviceOperation.length() == 0) {
                            serviceOperation = fe.getId() + "_ServiceOperation";
                            if (serviceOperationFeature != null) {
                                fe.getAnyAttribute().set(serviceOperationFeature,
                                                         serviceOperation);
                            }
                        }
                        touseOperation.setId(fe.getId() + "_ServiceOperation");
                        touseOperation.setName(serviceOperation);
                        touseOperation.setImplementationRef(serviceOperation);
                        Message message = Bpmn2Factory.eINSTANCE.createMessage();
                        message.setId(fe.getId() + "_InMessage");
                        ItemDefinition itemdef = Bpmn2Factory.eINSTANCE.createItemDefinition();
                        itemdef.setId(message.getId() + "Type");
                        message.setItemRef(itemdef);
                        toAddDefinitions.add(itemdef);
                        toAddMessages.add(message);
                        touseOperation.setInMessageRef(message);
                        touseInterface.getOperations().add(touseOperation);
                        ((ServiceTask) fe).setOperationRef(touseOperation);
                    }
                }
            } else if (fe instanceof FlowElementsContainer) {
                revisitServiceTasksExecute((FlowElementsContainer) fe,
                                           rootElements,
                                           toAddInterfaces,
                                           toAddMessages,
                                           toAddDefinitions);
            }
        }
    }

    /**
     * Revisit message to set their item ref to a item definition
     * @param def Definitions
     */
    private void revisitMessages(Definitions def) {
        List<RootElement> rootElements = def.getRootElements();
        List<ItemDefinition> toAddDefinitions = new ArrayList<ItemDefinition>();
        for (RootElement root : rootElements) {
            if (root instanceof Message) {
                if (!existsMessageItemDefinition(rootElements,
                                                 root.getId())) {
                    ItemDefinition itemdef = Bpmn2Factory.eINSTANCE.createItemDefinition();
                    itemdef.setId(root.getId() + "Type");
                    toAddDefinitions.add(itemdef);
                    ((Message) root).setItemRef(itemdef);
                }
            }
        }
        for (ItemDefinition id : toAddDefinitions) {
            def.getRootElements().add(id);
        }
    }

    private boolean existsMessageItemDefinition(List<RootElement> rootElements,
                                                String id) {
        for (RootElement root : rootElements) {
            if (root instanceof ItemDefinition && root.getId().equals(id + "Type")) {
                return true;
            }
        }
        return false;
    }

    private ItemDefinition getMessageItemDefinition(List<RootElement> rootElements,
                                                    String id) {
        String testId = "_" + id + "Item";
        for (RootElement root : rootElements) {
            if (root instanceof ItemDefinition && root.getId().equals(testId)) {
                return (ItemDefinition) root;
            }
        }
        return null;
    }

    /**
     * Reconnect the sequence flows and the flow nodes.
     * Done after the initial pass so that we have all the target information.
     */
    private void reconnectFlows() {
        // create the reverse id map:
        for (Entry<Object, List<String>> entry : _outgoingFlows.entrySet()) {
            for (String flowId : entry.getValue()) {
                if (entry.getKey() instanceof SequenceFlow) { // if it is a sequence flow, we can tell its targets
                    if (_idMap.get(flowId) instanceof FlowNode) {
                        ((SequenceFlow) entry.getKey()).setTargetRef((FlowNode) _idMap.get(flowId));
                    }
                    if (_idMap.get(flowId) instanceof Association) {
                        ((Association) _idMap.get(flowId)).setTargetRef((SequenceFlow) entry.getKey());
                    }
                } else if (entry.getKey() instanceof Association) {
                    ((Association) entry.getKey()).setTargetRef((BaseElement) _idMap.get(flowId));
                } else { // if it is a node, we can map it to its outgoing sequence flows
                    if (_idMap.get(flowId) instanceof SequenceFlow) {
                        ((FlowNode) entry.getKey()).getOutgoing().add((SequenceFlow) _idMap.get(flowId));
                    } else if (_idMap.get(flowId) instanceof Association) {
                        ((Association) _idMap.get(flowId)).setSourceRef((BaseElement) entry.getKey());
                    }
                }
            }
        }
    }

    private void createSubProcessDiagram(BPMNPlane plane,
                                         FlowElement flowElement,
                                         BpmnDiFactory factory) {
        SubProcess sp = (SubProcess) flowElement;
        for (FlowElement subProcessFlowElement : sp.getFlowElements()) {
            if (subProcessFlowElement instanceof SubProcess) {
                createBpmnShapeForElement(factory,
                                          plane,
                                          subProcessFlowElement);
                createSubProcessDiagram(plane,
                                        subProcessFlowElement,
                                        factory);
            } else if (subProcessFlowElement instanceof FlowNode) {
                createBpmnShapeForElement(factory,
                                          plane,
                                          subProcessFlowElement);
                if (subProcessFlowElement instanceof BoundaryEvent) {
                    createDockersForBoundaryEvent((BoundaryEvent) subProcessFlowElement);
                }
            } else if (subProcessFlowElement instanceof SequenceFlow) {
                createBpmnEdgeForSequenceFlow(factory,
                                              plane,
                                              (SequenceFlow) subProcessFlowElement);
            }
        }
        if (sp.getArtifacts() != null) {
            List<Association> incompleteAssociations = new ArrayList<Association>();
            for (Artifact artifact : sp.getArtifacts()) {
                //if (artifact instanceof TextAnnotation || artifact instanceof Group) {
                if (artifact instanceof Group) {
                    createBpmnShapeForElement(factory,
                                              plane,
                                              artifact);
                }
                if (artifact instanceof Association) {
                    Association association = (Association) artifact;
                    if (association.getSourceRef() != null && association.getTargetRef() != null) {
                        createBpmnEdgeForAssociation(factory,
                                                     plane,
                                                     association);
                    } else {
                        incompleteAssociations.add(association);
                    }
                }
            }
            if (!incompleteAssociations.isEmpty()) {
                for (Association incompleteAssociation : incompleteAssociations) {
                    sp.getArtifacts().remove(incompleteAssociation);
                }
            }
        }
    }

    private void createDiagram(Definitions def) {
        for (RootElement rootElement : def.getRootElements()) {
            if (rootElement instanceof Process) {
                Process process = (Process) rootElement;
                BpmnDiFactory factory = BpmnDiFactory.eINSTANCE;
                BPMNDiagram diagram = factory.createBPMNDiagram();
                BPMNPlane plane = factory.createBPMNPlane();
                plane.setBpmnElement(process);
                diagram.setPlane(plane);
                // first process flowNodes
                for (FlowElement flowElement : process.getFlowElements()) {
                    if (flowElement instanceof FlowNode) {
                        createBpmnShapeForElement(factory,
                                                  plane,
                                                  flowElement);
                        if (flowElement instanceof BoundaryEvent) {
                            createDockersForBoundaryEvent((BoundaryEvent) flowElement);
                        }
                        // check if its a subprocess
                        if (flowElement instanceof SubProcess) {
                            createSubProcessDiagram(plane,
                                                    flowElement,
                                                    factory);
                        }
                    } else if (flowElement instanceof DataObject) {
                        createBpmnShapeForElement(factory,
                                                  plane,
                                                  flowElement);
                    } else if (flowElement instanceof SequenceFlow) {
                        createBpmnEdgeForSequenceFlow(factory,
                                                      plane,
                                                      (SequenceFlow) flowElement);
                    }
                }
                // then process artifacts
                if (process.getArtifacts() != null) {
                    List<Association> incompleteAssociations = new ArrayList<Association>();
                    for (Artifact artifact : process.getArtifacts()) {
                        //if (artifact instanceof TextAnnotation || artifact instanceof Group) {
                        if (artifact instanceof Group) {
                            createBpmnShapeForElement(factory,
                                                      plane,
                                                      artifact);
                        }
                        if (artifact instanceof Association) {
                            Association association = (Association) artifact;
                            if (association.getSourceRef() != null && association.getTargetRef() != null) {
                                createBpmnEdgeForAssociation(factory,
                                                             plane,
                                                             association);
                            } else {
                                incompleteAssociations.add(association);
                            }
                        }
                    }
                    if (!incompleteAssociations.isEmpty()) {
                        for (Association incompleteAssociation : incompleteAssociations) {
                            process.getArtifacts().remove(incompleteAssociation);
                        }
                    }
                }
                // finally process lanes
                if (process.getLaneSets() != null && process.getLaneSets().size() > 0) {
                    for (LaneSet ls : process.getLaneSets()) {
                        for (Lane lane : ls.getLanes()) {
                            createBpmnShapeForElement(factory,
                                                      plane,
                                                      lane);
                        }
                    }
                }
                def.getDiagrams().add(diagram);
            }
        }
    }

    private void createBpmnShapeForElement(BpmnDiFactory factory,
                                           BPMNPlane plane,
                                           BaseElement element) {
        Bounds bounds = _bounds.get(element.getId());
        if (bounds != null) {
            BPMNShape shape = factory.createBPMNShape();
            shape.setBpmnElement(element);
            shape.setBounds(bounds);
            plane.getPlaneElement().add(shape);
        }
    }

    private void createDockersForBoundaryEvent(BoundaryEvent boundaryEvent) {
        List<Point> dockers = _dockers.get(boundaryEvent.getId());
        StringBuffer dockerBuff = new StringBuffer();
        for (int i = 0; i < dockers.size(); i++) {
            dockerBuff.append(dockers.get(i).getX());
            dockerBuff.append("^");
            dockerBuff.append(dockers.get(i).getY());
            dockerBuff.append("|");
        }
        ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
        EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                "http://www.jboss.org/drools",
                "dockerinfo",
                false,
                false);
        SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                         dockerBuff.toString());
        boundaryEvent.getAnyAttribute().add(extensionEntry);
    }

    private void createBpmnEdgeForSequenceFlow(BpmnDiFactory factory,
                                               BPMNPlane plane,
                                               SequenceFlow sequenceFlow) {
        BPMNEdge edge = factory.createBPMNEdge();
        edge.setBpmnElement(sequenceFlow);
        DcFactory dcFactory = DcFactory.eINSTANCE;
        Point point = dcFactory.createPoint();
        List<Point> dockers = _dockers.get(sequenceFlow.getId());
        if (sequenceFlow.getSourceRef() != null) {
            Bounds sourceBounds = _bounds.get(sequenceFlow.getSourceRef().getId());
            // Test for valid docker with X and Y  > -1, created by EdgeParser
            if (dockers != null && dockers.size() > 0 && dockers.get(0).getX() > -1 && dockers.get(0).getY() > -1) {
                // First docker is connection to Source
                point.setX(sourceBounds.getX() + dockers.get(0).getX());
                point.setY(sourceBounds.getY() + dockers.get(0).getY());
            } else {
                // Default is right middle of Source
                point.setX(sourceBounds.getX() + sourceBounds.getWidth());
                point.setY(sourceBounds.getY() + (sourceBounds.getHeight() / 2));
            }
        }
        edge.getWaypoint().add(point);
        for (int i = 1; i < dockers.size() - 1; i++) {
            edge.getWaypoint().add(dockers.get(i));
        }
        point = dcFactory.createPoint();
        if (sequenceFlow.getTargetRef() != null) {
            Bounds targetBounds = _bounds.get(sequenceFlow.getTargetRef().getId());
            // Test for valid docker with X and Y  > -1, created by EdgeParser
            if (dockers != null && dockers.size() > 1 && dockers.get(dockers.size() - 1).getX() > -1 && dockers.get(dockers.size() - 1).getY() > -1) {
                // Last docker is connection to Target
                point.setX(targetBounds.getX() + dockers.get(dockers.size() - 1).getX());
                point.setY(targetBounds.getY() + dockers.get(dockers.size() - 1).getY());
            } else {
                // Default is left middle of Target
                point.setX(targetBounds.getX());
                point.setY(targetBounds.getY() + (targetBounds.getHeight() / 2));
            }
        }
        edge.getWaypoint().add(point);
        plane.getPlaneElement().add(edge);
    }

    private void createBpmnEdgeForAssociation(BpmnDiFactory factory,
                                              BPMNPlane plane,
                                              Association association) {
        BPMNEdge edge = factory.createBPMNEdge();
        edge.setBpmnElement(association);
        DcFactory dcFactory = DcFactory.eINSTANCE;
        Point point = dcFactory.createPoint();
        Bounds sourceBounds = _bounds.get(association.getSourceRef().getId());
        point.setX(sourceBounds.getX() + (sourceBounds.getWidth() / 2));
        point.setY(sourceBounds.getY() + (sourceBounds.getHeight() / 2));
        edge.getWaypoint().add(point);
        List<Point> dockers = _dockers.get(association.getId());
        for (int i = 1; i < dockers.size() - 1; i++) {
            edge.getWaypoint().add(dockers.get(i));
        }
        point = dcFactory.createPoint();
        Bounds targetBounds = _bounds.get(association.getTargetRef().getId());
        point.setX(targetBounds.getX() + (targetBounds.getWidth() / 2));
        point.setY(targetBounds.getY() + (targetBounds.getHeight() / 2));
        edge.getWaypoint().add(point);
        plane.getPlaneElement().add(edge);
    }

    public BaseElement unmarshallItem(JsonParser parser,
                                      String preProcessingData) throws JsonParseException, IOException {
        String resourceId = null;
        Map<String, String> properties = null;
        String stencil = null;
        List<BaseElement> childElements = new ArrayList<BaseElement>();
        List<String> outgoing = new ArrayList<String>();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = parser.getCurrentName();
            parser.nextToken();
            if ("resourceId".equals(fieldname)) {
                resourceId = parser.getText();
            } else if ("properties".equals(fieldname)) {
                properties = unmarshallProperties(parser);
            } else if ("stencil".equals(fieldname)) {
                // "stencil":{"id":"Task"},
                parser.nextToken();
                parser.nextToken();
                stencil = parser.getText();
                parser.nextToken();
            } else if ("childShapes".equals(fieldname)) {
                while (parser.nextToken() != JsonToken.END_ARRAY) { // open the
                    // object
                    // the childShapes element is a json array. We opened the
                    // array.
                    childElements.add(unmarshallItem(parser,
                                                     preProcessingData));
                }
            } else if ("bounds".equals(fieldname)) {
                // bounds: {"lowerRight":{"x":484.0,"y":198.0},"upperLeft":{"x":454.0,"y":168.0}}
                parser.nextToken();
                parser.nextToken();
                parser.nextToken();
                parser.nextToken();
                Integer x2 = parser.getIntValue();
                parser.nextToken();
                parser.nextToken();
                Integer y2 = parser.getIntValue();
                parser.nextToken();
                parser.nextToken();
                parser.nextToken();
                parser.nextToken();
                parser.nextToken();
                Integer x1 = parser.getIntValue();
                parser.nextToken();
                parser.nextToken();
                Integer y1 = parser.getIntValue();
                parser.nextToken();
                parser.nextToken();
                Bounds b = DcFactory.eINSTANCE.createBounds();
                b.setX(x1);
                b.setY(y1);
                b.setWidth(x2 - x1);
                b.setHeight(y2 - y1);
                this._bounds.put(resourceId,
                                 b);
            } else if ("dockers".equals(fieldname)) {
                // "dockers":[{"x":50,"y":40},{"x":353.5,"y":115},{"x":353.5,"y":152},{"x":50,"y":40}],
                List<Point> dockers = new ArrayList<Point>();
                JsonToken nextToken = parser.nextToken();
                boolean end = JsonToken.END_ARRAY.equals(nextToken);
                while (!end) {
                    nextToken = parser.nextToken();
                    nextToken = parser.nextToken();
                    Integer x = parser.getIntValue();
                    parser.nextToken();
                    parser.nextToken();
                    Integer y = parser.getIntValue();
                    Point point = DcFactory.eINSTANCE.createPoint();
                    point.setX(x);
                    point.setY(y);
                    dockers.add(point);
                    parser.nextToken();
                    nextToken = parser.nextToken();
                    end = JsonToken.END_ARRAY.equals(nextToken);
                }
                this._dockers.put(resourceId,
                                  dockers);
            } else if ("outgoing".equals(fieldname)) {
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    // {resourceId: oryx_1AAA8C9A-39A5-42FC-8ED1-507A7F3728EA}
                    parser.nextToken();
                    parser.nextToken();
                    outgoing.add(parser.getText());
                    parser.nextToken();
                }
                // pass on the array
                parser.skipChildren();
            } else if ("target".equals(fieldname)) {
                // we already collected that info with the outgoing field.
                parser.skipChildren();
                // "target": {
                // "resourceId": "oryx_A75E7546-DF71-48EA-84D3-2A8FD4A47568"
                // }
                // add to the map:
                // parser.nextToken(); // resourceId:
                // parser.nextToken(); // the value we want to save
                // targetId = parser.getText();
                // parser.nextToken(); // }, closing the object
            }
        }
        properties.put("resourceId",
                       resourceId);
        boolean customElement = isCustomElement(properties.get("tasktype"),
                                                preProcessingData);
        BaseElement baseElt = this.createBaseElement(stencil,
                                                     properties.get("tasktype"),
                                                     customElement);
        // register the sequence flow targets.
        if (baseElt instanceof SequenceFlow) {
            _sequenceFlowTargets.addAll(outgoing);
        }
        _outgoingFlows.put(baseElt,
                           outgoing);
        _objMap.put(baseElt,
                    resourceId); // keep the object around to do connections
        _idMap.put(resourceId,
                   baseElt);
        // baseElt.setId(resourceId); commented out as bpmn2 seems to create
        // duplicate ids right now.
        applyProperties(baseElt,
                        properties,
                        preProcessingData);
        if (baseElt instanceof Definitions) {
            Process rootLevelProcess = null;
            if (childElements == null || childElements.size() < 1) {
                if (rootLevelProcess == null) {
                    rootLevelProcess = Bpmn2Factory.eINSTANCE.createProcess();
                    // set the properties and item definitions first
                    if (properties.get("vardefs") != null && properties.get("vardefs").length() > 0) {
                        String[] vardefs = properties.get("vardefs").split(",\\s*");
                        for (String vardef : vardefs) {
                            Property prop = Bpmn2Factory.eINSTANCE.createProperty();
                            ItemDefinition itemdef = Bpmn2Factory.eINSTANCE.createItemDefinition();
                            // check if we define a structure ref in the definition
                            if (vardef.contains(":")) {
                                String[] vardefParts = vardef.split(":\\s*");
                                prop.setId(vardefParts[0]);
                                itemdef.setId("_" + prop.getId() + "Item");
                                boolean haveKPI = false;
                                String kpiValue = "";
                                if (vardefParts.length == 3) {
                                    itemdef.setStructureRef(vardefParts[1]);
                                    if (vardefParts[2].equals("true")) {
                                        haveKPI = true;
                                        kpiValue = vardefParts[2];
                                    }
                                }
                                if (vardefParts.length == 2) {
                                    if (vardefParts[1].equals("true") || vardefParts[1].equals("false")) {
                                        if (vardefParts[1].equals("true")) {
                                            haveKPI = true;
                                            kpiValue = vardefParts[1];
                                        }
                                    } else {
                                        itemdef.setStructureRef(vardefParts[1]);
                                    }
                                }
                                if (haveKPI) {
                                    Utils.setMetaDataExtensionValue(prop,
                                                                    "customKPI",
                                                                    wrapInCDATABlock(kpiValue));
                                }
                            } else {
                                prop.setId(vardef);
                                itemdef.setId("_" + prop.getId() + "Item");
                            }
                            prop.setItemSubjectRef(itemdef);
                            rootLevelProcess.getProperties().add(prop);
                            ((Definitions) baseElt).getRootElements().add(itemdef);
                        }
                    }
                    if (properties.get("adhocprocess") != null && properties.get("adhocprocess").equals("true")) {
                        ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                        EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools",
                                "adHoc",
                                false,
                                false);
                        SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                         properties.get("adhocprocess"));
                        rootLevelProcess.getAnyAttribute().add(extensionEntry);
                    }
                    if (properties.get("customdescription") != null && properties.get("customdescription").length() > 0) {
                        Utils.setMetaDataExtensionValue(rootLevelProcess,
                                                        "customDescription",
                                                        wrapInCDATABlock(properties.get("customdescription")));
                    }
                    rootLevelProcess.setId(properties.get("id"));
                    applyProcessProperties(rootLevelProcess,
                                           properties);
                    ((Definitions) baseElt).getRootElements().add(rootLevelProcess);
                }
            } else {
                for (BaseElement child : childElements) {
                    // tasks are only permitted under processes.
                    // a process should be created implicitly for tasks at the root
                    // level.
                    // process designer doesn't make a difference between tasks and
                    // global tasks.
                    // if a task has sequence edges it is considered a task,
                    // otherwise it is considered a global task.
                    //                if (child instanceof Task && _outgoingFlows.get(child).isEmpty() && !_sequenceFlowTargets.contains(_objMap.get(child))) {
                    //                    // no edges on a task at the top level! We replace it with a
                    //                    // global task.
                    //                    GlobalTask task = null;
                    //                    if (child instanceof ScriptTask) {
                    //                        task = Bpmn2Factory.eINSTANCE.createGlobalScriptTask();
                    //                        ((GlobalScriptTask) task).setScript(((ScriptTask) child).getScript());
                    //                        ((GlobalScriptTask) task).setScriptLanguage(((ScriptTask) child).getScriptFormat());
                    //                        // TODO scriptLanguage missing on scriptTask
                    //                    } else if (child instanceof UserTask) {
                    //                        task = Bpmn2Factory.eINSTANCE.createGlobalUserTask();
                    //                    } else if (child instanceof ServiceTask) {
                    //                        // we don't have a global service task! Fallback on a
                    //                        // normal global task
                    //                        task = Bpmn2Factory.eINSTANCE.createGlobalTask();
                    //                    } else if (child instanceof BusinessRuleTask) {
                    //                        task = Bpmn2Factory.eINSTANCE.createGlobalBusinessRuleTask();
                    //                    } else if (child instanceof ManualTask) {
                    //                        task = Bpmn2Factory.eINSTANCE.createGlobalManualTask();
                    //                    } else {
                    //                        task = Bpmn2Factory.eINSTANCE.createGlobalTask();
                    //                    }
                    //
                    //                    task.setName(((Task) child).getName());
                    //                    task.setIoSpecification(((Task) child).getIoSpecification());
                    //                    task.getDocumentation().addAll(((Task) child).getDocumentation());
                    //                    ((Definitions) baseElt).getRootElements().add(task);
                    //                    continue;
                    //                } else {
                    if (child instanceof SequenceFlow) {
                        // for some reason sequence flows are placed as root elements.
                        // find if the target has a container, and if we can use it:
                        List<String> ids = _outgoingFlows.get(child);
                        FlowElementsContainer container = null;
                        for (String id : ids) { // yes, we iterate, but we'll take the first in the list that will work.
                            Object obj = _idMap.get(id);
                            if (obj instanceof EObject && ((EObject) obj).eContainer() instanceof FlowElementsContainer) {
                                container = (FlowElementsContainer) ((EObject) obj).eContainer();
                                break;
                            }
                        }
                        if (container != null) {
                            container.getFlowElements().add((SequenceFlow) child);
                            continue;
                        }
                    }
                    if (child instanceof Task || child instanceof SequenceFlow
                            || child instanceof Gateway || child instanceof Event
                            || child instanceof Artifact || child instanceof DataObject || child instanceof SubProcess
                            || child instanceof Lane || child instanceof CallActivity || child instanceof TextAnnotation) {
                        if (rootLevelProcess == null) {
                            rootLevelProcess = Bpmn2Factory.eINSTANCE.createProcess();
                            // set the properties and item definitions first
                            if (properties.get("vardefs") != null && properties.get("vardefs").length() > 0) {
                                String[] vardefs = properties.get("vardefs").split(",\\s*");
                                for (String vardef : vardefs) {
                                    Property prop = Bpmn2Factory.eINSTANCE.createProperty();
                                    ItemDefinition itemdef = Bpmn2Factory.eINSTANCE.createItemDefinition();
                                    // check if we define a structure ref in the definition
                                    if (vardef.contains(":")) {
                                        String[] vardefParts = vardef.split(":\\s*");
                                        prop.setId(vardefParts[0]);
                                        itemdef.setId("_" + prop.getId() + "Item");
                                        boolean haveKPI = false;
                                        String kpiValue = "";
                                        if (vardefParts.length == 3) {
                                            itemdef.setStructureRef(vardefParts[1]);
                                            if (vardefParts[2].equals("true")) {
                                                haveKPI = true;
                                                kpiValue = vardefParts[2];
                                            }
                                        }
                                        if (vardefParts.length == 2) {
                                            if (vardefParts[1].equals("true") || vardefParts[1].equals("false")) {
                                                if (vardefParts[1].equals("true")) {
                                                    haveKPI = true;
                                                    kpiValue = vardefParts[1];
                                                }
                                            } else {
                                                itemdef.setStructureRef(vardefParts[1]);
                                            }
                                        }
                                        if (haveKPI) {
                                            Utils.setMetaDataExtensionValue(prop,
                                                                            "customKPI",
                                                                            wrapInCDATABlock(kpiValue));
                                        }
                                    } else {
                                        prop.setId(vardef);
                                        itemdef.setId("_" + prop.getId() + "Item");
                                    }
                                    prop.setItemSubjectRef(itemdef);
                                    rootLevelProcess.getProperties().add(prop);
                                    ((Definitions) baseElt).getRootElements().add(itemdef);
                                }
                            }
                            if (properties.get("adhocprocess") != null && properties.get("adhocprocess").equals("true")) {
                                ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                                EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                        "http://www.jboss.org/drools",
                                        "adHoc",
                                        false,
                                        false);
                                SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                                 properties.get("adhocprocess"));
                                rootLevelProcess.getAnyAttribute().add(extensionEntry);
                            }
                            if (properties.get("customdescription") != null && properties.get("customdescription").length() > 0) {
                                Utils.setMetaDataExtensionValue(rootLevelProcess,
                                                                "customDescription",
                                                                wrapInCDATABlock(properties.get("customdescription")));
                            }
                            rootLevelProcess.setId(properties.get("id"));
                            applyProcessProperties(rootLevelProcess,
                                                   properties);
                            ((Definitions) baseElt).getRootElements().add(rootLevelProcess);
                        }
                    }
                    if (child instanceof Task) {
                        rootLevelProcess.getFlowElements().add((Task) child);
                    } else if (child instanceof CallActivity) {
                        rootLevelProcess.getFlowElements().add((CallActivity) child);
                    } else if (child instanceof RootElement) {
                        ((Definitions) baseElt).getRootElements().add((RootElement) child);
                    } else if (child instanceof SequenceFlow) {
                        rootLevelProcess.getFlowElements().add((SequenceFlow) child);
                    } else if (child instanceof Gateway) {
                        rootLevelProcess.getFlowElements().add((Gateway) child);
                    } else if (child instanceof Event) {
                        rootLevelProcess.getFlowElements().add((Event) child);
                    } else if (child instanceof TextAnnotation) {
                        rootLevelProcess.getFlowElements().add((TextAnnotation) child);
                    } else if (child instanceof Artifact) {
                        rootLevelProcess.getArtifacts().add((Artifact) child);
                    } else if (child instanceof DataObject) {
                        // bubble up data objects
                        //rootLevelProcess.getFlowElements().add(0, (DataObject) child);
                        rootLevelProcess.getFlowElements().add((DataObject) child);
                        //                        ItemDefinition def = ((DataObject) child).getItemSubjectRef();
                        //                        if (def != null) {
                        //                            if (def.eResource() == null) {
                        //                                ((Definitions) rootLevelProcess.eContainer()).getRootElements().add(0, def);
                        //                            }
                        //                            Import imported = def.getImport();
                        //                            if (imported != null && imported.eResource() == null) {
                        //                                ((Definitions) rootLevelProcess.eContainer()).getImports().add(0, imported);
                        //                            }
                        //                        }
                    } else if (child instanceof SubProcess) {
                        rootLevelProcess.getFlowElements().add((SubProcess) child);
                    } else if (child instanceof Lane) {
                        // lanes handled later
                    } else {
                        _logger.error("Don't know what to do of " + child);
                    }
                    // }
                }
            }
        } else if (baseElt instanceof Process) {
            for (BaseElement child : childElements) {
                if (child instanceof Lane) {
                    if (((Process) baseElt).getLaneSets().isEmpty()) {
                        ((Process) baseElt).getLaneSets().add(Bpmn2Factory.eINSTANCE.createLaneSet());
                    }
                    ((Process) baseElt).getLaneSets().get(0).getLanes().add((Lane) child);
                    addLaneFlowNodes((Process) baseElt,
                                     (Lane) child);
                } else if (child instanceof Artifact) {
                    ((Process) baseElt).getArtifacts().add((Artifact) child);
                } else {
                    _logger.error("Don't know what to do of " + child);
                }
            }
        } else if (baseElt instanceof SubProcess) {
            for (BaseElement child : childElements) {
                if (child instanceof FlowElement) {
                    ((SubProcess) baseElt).getFlowElements().add((FlowElement) child);
                } else if (child instanceof Artifact) {
                    ((SubProcess) baseElt).getArtifacts().add((Artifact) child);
                } else {
                    _logger.error("Subprocess - don't know what to do of " + child);
                }
            }
        } else if (baseElt instanceof Message) {
            // we do not support base-element messages from the json. They are created dynamically for events that use them.
        } else if (baseElt instanceof Lane) {
            for (BaseElement child : childElements) {
                if (child instanceof FlowNode) {
                    ((Lane) baseElt).getFlowNodeRefs().add((FlowNode) child);
                }
                // no support for child-lanes at this point
//                  else if (child instanceof Lane) {
//                       if (((Lane) baseElt).getChildLaneSet() == null) {
//                            ((Lane) baseElt).setChildLaneSet(Bpmn2Factory.eINSTANCE.createLaneSet());
//                       }
//                       ((Lane) baseElt).getChildLaneSet().getLanes().add((Lane) child);
//                  }
                else if (child instanceof Artifact) {
                    _artifacts.add((Artifact) child);
                } else {
                    _logger.error("Don't know what to do of " + childElements);
                }
            }
            _lanes.add((Lane) baseElt);
        } else {
            if (!childElements.isEmpty()) {
                _logger.error("Don't know what to do of " + childElements + " with " + baseElt);
            }
        }
        return baseElt;
    }

    private void addLaneFlowNodes(Process process,
                                  Lane lane) {
        process.getFlowElements().addAll(lane.getFlowNodeRefs());
//        for (FlowNode node : lane.getFlowNodeRefs()) {
//            if (node instanceof DataObject) {
//                ItemDefinition def = ((DataObject) node).getItemSubjectRef();
//                if (def != null) {
//                    if (def.eResource() == null) {
//                        ((Definitions) process.eContainer()).getRootElements().add(0, ((DataObject) node).getItemSubjectRef());
//                    }
//                    Import imported = def.getImport();
//                    if (imported != null && imported.eResource() == null) {
//                        ((Definitions) process.eContainer()).getImports().add(0, ((DataObject) node).getItemSubjectRef().getImport());
//                    }
//                }
//            }
//        }
        if (lane.getChildLaneSet() != null) {
            for (Lane l : lane.getChildLaneSet().getLanes()) {
                addLaneFlowNodes(process,
                                 l);
            }
        }
    }

    protected void applyProperties(BaseElement baseElement,
                                   Map<String, String> properties,
                                   String preProcessingData) {
        applyBaseElementProperties(baseElement,
                                   properties);
        if (baseElement instanceof SubProcess) {
            applySubProcessProperties((SubProcess) baseElement,
                                      properties);
        }
        if (baseElement instanceof AdHocSubProcess) {
            applyAdHocSubProcessProperties((AdHocSubProcess) baseElement,
                                           properties);
        }
        if (baseElement instanceof CallActivity) {
            applyCallActivityProperties((CallActivity) baseElement,
                                        properties);
        }
        if (baseElement instanceof GlobalTask) {
            applyGlobalTaskProperties((GlobalTask) baseElement,
                                      properties);
        }
        if (baseElement instanceof Definitions) {
            applyDefinitionProperties((Definitions) baseElement,
                                      properties);
        }
        if (baseElement instanceof Process) {
            applyProcessProperties((Process) baseElement,
                                   properties);
        }
        if (baseElement instanceof Lane) {
            applyLaneProperties((Lane) baseElement,
                                properties);
        }
        if (baseElement instanceof SequenceFlow) {
            applySequenceFlowProperties((SequenceFlow) baseElement,
                                        properties);
        }
        if (baseElement instanceof Task) {
            applyTaskProperties((Task) baseElement,
                                properties,
                                preProcessingData);
        }
        if (baseElement instanceof UserTask) {
            applyUserTaskProperties((UserTask) baseElement,
                                    properties);
        }
        if (baseElement instanceof BusinessRuleTask) {
            applyBusinessRuleTaskProperties((BusinessRuleTask) baseElement,
                                            properties);
        }
        if (baseElement instanceof ScriptTask) {
            applyScriptTaskProperties((ScriptTask) baseElement,
                                      properties);
        }
        if (baseElement instanceof ServiceTask) {
            applyServiceTaskProperties((ServiceTask) baseElement,
                                       properties);
        }
        if (baseElement instanceof ReceiveTask) {
            applyReceiveTaskProperties((ReceiveTask) baseElement,
                                       properties);
        }
        if (baseElement instanceof SendTask) {
            applySendTaskProperties((SendTask) baseElement,
                                    properties);
        }
        if (baseElement instanceof Gateway) {
            applyGatewayProperties((Gateway) baseElement,
                                   properties);
        }
        if (baseElement instanceof Event) {
            applyEventProperties((Event) baseElement,
                                 properties);
        }
        if (baseElement instanceof CatchEvent) {
            applyCatchEventProperties((CatchEvent) baseElement,
                                      properties);
        }
        if (baseElement instanceof ThrowEvent) {
            applyThrowEventProperties((ThrowEvent) baseElement,
                                      properties);
        }
        if (baseElement instanceof TextAnnotation) {
            applyTextAnnotationProperties((TextAnnotation) baseElement,
                                          properties);
        }
        if (baseElement instanceof Group) {
            applyGroupProperties((Group) baseElement,
                                 properties);
        }
        if (baseElement instanceof DataObject) {
            applyDataObjectProperties((DataObject) baseElement,
                                      properties);
        }
        if (baseElement instanceof DataStore) {
            applyDataStoreProperties((DataStore) baseElement,
                                     properties);
        }
        if (baseElement instanceof Message) {
            applyMessageProperties((Message) baseElement,
                                   properties);
        }
        if (baseElement instanceof StartEvent) {
            applyStartEventProperties((StartEvent) baseElement,
                                      properties);
        }
        if (baseElement instanceof EndEvent) {
            applyEndEventProperties((EndEvent) baseElement,
                                    properties);
        }
        if (baseElement instanceof Association) {
            applyAssociationProperties((Association) baseElement,
                                       properties);
        }
        // finally, apply properties from helpers:
        for (BpmnMarshallerHelper helper : _helpers) {
            helper.applyProperties(baseElement,
                                   properties);
        }
    }

    protected void applySubProcessProperties(SubProcess sp,
                                             Map<String, String> properties) {
        if (properties.get("name") != null) {
            sp.setName(StringEscapeUtils.escapeXml(properties.get("name")).replaceAll("\\r\\n|\\r|\\n",
                                                                                      " "));
            // add unescaped and untouched name value as extension element as well
            Utils.setMetaDataExtensionValue(sp,
                                            "elementname",
                                            wrapInCDATABlock(properties.get("name").replaceAll("\\\\n",
                                                                                               "\n")));
        } else {
            sp.setName("");
        }
        // process on-entry and on-exit actions as custom elements
        applyOnEntryActions(sp,
                            properties);
        applyOnExitActions(sp,
                           properties);

        // isAsync metadata
        if (properties.get("isasync") != null && properties.get("isasync").length() > 0 && properties.get("isasync").equals("true")) {
            Utils.setMetaDataExtensionValue(sp,
                                            "customAsync",
                                            wrapInCDATABlock(properties.get("isasync")));
        }
        if (sp.getIoSpecification() == null) {
            InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
            sp.setIoSpecification(iospec);
        }
        parseAssignmentsInfo(properties);
        // data input set
        applyDataInputProperties(sp,
                                 properties,
                                 new HashMap<String, DataInput>());
        // data output set
        applyDataOutputProperties(sp,
                                  properties);
        // assignments
        if (properties.get("assignments") != null && properties.get("assignments").length() > 0 && sp.getIoSpecification() != null) {
            String[] allAssignments = properties.get("assignments").split(",\\s*");
            for (String assignment : allAssignments) {
                if (assignment.contains("=")) {
                    String[] assignmentParts = assignment.split("=\\s*");
                    String fromPart = assignmentParts[0];
                    if (fromPart.startsWith("[din]")) {
                        fromPart = fromPart.substring(5,
                                                      fromPart.length());
                    }
                    DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                    if (sp.getIoSpecification() != null && sp.getIoSpecification().getDataOutputs() != null) {
                        List<DataInput> dataInputs = sp.getIoSpecification().getDataInputs();
                        for (DataInput di : dataInputs) {
                            if (di.getId().equals(sp.getId() + "_" + fromPart + "InputX")) {
                                dia.setTargetRef(di);
                                if (di.getName().equals("TaskName")) {
                                    break;
                                }
                            }
                        }
                    }
                    Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                    FormalExpression fromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    if (assignmentParts.length > 1) {
                        String replacer = decodeAssociationValue(assignmentParts[1]);
                        fromExpression.setBody(wrapInCDATABlock(replacer));
                    } else {
                        fromExpression.setBody("");
                    }
                    FormalExpression toExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    toExpression.setBody(dia.getTargetRef().getId());
                    a.setFrom(fromExpression);
                    a.setTo(toExpression);
                    dia.getAssignment().add(a);
                    sp.getDataInputAssociations().add(dia);
//                } else if(assignment.contains("<->")) {
//                    String[] assignmentParts = assignment.split( "<->\\s*" );
//                    DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
//                    DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
//
//                    ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
//                    ie.setId(assignmentParts[0]);
//                    dia.getSourceRef().add(ie);
//                    doa.setTargetRef(ie);
//
//                    List<DataInput> dataInputs = sp.getIoSpecification().getDataInputs();
//                    for(DataInput di : dataInputs) {
//                        if(di.getId().equals(sp.getId() + "_" + assignmentParts[1] + "InputX")) {
//                            dia.setTargetRef(di);
//                            break;
//                        }
//                    }
//                    List<DataOutput> dataOutputs = sp.getIoSpecification().getDataOutputs();
//                    for(DataOutput dout : dataOutputs) {
//                        if(dout.getId().equals(sp.getId() + "_" + assignmentParts[1] + "OutputX")) {
//                            doa.getSourceRef().add(dout);
//                            break;
//                        }
//                    }
//
//                    sp.getDataInputAssociations().add(dia);
//                    sp.getDataOutputAssociations().add(doa);
                } else if (assignment.contains("->")) {
                    String[] assignmentParts = assignment.split("->\\s*");
                    String fromPart = assignmentParts[0];
                    boolean isDataInput = false;
                    boolean isDataOutput = false;
                    if (fromPart.startsWith("[din]")) {
                        fromPart = fromPart.substring(5,
                                                      fromPart.length());
                        isDataInput = true;
                    }
                    if (fromPart.startsWith("[dout]")) {
                        fromPart = fromPart.substring(6,
                                                      fromPart.length());
                        isDataOutput = true;
                    }
                    List<DataOutput> dataOutputs = sp.getIoSpecification().getDataOutputs();
                    if (isDataOutput) {
                        DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
                        for (DataOutput dout : dataOutputs) {
                            if (dout.getId().equals(sp.getId() + "_" + fromPart + "OutputX")) {
                                doa.getSourceRef().add(dout);
                                break;
                            }
                        }
                        ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                        ie.setId(assignmentParts[1]);
                        doa.setTargetRef(ie);
                        sp.getDataOutputAssociations().add(doa);
                    } else if (isDataInput) {
                        DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                        // association from process var to dataInput var
                        ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                        ie.setId(fromPart);
                        dia.getSourceRef().add(ie);
                        List<DataInput> dataInputs = sp.getIoSpecification().getDataInputs();
                        for (DataInput di : dataInputs) {
                            if (di.getId().equals(sp.getId() + "_" + assignmentParts[1] + "InputX")) {
                                dia.setTargetRef(di);
                                break;
                            }
                        }
                        sp.getDataInputAssociations().add(dia);
                    }
                } else {
                    // TODO throw exception here?
                }
            }
        }
        // multi instance loop characteristics
        if (properties.get("mitrigger") != null && properties.get("mitrigger").equals("true")) {

            final MultiInstanceLoopCharacteristics loopCharacteristics = Bpmn2Factory.eINSTANCE.createMultiInstanceLoopCharacteristics();

            //ensure data inputs/outputs
            if (sp.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                sp.setIoSpecification(iospec);
            } else {
                sp.getIoSpecification().getDataInputs().clear();
                sp.getIoSpecification().getDataOutputs().clear();
                sp.getDataInputAssociations().clear();
                sp.getDataOutputAssociations().clear();
            }

            // loop characteristics input
            final String inputCollectionStr = properties.get("multipleinstancecollectioninput");
            if (inputCollectionStr != null && !inputCollectionStr.isEmpty()) {
                //set the input collection
                InputSet inset = sp.getIoSpecification().getInputSets().get(0);
                DataInput multiInput = Bpmn2Factory.eINSTANCE.createDataInput();
                multiInput.setId(sp.getId() + "_" + "input");
                multiInput.setName(inputCollectionStr);
                sp.getIoSpecification().getDataInputs().add(multiInput);
                inset.getDataInputRefs().add(multiInput);
                DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                ie.setId(inputCollectionStr);
                dia.getSourceRef().add(ie);
                dia.setTargetRef(multiInput);
                sp.getDataInputAssociations().add(dia);
                loopCharacteristics.setLoopDataInputRef(multiInput);
            }

            //set the input iteration variable
            final String miDataInputStr = properties.get("multipleinstancedatainput");
            if (miDataInputStr != null && !miDataInputStr.isEmpty()) {
                DataInput din = Bpmn2Factory.eINSTANCE.createDataInput();
                din.setId(miDataInputStr);
                ItemDefinition itemDef = Bpmn2Factory.eINSTANCE.createItemDefinition();
                itemDef.setId(sp.getId() + "_" + "multiInstanceInputItemType");
                din.setItemSubjectRef(itemDef);
                addSubprocessItemDefs(itemDef);
                loopCharacteristics.setInputDataItem(din);
            }

            // loop characteristics output
            final String outputCollectionStr = properties.get("multipleinstancecollectionoutput");
            if (outputCollectionStr != null && !outputCollectionStr.isEmpty()) {
                //set the output collection
                OutputSet outset = sp.getIoSpecification().getOutputSets().get(0);
                DataOutput multiOutput = Bpmn2Factory.eINSTANCE.createDataOutput();
                multiOutput.setId(sp.getId() + "_" + "output");
                multiOutput.setName(outputCollectionStr);
                sp.getIoSpecification().getDataOutputs().add(multiOutput);
                outset.getDataOutputRefs().add(multiOutput);
                DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
                ItemAwareElement ie2 = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                ie2.setId(outputCollectionStr);
                doa.getSourceRef().add(multiOutput);
                doa.setTargetRef(ie2);
                sp.getDataOutputAssociations().add(doa);
                loopCharacteristics.setLoopDataOutputRef(multiOutput);
            }

            //set the output iteration variable
            final String miDataOutputStr = properties.get("multipleinstancedataoutput");
            if (miDataOutputStr != null && !miDataOutputStr.isEmpty()) {
                DataOutput don = Bpmn2Factory.eINSTANCE.createDataOutput();
                don.setId(miDataOutputStr);
                ItemDefinition itemDef2 = Bpmn2Factory.eINSTANCE.createItemDefinition();
                itemDef2.setId(sp.getId() + "_" + "multiInstanceOutputItemType");
                don.setItemSubjectRef(itemDef2);
                addSubprocessItemDefs(itemDef2);
                loopCharacteristics.setOutputDataItem(don);
            }

            // loop characteristics completion condition
            final String completionConditionStr = properties.get("multipleinstancecompletioncondition");
            if (completionConditionStr != null && !completionConditionStr.isEmpty()) {
                FormalExpression expr = Bpmn2Factory.eINSTANCE.createFormalExpression();
                expr.setBody(wrapInCDATABlock(completionConditionStr));
                loopCharacteristics.setCompletionCondition(expr);
            }

            sp.setLoopCharacteristics(loopCharacteristics);
        }
        // properties
        if (properties.get("vardefs") != null && properties.get("vardefs").length() > 0) {
            String[] vardefs = properties.get("vardefs").split(",\\s*");
            for (String vardef : vardefs) {
                Property prop = Bpmn2Factory.eINSTANCE.createProperty();
                ItemDefinition itemdef = Bpmn2Factory.eINSTANCE.createItemDefinition();
                // check if we define a structure ref in the definition
                if (vardef.contains(":")) {
                    String[] vardefParts = vardef.split(":\\s*");
                    prop.setId(vardefParts[0]);
                    itemdef.setId("_" + prop.getId() + "Item");
                    boolean haveKPI = false;
                    String kpiValue = "";
                    if (vardefParts.length == 3) {
                        itemdef.setStructureRef(vardefParts[1]);
                        if (vardefParts[2].equals("true")) {
                            haveKPI = true;
                            kpiValue = vardefParts[2];
                        }
                    }
                    if (vardefParts.length == 2) {
                        if (vardefParts[1].equals("true") || vardefParts[1].equals("false")) {
                            if (vardefParts[1].equals("true")) {
                                haveKPI = true;
                                kpiValue = vardefParts[1];
                            }
                        } else {
                            itemdef.setStructureRef(vardefParts[1]);
                        }
                    }
                    if (haveKPI) {
                        Utils.setMetaDataExtensionValue(prop,
                                                        "customKPI",
                                                        wrapInCDATABlock(kpiValue));
                    }
                } else {
                    prop.setId(vardef);
                    itemdef.setId("_" + prop.getId() + "Item");
                }
                prop.setItemSubjectRef(itemdef);
                sp.getProperties().add(prop);
                addSubprocessItemDefs(itemdef);
            }
        }
        // event subprocess
        if (sp instanceof EventSubprocess) {
            sp.setTriggeredByEvent(true);
        }
        // simulation
        if (properties.get("distributiontype") != null && properties.get("distributiontype").length() > 0) {
            TimeParameters timeParams = BpsimFactory.eINSTANCE.createTimeParameters();
            Parameter processingTimeParam = BpsimFactory.eINSTANCE.createParameter();
            if (properties.get("distributiontype").equals("normal")) {
                NormalDistributionType normalDistributionType = BpsimFactory.eINSTANCE.createNormalDistributionType();
                normalDistributionType.setStandardDeviation(Double.valueOf(properties.get("standarddeviation")));
                normalDistributionType.setMean(Double.valueOf(properties.get("mean")));
                processingTimeParam.getParameterValue().add(normalDistributionType);
            } else if (properties.get("distributiontype").equals("uniform")) {
                UniformDistributionType uniformDistributionType = BpsimFactory.eINSTANCE.createUniformDistributionType();
                uniformDistributionType.setMax(Double.valueOf(properties.get("max")));
                uniformDistributionType.setMin(Double.valueOf(properties.get("min")));
                processingTimeParam.getParameterValue().add(uniformDistributionType);
                // random distribution not supported in bpsim 1.0
//             } else if(properties.get("distributiontype").equals("random")) {
//                  RandomDistributionType randomDistributionType = BpsimFactory.eINSTANCE.createRandomDistributionType();
//                  randomDistributionType.setMax(Double.valueOf(properties.get("max")));
//                  randomDistributionType.setMin(Double.valueOf(properties.get("min")));
//                  processingTimeParam.getParameterValue().add(randomDistributionType);
            } else if (properties.get("distributiontype").equals("poisson")) {
                PoissonDistributionType poissonDistributionType = BpsimFactory.eINSTANCE.createPoissonDistributionType();
                poissonDistributionType.setMean(Double.valueOf(properties.get("mean")));
                processingTimeParam.getParameterValue().add(poissonDistributionType);
            }
            // individual time unit not supported in bpsim 1.0
//             if(properties.get("timeunit") != null) {
//                  timeParams.setTimeUnit(TimeUnit.getByName(properties.get("timeunit")));
//             }
            if (properties.get("waittime") != null) {
                Parameter waittimeParam = BpsimFactory.eINSTANCE.createParameter();
                FloatingParameterType waittimeParamValue = BpsimFactory.eINSTANCE.createFloatingParameterType();
                DecimalFormat twoDForm = new DecimalFormat("#.##");
                waittimeParamValue.setValue(Double.valueOf(twoDForm.format(Double.valueOf(properties.get("waittime")))));
                waittimeParam.getParameterValue().add(waittimeParamValue);
                timeParams.setWaitTime(waittimeParam);
            }
            timeParams.setProcessingTime(processingTimeParam);
            if (_simulationElementParameters.containsKey(sp.getId())) {
                _simulationElementParameters.get(sp.getId()).add(timeParams);
            } else {
                List<EObject> values = new ArrayList<EObject>();
                values.add(timeParams);
                _simulationElementParameters.put(sp.getId(),
                                                 values);
            }
        }
        CostParameters costParameters = BpsimFactory.eINSTANCE.createCostParameters();
        if (properties.get("unitcost") != null && properties.get("unitcost").length() > 0) {
            Parameter unitcostParam = BpsimFactory.eINSTANCE.createParameter();
            FloatingParameterType unitCostParameterValue = BpsimFactory.eINSTANCE.createFloatingParameterType();
            unitCostParameterValue.setValue(new Double(properties.get("unitcost")));
            unitcostParam.getParameterValue().add(unitCostParameterValue);
            costParameters.setUnitCost(unitcostParam);
        }
        // no individual currency unit supported in bpsim 1.0
//        if(properties.get("currency") != null && properties.get("currency").length() > 0) {
//            costParameters.setCurrencyUnit(properties.get("currency"));
//        }
        if (_simulationElementParameters.containsKey(sp.getId())) {
            _simulationElementParameters.get(sp.getId()).add(costParameters);
        } else {
            List<EObject> values = new ArrayList<EObject>();
            values.add(costParameters);
            _simulationElementParameters.put(sp.getId(),
                                             values);
        }
    }

    private String getScriptLanguageFormat(Map<String, String> properties) {
        // default to java
        String scriptLanguage = "http://www.java.com/java";
        if (properties.get("script_language") != null && properties.get("script_language").length() > 0) {
            if (properties.get("script_language").equals("java")) {
                scriptLanguage = "http://www.java.com/java";
            } else if (properties.get("script_language").equals("mvel")) {
                scriptLanguage = "http://www.mvel.org/2.0";
            } else if (properties.get("script_language").equals("javascript")) {
                scriptLanguage = "http://www.javascript.com/javascript";
            }
        }
        return scriptLanguage;
    }

    private void applyDataOutputProperties(Activity activity,
                                           Map<String, String> properties) {
        if (properties.get("dataoutputset") != null && properties.get("dataoutputset").trim().length() > 0) {
            String[] allDataOutputs = properties.get("dataoutputset").split(",\\s*");
            if (activity.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                activity.setIoSpecification(iospec);
            }
            OutputSet outset = Bpmn2Factory.eINSTANCE.createOutputSet();
            for (String dataOutput : allDataOutputs) {
                if (dataOutput.trim().length() > 0) {
                    DataOutput nextOut = Bpmn2Factory.eINSTANCE.createDataOutput();
                    String[] dataOutputParts = dataOutput.split(":\\s*");
                    if (dataOutputParts.length == 2) {
                        nextOut.setId(activity.getId() + "_" + dataOutputParts[0] + (dataOutputParts[0].endsWith("OutputX") ? "" : "OutputX"));
                        nextOut.setName(dataOutputParts[0]);
                        ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                        EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools",
                                "dtype",
                                false,
                                false);
                        SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                         dataOutputParts[1]);
                        nextOut.getAnyAttribute().add(extensionEntry);
                    } else {
                        nextOut.setId(activity.getId() + "_" + dataOutput + (dataOutput.endsWith("OutputX") ? "" : "OutputX"));
                        nextOut.setName(dataOutput);
                        ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                        EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools",
                                "dtype",
                                false,
                                false);
                        SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                         "Object");
                        nextOut.getAnyAttribute().add(extensionEntry);
                    }
                    activity.getIoSpecification().getDataOutputs().add(nextOut);
                    outset.getDataOutputRefs().add(nextOut);
                }
            }
            activity.getIoSpecification().getOutputSets().add(outset);
        } else {
            if (activity.getIoSpecification() != null) {
                activity.getIoSpecification().getOutputSets().add(Bpmn2Factory.eINSTANCE.createOutputSet());
            }
        }
    }

    private void applyDataInputProperties(Activity activity,
                                          Map<String, String> properties,
                                          Map<String, DataInput> alreadyProcessedInputs) {
        if (properties.get("datainputset") != null && properties.get("datainputset").trim().length() > 0) {
            String[] allDataInputs = properties.get("datainputset").split(",\\s*");
            if (activity.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                activity.setIoSpecification(iospec);
            }
            InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
            for (String dataInput : allDataInputs) {
                if (dataInput.trim().length() > 0) {
                    DataInput nextInput = Bpmn2Factory.eINSTANCE.createDataInput();
                    String[] dataInputParts = dataInput.split(":\\s*");
                    if (dataInputParts.length == 2) {
                        for (String inputName : alreadyProcessedInputs.keySet()) {
                            if (inputName.equals(dataInputParts[0]) && alreadyProcessedInputs.get(inputName) != null) {
                                break;
                            }
                        }
                        nextInput.setId(activity.getId() + "_" + dataInputParts[0] + (dataInputParts[0].endsWith("InputX") ? "" : "InputX"));
                        nextInput.setName(dataInputParts[0]);
                        ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                        EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools",
                                "dtype",
                                false,
                                false);
                        SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                         dataInputParts[1]);
                        nextInput.getAnyAttribute().add(extensionEntry);
                    } else {
                        for (String inputName : alreadyProcessedInputs.keySet()) {
                            if (inputName.equals(dataInput) && alreadyProcessedInputs.get(inputName) != null) {
                                break;
                            }
                        }
                        nextInput.setId(activity.getId() + "_" + dataInput + (dataInput.endsWith("InputX") ? "" : "InputX"));
                        nextInput.setName(dataInput);
                        ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                        EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools",
                                "dtype",
                                false,
                                false);
                        SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                         "Object");
                        nextInput.getAnyAttribute().add(extensionEntry);
                    }
                    activity.getIoSpecification().getDataInputs().add(nextInput);
                    inset.getDataInputRefs().add(nextInput);
                }
            }
            activity.getIoSpecification().getInputSets().add(inset);
        } else {
            if (activity.getIoSpecification() != null) {
                activity.getIoSpecification().getInputSets().add(Bpmn2Factory.eINSTANCE.createInputSet());
            }
        }
    }

    protected void applyAdHocSubProcessProperties(AdHocSubProcess ahsp,
                                                  Map<String, String> properties) {
        if (properties.get("adhocordering") != null) {
            if (properties.get("adhocordering").equals("Parallel")) {
                ahsp.setOrdering(AdHocOrdering.PARALLEL);
            } else {
                ahsp.setOrdering(AdHocOrdering.SEQUENTIAL);
            }
        }
        String adHocCompletionCondition = properties.get("adhoccompletioncondition");
        if (adHocCompletionCondition != null) {
            ScriptTypeValue value = new ScriptTypeTypeSerializer().parse(adHocCompletionCondition);
            FormalExpression completionConditionExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
            String completionExpression = value.getScript();
            if (completionExpression == null || completionExpression.isEmpty()) {
                //default to autocomplete
                completionExpression = "autocomplete";
            }
            completionConditionExpression.setBody(wrapInCDATABlock(completionExpression));
            String languageFormat = Utils.getScriptLanguageFormat(value.getLanguage());
            if (languageFormat == null) {
                //default to mvel
                languageFormat = "mvel";
            }
            completionConditionExpression.setLanguage(languageFormat);
            ahsp.setCompletionCondition(completionConditionExpression);
        }
    }

    protected void applyOnEntryActions(BaseElement element,
                                       Map<String, String> properties) {
        if (properties.get("onentryactions") != null && properties.get("onentryactions").length() > 0) {
            ScriptTypeListValue onEntryActions = new ScriptTypeListTypeSerializer().parse(properties.get("onentryactions"));
            if (!onEntryActions.isEmpty()) {
                ScriptTypeValue onEntryAction = onEntryActions.getValues().get(0);
                if (onEntryAction.getScript() != null && !onEntryAction.getScript().isEmpty()) {
                    OnEntryScriptType onEntryScript = DroolsFactory.eINSTANCE.createOnEntryScriptType();
                    onEntryScript.setScript(wrapInCDATABlock(onEntryAction.getScript()));
                    String scriptLanguage = Utils.getScriptLanguageFormat(onEntryAction.getLanguage());
                    if (scriptLanguage == null) {
                        //default to java
                        scriptLanguage = "http://www.java.com/java";
                    }
                    onEntryScript.setScriptFormat(scriptLanguage);
                    if (element.getExtensionValues() == null || element.getExtensionValues().size() < 1) {
                        ExtensionAttributeValue extensionElement = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
                        element.getExtensionValues().add(extensionElement);
                    }
                    FeatureMap.Entry extensionElementEntry = new SimpleFeatureMapEntry(
                            (Internal) DroolsPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT,
                            onEntryScript);
                    element.getExtensionValues().get(0).getValue().add(extensionElementEntry);
                }
            }
        }
    }

    protected void applyOnExitActions(BaseElement element,
                                      Map<String, String> properties) {
        if (properties.get("onexitactions") != null && properties.get("onexitactions").length() > 0) {
            ScriptTypeListValue onExitActions = new ScriptTypeListTypeSerializer().parse(properties.get("onexitactions"));
            if (!onExitActions.isEmpty()) {
                ScriptTypeValue onExitAction = onExitActions.getValues().get(0);
                if (onExitAction.getScript() != null && !onExitAction.getScript().isEmpty()) {
                    OnExitScriptType onExitScript = DroolsFactory.eINSTANCE.createOnExitScriptType();
                    onExitScript.setScript(wrapInCDATABlock(onExitAction.getScript()));
                    String scriptLanguage = Utils.getScriptLanguageFormat(onExitAction.getLanguage());
                    if (scriptLanguage == null) {
                        //default to java
                        scriptLanguage = "http://www.java.com/java";
                    }
                    onExitScript.setScriptFormat(scriptLanguage);
                    if (element.getExtensionValues() == null || element.getExtensionValues().size() < 1) {
                        ExtensionAttributeValue extensionElement = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
                        element.getExtensionValues().add(extensionElement);
                    }
                    FeatureMap.Entry extensionElementEntry = new SimpleFeatureMapEntry(
                            (Internal) DroolsPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT,
                            onExitScript);
                    element.getExtensionValues().get(0).getValue().add(extensionElementEntry);
                }
            }
        }
    }

    protected void applyConditionalEventProperties(ConditionalEventDefinition event,
                                                   Map<String, String> properties) {
        FormalExpression conditionExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
        ScriptTypeValue value = new ScriptTypeTypeSerializer().parse(properties.get("conditionexpression"));
        if (value.getLanguage() != null && !value.getLanguage().isEmpty()) {
            String languageFormat = Utils.getScriptLanguageFormat(value.getLanguage());
            if (languageFormat == null) {
                //default to drools
                languageFormat = "http://www.jboss.org/drools/rule";
            }
            conditionExpression.setLanguage(languageFormat);
        }

        if (value.getScript() != null && !value.getScript().isEmpty()) {
            String scriptStr = value.getScript().replaceAll("\\\\n",
                                                            "\n");
            conditionExpression.setBody(wrapInCDATABlock(scriptStr));
        }
        event.setCondition(conditionExpression);
    }

    protected void applySequenceFlowCondition(SequenceFlow sequenceFlow,
                                              Map<String, String> properties) {
        String conditionExpression = properties.get("conditionexpression");
        if (conditionExpression != null && !conditionExpression.isEmpty()) {
            ScriptTypeValue value = new ScriptTypeTypeSerializer().parse(conditionExpression);
            if (value.getScript() != null && !value.getScript().isEmpty()) {
                FormalExpression expr = Bpmn2Factory.eINSTANCE.createFormalExpression();
                expr.setBody(wrapInCDATABlock(value.getScript()));
                if (value.getLanguage() != null && !value.getLanguage().isEmpty()) {
                    String languageFormat = Utils.getScriptLanguageFormat(value.getLanguage());
                    if (languageFormat == null) {
                        //default to mvel
                        languageFormat = "http://www.mvel.org/2.0";
                    }
                    expr.setLanguage(languageFormat);
                }
                sequenceFlow.setConditionExpression(expr);
            }
        }
    }

    protected void applyEndEventProperties(EndEvent ee,
                                           Map<String, String> properties) {
        ee.setId(properties.get("resourceId"));
//        List<EventDefinition> definitions = ee.getEventDefinitions();
//            if (definitions != null && !definitions.isEmpty()){
//                EventDefinition ed = definitions.get(0);
//                if(ed instanceof EscalationEventDefinition) {
//                if(properties.get("escalationcode") != null && !"".equals(properties.get("escalationcode"))) {
//                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
//                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
//                                "http://www.jboss.org/drools", "esccode", false, false);
//                    EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
//                        properties.get("escalationcode"));
//                    ((EscalationEventDefinition) ee.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
//                }
//            }
//        }
    }

    protected void applyAssociationProperties(Association association,
                                              Map<String, String> properties) {
        if (properties.get("type") != null) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "type",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("type"));
            association.getAnyAttribute().add(extensionEntry);
        }
        if (properties.get("bordercolor") != null && properties.get("bordercolor").length() > 0) {
            if (!(_elementColors.containsKey(association.getId()))) {
                List<String> colorsList = new ArrayList<String>();
                colorsList.add("bordercolor:" + properties.get("bordercolor"));
                _elementColors.put(association.getId(),
                                   colorsList);
            } else {
                _elementColors.get(association.getId()).add("bordercolor:" + properties.get("bordercolor"));
            }
        }
    }

    protected void applyStartEventProperties(StartEvent se,
                                             Map<String, String> properties) {
        se.setIsInterrupting(Boolean.parseBoolean(properties.get("isinterrupting")));
    }

    protected void applyMessageProperties(Message msg,
                                          Map<String, String> properties) {
        if (properties.get("name") != null && properties.get("name").length() > 0) {
            msg.setName(StringEscapeUtils.escapeXml(properties.get("name")).replaceAll("\\r\\n|\\r|\\n",
                                                                                       " "));
            msg.setId(properties.get("name") + "Message");
            // add unescaped and untouched name value as extension element as well
            Utils.setMetaDataExtensionValue(msg,
                                            "elementname",
                                            wrapInCDATABlock(properties.get("name").replaceAll("\\\\n",
                                                                                               "\n")));
        } else {
            msg.setName("");
            msg.setId("Message");
        }
    }

    protected void applyDataStoreProperties(DataStore da,
                                            Map<String, String> properties) {
        if (properties.get("name") != null) {
            da.setName(StringEscapeUtils.escapeXml(properties.get("name")).replaceAll("\\r\\n|\\r|\\n",
                                                                                      " "));
            // add unescaped and untouched name value as extension element as well
            Utils.setMetaDataExtensionValue(da,
                                            "elementname",
                                            wrapInCDATABlock(properties.get("name").replaceAll("\\\\n",
                                                                                               "\n")));
        } else {
            da.setName("");
        }
    }

    protected void applyDataObjectProperties(DataObject da,
                                             Map<String, String> properties) {
        if (properties.get("name") != null && properties.get("name").length() > 0) {
            da.setName(StringEscapeUtils.escapeXml(properties.get("name")).replaceAll("\\r\\n|\\r|\\n",
                                                                                      " "));
            // add unescaped and untouched name value as extension element as well
            Utils.setMetaDataExtensionValue(da,
                                            "elementname",
                                            wrapInCDATABlock(properties.get("name").replaceAll("\\\\n",
                                                                                               "\n")));
        } else {
            // we need a name, use id instead
            da.setName(da.getId());
        }
        boolean haveCustomType = false;
        if (properties.get("customtype") != null && properties.get("customtype").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "datype",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("customtype"));
            da.getAnyAttribute().add(extensionEntry);
            haveCustomType = true;
        }
        if (properties.get("standardtype") != null && properties.get("standardtype").length() > 0 && !haveCustomType) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "datype",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("standardtype"));
            da.getAnyAttribute().add(extensionEntry);
        }
    }

    protected void applyTextAnnotationProperties(TextAnnotation ta,
                                                 Map<String, String> properties) {
        if (properties.get("name") != null) {
            ta.setText(StringEscapeUtils.escapeXml(properties.get("name")).replaceAll("\\r\\n|\\r|\\n",
                                                                                      " "));
            // add unescaped and untouched name value as extension element as well
            Utils.setMetaDataExtensionValue(ta,
                                            "elementname",
                                            wrapInCDATABlock(properties.get("name").replaceAll("\\\\n",
                                                                                               "\n")));
        } else {
            ta.setText("");
        }
        // default
        ta.setTextFormat("text/plain");
        if (properties.get("bordercolor") != null && properties.get("bordercolor").length() > 0) {
            if (!(_elementColors.containsKey(ta.getId()))) {
                List<String> colorsList = new ArrayList<String>();
                colorsList.add("bordercolor:" + properties.get("bordercolor"));
                _elementColors.put(ta.getId(),
                                   colorsList);
            } else {
                _elementColors.get(ta.getId()).add("bordercolor:" + properties.get("bordercolor"));
            }
        }
        if (properties.get("fontsize") != null && properties.get("fontsize").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "fontsize",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("fontsize"));
            ta.getAnyAttribute().add(extensionEntry);
        }
        if (properties.get("fontcolor") != null && properties.get("fontcolor").length() > 0) {
            if (!(_elementColors.containsKey(ta.getId()))) {
                List<String> colorsList = new ArrayList<String>();
                colorsList.add("fontcolor:" + properties.get("fontcolor"));
                _elementColors.put(ta.getId(),
                                   colorsList);
            } else {
                _elementColors.get(ta.getId()).add("fontcolor:" + properties.get("fontcolor"));
            }
        }
    }

    protected void applyGroupProperties(Group group,
                                        Map<String, String> properties) {
        if (properties.get("name") != null) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "categoryval",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("name"));
            group.getAnyAttribute().add(extensionEntry);
        }
    }

    protected void applyEventProperties(Event event,
                                        Map<String, String> properties) {
        if (properties.get("name") != null) {
            event.setName(StringEscapeUtils.escapeXml(properties.get("name")).replaceAll("\\r\\n|\\r|\\n",
                                                                                         " "));
            // add unescaped and untouched name value as extension element as well
            Utils.setMetaDataExtensionValue(event,
                                            "elementname",
                                            wrapInCDATABlock(properties.get("name").replaceAll("\\\\n",
                                                                                               "\n")));
        } else {
            event.setName("");
        }
        if (properties.get("auditing") != null && !"".equals(properties.get("auditing"))) {
            Auditing audit = Bpmn2Factory.eINSTANCE.createAuditing();
            audit.getDocumentation().add(createDocumentation(properties.get("auditing")));
            event.setAuditing(audit);
        }
        if (properties.get("monitoring") != null && !"".equals(properties.get("monitoring"))) {
            Monitoring monitoring = Bpmn2Factory.eINSTANCE.createMonitoring();
            monitoring.getDocumentation().add(createDocumentation(properties.get("monitoring")));
            event.setMonitoring(monitoring);
        }
    }

    protected void applyCatchEventProperties(CatchEvent event,
                                             Map<String, String> properties) {
        parseAssignmentsInfo(properties);
        if (properties.get("dataoutput") != null && !"".equals(properties.get("dataoutput"))) {
            String[] allDataOutputs = properties.get("dataoutput").split(",\\s*");
            OutputSet outSet = Bpmn2Factory.eINSTANCE.createOutputSet();
            for (String dataOutput : allDataOutputs) {
                if (dataOutput.trim().length() > 0) {
                    DataOutput nextOutput = Bpmn2Factory.eINSTANCE.createDataOutput();
                    String[] doutputParts = dataOutput.split(":\\s*");
                    if (doutputParts.length == 2) {
                        nextOutput.setId(event.getId() + "_" + doutputParts[0]);
                        nextOutput.setName(doutputParts[0]);
                        ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                        EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools",
                                "dtype",
                                false,
                                false);
                        SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                         doutputParts[1]);
                        nextOutput.getAnyAttribute().add(extensionEntry);
                    } else {
                        nextOutput.setId(event.getId() + "_" + dataOutput);
                        nextOutput.setName(dataOutput);
                        ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                        EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools",
                                "dtype",
                                false,
                                false);
                        SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                         "Object");
                        nextOutput.getAnyAttribute().add(extensionEntry);
                    }
                    event.getDataOutputs().add(nextOutput);
                    outSet.getDataOutputRefs().add(nextOutput);
                }
            }
            event.setOutputSet(outSet);
        }
        if (properties.get("boundarycancelactivity") != null) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "boundaryca",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("boundarycancelactivity"));
            event.getAnyAttribute().add(extensionEntry);
        }
        // data output associations
        if (properties.get("dataoutputassociations") != null && !"".equals(properties.get("dataoutputassociations"))) {
            String[] allAssociations = properties.get("dataoutputassociations").split(",\\s*");
            for (String association : allAssociations) {
                // data outputs are uni-directional
                String[] associationParts = association.split("->\\s*");
                String fromPart = associationParts[0];
                if (fromPart.startsWith("[dout]")) {
                    fromPart = fromPart.substring(6,
                                                  fromPart.length());
                }
                DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
                // for source refs we loop through already defined data outputs
                List<DataOutput> dataOutputs = event.getDataOutputs();
                if (dataOutputs != null) {
                    for (DataOutput ddo : dataOutputs) {
                        if (ddo.getId().equals(event.getId() + "_" + fromPart)) {
                            doa.getSourceRef().add(ddo);
                        }
                    }
                }
                // since we dont have the process vars defined yet..need to improvise
                ItemAwareElement e = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                e.setId(associationParts[1]);
                doa.setTargetRef(e);
                event.getDataOutputAssociation().add(doa);
            }
        }
        try {
            if (event.getEventDefinitions() != null && event.getEventDefinitions().size() > 0) {
                EventDefinition ed = event.getEventDefinitions().get(0);
                if (ed instanceof TimerEventDefinition) {
                    applyTimerEventProperties((TimerEventDefinition) ed,
                                              properties);
                } else if (ed instanceof SignalEventDefinition) {
                    if (properties.get("signalref") != null && !"".equals(properties.get("signalref"))) {
                        ((SignalEventDefinition) ed).setSignalRef(properties.get("signalref"));
//                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
//                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
//                                "http://www.jboss.org/drools", "signalrefname", false, false);
//                    EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
//                        properties.get("signalref"));
//                    ((SignalEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                    }
                } else if (ed instanceof ErrorEventDefinition) {
                    if (properties.get("errorref") != null && !"".equals(properties.get("errorref"))) {
                        ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                        EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools",
                                "erefname",
                                false,
                                false);
                        SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                         properties.get("errorref"));
                        ((ErrorEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                    }
                } else if (ed instanceof ConditionalEventDefinition) {
                    applyConditionalEventProperties((ConditionalEventDefinition) ed,
                                                    properties);
                } else if (ed instanceof EscalationEventDefinition) {
                    if (properties.get("escalationcode") != null && !"".equals(properties.get("escalationcode"))) {
                        ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                        EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools",
                                "esccode",
                                false,
                                false);
                        SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                         properties.get("escalationcode"));
                        ((EscalationEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                    }
                } else if (ed instanceof MessageEventDefinition) {
                    if (properties.get("messageref") != null && !"".equals(properties.get("messageref"))) {
                        ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                        EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools",
                                "msgref",
                                false,
                                false);
                        SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                         properties.get("messageref"));
                        ((MessageEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                    }
                } else if (ed instanceof CompensateEventDefinition) {
                    if (properties.get("activityref") != null && !"".equals(properties.get("activityref"))) {
                        ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                        EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools",
                                "actrefname",
                                false,
                                false);
                        SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                         properties.get("activityref"));
                        ((CompensateEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                    }
                }
            }
        } catch (Exception e) {
            _logger.warn(e.getMessage());
        }
        // simulation
        if (properties.get("distributiontype") != null && properties.get("distributiontype").length() > 0) {
            TimeParameters timeParams = BpsimFactory.eINSTANCE.createTimeParameters();
            Parameter processingTimeParam = BpsimFactory.eINSTANCE.createParameter();
            if (properties.get("distributiontype").equals("normal")) {
                NormalDistributionType normalDistributionType = BpsimFactory.eINSTANCE.createNormalDistributionType();
                normalDistributionType.setStandardDeviation(Double.valueOf(properties.get("standarddeviation")));
                normalDistributionType.setMean(Double.valueOf(properties.get("mean")));
                processingTimeParam.getParameterValue().add(normalDistributionType);
            } else if (properties.get("distributiontype").equals("uniform")) {
                UniformDistributionType uniformDistributionType = BpsimFactory.eINSTANCE.createUniformDistributionType();
                uniformDistributionType.setMax(Double.valueOf(properties.get("max")));
                uniformDistributionType.setMin(Double.valueOf(properties.get("min")));
                processingTimeParam.getParameterValue().add(uniformDistributionType);
                // random distribution type not supported in bpsim 1.0
//            } else if(properties.get("distributiontype").equals("random")) {
//                RandomDistributionType randomDistributionType = DroolsFactory.eINSTANCE.createRandomDistributionType();
//                randomDistributionType.setMax(Double.valueOf(properties.get("max")));
//                randomDistributionType.setMin(Double.valueOf(properties.get("min")));
//                processingTimeParam.getParameterValue().add(randomDistributionType);
            } else if (properties.get("distributiontype").equals("poisson")) {
                PoissonDistributionType poissonDistributionType = BpsimFactory.eINSTANCE.createPoissonDistributionType();
                poissonDistributionType.setMean(Double.valueOf(properties.get("mean")));
                processingTimeParam.getParameterValue().add(poissonDistributionType);
            }
            // no specific time unit available in 1.0 bpsim - use global
//            if(properties.get("timeunit") != null) {
//                timeParams.setTimeUnit(TimeUnit.getByName(properties.get("timeunit")));
//            }
            timeParams.setProcessingTime(processingTimeParam);
            if (_simulationElementParameters.containsKey(event.getId())) {
                _simulationElementParameters.get(event.getId()).add(timeParams);
            } else {
                List<EObject> values = new ArrayList<EObject>();
                values.add(timeParams);
                _simulationElementParameters.put(event.getId(),
                                                 values);
            }
        }
        if (properties.get("probability") != null && properties.get("probability").length() > 0) {
            ControlParameters controlParams = BpsimFactory.eINSTANCE.createControlParameters();
            Parameter probParam = BpsimFactory.eINSTANCE.createParameter();
            FloatingParameterType probParamValueParam = BpsimFactory.eINSTANCE.createFloatingParameterType();
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            probParamValueParam.setValue(Double.valueOf(twoDForm.format(Double.valueOf(properties.get("probability")))));
            probParam.getParameterValue().add(probParamValueParam);
            controlParams.setProbability(probParam);
            if (_simulationElementParameters.containsKey(event.getId())) {
                _simulationElementParameters.get(event.getId()).add(controlParams);
            } else {
                List<EObject> values = new ArrayList<EObject>();
                values.add(controlParams);
                _simulationElementParameters.put(event.getId(),
                                                 values);
            }
        }
    }

    protected void applyThrowEventProperties(ThrowEvent event,
                                             Map<String, String> properties) {
        parseAssignmentsInfo(properties);
        if (properties.get("datainput") != null && properties.get("datainput").trim().length() > 0) {
            String[] allDataInputs = properties.get("datainput").split(",\\s*");
            InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
            for (String dataInput : allDataInputs) {
                if (dataInput.trim().length() > 0) {
                    DataInput nextInput = Bpmn2Factory.eINSTANCE.createDataInput();
                    String[] dataInputParts = dataInput.split(":\\s*");
                    if (dataInputParts.length == 2) {
                        nextInput.setId(event.getId() + "_" + dataInputParts[0] + (dataInputParts[0].endsWith("InputX") ? "" : "InputX"));
                        nextInput.setName(dataInputParts[0]);
                        ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                        EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools",
                                "dtype",
                                false,
                                false);
                        SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                         dataInputParts[1]);
                        nextInput.getAnyAttribute().add(extensionEntry);
                    } else {
                        nextInput.setId(event.getId() + "_" + dataInput + (dataInput.endsWith("InputX") ? "" : "InputX"));
                        nextInput.setName(dataInput);
                        ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                        EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools",
                                "dtype",
                                false,
                                false);
                        SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                         "Object");
                        nextInput.getAnyAttribute().add(extensionEntry);
                    }
                    event.getDataInputs().add(nextInput);
                    inset.getDataInputRefs().add(nextInput);
                }
            }
            event.setInputSet(inset);
        }
        if (properties.get("datainputassociations") != null && properties.get("datainputassociations").length() > 0) {
            String[] allAssignments = properties.get("datainputassociations").split(",\\s*");
            for (String assignment : allAssignments) {
                if (assignment.contains("=")) {
                    String[] assignmentParts = assignment.split("=\\s*");
                    String fromPart = assignmentParts[0];
                    if (fromPart.startsWith("[din]")) {
                        fromPart = fromPart.substring(5,
                                                      fromPart.length());
                    }
                    DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                    if (event.getInputSet() != null) {
                        List<DataInput> dataInputs = event.getInputSet().getDataInputRefs();
                        for (DataInput di : dataInputs) {
                            if (di.getId().equals(event.getId() + "_" + fromPart + (fromPart.endsWith("InputX") ? "" : "InputX"))) {
                                dia.setTargetRef(di);
                            }
                        }
                    }
                    Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                    FormalExpression fromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    if (assignmentParts.length > 1) {
                        String replacer = decodeAssociationValue(assignmentParts[1]);
                        fromExpression.setBody(wrapInCDATABlock(replacer));
                    } else {
                        fromExpression.setBody("");
                    }
                    FormalExpression toExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    toExpression.setBody(dia.getTargetRef().getId());
                    a.setFrom(fromExpression);
                    a.setTo(toExpression);
                    dia.getAssignment().add(a);
                    event.getDataInputAssociation().add(dia);
                } else if (assignment.contains("->")) {
                    String[] assignmentParts = assignment.split("->\\s*");
                    String fromPart = assignmentParts[0];
                    boolean isDataInput = false;
                    boolean isDataOutput = false;
                    if (fromPart.startsWith("[din]")) {
                        fromPart = fromPart.substring(5,
                                                      fromPart.length());
                        isDataInput = true;
                    }
                    if (isDataInput) {
                        DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                        // association from process var to dataInput var
                        ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                        ie.setId(fromPart);
                        dia.getSourceRef().add(ie);
                        List<DataInput> dataInputs = event.getInputSet().getDataInputRefs();
                        for (DataInput di : dataInputs) {
                            if (di.getId().equals(event.getId() + "_" + assignmentParts[1] + (assignmentParts[1].endsWith("InputX") ? "" : "InputX"))) {
                                dia.setTargetRef(di);
                                break;
                            }
                        }
                        event.getDataInputAssociation().add(dia);
                    }
                } else {
                    // TODO throw exception here?
                }
            }
        }
        // signal scope metadata
        if (properties.get("signalscope") != null && properties.get("signalscope").length() > 0 && !properties.get("signalscope").equals("default")) {
            Utils.setMetaDataExtensionValue(event,
                                            "customScope",
                                            wrapInCDATABlock(properties.get("signalscope")));
        }
        try {
            EventDefinition ed = event.getEventDefinitions().get(0);
            if (ed instanceof TimerEventDefinition) {
                applyTimerEventProperties((TimerEventDefinition) ed,
                                          properties);
            } else if (ed instanceof SignalEventDefinition) {
                if (properties.get("signalref") != null && !"".equals(properties.get("signalref"))) {
                    ((SignalEventDefinition) ed).setSignalRef(properties.get("signalref"));
//                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
//                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
//                                "http://www.jboss.org/drools", "signalrefname", false, false);
//                    EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
//                        properties.get("signalref"));
//                    ((SignalEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                }
            } else if (ed instanceof ErrorEventDefinition) {
                if (properties.get("errorref") != null && !"".equals(properties.get("errorref"))) {
                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                            "http://www.jboss.org/drools",
                            "erefname",
                            false,
                            false);
                    SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                     properties.get("errorref"));
                    ((ErrorEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                }
            } else if (ed instanceof ConditionalEventDefinition) {
                applyConditionalEventProperties((ConditionalEventDefinition) ed,
                                                properties);
            } else if (ed instanceof EscalationEventDefinition) {
                if (properties.get("escalationcode") != null && !"".equals(properties.get("escalationcode"))) {
                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                            "http://www.jboss.org/drools",
                            "esccode",
                            false,
                            false);
                    SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                     properties.get("escalationcode"));
                    ((EscalationEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                }
            } else if (ed instanceof MessageEventDefinition) {
                if (properties.get("messageref") != null && !"".equals(properties.get("messageref"))) {
                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                            "http://www.jboss.org/drools",
                            "msgref",
                            false,
                            false);
                    SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                     properties.get("messageref"));
                    ((MessageEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                }
            } else if (ed instanceof CompensateEventDefinition) {
                if (properties.get("activityref") != null && !"".equals(properties.get("activityref"))) {
                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                            "http://www.jboss.org/drools",
                            "actrefname",
                            false,
                            false);
                    SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                     properties.get("activityref"));
                    ((CompensateEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            // TODO we dont want to barf here as test for example do not define event definitions in the bpmn2....
        }
        // simulation
        if (properties.get("distributiontype") != null && properties.get("distributiontype").length() > 0) {
            TimeParameters timeParams = BpsimFactory.eINSTANCE.createTimeParameters();
            Parameter processingTimeParam = BpsimFactory.eINSTANCE.createParameter();
            if (properties.get("distributiontype").equals("normal")) {
                NormalDistributionType normalDistributionType = BpsimFactory.eINSTANCE.createNormalDistributionType();
                normalDistributionType.setStandardDeviation(Double.valueOf(properties.get("standarddeviation")));
                normalDistributionType.setMean(Double.valueOf(properties.get("mean")));
                processingTimeParam.getParameterValue().add(normalDistributionType);
            } else if (properties.get("distributiontype").equals("uniform")) {
                UniformDistributionType uniformDistributionType = BpsimFactory.eINSTANCE.createUniformDistributionType();
                uniformDistributionType.setMax(Double.valueOf(properties.get("max")));
                uniformDistributionType.setMin(Double.valueOf(properties.get("min")));
                processingTimeParam.getParameterValue().add(uniformDistributionType);
                // random distribution type not supported in bpsim 1.0
//            } else if(properties.get("distributiontype").equals("random")) {
//                RandomDistributionType randomDistributionType = DroolsFactory.eINSTANCE.createRandomDistributionType();
//                randomDistributionType.setMax(Double.valueOf(properties.get("max")));
//                randomDistributionType.setMin(Double.valueOf(properties.get("min")));
//                processingTimeParam.getParameterValue().add(randomDistributionType);
            } else if (properties.get("distributiontype").equals("poisson")) {
                PoissonDistributionType poissonDistributionType = BpsimFactory.eINSTANCE.createPoissonDistributionType();
                poissonDistributionType.setMean(Double.valueOf(properties.get("mean")));
                processingTimeParam.getParameterValue().add(poissonDistributionType);
            }
            // no specific time unit available in 1.0 bpsim - use global
//            if(properties.get("timeunit") != null) {
//                timeParams.setTimeUnit(TimeUnit.getByName(properties.get("timeunit")));
//            }
            timeParams.setProcessingTime(processingTimeParam);
            if (_simulationElementParameters.containsKey(event.getId())) {
                _simulationElementParameters.get(event.getId()).add(timeParams);
            } else {
                List<EObject> values = new ArrayList<EObject>();
                values.add(timeParams);
                _simulationElementParameters.put(event.getId(),
                                                 values);
            }
        }
    }

    protected void applyTimerEventProperties(TimerEventDefinition timerEventDef,
                                             Map<String, String> properties) {
        String timeDate;
        String timeDuration;
        String timeCycle;
        String timeCycleLanguage;
        String timerSettings;
        timerSettings = properties.get(TIMERSETTINGS);

        if (timerSettings != null && !"".equals(timerSettings)) {
            TimerSettingsValue timerSettingsObj = new TimerSettingsTypeSerializer().parse(timerSettings);
            timeDate = timerSettingsObj.getTimeDate();
            timeDuration = timerSettingsObj.getTimeDuration();
            timeCycle = timerSettingsObj.getTimeCycle();
            timeCycleLanguage = timerSettingsObj.getTimeCycleLanguage();
        } else {
            timeDate = properties.get(TIMEDATE);
            timeDuration = properties.get(TIMEDURATION);
            timeCycle = properties.get(TIMECYCLE);
            timeCycleLanguage = properties.get(TIMECYCLELANGUAGE);
        }

        if (timeDate != null && !"".equals(timeDate)) {
            FormalExpression timeDateExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
            timeDateExpression.setBody(timeDate);
            timerEventDef.setTimeDate(timeDateExpression);
        }
        if (timeDuration != null && !"".equals(timeDuration)) {
            FormalExpression timeDurationExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
            timeDurationExpression.setBody(timeDuration);
            timerEventDef.setTimeDuration(timeDurationExpression);
        }
        if (timeCycle != null && !"".equals(timeCycle)) {
            FormalExpression timeCycleExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
            timeCycleExpression.setBody(timeCycle);
            if (timeCycleLanguage != null && timeCycleLanguage.length() > 0) {
                timeCycleExpression.setLanguage(timeCycleLanguage);
            }
            timerEventDef.setTimeCycle(timeCycleExpression);
        }
    }

    protected void applyGlobalTaskProperties(GlobalTask globalTask,
                                             Map<String, String> properties) {
        if (properties.get("name") != null) {
            globalTask.setName(StringEscapeUtils.escapeXml(properties.get("name")).replaceAll("\\r\\n|\\r|\\n",
                                                                                              " "));
        } else {
            globalTask.setName("");
        }
        // add unescaped and untouched name value as extension element as well
        Utils.setMetaDataExtensionValue(globalTask,
                                        "elementname",
                                        wrapInCDATABlock(properties.get("name").replaceAll("\\\\n",
                                                                                           "\n")));
    }

    protected void applyBaseElementProperties(BaseElement baseElement,
                                              Map<String, String> properties) {
        if (properties.get("documentation") != null && !"".equals(properties.get("documentation"))) {
            if (baseElement instanceof Definitions) {
                this.processDocs = properties.get("documentation");
            } else {
                baseElement.getDocumentation().add(createDocumentation(wrapInCDATABlock(properties.get("documentation"))));
            }
        }
        if (baseElement.getId() == null || baseElement.getId().length() < 1) {
            baseElement.setId(properties.get("resourceId"));
        }
        if (properties.get("bgcolor") != null && properties.get("bgcolor").length() > 0) {
            if (!(_elementColors.containsKey(baseElement.getId()))) {
                List<String> colorsList = new ArrayList<String>();
                colorsList.add("bgcolor:" + properties.get("bgcolor"));
                _elementColors.put(baseElement.getId(),
                                   colorsList);
            } else {
                _elementColors.get(baseElement.getId()).add("bgcolor:" + properties.get("bgcolor"));
            }
        }
        if (properties.get("isselectable") != null && properties.get("isselectable").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "selectable",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("isselectable"));
            baseElement.getAnyAttribute().add(extensionEntry);
        }
        if (properties.get("bordercolor") != null && properties.get("bordercolor").length() > 0) {
            if (!(_elementColors.containsKey(baseElement.getId()))) {
                List<String> colorsList = new ArrayList<String>();
                colorsList.add("bordercolor:" + properties.get("bordercolor"));
                _elementColors.put(baseElement.getId(),
                                   colorsList);
            } else {
                _elementColors.get(baseElement.getId()).add("bordercolor:" + properties.get("bordercolor"));
            }
        }
        if (properties.get("fontsize") != null && properties.get("fontsize").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "fontsize",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("fontsize"));
            baseElement.getAnyAttribute().add(extensionEntry);
        }
        if (properties.get("fontcolor") != null && properties.get("fontcolor").length() > 0) {
            if (!(_elementColors.containsKey(baseElement.getId()))) {
                List<String> colorsList = new ArrayList<String>();
                colorsList.add("fontcolor:" + properties.get("fontcolor"));
                _elementColors.put(baseElement.getId(),
                                   colorsList);
            } else {
                _elementColors.get(baseElement.getId()).add("fontcolor:" + properties.get("fontcolor"));
            }
        }
    }

    protected void applyDefinitionProperties(Definitions def,
                                             Map<String, String> properties) {
        def.setTypeLanguage(properties.get("typelanguage"));
        //def.setTargetNamespace(properties.get("targetnamespace"));
        def.setTargetNamespace("http://www.omg.org/bpmn20");
        def.setExpressionLanguage(properties.get("expressionlanguage"));
        ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
        EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                "xsi",
                "schemaLocation",
                false,
                false);
        SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                         "http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd");
        def.getAnyAttribute().add(extensionEntry);
        //_currentResource.getContents().add(def);// hook the definitions object to the resource early.
    }

    protected void applyProcessProperties(Process process,
                                          Map<String, String> properties) {
        if (properties.get("processn") != null) {
            process.setName(StringEscapeUtils.escapeXml(properties.get("processn")));
        } else {
            process.setName("");
        }
        if (properties.get("auditing") != null && !"".equals(properties.get("auditing"))) {
            Auditing audit = Bpmn2Factory.eINSTANCE.createAuditing();
            audit.getDocumentation().add(createDocumentation(properties.get("auditing")));
            process.setAuditing(audit);
        }
        process.setProcessType(ProcessType.getByName(properties.get("processtype")));
        process.setIsClosed(Boolean.parseBoolean(properties.get("isclosed")));
        process.setIsExecutable(Boolean.parseBoolean(properties.get("executable")));
        // get the drools-specific extension packageName attribute to Process if defined
        if (properties.get("package") != null && properties.get("package").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "packageName",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("package"));
            process.getAnyAttribute().add(extensionEntry);
        }
        // add version attrbute to process
        if (properties.get("version") != null && properties.get("version").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "version",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("version"));
            process.getAnyAttribute().add(extensionEntry);
        }
        if (properties.get("monitoring") != null && !"".equals(properties.get("monitoring"))) {
            Monitoring monitoring = Bpmn2Factory.eINSTANCE.createMonitoring();
            monitoring.getDocumentation().add(createDocumentation(properties.get("monitoring")));
            process.setMonitoring(monitoring);
        }
        // import extension elements
        if (properties.get("imports") != null && properties.get("imports").length() > 0) {
            String[] allImports = properties.get("imports").split(",\\s*");
            for (String importStr : allImports) {
                String[] importParts = importStr.split("\\|\\s*");
                // sample 'com.sample.Myclass|default,location|namespace|wsdl
                if (importParts.length == 2 || importParts.length == 3) {
                    if (importParts[1] != null && importParts[1].equals("default")) {
                        ImportType importType = DroolsFactory.eINSTANCE.createImportType();
                        importType.setName(importParts[0]);
                        if (process.getExtensionValues() == null || process.getExtensionValues().size() < 1) {
                            ExtensionAttributeValue extensionElement = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
                            process.getExtensionValues().add(extensionElement);
                        }
                        FeatureMap.Entry extensionElementEntry = new SimpleFeatureMapEntry(
                                (Internal) DroolsPackage.Literals.DOCUMENT_ROOT__IMPORT,
                                importType);
                        process.getExtensionValues().get(0).getValue().add(extensionElementEntry);
                    } else {
                        Import imp = Bpmn2Factory.eINSTANCE.createImport();
                        imp.setImportType("http://schemas.xmlsoap.org/wsdl/");
                        imp.setLocation(importParts[0]);
                        imp.setNamespace(importParts[1]);
                        _wsdlImports.add(imp);
                    }
                } else {
                    // just default (support legacy)
                    ImportType importType = DroolsFactory.eINSTANCE.createImportType();
                    importType.setName(importStr);
                    if (process.getExtensionValues() == null || process.getExtensionValues().size() < 1) {
                        ExtensionAttributeValue extensionElement = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
                        process.getExtensionValues().add(extensionElement);
                    }
                    FeatureMap.Entry extensionElementEntry = new SimpleFeatureMapEntry(
                            (Internal) DroolsPackage.Literals.DOCUMENT_ROOT__IMPORT,
                            importType);
                    process.getExtensionValues().get(0).getValue().add(extensionElementEntry);
                }
            }
        }
        // globals extension elements
        if (properties.get("globals") != null && properties.get("globals").length() > 0) {
            String[] allGlobals = properties.get("globals").split(",\\s*");
            for (String globalStr : allGlobals) {
                String[] globalParts = globalStr.split(":\\s*"); // identifier:type
                if (globalParts.length == 2) {
                    GlobalType globalType = DroolsFactory.eINSTANCE.createGlobalType();
                    globalType.setIdentifier(globalParts[0]);
                    globalType.setType(globalParts[1]);
                    if (process.getExtensionValues() == null || process.getExtensionValues().size() < 1) {
                        ExtensionAttributeValue extensionElement = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
                        process.getExtensionValues().add(extensionElement);
                    }
                    FeatureMap.Entry extensionElementEntry = new SimpleFeatureMapEntry(
                            (Internal) DroolsPackage.Literals.DOCUMENT_ROOT__GLOBAL,
                            globalType);
                    process.getExtensionValues().get(0).getValue().add(extensionElementEntry);
                } else if (globalParts.length == 1) {
                    GlobalType globalType = DroolsFactory.eINSTANCE.createGlobalType();
                    globalType.setIdentifier(globalParts[0]);
                    globalType.setType("Object");
                    if (process.getExtensionValues() == null || process.getExtensionValues().size() < 1) {
                        ExtensionAttributeValue extensionElement = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
                        process.getExtensionValues().add(extensionElement);
                    }
                    FeatureMap.Entry extensionElementEntry = new SimpleFeatureMapEntry(
                            (Internal) DroolsPackage.Literals.DOCUMENT_ROOT__GLOBAL,
                            globalType);
                    process.getExtensionValues().get(0).getValue().add(extensionElementEntry);
                }
            }
        }
        // simulation properties
        if (properties.get("timeunit") != null && properties.get("timeunit").length() > 0) {
            _simulationScenarioParameters.setBaseTimeUnit(TimeUnit.getByName(properties.get("timeunit")));
        }
        if (properties.get("currency") != null && properties.get("currency").length() > 0) {
            _simulationScenarioParameters.setBaseCurrencyUnit(properties.get("currency"));
        }
    }

    protected void applyBusinessRuleTaskProperties(BusinessRuleTask task,
                                                   Map<String, String> properties) {
        if (properties.get("ruleflowgroup") != null && properties.get("ruleflowgroup").length() > 0) {
            // add kiegroup-specific attribute "ruleFlowGroup"
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "ruleFlowGroup",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("ruleflowgroup"));
            task.getAnyAttribute().add(extensionEntry);
        }
        if (properties.get("script_language") != null && properties.get("script_language").length() > 0) {
            String scriptLanguage = getScriptLanguageFormat(properties);
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl scriptLanguageElement = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "scriptFormat",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(scriptLanguageElement,
                                                                             scriptLanguage);
            task.getAnyAttribute().add(extensionEntry);
        }
    }

    protected void applyScriptTaskProperties(ScriptTask scriptTask,
                                             Map<String, String> properties) {
        String scriptTypeStr = properties.get("script");
        if (scriptTypeStr != null && !scriptTypeStr.isEmpty()) {
            ScriptTypeValue value = new ScriptTypeTypeSerializer().parse(scriptTypeStr);
            if (value.getScript() != null && !value.getScript().isEmpty()) {
                scriptTask.setScript(wrapInCDATABlock(value.getScript()));
            }
            String languageFormat = Utils.getScriptLanguageFormat(value.getLanguage());
            if (languageFormat == null) {
                //default to java
                languageFormat = "http://www.java.com/java";
            }
            scriptTask.setScriptFormat(languageFormat);
        }
    }

    public void applyServiceTaskProperties(ServiceTask serviceTask,
                                           Map<String, String> properties) {
        if (properties.get("serviceimplementation") != null && properties.get("serviceimplementation").length() > 0) {
            serviceTask.setImplementation(properties.get("serviceimplementation"));
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "serviceimplementation",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("serviceimplementation"));
            serviceTask.getAnyAttribute().add(extensionEntry);
        }
        if (properties.get("serviceoperation") != null && properties.get("serviceoperation").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "serviceoperation",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("serviceoperation"));
            serviceTask.getAnyAttribute().add(extensionEntry);
        }
        if (properties.get("serviceinterface") != null && properties.get("serviceinterface").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "serviceinterface",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("serviceinterface"));
            serviceTask.getAnyAttribute().add(extensionEntry);
        }
    }

    public void applyReceiveTaskProperties(ReceiveTask receiveTask,
                                           Map<String, String> properties) {
        if (properties.get("messageref") != null && properties.get("messageref").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "msgref",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("messageref"));
            receiveTask.getAnyAttribute().add(extensionEntry);
        }
        receiveTask.setImplementation("Other");
    }

    public void applySendTaskProperties(SendTask sendTask,
                                        Map<String, String> properties) {
        if (properties.get("messageref") != null && properties.get("messageref").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "msgref",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("messageref"));
            sendTask.getAnyAttribute().add(extensionEntry);
        }
        sendTask.setImplementation("Other");
    }

    protected void applyLaneProperties(Lane lane,
                                       Map<String, String> properties) {
        if (properties.get("name") != null) {
            lane.setName(StringEscapeUtils.escapeXml(properties.get("name")).replaceAll("\\r\\n|\\r|\\n",
                                                                                        " "));
            // add unescaped and untouched name value as extension element as well
            Utils.setMetaDataExtensionValue(lane,
                                            "elementname",
                                            wrapInCDATABlock(properties.get("name").replaceAll("\\\\n",
                                                                                               "\n")));
        } else {
            lane.setName("");
        }
    }

    protected void applyCallActivityProperties(CallActivity callActivity,
                                               Map<String, String> properties) {
        if (properties.get("name") != null) {
            callActivity.setName(StringEscapeUtils.escapeXml(properties.get("name")).replaceAll("\\r\\n|\\r|\\n",
                                                                                                " "));
            // add unescaped and untouched name value as extension element as well
            Utils.setMetaDataExtensionValue(callActivity,
                                            "elementname",
                                            wrapInCDATABlock(properties.get("name").replaceAll("\\\\n",
                                                                                               "\n")));
        } else {
            callActivity.setName("");
        }
        if (properties.get("independent") != null && properties.get("independent").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "independent",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("independent"));
            callActivity.getAnyAttribute().add(extensionEntry);
        }
        if (properties.get("waitforcompletion") != null && properties.get("waitforcompletion").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "waitForCompletion",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("waitforcompletion"));
            callActivity.getAnyAttribute().add(extensionEntry);
        }
        if (properties.get("calledelement") != null && properties.get("calledelement").length() > 0) {
            callActivity.setCalledElement(properties.get("calledelement"));
        }
        // isAsync metadata
        if (properties.get("isasync") != null && properties.get("isasync").length() > 0 && properties.get("isasync").equals("true")) {
            Utils.setMetaDataExtensionValue(callActivity,
                                            "customAsync",
                                            wrapInCDATABlock(properties.get("isasync")));
        }
        parseAssignmentsInfo(properties);
        //callActivity data input set
        applyDataInputProperties(callActivity,
                                 properties,
                                 new HashMap<String, DataInput>());
        //callActivity data output set
        applyDataOutputProperties(callActivity,
                                  properties);
        //callActivity assignments
        if (properties.get("assignments") != null && properties.get("assignments").length() > 0) {
            String[] allAssignments = properties.get("assignments").split(",\\s*");
            for (String assignment : allAssignments) {
                if (assignment.contains("=")) {
                    String[] assignmentParts = assignment.split("=\\s*");
                    DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                    String fromPart = assignmentParts[0];
                    if (fromPart.startsWith("[din]")) {
                        fromPart = fromPart.substring(5,
                                                      fromPart.length());
                    }
                    boolean foundTaskName = false;
                    if (callActivity.getIoSpecification() != null && callActivity.getIoSpecification().getDataOutputs() != null) {
                        List<DataInput> dataInputs = callActivity.getIoSpecification().getDataInputs();
                        for (DataInput di : dataInputs) {
                            if (di.getId().equals(callActivity.getId() + "_" + fromPart + "InputX")) {
                                dia.setTargetRef(di);
                                if (di.getName().equals("TaskName")) {
                                    foundTaskName = true;
                                    break;
                                }
                            }
                        }
                    }
                    Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                    FormalExpression fromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    if (assignmentParts.length > 1) {
                        String replacer = decodeAssociationValue(assignmentParts[1]);
                        fromExpression.setBody(wrapInCDATABlock(replacer));
                    } else {
                        fromExpression.setBody("");
                    }
                    FormalExpression toExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    toExpression.setBody(dia.getTargetRef().getId());
                    a.setFrom(fromExpression);
                    a.setTo(toExpression);
                    dia.getAssignment().add(a);
                    callActivity.getDataInputAssociations().add(dia);
//                } else if(assignment.contains("<->")) {
//                    String[] assignmentParts = assignment.split( "<->\\s*" );
//                    DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
//                    DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
//
//                    ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
//                    ie.setId(assignmentParts[0]);
//                    dia.getSourceRef().add(ie);
//                    doa.setTargetRef(ie);
//
//                    List<DataInput> dataInputs = callActivity.getIoSpecification().getDataInputs();
//                    for(DataInput di : dataInputs) {
//                        if(di.getId().equals(callActivity.getId() + "_" + assignmentParts[1] + "InputX")) {
//                            dia.setTargetRef(di);
//                            break;
//                        }
//                    }
//                    List<DataOutput> dataOutputs = callActivity.getIoSpecification().getDataOutputs();
//                    for(DataOutput dout : dataOutputs) {
//                        if(dout.getId().equals(callActivity.getId() + "_" + assignmentParts[1] + "OutputX")) {
//                            doa.getSourceRef().add(dout);
//                            break;
//                        }
//                    }
//
//                    callActivity.getDataInputAssociations().add(dia);
//                    callActivity.getDataOutputAssociations().add(doa);
                } else if (assignment.contains("->")) {
                    String[] assignmentParts = assignment.split("->\\s*");
                    String fromPart = assignmentParts[0];
                    boolean isDataInput = false;
                    boolean isDataOutput = false;
                    if (fromPart.startsWith("[din]")) {
                        fromPart = fromPart.substring(5,
                                                      fromPart.length());
                        isDataInput = true;
                    }
                    if (fromPart.startsWith("[dout]")) {
                        fromPart = fromPart.substring(6,
                                                      fromPart.length());
                        isDataOutput = true;
                    }
                    List<DataOutput> dataOutputs = callActivity.getIoSpecification().getDataOutputs();
                    if (isDataOutput) {
                        // doing data output
                        DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
                        for (DataOutput dout : dataOutputs) {
                            if (dout.getId().equals(callActivity.getId() + "_" + fromPart + "OutputX")) {
                                doa.getSourceRef().add(dout);
                                break;
                            }
                        }
                        ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                        ie.setId(assignmentParts[1]);
                        doa.setTargetRef(ie);
                        callActivity.getDataOutputAssociations().add(doa);
                    } else if (isDataInput) {
                        // doing data input
                        DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                        // association from process var to dataInput var
                        ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                        ie.setId(fromPart);
                        dia.getSourceRef().add(ie);
                        List<DataInput> dataInputs = callActivity.getIoSpecification().getDataInputs();
                        for (DataInput di : dataInputs) {
                            if (di.getId().equals(callActivity.getId() + "_" + assignmentParts[1] + "InputX")) {
                                dia.setTargetRef(di);
                                break;
                            }
                        }
                        callActivity.getDataInputAssociations().add(dia);
                    }
                } else {
                    // TODO throw exception here?
                }
            }
        }
        // process on-entry and on-exit actions as custom elements
        applyOnEntryActions(callActivity,
                            properties);
        applyOnExitActions(callActivity,
                           properties);
        // simulation
        if (properties.get("distributiontype") != null && properties.get("distributiontype").length() > 0) {
            TimeParameters timeParams = BpsimFactory.eINSTANCE.createTimeParameters();
            Parameter processingTimeParam = BpsimFactory.eINSTANCE.createParameter();
            if (properties.get("distributiontype").equals("normal")) {
                NormalDistributionType normalDistributionType = BpsimFactory.eINSTANCE.createNormalDistributionType();
                normalDistributionType.setStandardDeviation(Double.valueOf(properties.get("standarddeviation")));
                normalDistributionType.setMean(Double.valueOf(properties.get("mean")));
                processingTimeParam.getParameterValue().add(normalDistributionType);
            } else if (properties.get("distributiontype").equals("uniform")) {
                UniformDistributionType uniformDistributionType = BpsimFactory.eINSTANCE.createUniformDistributionType();
                uniformDistributionType.setMax(Double.valueOf(properties.get("max")));
                uniformDistributionType.setMin(Double.valueOf(properties.get("min")));
                processingTimeParam.getParameterValue().add(uniformDistributionType);
                // random distribution not supported in bpsim 1.0
//             } else if(properties.get("distributiontype").equals("random")) {
//                  RandomDistributionType randomDistributionType = BpsimFactory.eINSTANCE.createRandomDistributionType();
//                  randomDistributionType.setMax(Double.valueOf(properties.get("max")));
//                  randomDistributionType.setMin(Double.valueOf(properties.get("min")));
//                  processingTimeParam.getParameterValue().add(randomDistributionType);
            } else if (properties.get("distributiontype").equals("poisson")) {
                PoissonDistributionType poissonDistributionType = BpsimFactory.eINSTANCE.createPoissonDistributionType();
                poissonDistributionType.setMean(Double.valueOf(properties.get("mean")));
                processingTimeParam.getParameterValue().add(poissonDistributionType);
            }
            // individual time unit not supported in bpsim 1.0
//             if(properties.get("timeunit") != null) {
//                  timeParams.setTimeUnit(TimeUnit.getByName(properties.get("timeunit")));
//             }
            if (properties.get("waittime") != null) {
                Parameter waittimeParam = BpsimFactory.eINSTANCE.createParameter();
                FloatingParameterType waittimeParamValue = BpsimFactory.eINSTANCE.createFloatingParameterType();
                DecimalFormat twoDForm = new DecimalFormat("#.##");
                waittimeParamValue.setValue(Double.valueOf(twoDForm.format(Double.valueOf(properties.get("waittime")))));
                waittimeParam.getParameterValue().add(waittimeParamValue);
                timeParams.setWaitTime(waittimeParam);
            }
            timeParams.setProcessingTime(processingTimeParam);
            if (_simulationElementParameters.containsKey(callActivity.getId())) {
                _simulationElementParameters.get(callActivity.getId()).add(timeParams);
            } else {
                List<EObject> values = new ArrayList<EObject>();
                values.add(timeParams);
                _simulationElementParameters.put(callActivity.getId(),
                                                 values);
            }
        }
        CostParameters costParameters = BpsimFactory.eINSTANCE.createCostParameters();
        if (properties.get("unitcost") != null && properties.get("unitcost").length() > 0) {
            Parameter unitcostParam = BpsimFactory.eINSTANCE.createParameter();
            FloatingParameterType unitCostParameterValue = BpsimFactory.eINSTANCE.createFloatingParameterType();
            unitCostParameterValue.setValue(new Double(properties.get("unitcost")));
            unitcostParam.getParameterValue().add(unitCostParameterValue);
            costParameters.setUnitCost(unitcostParam);
        }
        // no individual currency unit supported in bpsim 1.0
//        if(properties.get("currency") != null && properties.get("currency").length() > 0) {
//            costParameters.setCurrencyUnit(properties.get("currency"));
//        }
        if (_simulationElementParameters.containsKey(callActivity.getId())) {
            _simulationElementParameters.get(callActivity.getId()).add(costParameters);
        } else {
            List<EObject> values = new ArrayList<EObject>();
            values.add(costParameters);
            _simulationElementParameters.put(callActivity.getId(),
                                             values);
        }
    }

    protected void applyTaskProperties(Task task,
                                       Map<String, String> properties,
                                       String preProcessingData) {
        if (properties.get("name") != null) {
            task.setName(StringEscapeUtils.escapeXml(properties.get("name")).replaceAll("\\r\\n|\\r|\\n",
                                                                                        " "));
        } else {
            task.setName("");
        }
        // add unescaped and untouched name value as extension element as well
        Utils.setMetaDataExtensionValue(task,
                                        "elementname",
                                        wrapInCDATABlock(properties.get("name").replaceAll("\\\\n",
                                                                                           "\n")));
        DataInput taskNameDataInput = null;
        if (properties.get("taskname") != null && properties.get("taskname").length() > 0) {
            if (isCustomElement(properties.get("tasktype"),
                                preProcessingData)) {
                // add kiegroup-specific attribute "taskName"
                ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                        "http://www.jboss.org/drools",
                        "taskName",
                        false,
                        false);
                SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                                 properties.get("taskname").replaceAll("&",
                                                                                                                       "").replaceAll(" ",
                                                                                                                                      ""));
                task.getAnyAttribute().add(extensionEntry);
            }
            // map the taskName to iospecification
            taskNameDataInput = Bpmn2Factory.eINSTANCE.createDataInput();
            taskNameDataInput.setId(task.getId() + "_TaskNameInputX");
            taskNameDataInput.setName("TaskName");
            // Make the DataInput a String
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "dtype",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             "String");
            taskNameDataInput.getAnyAttribute().add(extensionEntry);
            if (task.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                task.setIoSpecification(iospec);
            }
            task.getIoSpecification().getDataInputs().add(taskNameDataInput);
            // taskName also needs to be in dataInputAssociation
            DataInputAssociation taskNameDataInputAssociation = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
            taskNameDataInputAssociation.setTargetRef(taskNameDataInput);
            Assignment taskNameAssignment = Bpmn2Factory.eINSTANCE.createAssignment();
            FormalExpression fromExp = Bpmn2Factory.eINSTANCE.createFormalExpression();
            fromExp.setBody(properties.get("taskname").replaceAll("&",
                                                                  "").replaceAll(" ",
                                                                                 ""));
            taskNameAssignment.setFrom(fromExp);
            FormalExpression toExp = Bpmn2Factory.eINSTANCE.createFormalExpression();
            toExp.setBody(task.getId() + "_TaskNameInputX");
            taskNameAssignment.setTo(toExp);
            taskNameDataInputAssociation.getAssignment().add(taskNameAssignment);
            task.getDataInputAssociations().add(taskNameDataInputAssociation);
        }
        //process lanes
        if (properties.get("lanes") != null && properties.get("lanes").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "lanes",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("lanes"));
            task.getAnyAttribute().add(extensionEntry);
        }
        // isAsync metadata
        if (properties.get("isasync") != null && properties.get("isasync").length() > 0 && properties.get("isasync").equals("true")) {
            Utils.setMetaDataExtensionValue(task,
                                            "customAsync",
                                            wrapInCDATABlock(properties.get("isasync")));
        }
        // autostart metadata
        if (properties.get("customautostart") != null && properties.get("customautostart").length() > 0 && properties.get("customautostart").equals("true")) {
            Utils.setMetaDataExtensionValue(task,
                                            "customAutoStart",
                                            wrapInCDATABlock(properties.get("customautostart")));
        }

        parseAssignmentsInfo(properties);
        //process data input set
        Map<String, DataInput> alreadyProcessedInputs = new HashMap<String, DataInput>();
        alreadyProcessedInputs.put("TaskName",
                                   taskNameDataInput);
        applyDataInputProperties(task,
                                 properties,
                                 alreadyProcessedInputs);
        for (DataInput processedInput : alreadyProcessedInputs.values()) {
            if (processedInput != null) {
                task.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(processedInput);
            }
        }
        //process data output set
        applyDataOutputProperties(task,
                                  properties);
        //process assignments
        if (properties.get("assignments") != null && properties.get("assignments").length() > 0) {
            String[] allAssignments = properties.get("assignments").split(",\\s*");
            for (String assignment : allAssignments) {
                if (assignment.contains("=")) {
                    String[] assignmentParts = assignment.split("=\\s*");
                    String fromPart = assignmentParts[0];
                    if (fromPart.startsWith("[din]")) {
                        fromPart = fromPart.substring(5,
                                                      fromPart.length());
                    }
                    DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                    boolean foundTaskName = false;
                    if (task.getIoSpecification() != null && task.getIoSpecification().getDataOutputs() != null) {
                        List<DataInput> dataInputs = task.getIoSpecification().getDataInputs();
                        for (DataInput di : dataInputs) {
                            if (di.getId().equals(task.getId() + "_" + fromPart + (fromPart.endsWith("InputX") ? "" : "InputX"))) {
                                dia.setTargetRef(di);
                                if (di.getName().equals("TaskName")) {
                                    foundTaskName = true;
                                    break;
                                }
                            }
                        }
                    }
                    // if we are dealing with TaskName and none has been defined, add it
                    if (fromPart.equals("TaskName") && !foundTaskName) {
                        DataInput assignmentTaskNameDataInput = Bpmn2Factory.eINSTANCE.createDataInput();
                        assignmentTaskNameDataInput.setId(task.getId() + "_TaskNameInputX");
                        assignmentTaskNameDataInput.setName("TaskName");
                        if (task.getIoSpecification() == null) {
                            InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                            task.setIoSpecification(iospec);
                        }
                        task.getIoSpecification().getDataInputs().add(assignmentTaskNameDataInput);
                        dia.setTargetRef(assignmentTaskNameDataInput);
                        InputSet inset = task.getIoSpecification().getInputSets().get(0);
                        inset.getDataInputRefs().add(assignmentTaskNameDataInput);
                    }
                    Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                    FormalExpression fromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    if (assignmentParts.length > 1) {
                        String replacer = decodeAssociationValue(assignmentParts[1]);
                        fromExpression.setBody(wrapInCDATABlock(replacer));
                    } else {
                        // for custom workitem properties check individually for values
                        if (properties.get(fromPart.toLowerCase()) != null && properties.get(fromPart.toLowerCase()).length() > 0) {
                            fromExpression.setBody(properties.get(fromPart.toLowerCase()));
                        } else {
                            fromExpression.setBody("");
                        }
                    }
                    FormalExpression toExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    toExpression.setBody(dia.getTargetRef().getId());
                    a.setFrom(fromExpression);
                    a.setTo(toExpression);
                    dia.getAssignment().add(a);
                    task.getDataInputAssociations().add(dia);
//                } else if(assignment.contains("<->")) {
//                    String[] assignmentParts = assignment.split( "<->\\s*" );
//                    DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
//                    DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
//
//                    ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
//                    ie.setId(assignmentParts[0]);
//                    dia.getSourceRef().add(ie);
//                    doa.setTargetRef(ie);
//
//                    List<DataInput> dataInputs = task.getIoSpecification().getDataInputs();
//                    for(DataInput di : dataInputs) {
//                        if(di.getId().equals(task.getId() + "_" + assignmentParts[1] + (assignmentParts[1].endsWith("InputX") ? "" : "InputX"))) {
//                            dia.setTargetRef(di);
//                            break;
//                        }
//                    }
//                    List<DataOutput> dataOutputs = task.getIoSpecification().getDataOutputs();
//                    for(DataOutput dout : dataOutputs) {
//                        if(dout.getId().equals(task.getId() + "_" + assignmentParts[1] + (assignmentParts[1].endsWith("OutputX") ? "" : "OutputX"))) {
//                            doa.getSourceRef().add(dout);
//                            break;
//                        }
//                    }
//                    task.getDataInputAssociations().add(dia);
//                    task.getDataOutputAssociations().add(doa);
                } else if (assignment.contains("->")) {
                    String[] assignmentParts = assignment.split("->\\s*");
                    String fromPart = assignmentParts[0];
                    boolean isDataInput = false;
                    boolean isDataOutput = false;
                    if (fromPart.startsWith("[din]")) {
                        fromPart = fromPart.substring(5,
                                                      fromPart.length());
                        isDataInput = true;
                    }
                    if (fromPart.startsWith("[dout]")) {
                        fromPart = fromPart.substring(6,
                                                      fromPart.length());
                        isDataOutput = true;
                    }
                    List<DataOutput> dataOutputs = task.getIoSpecification().getDataOutputs();
                    if (isDataOutput) {
                        DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
                        for (DataOutput dout : dataOutputs) {
                            if (dout.getId().equals(task.getId() + "_" + fromPart + (fromPart.endsWith("OutputX") ? "" : "OutputX"))) {
                                doa.getSourceRef().add(dout);
                                break;
                            }
                        }
                        ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                        ie.setId(assignmentParts[1]);
                        doa.setTargetRef(ie);
                        task.getDataOutputAssociations().add(doa);
                    } else if (isDataInput) {
                        DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                        // association from process var to dataInput var
                        ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                        ie.setId(fromPart);
                        dia.getSourceRef().add(ie);
                        List<DataInput> dataInputs = task.getIoSpecification().getDataInputs();
                        for (DataInput di : dataInputs) {
                            if (di.getId().equals(task.getId() + "_" + assignmentParts[1] + (assignmentParts[1].endsWith("InputX") ? "" : "InputX"))) {
                                dia.setTargetRef(di);
                                break;
                            }
                        }
                        task.getDataInputAssociations().add(dia);
                    }
                } else {
                    // TODO throw exception here?
                }
            }
            // check if multiple taskname datainput associations exist and remove them
            List<DataInputAssociation> dataInputAssociations = task.getDataInputAssociations();
            boolean haveTaskNameInput = false;
            for (Iterator<DataInputAssociation> itr = dataInputAssociations.iterator(); itr.hasNext(); ) {
                DataInputAssociation da = itr.next();
                if (da.getAssignment() != null && da.getAssignment().size() > 0) {
                    Assignment a = da.getAssignment().get(0);
                    if (((FormalExpression) a.getTo()).getBody().equals(task.getId() + "_TaskNameInputX")) {
                        if (!haveTaskNameInput) {
                            haveTaskNameInput = true;
                        } else {
                            itr.remove();
                        }
                    }
                }
            }
        }
        // process on-entry and on-exit actions as custom elements
        applyOnEntryActions(task,
                            properties);
        applyOnExitActions(task,
                           properties);

        // multi instance
        if (properties.get("multipleinstance") != null && properties.get("multipleinstance").length() > 0 && properties.get("multipleinstance").equals("true")) {
            // will be revisited at end
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "mitask",
                    false,
                    false);
            StringBuffer buff = new StringBuffer();
            buff.append((properties.get("multipleinstancecollectioninput") != null && properties.get("multipleinstancecollectioninput").length() > 0) ? properties.get("multipleinstancecollectioninput") : " ");
            buff.append("@");
            buff.append((properties.get("multipleinstancecollectionoutput") != null && properties.get("multipleinstancecollectionoutput").length() > 0) ? properties.get("multipleinstancecollectionoutput") : " ");
            buff.append("@");
            buff.append((properties.get("multipleinstancedatainput") != null && properties.get("multipleinstancedatainput").length() > 0) ? properties.get("multipleinstancedatainput") : " ");
            buff.append("@");
            buff.append((properties.get("multipleinstancedataoutput") != null && properties.get("multipleinstancedataoutput").length() > 0) ? properties.get("multipleinstancedataoutput") : " ");
            buff.append("@");
            buff.append((properties.get("multipleinstancecompletioncondition") != null && properties.get("multipleinstancecompletioncondition").length() > 0) ? properties.get("multipleinstancecompletioncondition") : " ");
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             buff.toString());
            task.getAnyAttribute().add(extensionEntry);
        }
        // simulation
        if (properties.get("distributiontype") != null && properties.get("distributiontype").length() > 0) {
            TimeParameters timeParams = BpsimFactory.eINSTANCE.createTimeParameters();
            Parameter processingTimeParam = BpsimFactory.eINSTANCE.createParameter();
            if (properties.get("distributiontype").equals("normal")) {
                NormalDistributionType normalDistributionType = BpsimFactory.eINSTANCE.createNormalDistributionType();
                normalDistributionType.setStandardDeviation(Double.valueOf(properties.get("standarddeviation")));
                normalDistributionType.setMean(Double.valueOf(properties.get("mean")));
                processingTimeParam.getParameterValue().add(normalDistributionType);
            } else if (properties.get("distributiontype").equals("uniform")) {
                UniformDistributionType uniformDistributionType = BpsimFactory.eINSTANCE.createUniformDistributionType();
                uniformDistributionType.setMax(Double.valueOf(properties.get("max")));
                uniformDistributionType.setMin(Double.valueOf(properties.get("min")));
                processingTimeParam.getParameterValue().add(uniformDistributionType);
                // random distribution not supported in bpsim 1.0
//             } else if(properties.get("distributiontype").equals("random")) {
//                  RandomDistributionType randomDistributionType = BpsimFactory.eINSTANCE.createRandomDistributionType();
//                  randomDistributionType.setMax(Double.valueOf(properties.get("max")));
//                  randomDistributionType.setMin(Double.valueOf(properties.get("min")));
//                  processingTimeParam.getParameterValue().add(randomDistributionType);
            } else if (properties.get("distributiontype").equals("poisson")) {
                PoissonDistributionType poissonDistributionType = BpsimFactory.eINSTANCE.createPoissonDistributionType();
                poissonDistributionType.setMean(Double.valueOf(properties.get("mean")));
                processingTimeParam.getParameterValue().add(poissonDistributionType);
            }
            // individual time unit not supported in bpsim 1.0
//             if(properties.get("timeunit") != null) {
//                  timeParams.setTimeUnit(TimeUnit.getByName(properties.get("timeunit")));
//             }
            if (properties.get("waittime") != null) {
                Parameter waittimeParam = BpsimFactory.eINSTANCE.createParameter();
                FloatingParameterType waittimeParamValue = BpsimFactory.eINSTANCE.createFloatingParameterType();
                DecimalFormat twoDForm = new DecimalFormat("#.##");
                waittimeParamValue.setValue(Double.valueOf(twoDForm.format(Double.valueOf(properties.get("waittime")))));
                waittimeParam.getParameterValue().add(waittimeParamValue);
                timeParams.setWaitTime(waittimeParam);
            }
            timeParams.setProcessingTime(processingTimeParam);
            if (_simulationElementParameters.containsKey(task.getId())) {
                _simulationElementParameters.get(task.getId()).add(timeParams);
            } else {
                List<EObject> values = new ArrayList<EObject>();
                values.add(timeParams);
                _simulationElementParameters.put(task.getId(),
                                                 values);
            }
        }
        CostParameters costParameters = BpsimFactory.eINSTANCE.createCostParameters();
        if (properties.get("unitcost") != null && properties.get("unitcost").length() > 0) {
            Parameter unitcostParam = BpsimFactory.eINSTANCE.createParameter();
            FloatingParameterType unitCostParameterValue = BpsimFactory.eINSTANCE.createFloatingParameterType();
            unitCostParameterValue.setValue(new Double(properties.get("unitcost")));
            unitcostParam.getParameterValue().add(unitCostParameterValue);
            costParameters.setUnitCost(unitcostParam);
        }
        // no individual currency unit supported in bpsim 1.0
//        if(properties.get("currency") != null && properties.get("currency").length() > 0) {
//            costParameters.setCurrencyUnit(properties.get("currency"));
//        }
        if (_simulationElementParameters.containsKey(task.getId())) {
            _simulationElementParameters.get(task.getId()).add(costParameters);
        } else {
            List<EObject> values = new ArrayList<EObject>();
            values.add(costParameters);
            _simulationElementParameters.put(task.getId(),
                                             values);
        }
    }

    protected void applyUserTaskProperties(UserTask task,
                                           Map<String, String> properties) {
        setLastUserTaskID(task);
        if (properties.get("actors") != null && properties.get("actors").length() > 0) {
            String[] allActors = properties.get("actors").split(",\\s*");
            for (String actor : allActors) {
                PotentialOwner po = Bpmn2Factory.eINSTANCE.createPotentialOwner();
                ResourceAssignmentExpression rae = Bpmn2Factory.eINSTANCE.createResourceAssignmentExpression();
                FormalExpression fe = Bpmn2Factory.eINSTANCE.createFormalExpression();
                fe.setBody(actor);
                rae.setExpression(fe);
                po.setResourceAssignmentExpression(rae);
                task.getResources().add(po);
            }
        }
        if (properties.get("script_language") != null && properties.get("script_language").length() > 0) {
            String scriptLanguage = getScriptLanguageFormat(properties);
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl scriptLanguageElement = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "scriptFormat",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(scriptLanguageElement,
                                                                             scriptLanguage);
            task.getAnyAttribute().add(extensionEntry);
        }
        if (properties.get("groupid") != null && properties.get("groupid").length() > 0) {
            if (task.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                task.setIoSpecification(iospec);
            }
            List<DataInput> dataInputs = task.getIoSpecification().getDataInputs();
            boolean foundGroupIdInput = false;
            DataInput foundInput = null;
            for (DataInput din : dataInputs) {
                if (din.getName().equals("GroupId")) {
                    foundGroupIdInput = true;
                    foundInput = din;
                    break;
                }
            }
            if (!foundGroupIdInput) {
                DataInput d = Bpmn2Factory.eINSTANCE.createDataInput();
                d.setId(task.getId() + "_" + "GroupId" + "InputX");
                d.setName("GroupId");
                task.getIoSpecification().getDataInputs().add(d);
                foundInput = d;
                if (task.getIoSpecification().getInputSets() == null || task.getIoSpecification().getInputSets().size() < 1) {
                    InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                    task.getIoSpecification().getInputSets().add(inset);
                }
                task.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(d);
            }
            boolean foundGroupIdAssociation = false;
            List<DataInputAssociation> inputAssociations = task.getDataInputAssociations();
            for (DataInputAssociation da : inputAssociations) {
                if (da.getTargetRef() != null && da.getTargetRef().getId().equals(foundInput.getId())) {
                    foundGroupIdAssociation = true;
                    ((FormalExpression) da.getAssignment().get(0).getFrom()).setBody(wrapInCDATABlock(properties.get("groupid")));
                }
            }
            if (!foundGroupIdAssociation) {
                DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                dia.setTargetRef(foundInput);
                Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                FormalExpression groupFromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                groupFromExpression.setBody(wrapInCDATABlock(properties.get("groupid")));
                FormalExpression groupToExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                groupToExpression.setBody(foundInput.getId());
                a.setFrom(groupFromExpression);
                a.setTo(groupToExpression);
                dia.getAssignment().add(a);
                task.getDataInputAssociations().add(dia);
            }
        }
        String skippableStr = (properties.get("skippable") == null ? "true" : properties.get("skippable"));   // default to true if not set
        if (skippableStr.length() > 0) {
            if (task.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                task.setIoSpecification(iospec);
            }
            List<DataInput> dataInputs = task.getIoSpecification().getDataInputs();
            boolean foundSkippableInput = false;
            DataInput foundInput = null;
            for (DataInput din : dataInputs) {
                if (din.getName().equals("Skippable")) {
                    foundSkippableInput = true;
                    foundInput = din;
                    break;
                }
            }
            if (!foundSkippableInput) {
                DataInput d = Bpmn2Factory.eINSTANCE.createDataInput();
                d.setId(task.getId() + "_" + "Skippable" + "InputX");
                d.setName("Skippable");
                task.getIoSpecification().getDataInputs().add(d);
                foundInput = d;
                if (task.getIoSpecification().getInputSets() == null || task.getIoSpecification().getInputSets().size() < 1) {
                    InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                    task.getIoSpecification().getInputSets().add(inset);
                }
                task.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(d);
            }
            boolean foundSkippableAssociation = false;
            List<DataInputAssociation> inputAssociations = task.getDataInputAssociations();
            for (DataInputAssociation da : inputAssociations) {
                if (da.getTargetRef() != null && da.getTargetRef().getId().equals(foundInput.getId())) {
                    foundSkippableAssociation = true;
                    ((FormalExpression) da.getAssignment().get(0).getFrom()).setBody(skippableStr);
                }
            }
            if (!foundSkippableAssociation) {
                DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                dia.setTargetRef(foundInput);
                Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                FormalExpression skippableFromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                skippableFromExpression.setBody(skippableStr);
                FormalExpression skippableToExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                skippableToExpression.setBody(foundInput.getId());
                a.setFrom(skippableFromExpression);
                a.setTo(skippableToExpression);
                dia.getAssignment().add(a);
                task.getDataInputAssociations().add(dia);
            }
        }
        if (properties.get("subject") != null && properties.get("subject").length() > 0) {
            if (task.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                task.setIoSpecification(iospec);
            }
            List<DataInput> dataInputs = task.getIoSpecification().getDataInputs();
            boolean foundCommentInput = false;
            DataInput foundInput = null;
            for (DataInput din : dataInputs) {
                if (din.getName().equals("Comment")) {
                    foundCommentInput = true;
                    foundInput = din;
                    break;
                }
            }
            if (!foundCommentInput) {
                DataInput d = Bpmn2Factory.eINSTANCE.createDataInput();
                d.setId(task.getId() + "_" + "Comment" + "InputX");
                d.setName("Comment");
                task.getIoSpecification().getDataInputs().add(d);
                foundInput = d;
                if (task.getIoSpecification().getInputSets() == null || task.getIoSpecification().getInputSets().size() < 1) {
                    InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                    task.getIoSpecification().getInputSets().add(inset);
                }
                task.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(d);
            }
            boolean foundCommentAssociation = false;
            List<DataInputAssociation> inputAssociations = task.getDataInputAssociations();
            for (DataInputAssociation da : inputAssociations) {
                if (da.getTargetRef() != null && da.getTargetRef().getId().equals(foundInput.getId())) {
                    foundCommentAssociation = true;
                    ((FormalExpression) da.getAssignment().get(0).getFrom()).setBody(wrapInCDATABlock(properties.get("subject")));
                }
            }
            if (!foundCommentAssociation) {
                DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                dia.setTargetRef(foundInput);
                Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                FormalExpression commentFromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                commentFromExpression.setBody(wrapInCDATABlock(properties.get("subject")));
                FormalExpression commentToExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                commentToExpression.setBody(foundInput.getId());
                a.setFrom(commentFromExpression);
                a.setTo(commentToExpression);
                dia.getAssignment().add(a);
                task.getDataInputAssociations().add(dia);
            }
        }
        if (properties.get("description") != null && properties.get("description").length() > 0) {
            if (task.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                task.setIoSpecification(iospec);
            }
            List<DataInput> dataInputs = task.getIoSpecification().getDataInputs();
            boolean foundDescriptionInput = false;
            DataInput foundInput = null;
            for (DataInput din : dataInputs) {
                if (din.getName().equals("Description")) {
                    foundDescriptionInput = true;
                    foundInput = din;
                    break;
                }
            }
            if (!foundDescriptionInput) {
                DataInput d = Bpmn2Factory.eINSTANCE.createDataInput();
                d.setId(task.getId() + "_" + "Description" + "InputX");
                d.setName("Description");
                task.getIoSpecification().getDataInputs().add(d);
                foundInput = d;
                if (task.getIoSpecification().getInputSets() == null || task.getIoSpecification().getInputSets().size() < 1) {
                    InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                    task.getIoSpecification().getInputSets().add(inset);
                }
                task.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(d);
            }
            boolean foundDescriptionAssociation = false;
            List<DataInputAssociation> inputAssociations = task.getDataInputAssociations();
            for (DataInputAssociation da : inputAssociations) {
                if (da.getTargetRef() != null && da.getTargetRef().getId().equals(foundInput.getId())) {
                    foundDescriptionAssociation = true;
                    ((FormalExpression) da.getAssignment().get(0).getFrom()).setBody(wrapInCDATABlock(properties.get("description")));
                }
            }
            if (!foundDescriptionAssociation) {
                DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                dia.setTargetRef(foundInput);
                Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                FormalExpression descriptionFromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                descriptionFromExpression.setBody(wrapInCDATABlock(properties.get("description")));
                FormalExpression descriptionToExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                descriptionToExpression.setBody(foundInput.getId());
                a.setFrom(descriptionFromExpression);
                a.setTo(descriptionToExpression);
                dia.getAssignment().add(a);
                task.getDataInputAssociations().add(dia);
            }
        }
        if (properties.get("priority") != null && properties.get("priority").length() > 0) {
            if (task.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                task.setIoSpecification(iospec);
            }
            List<DataInput> dataInputs = task.getIoSpecification().getDataInputs();
            boolean foundPriorityInput = false;
            DataInput foundInput = null;
            for (DataInput din : dataInputs) {
                if (din.getName().equals("Priority")) {
                    foundPriorityInput = true;
                    foundInput = din;
                    break;
                }
            }
            if (!foundPriorityInput) {
                DataInput d = Bpmn2Factory.eINSTANCE.createDataInput();
                d.setId(task.getId() + "_" + "Priority" + "InputX");
                d.setName("Priority");
                task.getIoSpecification().getDataInputs().add(d);
                foundInput = d;
                if (task.getIoSpecification().getInputSets() == null || task.getIoSpecification().getInputSets().size() < 1) {
                    InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                    task.getIoSpecification().getInputSets().add(inset);
                }
                task.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(d);
            }
            boolean foundPriorityAssociation = false;
            List<DataInputAssociation> inputAssociations = task.getDataInputAssociations();
            for (DataInputAssociation da : inputAssociations) {
                if (da.getTargetRef() != null && da.getTargetRef().getId().equals(foundInput.getId())) {
                    foundPriorityAssociation = true;
                    ((FormalExpression) da.getAssignment().get(0).getFrom()).setBody(properties.get("priority"));
                }
            }
            if (!foundPriorityAssociation) {
                DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                dia.setTargetRef(foundInput);
                Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                FormalExpression priorityFromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                priorityFromExpression.setBody(properties.get("priority"));
                FormalExpression priorityToExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                priorityToExpression.setBody(foundInput.getId());
                a.setFrom(priorityFromExpression);
                a.setTo(priorityToExpression);
                dia.getAssignment().add(a);
                task.getDataInputAssociations().add(dia);
            }
        }
        if (properties.get("content") != null && properties.get("content").length() > 0) {
            if (task.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                OutputSet outSet = Bpmn2Factory.eINSTANCE.createOutputSet();
                iospec.getOutputSets().add(outSet);
                task.setIoSpecification(iospec);
            }
            List<DataInput> dataInputs = task.getIoSpecification().getDataInputs();
            boolean foundContentInput = false;
            DataInput foundInput = null;
            for (DataInput din : dataInputs) {
                if (din.getName().equals("Content")) {
                    foundContentInput = true;
                    foundInput = din;
                    break;
                }
            }
            if (!foundContentInput) {
                DataInput d = Bpmn2Factory.eINSTANCE.createDataInput();
                d.setId(task.getId() + "_" + "Content" + "InputX");
                d.setName("Content");
                task.getIoSpecification().getDataInputs().add(d);
                foundInput = d;
                if (task.getIoSpecification().getInputSets() == null || task.getIoSpecification().getInputSets().size() < 1) {
                    InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                    task.getIoSpecification().getInputSets().add(inset);
                }
                task.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(d);
            }
            boolean foundContentAssociation = false;
            List<DataInputAssociation> inputAssociations = task.getDataInputAssociations();
            for (DataInputAssociation da : inputAssociations) {
                if (da.getTargetRef() != null && da.getTargetRef().getId().equals(foundInput.getId())) {
                    foundContentAssociation = true;
                    ((FormalExpression) da.getAssignment().get(0).getFrom()).setBody(wrapInCDATABlock(properties.get("content")));
                }
            }
            if (!foundContentAssociation) {
                DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                dia.setTargetRef(foundInput);
                Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                FormalExpression contentFromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                contentFromExpression.setBody(wrapInCDATABlock(properties.get("content")));
                FormalExpression contentToExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                contentToExpression.setBody(foundInput.getId());
                a.setFrom(contentFromExpression);
                a.setTo(contentToExpression);
                dia.getAssignment().add(a);
                task.getDataInputAssociations().add(dia);
            }
        }
        if (properties.get("locale") != null && properties.get("locale").length() > 0) {
            if (task.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                task.setIoSpecification(iospec);
            }
            List<DataInput> dataInputs = task.getIoSpecification().getDataInputs();
            boolean foundLocaleInput = false;
            DataInput foundInput = null;
            for (DataInput din : dataInputs) {
                if (din.getName().equals("Locale")) {
                    foundLocaleInput = true;
                    foundInput = din;
                    break;
                }
            }
            if (!foundLocaleInput) {
                DataInput d = Bpmn2Factory.eINSTANCE.createDataInput();
                d.setId(task.getId() + "_" + "Locale" + "InputX");
                d.setName("Locale");
                task.getIoSpecification().getDataInputs().add(d);
                foundInput = d;
                if (task.getIoSpecification().getInputSets() == null || task.getIoSpecification().getInputSets().size() < 1) {
                    InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                    task.getIoSpecification().getInputSets().add(inset);
                }
                task.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(d);
            }
            boolean foundLocaleAssociation = false;
            List<DataInputAssociation> inputAssociations = task.getDataInputAssociations();
            for (DataInputAssociation da : inputAssociations) {
                if (da.getTargetRef() != null && da.getTargetRef().getId().equals(foundInput.getId())) {
                    foundLocaleAssociation = true;
                    ((FormalExpression) da.getAssignment().get(0).getFrom()).setBody(wrapInCDATABlock(properties.get("locale")));
                }
            }
            if (!foundLocaleAssociation) {
                DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                dia.setTargetRef(foundInput);
                Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                FormalExpression localeFromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                localeFromExpression.setBody(wrapInCDATABlock(properties.get("locale")));
                FormalExpression localeToExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                localeToExpression.setBody(foundInput.getId());
                a.setFrom(localeFromExpression);
                a.setTo(localeToExpression);
                dia.getAssignment().add(a);
                task.getDataInputAssociations().add(dia);
            }
        }
        if (properties.get("createdby") != null && properties.get("createdby").length() > 0) {
            if (task.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                task.setIoSpecification(iospec);
            }
            List<DataInput> dataInputs = task.getIoSpecification().getDataInputs();
            boolean foundCreatedByInput = false;
            DataInput foundInput = null;
            for (DataInput din : dataInputs) {
                if (din.getName().equals("CreatedBy")) {
                    foundCreatedByInput = true;
                    foundInput = din;
                    break;
                }
            }
            if (!foundCreatedByInput) {
                DataInput d = Bpmn2Factory.eINSTANCE.createDataInput();
                d.setId(task.getId() + "_" + "CreatedBy" + "InputX");
                d.setName("CreatedBy");
                task.getIoSpecification().getDataInputs().add(d);
                foundInput = d;
                if (task.getIoSpecification().getInputSets() == null || task.getIoSpecification().getInputSets().size() < 1) {
                    InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                    task.getIoSpecification().getInputSets().add(inset);
                }
                task.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(d);
            }
            boolean foundCreatedByAssociation = false;
            List<DataInputAssociation> inputAssociations = task.getDataInputAssociations();
            for (DataInputAssociation da : inputAssociations) {
                if (da.getTargetRef() != null && da.getTargetRef().getId().equals(foundInput.getId())) {
                    foundCreatedByAssociation = true;
                    ((FormalExpression) da.getAssignment().get(0).getFrom()).setBody(wrapInCDATABlock(properties.get("createdby")));
                }
            }
            if (!foundCreatedByAssociation) {
                DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                dia.setTargetRef(foundInput);
                Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                FormalExpression createdByFromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                createdByFromExpression.setBody(wrapInCDATABlock(properties.get("createdby")));
                FormalExpression createdByToExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                createdByToExpression.setBody(foundInput.getId());
                a.setFrom(createdByFromExpression);
                a.setTo(createdByToExpression);
                dia.getAssignment().add(a);
                task.getDataInputAssociations().add(dia);
            }
        }
        // reassignments
        if (properties.get("reassignment") != null && properties.get("reassignment").length() > 0) {
            if (task.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                task.setIoSpecification(iospec);
            }
            List<DataInput> dataInputs = task.getIoSpecification().getDataInputs();
            boolean foundNotCompletedReassignmentsInput = false;
            boolean foundNotStartedReassignmentsInput = false;
            DataInput foundNotCompletedDataInput = null;
            DataInput foundNotStartedDataInput = null;
            for (DataInput din : dataInputs) {
                if (din.getName().equals("NotCompletedReassign")) {
                    foundNotCompletedReassignmentsInput = true;
                    foundNotCompletedDataInput = din;
                }
                if (din.getName().equals("NotStartedReassign")) {
                    foundNotStartedReassignmentsInput = true;
                    foundNotStartedDataInput = din;
                }
            }
            if (!foundNotCompletedReassignmentsInput) {
                DataInput d = Bpmn2Factory.eINSTANCE.createDataInput();
                d.setId(task.getId() + "_" + "NotCompletedReassign" + "InputX");
                d.setName("NotCompletedReassign");
                task.getIoSpecification().getDataInputs().add(d);
                foundNotCompletedDataInput = d;
                if (task.getIoSpecification().getInputSets() == null || task.getIoSpecification().getInputSets().size() < 1) {
                    InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                    task.getIoSpecification().getInputSets().add(inset);
                }
                task.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(d);
            }
            if (!foundNotStartedReassignmentsInput) {
                DataInput d = Bpmn2Factory.eINSTANCE.createDataInput();
                d.setId(task.getId() + "_" + "NotStartedReassign" + "InputX");
                d.setName("NotStartedReassign");
                task.getIoSpecification().getDataInputs().add(d);
                foundNotStartedDataInput = d;
                if (task.getIoSpecification().getInputSets() == null || task.getIoSpecification().getInputSets().size() < 1) {
                    InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                    task.getIoSpecification().getInputSets().add(inset);
                }
                task.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(d);
            }
            boolean foundNotCompletedReassignmentAssociation = false;
            boolean foundNotStartedReassignmentAssociation = false;
            List<DataInputAssociation> inputAssociations = task.getDataInputAssociations();
            for (DataInputAssociation da : inputAssociations) {
                if (da.getTargetRef() != null && da.getTargetRef().getId().equals(foundNotCompletedDataInput.getId())) {
                    foundNotCompletedReassignmentAssociation = true;
                    ((FormalExpression) da.getAssignment().get(0).getFrom()).setBody(getReassignmentsAndNotificationsForType(properties.get("reassignment"),
                                                                                                                             "not-completed"));
                }
                if (da.getTargetRef() != null && da.getTargetRef().getId().equals(foundNotStartedDataInput.getId())) {
                    foundNotStartedReassignmentAssociation = true;
                    ((FormalExpression) da.getAssignment().get(0).getFrom()).setBody(getReassignmentsAndNotificationsForType(properties.get("reassignment"),
                                                                                                                             "not-started"));
                }
            }
            if (!foundNotCompletedReassignmentAssociation) {
                DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                dia.setTargetRef(foundNotCompletedDataInput);
                Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                FormalExpression notCompletedFromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                notCompletedFromExpression.setBody(getReassignmentsAndNotificationsForType(properties.get("reassignment"),
                                                                                           "not-completed"));
                FormalExpression notCompletedToExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                notCompletedToExpression.setBody(foundNotCompletedDataInput.getId());
                a.setFrom(notCompletedFromExpression);
                a.setTo(notCompletedToExpression);
                dia.getAssignment().add(a);
                task.getDataInputAssociations().add(dia);
            }
            if (!foundNotStartedReassignmentAssociation) {
                DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                dia.setTargetRef(foundNotStartedDataInput);
                Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                FormalExpression notStartedFromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                notStartedFromExpression.setBody(getReassignmentsAndNotificationsForType(properties.get("reassignment"),
                                                                                         "not-started"));
                FormalExpression notStartedToExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                notStartedToExpression.setBody(foundNotStartedDataInput.getId());
                a.setFrom(notStartedFromExpression);
                a.setTo(notStartedToExpression);
                dia.getAssignment().add(a);
                task.getDataInputAssociations().add(dia);
            }
        }
        // end reassignments
        // start notifications
        if (properties.get("notifications") != null && properties.get("notifications").length() > 0) {
            if (task.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                task.setIoSpecification(iospec);
            }
            List<DataInput> dataInputs = task.getIoSpecification().getDataInputs();
            boolean foundNotCompletedNotificationsInput = false;
            boolean foundNotStartedNotificationsInput = false;
            DataInput foundNotCompletedDataInput = null;
            DataInput foundNotStartedDataInput = null;
            for (DataInput din : dataInputs) {
                if (din.getName().equals("NotCompletedNotify")) {
                    foundNotCompletedNotificationsInput = true;
                    foundNotCompletedDataInput = din;
                }
                if (din.getName().equals("NotStartedNotify")) {
                    foundNotStartedNotificationsInput = true;
                    foundNotStartedDataInput = din;
                }
            }
            if (!foundNotCompletedNotificationsInput) {
                DataInput d = Bpmn2Factory.eINSTANCE.createDataInput();
                d.setId(task.getId() + "_" + "NotCompletedNotify" + "InputX");
                d.setName("NotCompletedNotify");
                task.getIoSpecification().getDataInputs().add(d);
                foundNotCompletedDataInput = d;
                if (task.getIoSpecification().getInputSets() == null || task.getIoSpecification().getInputSets().size() < 1) {
                    InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                    task.getIoSpecification().getInputSets().add(inset);
                }
                task.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(d);
            }
            if (!foundNotStartedNotificationsInput) {
                DataInput d = Bpmn2Factory.eINSTANCE.createDataInput();
                d.setId(task.getId() + "_" + "NotStartedNotify" + "InputX");
                d.setName("NotStartedNotify");
                task.getIoSpecification().getDataInputs().add(d);
                foundNotStartedDataInput = d;
                if (task.getIoSpecification().getInputSets() == null || task.getIoSpecification().getInputSets().size() < 1) {
                    InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                    task.getIoSpecification().getInputSets().add(inset);
                }
                task.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(d);
            }
            boolean foundNotCompletedNotificationAssociation = false;
            boolean foundNotStartedNotificationAssociation = false;
            List<DataInputAssociation> inputAssociations = task.getDataInputAssociations();
            for (DataInputAssociation da : inputAssociations) {
                if (da.getTargetRef() != null && da.getTargetRef().getId().equals(foundNotCompletedDataInput.getId())) {
                    foundNotCompletedNotificationAssociation = true;
                    ((FormalExpression) da.getAssignment().get(0).getFrom()).setBody(getReassignmentsAndNotificationsForType(properties.get("notifications"),
                                                                                                                             "not-completed"));
                }
                if (da.getTargetRef() != null && da.getTargetRef().getId().equals(foundNotStartedDataInput.getId())) {
                    foundNotStartedNotificationAssociation = true;
                    ((FormalExpression) da.getAssignment().get(0).getFrom()).setBody(getReassignmentsAndNotificationsForType(properties.get("notifications"),
                                                                                                                             "not-started"));
                }
            }
            if (!foundNotCompletedNotificationAssociation) {
                DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                dia.setTargetRef(foundNotCompletedDataInput);
                Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                FormalExpression notCompletedFromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                notCompletedFromExpression.setBody(getReassignmentsAndNotificationsForType(properties.get("notifications"),
                                                                                           "not-completed"));
                FormalExpression notCompletedToExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                notCompletedToExpression.setBody(foundNotCompletedDataInput.getId());
                a.setFrom(notCompletedFromExpression);
                a.setTo(notCompletedToExpression);
                dia.getAssignment().add(a);
                task.getDataInputAssociations().add(dia);
            }
            if (!foundNotStartedNotificationAssociation) {
                DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                dia.setTargetRef(foundNotStartedDataInput);
                Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                FormalExpression notStartedFromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                notStartedFromExpression.setBody(getReassignmentsAndNotificationsForType(properties.get("notifications"),
                                                                                         "not-started"));
                FormalExpression notStartedToExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                notStartedToExpression.setBody(foundNotStartedDataInput.getId());
                a.setFrom(notStartedFromExpression);
                a.setTo(notStartedToExpression);
                dia.getAssignment().add(a);
                task.getDataInputAssociations().add(dia);
            }
        }
        // end notifications
        // revisit data assignments
        if (task.getDataInputAssociations() != null) {
            List<DataInputAssociation> dataInputAssociations = task.getDataInputAssociations();
            List<DataInputAssociation> incompleteAssociations = new ArrayList<DataInputAssociation>();
            for (DataInputAssociation dia : dataInputAssociations) {
                DataInput targetInput = (DataInput) dia.getTargetRef();
                if (targetInput != null && targetInput.getName() != null) {
                    if (targetInput.getName().equals("GroupId") && (properties.get("groupid") == null || properties.get("groupid").length() == 0)) {
                        incompleteAssociations.add(dia);
                    } else if (targetInput.getName().equalsIgnoreCase("Skippable") && (skippableStr == null || skippableStr.length() == 0)) {
                        incompleteAssociations.add(dia);
                    } else if (targetInput.getName().equalsIgnoreCase("Comment") && (properties.get("subject") == null || properties.get("subject").length() == 0)) {
                        incompleteAssociations.add(dia);
                    } else if (targetInput.getName().equalsIgnoreCase("Description") && (properties.get("description") == null || properties.get("description").length() == 0)) {
                        incompleteAssociations.add(dia);
                    } else if (targetInput.getName().equalsIgnoreCase("Priority") && (properties.get("priority") == null || properties.get("priority").length() == 0)) {
                        incompleteAssociations.add(dia);
                    } else if (targetInput.getName().equalsIgnoreCase("Content") && (properties.get("content") == null || properties.get("content").length() == 0)) {
                        incompleteAssociations.add(dia);
                    } else if (targetInput.getName().equalsIgnoreCase("Locale") && (properties.get("locale") == null || properties.get("locale").length() == 0)) {
                        incompleteAssociations.add(dia);
                    } else if (targetInput.getName().equalsIgnoreCase("CreatedBy") && (properties.get("createdby") == null || properties.get("createdby").length() == 0)) {
                        incompleteAssociations.add(dia);
                    } else if (targetInput.getName().equalsIgnoreCase("NotCompletedReassign") && (properties.get("reassignment") == null || properties.get("reassignment").length() == 0)) {
                        incompleteAssociations.add(dia);
                    } else if (targetInput.getName().equalsIgnoreCase("NotStartedReassign") && (properties.get("reassignment") == null || properties.get("reassignment").length() == 0)) {
                        incompleteAssociations.add(dia);
                    } else if (targetInput.getName().equalsIgnoreCase("NotCompletedNotify") && (properties.get("notifications") == null || properties.get("notifications").length() == 0)) {
                        incompleteAssociations.add(dia);
                    } else if (targetInput.getName().equalsIgnoreCase("NotStartedNotify") && (properties.get("notifications") == null || properties.get("notifications").length() == 0)) {
                        incompleteAssociations.add(dia);
                    }
                }
            }
            for (DataInputAssociation tr : incompleteAssociations) {
                if (task.getDataInputAssociations() != null) {
                    task.getDataInputAssociations().remove(tr);
                }
            }
        }
        List<DataInput> toRemoveDataInputs = new ArrayList<DataInput>();
        if (task.getIoSpecification() != null && task.getIoSpecification().getDataInputs() != null) {
            List<DataInput> taskDataInputs = task.getIoSpecification().getDataInputs();
            for (DataInput din : taskDataInputs) {
                if (din.getName().equals("GroupId") && (properties.get("groupid") == null || properties.get("groupid").length() == 0)) {
                    toRemoveDataInputs.add(din);
                } else if (din.getName().equalsIgnoreCase("Skippable") && (skippableStr == null || skippableStr.length() == 0)) {
                    toRemoveDataInputs.add(din);
                } else if (din.getName().equalsIgnoreCase("Comment") && (properties.get("subject") == null || properties.get("subject").length() == 0)) {
                    toRemoveDataInputs.add(din);
                } else if (din.getName().equalsIgnoreCase("Description") && (properties.get("description") == null || properties.get("description").length() == 0)) {
                    toRemoveDataInputs.add(din);
                } else if (din.getName().equalsIgnoreCase("Priority") && (properties.get("priority") == null || properties.get("priority").length() == 0)) {
                    toRemoveDataInputs.add(din);
                } else if (din.getName().equalsIgnoreCase("Content") && (properties.get("content") == null || properties.get("content").length() == 0)) {
                    toRemoveDataInputs.add(din);
                } else if (din.getName().equalsIgnoreCase("Locale") && (properties.get("locale") == null || properties.get("locale").length() == 0)) {
                    toRemoveDataInputs.add(din);
                } else if (din.getName().equalsIgnoreCase("CreatedBy") && (properties.get("createdby") == null || properties.get("createdby").length() == 0)) {
                    toRemoveDataInputs.add(din);
                } else if (din.getName().equalsIgnoreCase("NotCompletedReassign") && (properties.get("reassignment") == null || properties.get("reassignment").length() == 0)) {
                    toRemoveDataInputs.add(din);
                } else if (din.getName().equalsIgnoreCase("NotStartedReassign") && (properties.get("reassignment") == null || properties.get("reassignment").length() == 0)) {
                    toRemoveDataInputs.add(din);
                } else if (din.getName().equalsIgnoreCase("NotCompletedNotify") && (properties.get("notifications") == null || properties.get("notifications").length() == 0)) {
                    toRemoveDataInputs.add(din);
                } else if (din.getName().equalsIgnoreCase("NotStartedNotify") && (properties.get("notifications") == null || properties.get("notifications").length() == 0)) {
                    toRemoveDataInputs.add(din);
                }
            }
        }
        for (DataInput trdin : toRemoveDataInputs) {
            if (task.getIoSpecification() != null && task.getIoSpecification().getDataInputs() != null) {
                if (task.getIoSpecification().getInputSets().size() > 0) {
                    task.getIoSpecification().getInputSets().get(0).getDataInputRefs().remove(trdin);
                }
            }
            task.getIoSpecification().getDataInputs().remove(trdin);
        }
        // simulation properties
        ResourceParameters resourceParameters = BpsimFactory.eINSTANCE.createResourceParameters();
        if (properties.get("quantity") != null && properties.get("quantity").length() > 0) {
            Parameter quantityParam = BpsimFactory.eINSTANCE.createParameter();
            FloatingParameterType quantityValueParam = BpsimFactory.eINSTANCE.createFloatingParameterType();
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            quantityValueParam.setValue(Double.valueOf(twoDForm.format(Double.valueOf(properties.get("quantity")))));
            quantityParam.getParameterValue().add(quantityValueParam);
            resourceParameters.setQuantity(quantityParam);
        }
        if (properties.get("workinghours") != null && properties.get("workinghours").length() > 0) {
            Parameter workingHoursParam = BpsimFactory.eINSTANCE.createParameter();
            FloatingParameterType workingHoursValueParam = BpsimFactory.eINSTANCE.createFloatingParameterType();
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            workingHoursValueParam.setValue(Double.valueOf(twoDForm.format(Double.valueOf(properties.get("workinghours")))));
            workingHoursParam.getParameterValue().add(workingHoursValueParam);
            resourceParameters.setAvailability(workingHoursParam);
        }
        if (_simulationElementParameters.containsKey(task.getId())) {
            _simulationElementParameters.get(task.getId()).add(resourceParameters);
        } else {
            List<EObject> values = new ArrayList<EObject>();
            values.add(resourceParameters);
            _simulationElementParameters.put(task.getId(),
                                             values);
        }
    }

    /*
     * Set LastUserTaskID variable so that UserTasks with no name
     * can be given a default name of "Task_n"
     *
     * @param task
     */
    protected void setLastUserTaskID(UserTask task) {
        String name = task.getName();
        if (name != null && name.length() > 0) {
            int i = name.indexOf(DEFAULT_USERTASK_NAME_PREFIX);
            if (i == 0) {
                String numStr = name.substring(5);
                try {
                    int num = Integer.parseInt(numStr);
                    if (num >= LastUserTaskID) {
                        LastUserTaskID = num;
                    }
                } catch (NumberFormatException nfe) {
                    // do nothing
                }
            }
        }
    }

    protected void applyGatewayProperties(Gateway gateway,
                                          Map<String, String> properties) {
        if (properties.get("name") != null && properties.get("name").length() > 0) {
            gateway.setName(StringEscapeUtils.escapeXml(properties.get("name")).replaceAll("\\r\\n|\\r|\\n",
                                                                                           " "));
            // add unescaped and untouched name value as extension element as well
            Utils.setMetaDataExtensionValue(gateway,
                                            "elementname",
                                            wrapInCDATABlock(properties.get("name").replaceAll("\\\\n",
                                                                                               "\n")));
        } else {
            gateway.setName("");
        }
        if (properties.get("defaultgate") != null && (gateway instanceof InclusiveGateway || gateway instanceof ExclusiveGateway)) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "dg",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("defaultgate"));
            gateway.getAnyAttribute().add(extensionEntry);
        }
    }

    protected void applySequenceFlowProperties(SequenceFlow sequenceFlow,
                                               Map<String, String> properties) {
        // sequence flow name is options
        if (properties.get("name") != null && !"".equals(properties.get("name"))) {
            sequenceFlow.setName(StringEscapeUtils.escapeXml(properties.get("name")).replaceAll("\\r\\n|\\r|\\n",
                                                                                                " "));
            // add unescaped and untouched name value as extension eleent as well
            Utils.setMetaDataExtensionValue(sequenceFlow,
                                            "elementname",
                                            wrapInCDATABlock(properties.get("name").replaceAll("\\\\n",
                                                                                               "\n")));
        }
        if (properties.get("bgcolor") != null && properties.get("bgcolor").length() > 0) {
            if (!(_elementColors.containsKey(sequenceFlow.getId()))) {
                List<String> colorsList = new ArrayList<String>();
                colorsList.add("bgcolor:" + properties.get("bgcolor"));
                _elementColors.put(sequenceFlow.getId(),
                                   colorsList);
            } else {
                _elementColors.get(sequenceFlow.getId()).add("bgcolor:" + properties.get("bgcolor"));
            }
        }
        if (properties.get("bordercolor") != null && properties.get("bordercolor").length() > 0) {
            if (!(_elementColors.containsKey(sequenceFlow.getId()))) {
                List<String> colorsList = new ArrayList<String>();
                colorsList.add("bordercolor:" + properties.get("bordercolor"));
                _elementColors.put(sequenceFlow.getId(),
                                   colorsList);
            } else {
                _elementColors.get(sequenceFlow.getId()).add("bordercolor:" + properties.get("bordercolor"));
            }
        }
        if (properties.get("fontsize") != null && properties.get("fontsize").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "fontsize",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("fontsize"));
            sequenceFlow.getAnyAttribute().add(extensionEntry);
        }
        if (properties.get("fontcolor") != null && properties.get("fontcolor").length() > 0) {
            if (!(_elementColors.containsKey(sequenceFlow.getId()))) {
                List<String> colorsList = new ArrayList<String>();
                colorsList.add("fontcolor:" + properties.get("fontcolor"));
                _elementColors.put(sequenceFlow.getId(),
                                   colorsList);
            } else {
                _elementColors.get(sequenceFlow.getId()).add("fontcolor:" + properties.get("fontcolor"));
            }
        }

        // Custom extended auto connection property for Stunner.
        String sourceConnAutoPropertyName = Bpmn2OryxManager.MAGNET_AUTO_CONNECTION +
                Bpmn2OryxManager.SOURCE;
        String sourceConnAutoRaw = properties.get(sourceConnAutoPropertyName);
        if (null != sourceConnAutoRaw && Boolean.TRUE.equals(Boolean.parseBoolean(sourceConnAutoRaw))) {
            Utils.setMetaDataExtensionValue(sequenceFlow,
                                            sourceConnAutoPropertyName,
                                            Boolean.toString(true));
        }
        String targetConnAutoPropertyName = Bpmn2OryxManager.MAGNET_AUTO_CONNECTION +
                Bpmn2OryxManager.TARGET;
        String targetConnAutoRaw = properties.get(targetConnAutoPropertyName);
        if (null != targetConnAutoRaw && Boolean.TRUE.equals(Boolean.parseBoolean(targetConnAutoRaw))) {
            Utils.setMetaDataExtensionValue(sequenceFlow,
                                            targetConnAutoPropertyName,
                                            Boolean.toString(true));
        }

        if (properties.get("isselectable") != null && properties.get("isselectable").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "selectable",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(extensionAttribute,
                                                                             properties.get("isselectable"));
            sequenceFlow.getAnyAttribute().add(extensionEntry);
        }
        if (properties.get("auditing") != null && !"".equals(properties.get("auditing"))) {
            Auditing audit = Bpmn2Factory.eINSTANCE.createAuditing();
            audit.getDocumentation().add(createDocumentation(properties.get("auditing")));
            sequenceFlow.setAuditing(audit);
        }
        applySequenceFlowCondition(sequenceFlow,
                                   properties);
        if (properties.get("priority") != null && !"".equals(properties.get("priority"))) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl priorityElement = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools",
                    "priority",
                    false,
                    false);
            SimpleFeatureMapEntry extensionEntry = new SimpleFeatureMapEntry(priorityElement,
                                                                             properties.get("priority"));
            sequenceFlow.getAnyAttribute().add(extensionEntry);
        }
        if (properties.get("monitoring") != null && !"".equals(properties.get("monitoring"))) {
            Monitoring monitoring = Bpmn2Factory.eINSTANCE.createMonitoring();
            monitoring.getDocumentation().add(createDocumentation(properties.get("monitoring")));
            sequenceFlow.setMonitoring(monitoring);
        }
        sequenceFlow.setIsImmediate(Boolean.parseBoolean(properties.get("isimmediate")));
        // simulation properties
        if (properties.get("probability") != null && properties.get("probability").length() > 0) {
            ControlParameters controlParams = BpsimFactory.eINSTANCE.createControlParameters();
            Parameter probParam = BpsimFactory.eINSTANCE.createParameter();
            FloatingParameterType probParamValueParam = BpsimFactory.eINSTANCE.createFloatingParameterType();
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            probParamValueParam.setValue(Double.valueOf(twoDForm.format(Double.valueOf(properties.get("probability")))));
            probParam.getParameterValue().add(probParamValueParam);
            controlParams.setProbability(probParam);
            if (_simulationElementParameters.containsKey(sequenceFlow.getId())) {
                _simulationElementParameters.get(sequenceFlow.getId()).add(controlParams);
            } else {
                List<EObject> values = new ArrayList<EObject>();
                values.add(controlParams);
                _simulationElementParameters.put(sequenceFlow.getId(),
                                                 values);
            }
        }
    }

    private Map<String, String> unmarshallProperties(JsonParser parser) throws JsonParseException, IOException {
        Map<String, String> properties = new HashMap<String, String>();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = parser.getCurrentName();
            parser.nextToken();
            properties.put(fieldname,
                           parser.getText());
        }
        return properties;
    }

    private Documentation createDocumentation(String text) {
        Documentation doc = Bpmn2Factory.eINSTANCE.createDocumentation();
        doc.setText(text);
        return doc;
    }

    private boolean isCustomElement(String taskType,
                                    String preProcessingData) {
        if (taskType != null && taskType.length() > 0 && preProcessingData != null && preProcessingData.length() > 0) {
            String[] preProcessingDataElements = preProcessingData.split(",\\s*");
            for (String preProcessingDataElement : preProcessingDataElements) {
                if (taskType.equals(preProcessingDataElement)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected BaseElement createBaseElement(String stencil,
                                            String taskType,
                                            boolean customElement) {
        return Bpmn20Stencil.createElement(stencil,
                                           taskType,
                                           customElement);
    }

    protected String wrapInCDATABlock(String value) {
        return "<![CDATA[" + value + "]]>";
    }

    private String getReassignmentsAndNotificationsForType(String inputStr,
                                                           String type) {
        String[] parts = inputStr.split("\\^\\s*");
        String ret = "";
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].endsWith("^")) {
                parts[i] = parts[i].substring(0,
                                              parts[i].length() - 1);
            }
            if (parts[i].endsWith("@" + type)) {
                ret += parts[i].substring(0,
                                          parts[i].length() - ("@" + type).length());
                ret += "^";
            }
        }
        if (ret.endsWith("^")) {
            ret = ret.substring(0,
                                ret.length() - 1);
        }
        return wrapInCDATABlock(ret);
    }

    private String decodeAssociationValue(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        try {
            return URLDecoder.decode(s,
                                     "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    private void parseAssignmentsInfo(Map<String, String> properties) {
        if (properties != null && properties.get("assignmentsinfo") != null) {
            String assignmentsinfo = properties.get("assignmentsinfo");
            String[] parts = assignmentsinfo.split("\\|");
            if (parts.length > 0 && parts[0] != null && parts[0].length() > 0) {
                properties.put("datainput",
                               parts[0]);
                if (parts.length > 4 && parts[4] != null && parts[4].length() > 0) {
                    properties.put("datainputassociations",
                                   parts[4]);
                }
            }
            if (parts.length > 1 && parts[1] != null && parts[1].length() > 0) {
                properties.put("datainputset",
                               parts[1]);
                if (parts.length > 4 && parts[4] != null && parts[4].length() > 0) {
                    properties.put("assignments",
                                   parts[4]);
                }
            }
            if (parts.length > 2 && parts[2] != null && parts[2].length() > 0) {
                properties.put("dataoutput",
                               parts[2]);
                if (parts.length > 4 && parts[4] != null && parts[4].length() > 0) {
                    properties.put("dataoutputassociations",
                                   parts[4]);
                }
            }
            if (parts.length > 3 && parts[3] != null && parts[3].length() > 0) {
                properties.put("dataoutputset",
                               parts[3]);
                if (parts.length > 4 && parts[4] != null && parts[4].length() > 0) {
                    properties.put("assignments",
                                   parts[4]);
                }
            }
        }
    }

    protected void addSubprocessItemDefs(ItemDefinition itemdef) {
        _subprocessItemDefs.put(itemdef.getId(), itemdef);
    }
}

