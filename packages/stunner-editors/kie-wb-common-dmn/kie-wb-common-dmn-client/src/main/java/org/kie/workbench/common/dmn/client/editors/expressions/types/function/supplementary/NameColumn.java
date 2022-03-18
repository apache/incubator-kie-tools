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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary;

import java.util.Collections;

import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.NameAndDataTypeColumnRenderer;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;

public class NameColumn extends DMNGridColumn<FunctionSupplementaryGrid, InformationItemCell.HasNameCell> {

    public NameColumn(final double width,
                      final FunctionSupplementaryGrid gridWidget) {
        super(Collections.emptyList(),
              new NameAndDataTypeColumnRenderer(),
              width,
              gridWidget);

        setMovable(false);
        setResizable(true);
    }

    @Override
    public void setWidth(final double width) {
        super.setWidth(width);
        updateWidthOfPeers();
    }
}
