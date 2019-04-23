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

package org.kie.workbench.common.stunner.bpmn.client.documentation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.IsElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.components.palette.BPMNCategoryDefinitionProvider;
import org.kie.workbench.common.stunner.bpmn.client.documentation.decorator.PropertyDecorators;
import org.kie.workbench.common.stunner.bpmn.client.shape.factory.BPMNShapeFactory;
import org.kie.workbench.common.stunner.bpmn.client.workitem.WorkItemDefinitionClientRegistry;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Actors;
import org.kie.workbench.common.stunner.bpmn.definition.property.assignee.Groupid;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseManagementSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.connectors.Priority;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.GlobalVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessInstanceDescription;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Version;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.execution.EmbeddedSubprocessExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocAutostart;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Content;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.CreatedBy;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Description;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCompletionCondition;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnEntryAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.OnExitAction;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Skippable;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.Subject;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskName;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskType;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.bpmn.documentation.model.BPMNDocumentation;
import org.kie.workbench.common.stunner.bpmn.documentation.model.element.Element;
import org.kie.workbench.common.stunner.bpmn.documentation.model.element.ElementTotal;
import org.kie.workbench.common.stunner.bpmn.documentation.model.general.General;
import org.kie.workbench.common.stunner.bpmn.documentation.model.general.ProcessOverview;
import org.kie.workbench.common.stunner.bpmn.documentation.model.general.ProcessVariablesTotal;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.shape.ImageStripGlyph;
import org.kie.workbench.common.stunner.core.client.util.js.KeyValue;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertySetAdapter;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.model.DocumentationOutput;
import org.kie.workbench.common.stunner.core.documentation.model.HTMLDocumentationTemplate;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.uberfire.ext.editor.commons.client.template.mustache.ClientMustacheTemplateRenderer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ClientBPMNDocumentationServiceTest {

    private static final String VARIABLES = "PV1:java.lang.String,PV2:java.lang.Boolean";
    private static final String GLOBAL_VARIABLES = "GL1:java.lang.String,GL2:java.lang.Boolean";
    private static final String SUB_PROCESS_VARIABLES = "SUBPV1:java.lang.String,SUBPV2:java.lang.Boolean";
    public static final String PROCESS_NAME = "processTest";
    public static final String PROCESS_DOCUMENTATION = "Process Documentation\nLine2";
    public static final String PROCESS_VERSION = "1.0";
    public static final String PROCESS_DECRIPTION = "Process Description";
    public static final String PROCESS_UUID = UUID.uuid();
    public static final String TASK_NAME = "Task";
    public static final String TASK_DOC = "Task Doc";
    public static final String TASK_PRIORITY = "1";
    public static final String TASK_SUBJECT = "Task Subject";
    public static final String TASK_DESCRIPTION = "Task Description";
    public static final String SUB_PROCESS_NAME = "Sub-process";
    public static final String RENDERED_DOC = "rendered";
    public static final String TASK_DOCUMENTATION = "TASK DOC";
    public static final String PROCESS_PACKAGE = "";
    public static final Boolean PROCESS_IS_EXECUTABLE = true;
    public static final Boolean PROCESS_IS_ADHOC = false;
    public static final String TASK_GROUPID = "groupId";
    public static final String DOCUMENTATION_CAPTION = "DOCUMENTATION CAPTION";
    public static final String NAME_CAPTION = "NAME_CAPTION";
    public static final String SUBJECT_CAPTION = "SUBJECT_CAPTION";
    public static final String GROUP_CAPTION = "GROUP_CAPTION";
    public static final String PRIORITY_CAPTION = "PRIORITY_CAPTION";
    public static final String DESCRIPTION_CAPTION = "DESCRIPTION_CAPTION";
    public static final String SCRIPT = "SCRIPT";
    public static final String LANGUAGE = "java";
    public static final String ON_ENTRY_ACTION = LANGUAGE + ":" + SCRIPT;
    public static final String ON_EXIT_ACTION = LANGUAGE + ":" + SCRIPT;
    public static final String SUB_PROCESS_DOCUMENTATION = "SUB_PROCESS_DOCUMENTATION";
    public static final String ON_ENTRY_CAPTION = "ONENTRY_CAPTION";
    private static final String ISASYNC_CAPTION = "ISASYNC_CAPTION";
    private static final String ON_EXIT_CAPTION = "ONEXIT_CAPTION";
    public static final String TEMPLATE = "documentationTemplate";
    private static final String ICON_HTML = "icon image";
    public static final String ASSIGNEMNTS = "assignemnts";
    private static final String ASSIGNEMNTS_CAPTION = "ASSIGNEMNTS_CAPTION";

    private ClientBPMNDocumentationService tested;

    @Mock
    private ClientMustacheTemplateRenderer mustacheTemplateRenderer;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private BPMNShapeFactory shapeFactory;

    @Mock
    private CanvasFileExport canvasFileExport;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private BPMNCategoryDefinitionProvider categoryDefinitionProvider;

    @Mock
    private DOMGlyphRenderers glyphRenderer;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private WorkItemDefinitionClientRegistry workItemDefinitionClientRegistry;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Mock
    private AdapterManager adapters;

    private List<Node> nodes;

    private BPMNDiagramImpl bpmnDiagram;

    private DiagramSet diagramSet;

    private Name processName;

    private Documentation processDocumentation;

    private Id processId;

    private Package packageProperty;

    private Version version;

    private AdHoc adHoc;

    private ProcessInstanceDescription processInstanceDescription;

    private Executable executable;

    private ProcessData processData;

    private ProcessVariables processVariables;

    private UserTask userTask;

    private EmbeddedSubprocess embeddedSubprocess;

    private GlobalVariables globalVariables;

    @Mock
    private DefinitionAdapter<Object> definitionAdapter;

    @Mock
    private DefinitionSetAdapter<Object> definitionSetAdapter;

    @Mock
    private PropertyAdapter<Object, Object> propertyAdapter;

    @Mock
    private PropertySetAdapter<Object> propertySetAdapter;

    @Mock
    private PropertyDecorators decorators;

    private Function<String, Glyph> glyphProvider;

    @Mock
    private Glyph glyphIcon;

    @Mock
    private IsElement iconElement;

    @Mock
    private org.jboss.errai.common.client.dom.HTMLElement iconHTMLElement;

    @Mock
    private ImageStripGlyph imageStripGlyph;

    @Mock
    private DefinitionId userTaskId;

    @Mock
    private DefinitionId subprocessId;

    @Before
    public void setUp() throws Exception {

        //DiagramSet
        processName = new Name(PROCESS_NAME);
        processDocumentation = new Documentation(PROCESS_DOCUMENTATION);
        packageProperty = new Package(PROCESS_PACKAGE);
        version = new Version(PROCESS_VERSION);
        adHoc = new AdHoc(PROCESS_IS_ADHOC);
        processInstanceDescription = new ProcessInstanceDescription(PROCESS_DECRIPTION);
        executable = new Executable(PROCESS_IS_EXECUTABLE);
        processId = new Id(PROCESS_UUID);
        globalVariables = new GlobalVariables(GLOBAL_VARIABLES);
        diagramSet = new DiagramSet(processName, processDocumentation, processId,
                                    packageProperty, version, adHoc,
                                    processInstanceDescription, globalVariables, executable);
        //ProcessData
        processVariables = new ProcessVariables(VARIABLES);
        processData = new ProcessData(processVariables);

        bpmnDiagram = new BPMNDiagramImpl(
                diagramSet,
                processData,
                new CaseManagementSet(),
                new BackgroundSet(),
                new FontSet(),
                new RectangleDimensionsSet()
        );

        userTask = new UserTask(new TaskGeneralSet(new Name(TASK_NAME),
                                                   new Documentation(TASK_DOC)),
                                new UserTaskExecutionSet(new TaskName(TASK_NAME),
                                                         new Actors(),
                                                         new Groupid(TASK_GROUPID),
                                                         new AssignmentsInfo(),
                                                         new IsAsync(true),
                                                         new Skippable(),
                                                         new Priority(TASK_PRIORITY),
                                                         new Subject(TASK_SUBJECT),
                                                         new Description(TASK_DESCRIPTION),
                                                         new CreatedBy(),
                                                         new AdHocAutostart(true),
                                                         new IsMultipleInstance(false),
                                                         new MultipleInstanceCollectionInput(),
                                                         new MultipleInstanceDataInput(),
                                                         new MultipleInstanceCollectionOutput(),
                                                         new MultipleInstanceDataOutput(),
                                                         new MultipleInstanceCompletionCondition(),
                                                         new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue())),
                                                         new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue())),
                                                         new Content(),
                                                         new SLADueDate()),
                                new BackgroundSet(),
                                new FontSet(),
                                new RectangleDimensionsSet(),
                                new SimulationSet(),
                                new TaskType(TaskTypes.USER));

        embeddedSubprocess = new EmbeddedSubprocess(new BPMNGeneralSet(new Name(SUB_PROCESS_NAME),
                                                                       new Documentation(SUB_PROCESS_DOCUMENTATION)),
                                                    new BackgroundSet(),
                                                    new FontSet(),
                                                    new RectangleDimensionsSet(),
                                                    new SimulationSet(),
                                                    new EmbeddedSubprocessExecutionSet(new OnEntryAction(new ScriptTypeListValue().addValue(new ScriptTypeValue(LANGUAGE,
                                                                                                                                                                SCRIPT))),
                                                                                       new OnExitAction(new ScriptTypeListValue().addValue(new ScriptTypeValue(LANGUAGE,
                                                                                                                                                               SCRIPT))),
                                                                                       new IsAsync(false)),
                                                    new ProcessData(SUB_PROCESS_VARIABLES));

        nodes = Arrays.asList(createNode(bpmnDiagram), createNode(userTask), createNode(embeddedSubprocess));

        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(nodes);

        when(mustacheTemplateRenderer.render(eq(TEMPLATE), any(BPMNDocumentation.class))).thenReturn(RENDERED_DOC);
        when(workItemDefinitionClientRegistry.getRegistry()).thenReturn(workItemDefinitionClientRegistry);
        when(decorators.getDecorator(anyString())).thenReturn(Optional.empty());

        //adapters mock
        when(definitionManager.adapters()).thenReturn(adapters);
        when(adapters.forDefinition()).thenReturn(definitionAdapter);
        when(adapters.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(adapters.forProperty()).thenReturn(propertyAdapter);
        when(adapters.forPropertySet()).thenReturn(propertySetAdapter);

        when(definitionAdapter.getId(userTask)).thenReturn(userTaskId);
        when(definitionAdapter.getId(embeddedSubprocess)).thenReturn(subprocessId);
        when(definitionUtils.getName(bpmnDiagram)).thenReturn(PROCESS_NAME);
        when(definitionUtils.getName(userTask)).thenReturn(TASK_NAME);
        when(definitionUtils.getTitle(userTask.getClass().getName())).thenReturn(TASK_NAME);
        when(definitionUtils.getName(embeddedSubprocess)).thenReturn(SUB_PROCESS_NAME);
        when(definitionUtils.getTitle(embeddedSubprocess.getClass().getName())).thenReturn(SUB_PROCESS_NAME);

        //mock icons
        glyphProvider = (defId) -> glyphIcon;
        when(categoryDefinitionProvider.glyphProvider()).thenReturn(glyphProvider);
        when(glyphRenderer.render(eq(glyphIcon), anyDouble(), anyDouble())).thenReturn(iconElement);
        when(iconElement.getElement()).thenReturn(iconHTMLElement);
        when(iconHTMLElement.getInnerHTML()).thenReturn(ICON_HTML);
        when(shapeFactory.getGlyph(anyString())).thenReturn(imageStripGlyph);
        when(glyphRenderer.render(eq(imageStripGlyph), anyDouble(), anyDouble())).thenReturn(iconElement);
        when(userTaskId.value()).thenReturn(UserTask.class.getName());
        when(subprocessId.value()).thenReturn(EmbeddedSubprocess.class.getName());

        //adapters mock for task
        final TaskGeneralSet taskGeneral = userTask.getGeneral();
        final UserTaskExecutionSet taskExecutionSet = userTask.getExecutionSet();
        when(definitionAdapter.getCategory(userTask)).thenReturn(BPMNCategories.ACTIVITIES);
        when(definitionAdapter.getProperties(userTask)).thenReturn(toSet(taskGeneral.getDocumentation(),
                                                                         taskGeneral.getName(),
                                                                         taskExecutionSet.getSubject(),
                                                                         taskExecutionSet.getGroupid(),
                                                                         taskExecutionSet.getPriority(),
                                                                         taskExecutionSet.getAssignmentsinfo(),
                                                                         taskExecutionSet.getDescription()
        ));

        mockProperty(taskGeneral.getDocumentation(), TASK_DOCUMENTATION, DOCUMENTATION_CAPTION);
        mockProperty(taskGeneral, TASK_NAME, NAME_CAPTION);
        mockProperty(taskExecutionSet.getSubject(), TASK_SUBJECT, SUBJECT_CAPTION);
        mockProperty(taskExecutionSet.getGroupid(), TASK_GROUPID, GROUP_CAPTION);
        mockProperty(taskExecutionSet.getPriority(), TASK_PRIORITY, PRIORITY_CAPTION);
        mockProperty(taskExecutionSet.getDescription(), TASK_DESCRIPTION, DESCRIPTION_CAPTION);
        mockProperty(taskExecutionSet.getAssignmentsinfo(), "", ASSIGNEMNTS_CAPTION);
        when(decorators.getDecorator(taskExecutionSet.getAssignmentsinfo())).thenReturn(Optional.of(() -> ASSIGNEMNTS));

        //adapters mock for subprocess
        final BPMNGeneralSet subprocessGeneral = embeddedSubprocess.getGeneral();
        final EmbeddedSubprocessExecutionSet subprocessExecutionSet = embeddedSubprocess.getExecutionSet();
        when(definitionAdapter.getCategory(embeddedSubprocess)).thenReturn(BPMNCategories.SUB_PROCESSES);
        when(definitionAdapter.getProperties(embeddedSubprocess)).thenReturn(toSet(
                subprocessGeneral.getDocumentation(),
                subprocessGeneral.getName(),
                subprocessExecutionSet.getOnEntryAction(),
                subprocessExecutionSet.getOnExitAction(),
                subprocessExecutionSet.getIsAsync()));

        mockProperty(subprocessGeneral.getDocumentation(), SUB_PROCESS_DOCUMENTATION, DOCUMENTATION_CAPTION);
        mockProperty(subprocessGeneral, SUB_PROCESS_NAME, NAME_CAPTION);

        mockProperty(subprocessExecutionSet.getOnEntryAction(), ON_ENTRY_ACTION, ON_ENTRY_CAPTION);
        mockProperty(subprocessExecutionSet.getOnExitAction(), ON_EXIT_ACTION, ON_EXIT_CAPTION);
        mockProperty(subprocessExecutionSet.getIsAsync(), "false", ISASYNC_CAPTION);

        tested = new ClientBPMNDocumentationService(mustacheTemplateRenderer,
                                                    definitionManager,
                                                    definitionUtils,
                                                    shapeFactory,
                                                    canvasFileExport,
                                                    sessionManager,
                                                    categoryDefinitionProvider,
                                                    glyphRenderer,
                                                    translationService,
                                                    workItemDefinitionClientRegistry,
                                                    decorators);
    }

    private void mockProperty(Object property, String value, String caption) {
        when(propertyAdapter.getValue(property)).thenReturn(value);
        when(propertyAdapter.getId(property)).thenReturn(property.getClass().getName());
        when(propertyAdapter.getCaption(property)).thenReturn(caption);
    }

    private Set toSet(Object... elements) {
        return Stream.of(elements).collect(Collectors.toSet());
    }

    private Node createNode(Object content) {
        NodeImpl node = new NodeImpl(UUID.uuid());
        node.setContent(new ViewImpl<>(content, new Bounds(new Bound(0d, 0d), new Bound(1d, 1d))));
        return node;
    }

    @Test
    public void processDocumentation() {
        final BPMNDocumentation bpmnDocumentation = tested.processDocumentation(diagram);
        final ProcessOverview process = bpmnDocumentation.getProcess();
        final General general = process.getGeneral();

        assertEquals(bpmnDocumentation.getModuleName(), PROCESS_PACKAGE);
        assertEquals(general.getDescription(), PROCESS_DECRIPTION);
        assertNotEquals(general.getDocumentation(), PROCESS_DOCUMENTATION);
        //testing spaces on the value
        assertEquals(general.getDocumentation(), PROCESS_DOCUMENTATION.replaceAll("\n", "<br/>"));
        assertEquals(general.getId(), PROCESS_UUID);
        assertEquals(general.getIsAdhoc(), PROCESS_IS_ADHOC.toString());
        assertEquals(general.getIsExecutable(), PROCESS_IS_EXECUTABLE.toString());
        assertEquals(general.getName(), PROCESS_NAME);
        assertEquals(general.getPkg(), PROCESS_PACKAGE);
        assertEquals(general.getVersion(), PROCESS_VERSION);

        final ProcessVariablesTotal dataTotal = process.getDataTotal();
        assertEquals(dataTotal.getTotal(), 6, 0);
        assertEquals(dataTotal.getTotalVariables(), 6, 0);

        //assert sorting based on the variables names
        final KeyValue[] variables = dataTotal.getVariables();
        assertEquals(variables[0].getKey(), "GL1");
        assertEquals(variables[0].getValue(), String.class.getName());
        assertEquals(variables[1].getKey(), "GL2");
        assertEquals(variables[1].getValue(), Boolean.class.getName());
        assertEquals(variables[2].getKey(), "PV1");
        assertEquals(variables[2].getValue(), String.class.getName());
        assertEquals(variables[3].getKey(), "PV2");
        assertEquals(variables[3].getValue(), Boolean.class.getName());
        assertEquals(variables[4].getKey(), "SUBPV1");
        assertEquals(variables[4].getValue(), String.class.getName());
        assertEquals(variables[5].getKey(), "SUBPV2");
        assertEquals(variables[5].getValue(), Boolean.class.getName());

        final ElementTotal[] totals = bpmnDocumentation.getElementsDetails().getTotals();
        assertEquals(totals.length, 2);

        //assert category order
        //usertask
        final ElementTotal activities = totals[0];
        assertEquals(activities.getType(), BPMNCategories.ACTIVITIES);
        assertEquals(activities.getTypeIcon(), ICON_HTML);
        assertEquals(activities.getQuantity(), 1);
        //subprocess
        final ElementTotal subprocesses = totals[1];
        assertEquals(subprocesses.getType(), BPMNCategories.SUB_PROCESSES);
        assertEquals(subprocesses.getTypeIcon(), ICON_HTML);
        assertEquals(subprocesses.getQuantity(), 1);

        //assert elements order in the category
        //usertask
        final Element[] activitiesElements = activities.getElements();
        final Element task = activitiesElements[0];
        assertEquals(task.getTitle(), TASK_NAME);
        assertEquals(task.getType(), BPMNCategories.ACTIVITIES);
        assertEquals(task.getIcon(), ICON_HTML);
        assertEquals(task.getName(), TASK_NAME);
        //assert usertask properties sorted by name
        final KeyValue[] taskProperties = task.getProperties();
        assertProperty(taskProperties[0], ASSIGNEMNTS, ASSIGNEMNTS_CAPTION,
                       userTask.getExecutionSet().getDescription());
        assertProperty(taskProperties[1], TASK_DESCRIPTION, DESCRIPTION_CAPTION,
                       userTask.getExecutionSet().getDescription());
        assertProperty(taskProperties[2], TASK_DOCUMENTATION, DOCUMENTATION_CAPTION,
                       userTask.getGeneral().getDocumentation());
        assertProperty(taskProperties[3], TASK_GROUPID, GROUP_CAPTION,
                       userTask.getExecutionSet().getGroupid());
        assertProperty(taskProperties[4], TASK_PRIORITY, PRIORITY_CAPTION,
                       userTask.getExecutionSet().getPriority());
        assertProperty(taskProperties[5], TASK_SUBJECT, SUBJECT_CAPTION,
                       userTask.getExecutionSet().getSubject());

        //subprocess
        final Element[] subprocessesElements = subprocesses.getElements();
        final Element subprocess = subprocessesElements[0];
        assertEquals(subprocess.getTitle(), SUB_PROCESS_NAME);
        assertEquals(subprocess.getType(), BPMNCategories.SUB_PROCESSES);
        assertEquals(subprocess.getIcon(), ICON_HTML);
        assertEquals(subprocess.getName(), SUB_PROCESS_NAME);

        //assert subprocess properties
        final KeyValue[] subprocessProperties = subprocess.getProperties();
        assertProperty(subprocessProperties[0], SUB_PROCESS_DOCUMENTATION, DOCUMENTATION_CAPTION,
                       embeddedSubprocess.getGeneral().getDocumentation());
        assertProperty(subprocessProperties[1], "false", ISASYNC_CAPTION,
                       embeddedSubprocess.getExecutionSet().getIsAsync());
        assertProperty(subprocessProperties[2], ON_ENTRY_ACTION, ON_ENTRY_CAPTION,
                       embeddedSubprocess.getExecutionSet().getOnEntryAction());
        assertProperty(subprocessProperties[3], ON_EXIT_ACTION, ON_EXIT_CAPTION,
                       embeddedSubprocess.getExecutionSet().getOnEntryAction());
    }

    private void assertProperty(KeyValue taskProperty, String value, String caption, Object property) {
        assertEquals(taskProperty.getKey(), caption);
        assertEquals(taskProperty.getValue(), value);
        verify(decorators).getDecorator(property);
    }

    @Test
    public void getDocumentationTemplate() {
        final HTMLDocumentationTemplate documentationTemplate = tested.getDocumentationTemplate();
        assertEquals(documentationTemplate.getTemplate(), TEMPLATE);
    }

    @Test
    public void buildDocumentation() {
        final HTMLDocumentationTemplate documentationTemplate = tested.getDocumentationTemplate();
        final BPMNDocumentation bpmnDocumentation = tested.processDocumentation(diagram);
        final DocumentationOutput documentationOutput = tested.buildDocumentation(documentationTemplate, bpmnDocumentation);
        assertEquals(documentationOutput.getValue(), RENDERED_DOC);
    }

    @Test
    public void generate() {
        final DocumentationOutput documentationOutput = tested.generate(diagram);
        assertEquals(documentationOutput.getValue(), RENDERED_DOC);
    }
}