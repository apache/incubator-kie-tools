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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

import static org.assertj.core.api.Assertions.assertThat;

public class LiteralExpressionPMMLColumnRendererTest {

    private static final String PLACEHOLDER = "placeholder";

    private LiteralExpressionPMMLColumnRenderer renderer;

    @Before
    public void setup() {
        this.renderer = new LiteralExpressionPMMLColumnRenderer();
    }

    @Test
    public void testShowPlaceHolder_WithNullCellWithNullPlaceHolder() {
        assertThat(renderer.isPlaceHolderToBeShown(null)).isFalse();
    }

    @Test
    public void testShowPlaceHolder_WithNullCellValueWithNullPlaceHolder() {
        assertThat(renderer.isPlaceHolderToBeShown(new BaseGridCell<>(null))).isFalse();
    }

    @Test
    public void testShowPlaceHolder_WithEmptyCellValueWithEmptyValueWithNullPlaceHolder() {
        assertThat(renderer.isPlaceHolderToBeShown(new BaseGridCell<>(new BaseGridCellValue<>("")))).isFalse();
    }

    @Test
    public void testShowPlaceHolder_WithNullEmptyCellValueWithNullValueWithNullPlaceHolder() {
        assertThat(renderer.isPlaceHolderToBeShown(new BaseGridCell<>(new BaseGridCellValue<>(null)))).isFalse();
    }

    @Test
    public void testShowPlaceHolder_WithEmptyCellValueWithEmptyValueWithPlaceHolder() {
        assertThat(renderer.isPlaceHolderToBeShown(new BaseGridCell<>(new BaseGridCellValue<>("", PLACEHOLDER)))).isTrue();
    }

    @Test
    public void testShowPlaceHolder_WithEmptyCellValueWithNullValueWithPlaceHolder() {
        assertThat(renderer.isPlaceHolderToBeShown(new BaseGridCell<>(new BaseGridCellValue<>(null, PLACEHOLDER)))).isTrue();
    }
}
