/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuidedTourUtilsTest {

    @Mock
    private TextPropertyProviderFactory textPropertyProviderFactory;

    private GuidedTourUtils utils;

    @Before
    public void setup() {
        utils = new GuidedTourUtils(textPropertyProviderFactory);
    }

    @Test
    public void getNameWithAbstractCanvasHandlerElementEvent() {
        final String expectedName = "name";
        final NodeImpl<View> node = new NodeImpl<>("uuid");
        final CanvasHandler canvasHandler = mock(CanvasHandler.class);
        final TextPropertyProvider textPropertyProvider = mock(TextPropertyProvider.class);
        final CanvasElementAddedEvent event = new CanvasElementAddedEvent(canvasHandler, node);

        when(textPropertyProviderFactory.getProvider(node)).thenReturn(textPropertyProvider);
        when(textPropertyProvider.getText(node)).thenReturn(expectedName);

        final Optional<String> actualName = utils.getName(event);

        assertTrue(actualName.isPresent());
        assertEquals(expectedName, actualName.get());
    }

    @Test
    public void testGetNodeWhenElementIsNotNodeImpl() {
        final EdgeImpl<View> edge = new EdgeImpl<>("uuid");
        final CanvasHandler canvasHandler = mock(CanvasHandler.class);
        final CanvasElementAddedEvent event = new CanvasElementAddedEvent(canvasHandler, edge);

        assertFalse(utils.getName(event).isPresent());
    }

    @Test
    public void testGetNameWithNodeImpl() {
        final NodeImpl<View> node = new NodeImpl<>("uuid");
        final TextPropertyProvider textPropertyProvider = mock(TextPropertyProvider.class);
        final String expectedNodeName = "Decision-1";

        when(textPropertyProviderFactory.getProvider(node)).thenReturn(textPropertyProvider);
        when(textPropertyProvider.getText(node)).thenReturn(expectedNodeName);

        final String actualNodeName = utils.getName(node);

        assertEquals(expectedNodeName, actualNodeName);
    }

    @Test
    public void testAsNodeImpl() {
        final NodeImpl<View> node = new NodeImpl<>("uuid");
        assertEquals(node, utils.asNodeImpl(node));
    }

    @Test
    public void testAsNodeImplWithEdge() {
        assertNull(utils.asNodeImpl(new EdgeImpl<View>("uuid")));
    }
}
