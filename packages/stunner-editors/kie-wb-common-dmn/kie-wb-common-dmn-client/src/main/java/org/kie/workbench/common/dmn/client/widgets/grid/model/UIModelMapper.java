/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.grid.model;

import java.util.Optional;
import java.util.function.Supplier;

import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;

public interface UIModelMapper {

    void fromDMNModel(final int rowIndex,
                      final int columnIndex);

    void toDMNModel(final int rowIndex,
                    final int columnIndex,
                    final Supplier<Optional<GridCellValue<?>>> cell);
}
