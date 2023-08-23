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

package org.kie.workbench.common.dmn.client.editors.included.imports.persistence;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.DMNElement;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.NamedElement;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.Mockito;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.api.property.dmn.QName.DEFAULT_NS_PREFIX;
import static org.kie.workbench.common.dmn.api.property.dmn.QName.NULL_NS_URI;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNIncludedModelHandlerTest {

    @Mock
    private DMNGraphUtils dmnGraphUtils;

    @Mock
    private DefaultCanvasCommandFactory canvasCommandFactory;

    @Mock
    private SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private CanvasCommand<AbstractCanvasHandler> canvasCommand;

    @Mock
    private CompositeCommand<AbstractCanvasHandler, CanvasViolation> compositeCommand;

    private DMNIncludedModelHandler handler;

    @Before
    public void setup() {
        handler = spy(new DMNIncludedModelHandler(dmnGraphUtils, canvasCommandFactory, sessionCommandManager, definitionUtils));
    }

    @Test
    public void testUpdate() {

        final Decision drgElement1 = makeDecision("model1.tUUID", "string", true);
        final Decision drgElement2 = makeDecision("model1.imported person", "model1.tPerson", true);
        final InputData drgElement3 = makeInputData("local person", "model1.tPerson", false);
        final InputData drgElement4 = makeInputData("regular DRG Element", "boolean", false);
        setId(drgElement2, "model1.uuid");

        final List<DRGElement> drgElements = asList(drgElement1, drgElement2, drgElement3, drgElement4);

        doNothing().when(handler).updateDRGElementName(any(), Mockito.<String>any());
        when(dmnGraphUtils.getModelDRGElements()).thenReturn(drgElements);

        handler.update("model1", "model2");

        verify(handler).updateDRGElementName(drgElement1, "model2.tUUID");
        verify(handler).updateDRGElementName(drgElement2, "model2.imported person");

        assertEquals("string", drgElement1.getVariable().getTypeRef().getLocalPart());
        assertEquals("model2.uuid", drgElement2.getId().getValue());
        assertEquals("model2.tPerson", drgElement2.getVariable().getTypeRef().getLocalPart());
        assertEquals("model2.tPerson", drgElement3.getVariable().getTypeRef().getLocalPart());
        assertEquals("boolean", drgElement4.getVariable().getTypeRef().getLocalPart());
    }

    @Test
    public void testDestroy() {

        final Decision drgElement1 = makeDecision("model1.tUUID", "string", true);
        final Decision drgElement2 = makeDecision("model1.imported person", "model1.tPerson", true);
        final InputData drgElement3 = makeInputData("local person", "model1.tPerson", false);
        final InputData drgElement4 = makeInputData("regular DRG Element", "boolean", false);

        final List<DRGElement> drgElements = asList(drgElement1, drgElement2, drgElement3, drgElement4);

        doNothing().when(handler).deleteDRGElement(any());
        when(dmnGraphUtils.getModelDRGElements()).thenReturn(drgElements);

        handler.destroy("model1");

        verify(handler).deleteDRGElement(drgElement1);
        verify(handler).deleteDRGElement(drgElement2);
    }

    @Test
    public void testUpdateDRGElementName() {

        final DRGElement drgElement = mock(DRGElement.class);
        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);
        final String newName = "new name";

        when(dmnGraphUtils.getCanvasHandler()).thenReturn(canvasHandler);
        doReturn(compositeCommand).when(handler).buildUpdateCommand(drgElement, newName);

        handler.updateDRGElementName(drgElement, newName);

        verify(sessionCommandManager).execute(canvasHandler, compositeCommand);
    }

    @Test
    public void testDeleteDRGElement() {

        final DRGElement drgElement = mock(DRGElement.class);
        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);

        when(dmnGraphUtils.getCanvasHandler()).thenReturn(canvasHandler);
        doReturn(compositeCommand).when(handler).buildDeleteCommand(drgElement);

        handler.deleteDRGElement(drgElement);

        verify(sessionCommandManager).execute(canvasHandler, compositeCommand);
    }

    @Test
    public void testBuildUpdateCommand() {

        final Decision drgElement = makeDecision("model1.tUUID", "string", true);
        final String newName = "model2.tUUID";
        final String nameId = "nameId";
        final AbstractCanvasHandler context = mock(AbstractCanvasHandler.class);
        final Node node = mock(Node.class);
        final Definition definition = mock(Definition.class);
        final Object definitionObject = mock(Object.class);

        when(node.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(definitionObject);
        when(definitionUtils.getNameIdentifier(definitionObject)).thenReturn(nameId);
        when(canvasCommandFactory.updatePropertyValue(eq(node), eq(nameId), any(Name.class))).thenReturn(canvasCommand);
        doReturn(node).when(handler).getNode(drgElement);

        final Command<AbstractCanvasHandler, CanvasViolation> command = handler.buildUpdateCommand(drgElement, newName).getCommands().get(0);
        command.execute(context);

        assertEquals(canvasCommand, command);
    }

    @Test
    public void testBuildDeleteCommand() {

        final Decision drgElement = makeDecision("model1.tUUID", "string", true);
        final AbstractCanvasHandler context = mock(AbstractCanvasHandler.class);
        final Node node = mock(Node.class);

        when(canvasCommandFactory.deleteNode(node)).thenReturn(canvasCommand);
        doReturn(node).when(handler).getNode(drgElement);

        final Command<AbstractCanvasHandler, CanvasViolation> command = handler.buildDeleteCommand(drgElement).getCommands().get(0);
        command.execute(context);

        assertEquals(canvasCommand, command);
    }

    @Test
    public void testGetNode() {

        final DRGElement drgElement = mock(DRGElement.class);
        final AbstractCanvasHandler canvasHandler = mock(AbstractCanvasHandler.class);
        final Diagram diagram = mock(Diagram.class);
        final Graph graph = mock(Graph.class);
        final Node node1 = mock(Node.class);
        final Node node2 = mock(Node.class);
        final Node node3 = mock(Node.class);
        final Definition definition1 = mock(Definition.class);
        final Definition definition2 = mock(Definition.class);
        final Definition definition3 = mock(Definition.class);

        when(definition1.getDefinition()).thenReturn(new Object());
        when(definition2.getDefinition()).thenReturn(drgElement);
        when(definition3.getDefinition()).thenReturn(new Object());
        when(node1.getContent()).thenReturn(definition1);
        when(node2.getContent()).thenReturn(definition2);
        when(node3.getContent()).thenReturn(definition3);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(asList(node1, node2, node3));
        when(dmnGraphUtils.getCanvasHandler()).thenReturn(canvasHandler);

        final Node actualNode = handler.getNode(drgElement);

        assertEquals(node2, actualNode);
    }

    private Decision makeDecision(final String name,
                                  final String type,
                                  final boolean allowOnlyVisualChange) {

        final Decision decision = new Decision();

        setName(decision, name);
        setType(decision, type);
        decision.setAllowOnlyVisualChange(allowOnlyVisualChange);

        return decision;
    }

    private InputData makeInputData(final String name,
                                    final String type,
                                    final boolean allowOnlyVisualChange) {

        final InputData inputData = new InputData();

        setName(inputData, name);
        setType(inputData, type);
        inputData.setAllowOnlyVisualChange(allowOnlyVisualChange);

        return inputData;
    }

    private void setId(final DMNElement dmnElement,
                       final String name) {

        dmnElement.setId(new Id(name));
    }

    private void setName(final NamedElement namedElement,
                         final String name) {

        namedElement.setName(new Name(name));
    }

    private void setType(final HasVariable<InformationItemPrimary> hasVariable,
                         final String type) {

        final InformationItemPrimary variable = new InformationItemPrimary();

        hasVariable.setVariable(variable);
        variable.setTypeRef(type == null ? null : new QName(NULL_NS_URI, type, DEFAULT_NS_PREFIX));
    }
}
