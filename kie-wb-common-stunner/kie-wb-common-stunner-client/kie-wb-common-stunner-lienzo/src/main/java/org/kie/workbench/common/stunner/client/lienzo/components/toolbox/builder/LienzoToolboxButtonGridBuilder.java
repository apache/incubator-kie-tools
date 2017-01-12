/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.components.toolbox.builder;

import org.kie.workbench.common.stunner.client.lienzo.components.toolbox.LienzoToolboxButtonGrid;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxButtonGrid;
import org.kie.workbench.common.stunner.core.client.components.toolbox.builder.ToolboxButtonGridBuilder;

public class LienzoToolboxButtonGridBuilder implements ToolboxButtonGridBuilder {

    public static final int PADDING = 5;
    public static final int ICON_SIZE = 12;

    private int padding = PADDING;
    private int iconSize = ICON_SIZE;
    private int rows;
    private int cols;

    @Override
    public ToolboxButtonGridBuilder setPadding( final int padding ) {
        this.padding = padding;
        return this;
    }

    @Override
    public ToolboxButtonGridBuilder setIconSize( final int iconSize ) {
        this.iconSize = iconSize;
        return this;
    }

    @Override
    public ToolboxButtonGridBuilder setRows( final int rows ) {
        this.rows = rows;
        return this;
    }

    @Override
    public ToolboxButtonGridBuilder setColumns( final int cols ) {
        this.cols = cols;
        return this;
    }

    @Override
    public ToolboxButtonGrid build() {
        return new LienzoToolboxButtonGrid( padding,
                                            iconSize,
                                            rows,
                                            cols );
    }
}
