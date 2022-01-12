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

package org.kie.workbench.common.dmn.client.editors.expressions.util;

import java.util.List;
import java.util.stream.Collectors;

import org.uberfire.ext.wires.core.grids.client.model.GridData;

public class SelectionUtils {

    public static boolean isMultiSelect(final GridData uiModel) {
        return uiModel.getSelectedCells().size() > 1;
    }

    public static boolean isMultiRow(final GridData uiModel) {
        return uiModel.getSelectedCells()
                .stream()
                .map(GridData.SelectedCell::getRowIndex)
                .distinct()
                .collect(Collectors.toList())
                .size() > 1;
    }

    public static boolean isMultiColumn(final GridData uiModel) {
        return isMultiColumn(uiModel.getSelectedCells());
    }

    public static boolean isMultiHeaderColumn(final GridData uiModel) {
        return isMultiColumn(uiModel.getSelectedHeaderCells());
    }

    private static boolean isMultiColumn(final List<GridData.SelectedCell> selectedCells) {
        return selectedCells
                .stream()
                .map(GridData.SelectedCell::getColumnIndex)
                .distinct()
                .collect(Collectors.toList())
                .size() > 1;
    }
}
