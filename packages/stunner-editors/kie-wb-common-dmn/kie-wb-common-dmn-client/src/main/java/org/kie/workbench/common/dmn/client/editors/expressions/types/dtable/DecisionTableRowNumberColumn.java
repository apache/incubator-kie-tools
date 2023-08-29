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

import org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.IsRowDragHandle;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.IntegerColumnRenderer;

public class DecisionTableRowNumberColumn extends DMNGridColumn<DecisionTableGrid, Integer> implements IsRowDragHandle {

    public static final double DEFAULT_WIDTH = 50.0;

    public DecisionTableRowNumberColumn(final Supplier<HitPolicy> hitPolicySupplier,
                                        final Supplier<BuiltinAggregator> builtinAggregatorSupplier,
                                        final CellEditorControlsView.Presenter cellEditorControls,
                                        final HitPolicyPopoverView.Presenter editor,
                                        final double width,
                                        final DecisionTableGrid gridWidget) {
        super(new RowNumberColumnHeaderMetaData(hitPolicySupplier,
                                                builtinAggregatorSupplier,
                                                cellEditorControls,
                                                editor,
                                                gridWidget),
              new IntegerColumnRenderer(),
              width,
              gridWidget);
        setMovable(false);
        setResizable(false);
        setFloatable(true);
    }
}