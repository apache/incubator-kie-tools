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

package org.kie.workbench.common.dmn.client.editors.documentation.common;

import java.util.List;
import java.util.Optional;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.shared.core.types.DataURLType;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.DocumentationLinksHolder;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.client.common.BoxedExpressionHelper;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionContainerGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionUIModelMapper;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseExpressionGrid;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.BOOLEAN;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.UNDEFINED;
import static org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationDRDsFactory.NONE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDocumentationDRDsFactoryTest {

    @Mock
    private SessionManager sessionManager;

    @Mock
    private BoxedExpressionHelper expressionHelper;

    @Mock
    private Diagram diagram;

    @Mock
    private DMNSession dmnSession;

    @Mock
    private ExpressionEditorView.Presenter expressionEditor;

    @Mock
    private ExpressionEditorViewImpl editorView;

    @Mock
    private ExpressionContainerGrid expressionContainerGrid;

    @Mock
    private BaseExpressionGrid<LiteralExpression, DMNGridData, LiteralExpressionUIModelMapper> expressionGrid;

    @Mock
    private GridData gridData;

    @Mock
    private Graph graph;

    @Mock
    private Viewport viewport;

    private DMNDocumentationDRDsFactory factory;

    @Before
    public void setup() {

        when(sessionManager.getCurrentSession()).thenReturn(dmnSession);
        when(dmnSession.getExpressionEditor()).thenReturn(expressionEditor);
        when(expressionEditor.getView()).thenReturn(editorView);
        when(editorView.getExpressionContainerGrid()).thenReturn(expressionContainerGrid);
        when(expressionContainerGrid.getViewport()).thenReturn(viewport);
        when(expressionContainerGrid.getBaseExpressionGrid()).thenReturn(Optional.of(expressionGrid));
        when(expressionGrid.getModel()).thenReturn(gridData);
        when(diagram.getGraph()).thenReturn(graph);

        factory = spy(new DMNDocumentationDRDsFactory(sessionManager, expressionHelper));
    }

    @Test
    public void testCreate() {

        final String nodeUUID1 = "1111-1111-1111-1111";
        final String nodeUUID2 = "2222-2222-2222-2222";
        final Node<View, Edge> node1 = new NodeImpl<>(nodeUUID1);
        final Node<View, Edge> node2 = new NodeImpl<>(nodeUUID2);
        final View view1 = mock(View.class);
        final View view2 = mock(View.class);
        final HasExpression hasExpression1 = mock(HasExpression.class);
        final List<Node<View, Edge>> nodes = asList(node1, node2);
        final Decision drgElement1 = new Decision();
        final InputData drgElement2 = new InputData();
        final String name1 = "Decision-1";
        final String name2 = "Input-data-2";
        final String description1 = "Description...";
        final InformationItemPrimary variable1 = new InformationItemPrimary();
        final QName typeRef1 = BOOLEAN.asQName();
        final String image1 = "<image1>";
        final DMNExternalLink externalLink = new DMNExternalLink();
        final DocumentationLinksHolder linksHolder = new DocumentationLinksHolder();
        linksHolder.getValue().addLink(externalLink);
        drgElement2.setLinksHolder(linksHolder);

        node1.setContent(view1);
        node2.setContent(view2);
        when(view1.getDefinition()).thenReturn(drgElement1);
        when(view2.getDefinition()).thenReturn(drgElement2);
        when(expressionHelper.getOptionalHasExpression(node1)).thenReturn(Optional.ofNullable(hasExpression1));
        when(expressionHelper.getOptionalHasExpression(node2)).thenReturn(Optional.empty());
        when(expressionContainerGrid.getNodeUUID()).thenReturn(Optional.of(nodeUUID2));
        when(graph.nodes()).thenReturn(nodes);

        doReturn(image1).when(factory).getNodeImage(diagram, node1);
        doNothing().when(factory).setExpressionContainerGrid(any(), any());

        variable1.setTypeRef(typeRef1);
        drgElement1.setVariable(variable1);
        drgElement1.setDescription(new Description(description1));
        drgElement1.setName(new Name(name1));
        drgElement2.setName(new Name(name2));

        final List<DMNDocumentationDRD> drds = factory.create(diagram);
        final DMNDocumentationDRD documentationDRD1 = drds.get(0);
        final DMNDocumentationDRD documentationDRD2 = drds.get(1);

        assertEquals(2, drds.size());

        assertEquals(name1, documentationDRD1.getDrdName());
        assertEquals(BOOLEAN.getName(), documentationDRD1.getDrdType());
        assertEquals(description1, documentationDRD1.getDrdDescription());
        assertEquals(image1, documentationDRD1.getDrdBoxedExpressionImage());

        assertEquals(NONE, documentationDRD2.getDrdDescription());
        assertEquals(UNDEFINED.getName(), documentationDRD2.getDrdType());
        assertEquals(name2, documentationDRD2.getDrdName());
        assertEquals(NONE, documentationDRD2.getDrdBoxedExpressionImage());

        verify(factory).setExpressionContainerGrid(diagram, nodeUUID2);
    }

    @Test
    public void testCreateTextAnnotation() {

        final String nodeUUID1 = "1111-1111-1111-1111";
        final Node<View, Edge> node1 = new NodeImpl<>(nodeUUID1);
        final View view1 = mock(View.class);
        final List<Node<View, Edge>> nodes = asList(node1);
        final TextAnnotation drgElement1 = new TextAnnotation();
        final String name1 = "Text Annotation";
        final String description1 = "Description...";

        node1.setContent(view1);
        when(view1.getDefinition()).thenReturn(drgElement1);
        when(graph.nodes()).thenReturn(nodes);

        drgElement1.setDescription(new Description(description1));
        drgElement1.setText(new Text(name1));

        final List<DMNDocumentationDRD> drds = factory.create(diagram);

        assertThat(drds)
                .hasSize(1)
                .first()
                .satisfies(ta -> {
                    assertThat(ta).extracting("drdName").isEqualTo(name1);
                    assertThat(ta).extracting("drdDescription").isEqualTo(description1);
                });
    }

    @Test
    public void testGetNodeImage() {

        final String uuid = "0000-1111-2222-3333";
        final Node<View, Edge> node = new NodeImpl<>(uuid);
        final HasExpression hasExpression = mock(HasExpression.class);
        final String expectedImage = "<image>";
        final double wide = 800;
        final double high = 600;

        doNothing().when(factory).setExpressionContainerGrid(any(), any());
        when(expressionHelper.getOptionalHasExpression(node)).thenReturn(Optional.of(hasExpression));
        when(expressionContainerGrid.getWidth()).thenReturn(wide);
        when(expressionContainerGrid.getHeight()).thenReturn(high);
        when(viewport.toDataURL(DataURLType.PNG)).thenReturn(expectedImage);

        final String actualImage = factory.getNodeImage(diagram, node);

        verify(viewport).setPixelSize(810, 610);
        verify(factory).setExpressionContainerGrid(diagram, uuid);
        assertEquals(expectedImage, actualImage);
    }

    @Test
    public void testSetExpressionContainerGrid() {

        final String uuid = "0000-1111-2222-3333";
        final String name = "Decision-1";
        final Node<View, Edge> node = new NodeImpl<>(uuid);
        final Decision drgElement = new Decision();
        final HasExpression hasExpression = mock(HasExpression.class);
        final View view = mock(View.class);

        node.setContent(view);
        drgElement.setName(new Name(name));
        when(graph.nodes()).thenReturn(singletonList(node));
        when(view.getDefinition()).thenReturn(drgElement);
        when(expressionHelper.getHasExpression(node)).thenReturn(hasExpression);

        factory.setExpressionContainerGrid(diagram, uuid);

        verify(expressionContainerGrid).setExpression(uuid, hasExpression, Optional.of(drgElement), false);
        verify(factory).clearSelections(expressionContainerGrid);
    }

    @Test
    public void testClearSelections() {

        factory.clearSelections(expressionContainerGrid);

        verify(expressionGrid.getModel()).clearSelections();
        verify(expressionGrid).draw();
    }

    @Test
    public void testGetNodeImageWhenNodeDoesNotHaveExpression() {

        final String uuid = "0000-1111-2222-3333";
        final Node<View, Edge> node = new NodeImpl<>(uuid);

        when(expressionHelper.getOptionalHasExpression(node)).thenReturn(Optional.empty());

        final String image = factory.getNodeImage(diagram, node);

        assertEquals(NONE, image);
    }
}
