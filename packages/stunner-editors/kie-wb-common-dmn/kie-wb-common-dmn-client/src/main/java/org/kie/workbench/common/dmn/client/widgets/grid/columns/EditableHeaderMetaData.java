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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import java.util.Optional;

import com.ait.lienzo.client.core.shape.Group;
import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn.HeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.HasDOMElementResources;

public interface EditableHeaderMetaData extends HeaderMetaData,
                                                HasDOMElementResources {

    /**
     * Delegates rendering to the {@link HeaderMetaData}.
     * @param context The context of a Grid's cell header during the rendering phase.
     * @param blockWidth Width of the {@link HeaderMetaData} column(s) block.
     * @param blockHeight Width of the {@link HeaderMetaData} row(s) block.
     * @return
     */
    default Group render(final GridHeaderColumnRenderContext context,
                         final double blockWidth,
                         final double blockHeight) {
        return RendererUtils.getEditableHeaderText(this,
                                                   context,
                                                   blockWidth,
                                                   blockHeight);
    }

    /**
     * Delegates rendering of the 'place holder' to the {@link HeaderMetaData}.
     * @param context The context of a Grid's cell header during the rendering phase.
     * @param blockWidth Width of the {@link HeaderMetaData} column(s) block.
     * @param blockHeight Width of the {@link HeaderMetaData} row(s) block.
     * @return
     */
    default Group renderPlaceHolder(final GridHeaderColumnRenderContext context,
                                    final double blockWidth,
                                    final double blockHeight) {
        return RendererUtils.getEditableHeaderPlaceHolderText(this,
                                                              context,
                                                              blockWidth,
                                                              blockHeight);
    }

    /**
     * Returns the default action that will trigger editing of the cells value.
     * @return
     */
    default GridCellEditAction getSupportedEditAction() {
        return GridCellEditAction.SINGLE_CLICK;
    }

    /**
     * Returns 'place holder' text to show if the {@link HeaderMetaData#getTitle()} is {@link StringUtils#isEmpty(String)}
     * @return Optional text to show as the 'place holder'
     */
    default Optional<String> getPlaceHolder() {
        return Optional.empty();
    }
}