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

package org.drools.workbench.screens.scenariosimulation.client.renderers;

import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.impl.BaseGridRenderer;

public class BaseExpressionGridRenderer extends BaseGridRenderer {

    protected static final double HEADER_HEIGHT = 64;

    protected static final double HEADER_ROW_HEIGHT = 64;

    protected final double headerHeight;

    protected final double headerRowHeight;

    public BaseExpressionGridRenderer(final boolean isHeaderHidden) {
        super(new BaseExpressionGridTheme());
        this.headerHeight = isHeaderHidden ? 0.0 : HEADER_HEIGHT;
        this.headerRowHeight = isHeaderHidden ? 0.0 : HEADER_ROW_HEIGHT;
    }

    @Override
    public double getHeaderHeight() {
        return headerHeight;
    }

    @Override
    public double getHeaderRowHeight() {
        return headerRowHeight;
    }
}