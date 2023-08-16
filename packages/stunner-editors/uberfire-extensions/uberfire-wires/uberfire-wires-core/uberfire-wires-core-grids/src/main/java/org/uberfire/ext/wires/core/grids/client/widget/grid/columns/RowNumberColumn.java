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

package org.uberfire.ext.wires.core.grids.client.widget.grid.columns;

import java.util.ArrayList;
import java.util.List;

import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.IsRowDragHandle;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.GridColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.IntegerColumnRenderer;

public class RowNumberColumn extends BaseGridColumn<Integer> implements IsRowDragHandle {

    public static final double DEFAULT_WIDTH = 50.0;

    public RowNumberColumn() {
        this(new ArrayList<HeaderMetaData>() {{
                 add(new BaseHeaderMetaData("#"));
             }},
             new IntegerColumnRenderer());
    }

    public RowNumberColumn(final List<HeaderMetaData> headerMetaData) {
        this(headerMetaData,
             new IntegerColumnRenderer());
    }

    public RowNumberColumn(final List<HeaderMetaData> headerMetaData,
                           final GridColumnRenderer<Integer> columnRenderer) {
        super(headerMetaData,
              columnRenderer,
              DEFAULT_WIDTH);
        setMovable(false);
        setResizable(false);
        setFloatable(true);
    }
}