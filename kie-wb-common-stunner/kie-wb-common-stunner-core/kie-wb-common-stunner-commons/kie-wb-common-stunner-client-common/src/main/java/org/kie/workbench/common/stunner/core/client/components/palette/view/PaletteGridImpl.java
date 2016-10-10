/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.components.palette.view;

public final class PaletteGridImpl implements PaletteGrid {

    private final int rows;
    private final int cols;
    private final int iconSize;
    private final int padding;

    public PaletteGridImpl( final int iconSize,
                            final int padding ) {
        this( -1, -1, iconSize, padding );
    }

    public PaletteGridImpl( final int rows,
                            final int cols,
                            final int iconSize,
                            final int padding ) {
        this.rows = rows;
        this.cols = cols;
        this.iconSize = iconSize;
        this.padding = padding;
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getColumns() {
        return cols;
    }

    @Override
    public int getIconSize() {
        return iconSize;
    }

    @Override
    public int getPadding() {
        return padding;
    }

}