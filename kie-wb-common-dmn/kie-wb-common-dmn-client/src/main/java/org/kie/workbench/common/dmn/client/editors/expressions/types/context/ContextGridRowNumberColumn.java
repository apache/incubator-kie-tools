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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.List;

import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.IsRowDragHandle;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.IntegerColumnRenderer;

public class ContextGridRowNumberColumn extends BaseGridColumn<Integer> implements IsRowDragHandle {

    public ContextGridRowNumberColumn(final List<HeaderMetaData> headerMetaData) {
        super(headerMetaData,
              new IntegerColumnRenderer(),
              50.0);
        setMovable(false);
        setResizable(false);
        setFloatable(true);
    }
}
