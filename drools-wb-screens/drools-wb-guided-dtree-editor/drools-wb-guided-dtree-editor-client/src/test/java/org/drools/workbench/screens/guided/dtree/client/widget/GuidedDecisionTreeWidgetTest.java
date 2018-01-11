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
package org.drools.workbench.screens.guided.dtree.client.widget;

import java.util.Collections;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.TypeNode;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.ConstraintNodeImpl;
import org.drools.workbench.models.guided.dtree.shared.model.nodes.impl.TypeNodeImpl;
import org.drools.workbench.screens.guided.dtree.client.editor.GuidedDecisionTreeEditorPresenter;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ActionInsertNodeFactory;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ActionRetractNodeFactory;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ActionUpdateNodeFactory;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.ConstraintNodeFactory;
import org.drools.workbench.screens.guided.dtree.client.widget.factories.TypeNodeFactory;
import org.drools.workbench.screens.guided.dtree.client.widget.shapes.BaseGuidedDecisionTreeShape;
import org.drools.workbench.screens.guided.dtree.client.widget.shapes.ConstraintShape;
import org.drools.workbench.screens.guided.dtree.client.widget.shapes.TypeShape;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.api.events.ClearEvent;
import org.uberfire.ext.wires.core.api.events.ShapeAddedEvent;
import org.uberfire.ext.wires.core.api.events.ShapeDeletedEvent;
import org.uberfire.ext.wires.core.api.events.ShapeSelectedEvent;
import org.uberfire.ext.wires.core.api.layout.LayoutManager;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class GuidedDecisionTreeWidgetTest {

    @Mock
    private EventSourceMock<ClearEvent> clearEvent;

    @Mock
    private EventSourceMock<ShapeSelectedEvent> shapeSelectedEvent;

    @Mock
    private EventSourceMock<ShapeAddedEvent> shapeAddedEvent;

    @Mock
    private EventSourceMock<ShapeDeletedEvent> shapeDeletedEvent;

    @Mock
    private LayoutManager layoutManager;

    @Mock
    private TypeNodeFactory typeNodeFactory;

    @Mock
    private ConstraintNodeFactory constraintNodeFactory;

    @Mock
    private ActionInsertNodeFactory actionInsertNodeFactory;

    @Mock
    private ActionUpdateNodeFactory actionUpdateNodeFactory;

    @Mock
    private ActionRetractNodeFactory actionRetractNodeFactory;

    @Mock
    private GuidedDecisionTreeEditorPresenter presenter;

    @Captor
    private ArgumentCaptor<ShapeDeletedEvent> shapeDeletedEventCaptor;

    private TypeShape uiRootShape;
    private TypeNode uiRootNode = new TypeNodeImpl("Person");

    private ConstraintShape uiChildShape;
    private ConstraintNodeImpl uiChildNode = new ConstraintNodeImpl("Person", "age");

    private GuidedDecisionTreeWidget widget;

    private GuidedDecisionTree uiModel;

    @Before
    public void setup() {
        ApplicationPreferences.setUp(Collections.singletonMap(ApplicationPreferences.DATE_FORMAT, "dd/mm/yyyy"));

        this.widget = spy(new GuidedDecisionTreeWidget(clearEvent,
                                                       shapeSelectedEvent,
                                                       shapeAddedEvent,
                                                       shapeDeletedEvent,
                                                       layoutManager,
                                                       typeNodeFactory,
                                                       constraintNodeFactory,
                                                       actionInsertNodeFactory,
                                                       actionUpdateNodeFactory,
                                                       actionRetractNodeFactory));

        //We need to mock these after ApplicationPreferences has been initialised
        uiRootShape = mock(TypeShape.class);
        uiChildShape = mock(ConstraintShape.class);
        doReturn(uiRootNode).when(uiRootShape).getModelNode();
        doReturn(uiChildNode).when(uiChildShape).getModelNode();
        doReturn(uiRootShape).when(uiChildShape).getParentNode();

        widget.init(presenter);
        widget.init();

        uiModel = new GuidedDecisionTree();
        uiModel.setRoot(uiRootNode);
        widget.setModel(uiModel);
        widget.setUiRoot(uiRootShape);

        uiRootNode.addChild(uiChildNode);
    }

    @Test
    public void testDeleteShapeNotConfirmed() {
        doReturn(false).when(widget).confirmShapeDeletion();

        final BaseGuidedDecisionTreeShape shapeToDelete = uiRootShape;

        widget.deleteShape(shapeToDelete);

        verify(shapeDeletedEvent,
               never()).fire(any(ShapeDeletedEvent.class));
        verify(widget,
               never()).layout();

        assertEquals(uiRootNode,
                     uiModel.getRoot());
    }

    @Test
    public void testDeleteShapeConfirmedRootNode() {
        doReturn(true).when(widget).confirmShapeDeletion();

        final BaseGuidedDecisionTreeShape shapeToDelete = uiRootShape;

        widget.deleteShape(shapeToDelete);

        verify(shapeDeletedEvent).fire(shapeDeletedEventCaptor.capture());
        assertEquals(shapeToDelete,
                     shapeDeletedEventCaptor.getValue().getShape());

        verify(widget,
               never()).layout();

        assertNull(uiModel.getRoot());
    }

    @Test
    public void testDeleteShapeConfirmedNonRootNode() {
        doReturn(true).when(widget).confirmShapeDeletion();

        final BaseGuidedDecisionTreeShape shapeToDelete = uiChildShape;

        widget.deleteShape(shapeToDelete);

        verify(shapeDeletedEvent).fire(shapeDeletedEventCaptor.capture());
        assertEquals(shapeToDelete,
                     shapeDeletedEventCaptor.getValue().getShape());

        verify(widget).layout();

        assertFalse(uiRootNode.getChildren().contains(uiChildNode));
    }
}
