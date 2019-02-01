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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import com.ait.lienzo.client.core.shape.Group;
import org.gwtbootstrap3.client.ui.TextArea;
import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextAreaDOMElement;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.single.impl.BaseGridColumnSingletonDOMElementRenderer;

public class DescriptionColumnRenderer extends BaseGridColumnSingletonDOMElementRenderer<String, TextArea, TextAreaDOMElement> {

    public DescriptionColumnRenderer(final TextAreaSingletonDOMElementFactory factory) {
        super(factory);
    }

    @Override
    public Group renderCell(final GridCell<String> cell,
                            final GridBodyCellRenderContext context) {
        if (cell == null || cell.getValue() == null) {
            return null;
        }

        return RendererUtils.getCenteredCellText(context, cell);
    }
}
