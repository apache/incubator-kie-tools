/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns;

import java.util.List;
import java.util.function.Consumer;

import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.TextBoxIntegerSingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;

public class SalienceUiColumn extends IntegerUiColumn {

    private boolean useRowNumber = false;

    public SalienceUiColumn( final List<HeaderMetaData> headerMetaData,
                             final double width,
                             final boolean isResizable,
                             final boolean isVisible,
                             final GuidedDecisionTablePresenter.Access access,
                             final boolean useRowNumber,
                             final TextBoxIntegerSingletonDOMElementFactory factory ) {
        super( headerMetaData,
               width,
               isResizable,
               isVisible,
               access,
               factory );
        this.useRowNumber = useRowNumber;
    }

    @Override
    public void doEdit( final GridCell<Integer> cell,
                        final GridBodyCellRenderContext context,
                        final Consumer<GridCellValue<Integer>> callback ) {
        if ( useRowNumber ) {
            return;
        }
        super.doEdit( cell,
                      context,
                      callback );
    }

    public boolean isUseRowNumber() {
        return this.useRowNumber;
    }

    public void setUseRowNumber( final boolean useRowNumber ) {
        this.useRowNumber = useRowNumber;
    }

}
