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
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HasHitPolicyControl;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyPopoverView;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditablePopupHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;

class RowNumberColumnHeaderMetaData extends EditablePopupHeaderMetaData<HasHitPolicyControl, HitPolicyPopoverView.Presenter> {

    private static final String ROW_NUMBER_GROUP = "RowNumberColumn";

    private final Supplier<HitPolicy> hitPolicySupplier;
    private final Supplier<BuiltinAggregator> builtinAggregatorSupplier;
    private final DecisionTableGrid gridWidget;

    public RowNumberColumnHeaderMetaData(final Supplier<HitPolicy> hitPolicySupplier,
                                         final Supplier<BuiltinAggregator> builtinAggregatorSupplier,
                                         final CellEditorControlsView.Presenter cellEditorControls,
                                         final HitPolicyPopoverView.Presenter editor,
                                         final DecisionTableGrid gridWidget) {
        super(cellEditorControls,
              editor);
        this.hitPolicySupplier = hitPolicySupplier;
        this.builtinAggregatorSupplier = builtinAggregatorSupplier;
        this.gridWidget = gridWidget;
    }

    @Override
    protected HasHitPolicyControl getPresenter() {
        return gridWidget;
    }

    @Override
    public String getColumnGroup() {
        return ROW_NUMBER_GROUP;
    }

    @Override
    public void setColumnGroup(final String columnGroup) {
        throw new UnsupportedOperationException("Group cannot be set.");
    }

    @Override
    public String getTitle() {
        final HitPolicy hitPolicy = hitPolicySupplier.get();
        final BuiltinAggregator builtinAggregator = builtinAggregatorSupplier.get();
        final StringBuilder sb = new StringBuilder(hitPolicy.value().substring(0, 1).toUpperCase());
        if (HitPolicy.COLLECT == hitPolicy) {
            if (builtinAggregator != null) {
                sb.append(builtinAggregator.getCode());
            }
        }
        return sb.toString();
    }
}
