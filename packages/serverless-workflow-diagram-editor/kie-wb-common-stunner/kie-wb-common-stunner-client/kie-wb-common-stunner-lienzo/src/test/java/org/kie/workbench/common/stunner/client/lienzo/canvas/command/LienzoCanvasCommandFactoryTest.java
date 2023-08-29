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


package org.kie.workbench.common.stunner.client.lienzo.canvas.command;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.shape.view.BoundingBox;
import org.kie.workbench.common.stunner.core.graph.Element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(LienzoMockitoTestRunner.class)
public class LienzoCanvasCommandFactoryTest {

    private LienzoCanvasCommandFactory tested;

    @Before
    public void setUp() {
        tested = new LienzoCanvasCommandFactory();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateResizeCommand() {
        Element element = mock(Element.class);
        BoundingBox boundingBox = new BoundingBox(0, 0, 1, 2);
        final CanvasCommand<AbstractCanvasHandler> command = tested.resize(element, boundingBox);
        assertNotNull(command);
        assertTrue(command instanceof LienzoResizeNodeCommand);
        LienzoResizeNodeCommand lienzoCommand = (LienzoResizeNodeCommand) command;
        assertEquals(element, lienzoCommand.getCandidate());
        assertEquals(boundingBox, lienzoCommand.getBoundingBox());
    }
}
