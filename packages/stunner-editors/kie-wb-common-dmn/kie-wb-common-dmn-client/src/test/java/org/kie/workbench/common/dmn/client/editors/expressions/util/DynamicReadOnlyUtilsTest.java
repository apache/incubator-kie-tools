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
package org.kie.workbench.common.dmn.client.editors.expressions.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.BaseGrid;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DynamicReadOnlyUtilsTest {

    @Mock
    private BaseGrid gridWidget;

    @Test
    public void testGridWidget() {
        assertThat(DynamicReadOnlyUtils.isOnlyVisualChangeAllowed(mock(GridWidget.class))).isFalse();
    }

    @Test
    public void testBaseGrid() {
        when(gridWidget.isOnlyVisualChangeAllowed()).thenReturn(false);

        assertThat(DynamicReadOnlyUtils.isOnlyVisualChangeAllowed(gridWidget)).isFalse();
    }

    @Test
    public void testBaseGridWhenOnlyVisualChangeAllowed() {
        when(gridWidget.isOnlyVisualChangeAllowed()).thenReturn(true);

        assertThat(DynamicReadOnlyUtils.isOnlyVisualChangeAllowed(gridWidget)).isTrue();
    }
}
