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


package org.uberfire.ext.wires.core.grids.client.model;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseWheelEvent;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(LienzoMockitoTestRunner.class)
public class GridCellEditActionTest {

    private NodeMouseClickEvent nodeMouseClickEvent;

    private NodeMouseDoubleClickEvent nodeMouseDoubleClickEvent;

    private NodeMouseWheelEvent nodeMouseWheelEvent;

    @Before
    public void setup() {
        this.nodeMouseClickEvent = new NodeMouseClickEvent(mock(HTMLElement.class));
        this.nodeMouseDoubleClickEvent = new NodeMouseDoubleClickEvent(mock(HTMLElement.class));
        this.nodeMouseWheelEvent = new NodeMouseWheelEvent(mock(HTMLElement.class));
    }

    @Test
    public void assertEnumeratedValues() {
        assertThat(GridCellEditAction.getSupportedEditAction(nodeMouseClickEvent)).isEqualTo(GridCellEditAction.SINGLE_CLICK);
        assertThat(GridCellEditAction.getSupportedEditAction(nodeMouseDoubleClickEvent)).isEqualTo(GridCellEditAction.DOUBLE_CLICK);
        assertThat(GridCellEditAction.getSupportedEditAction(nodeMouseWheelEvent)).isEqualTo(GridCellEditAction.NONE);
    }
}
