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
package org.kie.workbench.common.dmn.client.widgets.grid.model;

import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;

import static org.kie.workbench.common.dmn.client.widgets.grid.model.BaseHasDynamicHeightCell.DEFAULT_HEIGHT;

public class LiteralExpressionGridRow extends BaseGridRow {

    public LiteralExpressionGridRow() {
        super(DEFAULT_HEIGHT);
    }

    @Override
    public double getHeight() {
        double height = DEFAULT_HEIGHT;
        for (GridCell<?> cell : this.getCells().values()) {
            if (cell instanceof HasDynamicHeight) {
                height = Math.max(((HasDynamicHeight) cell).getHeight(), height);
            }
        }
        return height;
    }
}
