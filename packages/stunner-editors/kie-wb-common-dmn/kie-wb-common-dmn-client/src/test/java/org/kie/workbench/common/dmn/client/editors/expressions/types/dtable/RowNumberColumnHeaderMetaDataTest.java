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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.function.Supplier;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;

@RunWith(LienzoMockitoTestRunner.class)
public class RowNumberColumnHeaderMetaDataTest {

    @Mock
    private CellEditorControlsView.Presenter cellEditorControls;

    @Mock
    private HitPolicyPopoverView.Presenter hitPolicyEditor;

    @Mock
    private DecisionTableGrid gridWidget;

    private HitPolicy hitPolicy = HitPolicy.ANY;

    private BuiltinAggregator builtinAggregator = null;

    private RowNumberColumnHeaderMetaData header;

    @Before
    public void setup() {
        this.header = new RowNumberColumnHeaderMetaData(hitPolicySupplier(),
                                                        builtinAggregatorSupplier(),
                                                        cellEditorControls,
                                                        hitPolicyEditor,
                                                        gridWidget);
    }

    private Supplier<HitPolicy> hitPolicySupplier() {
        return () -> hitPolicy;
    }

    private Supplier<BuiltinAggregator> builtinAggregatorSupplier() {
        return () -> builtinAggregator;
    }

    @Test
    public void testTitle_Without_Aggregator() {
        testTitle(HitPolicy.ANY, null, "A");
        testTitle(HitPolicy.COLLECT, null, "C");
        testTitle(HitPolicy.FIRST, null, "F");
        testTitle(HitPolicy.OUTPUT_ORDER, null, "O");
        testTitle(HitPolicy.PRIORITY, null, "P");
        testTitle(HitPolicy.RULE_ORDER, null, "R");
        testTitle(HitPolicy.UNIQUE, null, "U");
    }

    @Test
    public void testTitle_Count() {
        testTitle(HitPolicy.ANY, BuiltinAggregator.COUNT, "A");
        testTitle(HitPolicy.COLLECT, BuiltinAggregator.COUNT, "C#");
        testTitle(HitPolicy.FIRST, BuiltinAggregator.COUNT, "F");
        testTitle(HitPolicy.OUTPUT_ORDER, BuiltinAggregator.COUNT, "O");
        testTitle(HitPolicy.PRIORITY, BuiltinAggregator.COUNT, "P");
        testTitle(HitPolicy.RULE_ORDER, BuiltinAggregator.COUNT, "R");
        testTitle(HitPolicy.UNIQUE, BuiltinAggregator.COUNT, "U");
    }

    @Test
    public void testTitle_Max() {
        testTitle(HitPolicy.ANY, BuiltinAggregator.MAX, "A");
        testTitle(HitPolicy.COLLECT, BuiltinAggregator.MAX, "C>");
        testTitle(HitPolicy.FIRST, BuiltinAggregator.MAX, "F");
        testTitle(HitPolicy.OUTPUT_ORDER, BuiltinAggregator.MAX, "O");
        testTitle(HitPolicy.PRIORITY, BuiltinAggregator.MAX, "P");
        testTitle(HitPolicy.RULE_ORDER, BuiltinAggregator.MAX, "R");
        testTitle(HitPolicy.UNIQUE, BuiltinAggregator.MAX, "U");
    }

    @Test
    public void testTitle_Min() {
        testTitle(HitPolicy.ANY, BuiltinAggregator.MIN, "A");
        testTitle(HitPolicy.COLLECT, BuiltinAggregator.MIN, "C<");
        testTitle(HitPolicy.FIRST, BuiltinAggregator.MIN, "F");
        testTitle(HitPolicy.OUTPUT_ORDER, BuiltinAggregator.MIN, "O");
        testTitle(HitPolicy.PRIORITY, BuiltinAggregator.MIN, "P");
        testTitle(HitPolicy.RULE_ORDER, BuiltinAggregator.MIN, "R");
        testTitle(HitPolicy.UNIQUE, BuiltinAggregator.MIN, "U");
    }

    @Test
    public void testTitle_Sum() {
        testTitle(HitPolicy.ANY, BuiltinAggregator.SUM, "A");
        testTitle(HitPolicy.COLLECT, BuiltinAggregator.SUM, "C+");
        testTitle(HitPolicy.FIRST, BuiltinAggregator.SUM, "F");
        testTitle(HitPolicy.OUTPUT_ORDER, BuiltinAggregator.SUM, "O");
        testTitle(HitPolicy.PRIORITY, BuiltinAggregator.SUM, "P");
        testTitle(HitPolicy.RULE_ORDER, BuiltinAggregator.SUM, "R");
        testTitle(HitPolicy.UNIQUE, BuiltinAggregator.SUM, "U");
    }

    private void testTitle(final HitPolicy hitPolicy,
                           final BuiltinAggregator builtinAggregator,
                           final String expected) {
        this.hitPolicy = hitPolicy;
        this.builtinAggregator = builtinAggregator;
        assertEquals(expected, header.getTitle());
    }
}
