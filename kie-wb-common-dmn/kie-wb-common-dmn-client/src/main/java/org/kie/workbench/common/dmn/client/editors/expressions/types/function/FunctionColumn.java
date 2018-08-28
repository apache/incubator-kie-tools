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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.Collections;
import java.util.List;

import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridWidgetRegistry;

public class FunctionColumn extends ExpressionEditorColumn {

    public FunctionColumn(final GridWidgetRegistry registry,
                          final HeaderMetaData headerMetaData,
                          final GridWidget gridWidget) {
        this(registry,
             Collections.singletonList(headerMetaData),
             gridWidget);
    }

    public FunctionColumn(final GridWidgetRegistry registry,
                          final List<HeaderMetaData> headerMetaData,
                          final GridWidget gridWidget) {
        super(headerMetaData,
              new FunctionColumnRenderer(registry),
              gridWidget);
        setMovable(false);
        setResizable(false);
    }
}
