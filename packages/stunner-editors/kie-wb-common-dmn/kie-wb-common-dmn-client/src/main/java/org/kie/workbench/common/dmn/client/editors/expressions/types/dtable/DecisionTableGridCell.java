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

import java.util.Optional;

import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.BaseHasDynamicHeightCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;

public class DecisionTableGridCell<T> extends BaseHasDynamicHeightCell<T> {

    private final ListSelectorView.Presenter listSelector;

    public DecisionTableGridCell(final GridCellValue<T> value,
                                 final ListSelectorView.Presenter listSelector,
                                 final double lineHeight) {
        super(value, lineHeight);
        this.listSelector = listSelector;
    }

    @Override
    public Optional<Editor> getEditor() {
        return Optional.of(listSelector);
    }
}
