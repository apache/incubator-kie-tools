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

package org.uberfire.ext.wires.core.grids.client.model;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseWheelEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GridCellEditActionTest {

    @Mock
    private MouseEvent clickEvent;

    @Mock
    private DoubleClickEvent doubleClickEvent;

    @Mock
    private MouseWheelEvent mouseWheelEvent;

    private NodeMouseClickEvent nodeMouseClickEvent;

    private NodeMouseDoubleClickEvent nodeMouseDoubleClickEvent;

    private NodeMouseWheelEvent nodeMouseWheelEvent;

    @Before
    public void setup() {
        this.nodeMouseClickEvent = new NodeMouseClickEvent(clickEvent);
        this.nodeMouseDoubleClickEvent = new NodeMouseDoubleClickEvent(doubleClickEvent);
        this.nodeMouseWheelEvent = new NodeMouseWheelEvent(mouseWheelEvent);
    }

    @Test
    public void assertEnumeratedValues() {
        assertThat(GridCellEditAction.getSupportedEditAction(nodeMouseClickEvent)).isEqualTo(GridCellEditAction.SINGLE_CLICK);
        assertThat(GridCellEditAction.getSupportedEditAction(nodeMouseDoubleClickEvent)).isEqualTo(GridCellEditAction.DOUBLE_CLICK);
        assertThat(GridCellEditAction.getSupportedEditAction(nodeMouseWheelEvent)).isEqualTo(GridCellEditAction.NONE);
    }
}
