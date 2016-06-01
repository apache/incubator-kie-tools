/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

/**
 * Definition of themes used by a render for the pluggable rendering mechanism.
 */
public interface GridRendererTheme {

    /**
     * Returns a display name for the theme.
     * @return
     */
    String getName();

    /**
     * Delegates construction of the "selector" to sub-classes. All implementations
     * are to provide a Rectangle surrounding the whole GridWidget.
     * @return
     */
    Rectangle getSelector();

    /**
     * Delegates construction of the cell "selector" to sub-classes. All implementations
     * are to provide a Rectangle surrounding the whole cell.
     * @return
     */
    Rectangle getCellSelector();

    /**
     * Delegates the Header's background Rectangle to sub-classes.
     * @return
     */
    Rectangle getHeaderBackground();

    /**
     * Delegates the Header's background Rectangle, used for "linked" columns to sub-classes.
     * @return
     */
    Rectangle getHeaderLinkBackground();

    /**
     * Delegates the Header's grid lines to sub-classes.
     * @return
     */
    MultiPath getHeaderGridLine();

    /**
     * Delegates the Header's Text to sub-classes.
     * @return
     */
    Text getHeaderText();

    /**
     * Delegates the Body's background Rectangle to sub-classes.
     * @param column The column being rendered.
     * @return
     */
    Rectangle getBodyBackground( final GridColumn<?> column );

    /**
     * Delegates the Body's grid lines to sub-classes.
     * @return
     */
    MultiPath getBodyGridLine();

    /**
     * Delegates the Body's Text to sub-classes.
     * @return
     */
    Text getBodyText();

}
