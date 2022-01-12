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

package org.kie.workbench.common.dmn.client.editors.expressions.types.invocation;

import java.util.Optional;

import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;

public class InvocationGridCell<T> extends DMNGridCell<T> {

    private final ListSelectorView.Presenter listSelector;

    public InvocationGridCell(final GridCellValue<T> value,
                              final ListSelectorView.Presenter listSelector) {
        super(value);
        this.listSelector = listSelector;
    }

    @Override
    public Optional<Editor> getEditor() {
        return Optional.of(listSelector);
    }
}
