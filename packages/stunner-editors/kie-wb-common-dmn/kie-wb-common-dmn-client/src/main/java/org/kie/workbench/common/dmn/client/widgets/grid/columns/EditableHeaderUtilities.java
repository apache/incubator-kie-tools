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

import java.util.List;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Group;
import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;

public class EditableHeaderUtilities {

    public static boolean hasEditableHeader(final GridColumn<?> column) {
        return column.getHeaderMetaData().stream().anyMatch(md -> md instanceof EditableHeaderMetaData);
    }

    public static boolean isEditableHeader(final GridColumn<?> column,
                                           final Integer uiHeaderRowIndex) {
        return column.getHeaderMetaData().get(uiHeaderRowIndex) instanceof EditableHeaderMetaData;
    }

    public static boolean isPlaceHolderToBeShown(final EditableHeaderMetaData headerMetaData) {
        return StringUtils.isEmpty(headerMetaData.getTitle()) && headerMetaData.getPlaceHolder().isPresent();
    }

    public static Group getEditableHeaderContent(final Supplier<Group> defaultHeaderContentSupplier,
                                                 final List<GridColumn.HeaderMetaData> headerMetaData,
                                                 final GridHeaderColumnRenderContext context,
                                                 final int headerRowIndex,
                                                 final double blockWidth,
                                                 final double rowHeight) {
        final Group headerGroup = GWT.create(Group.class);

        if (headerRowIndex >= headerMetaData.size()) {
            return headerGroup;
        }

        final GridColumn.HeaderMetaData headerRowMetaData = headerMetaData.get(headerRowIndex);
        if (headerRowMetaData instanceof EditableHeaderMetaData) {
            final EditableHeaderMetaData editableHeaderMetaData = (EditableHeaderMetaData) headerRowMetaData;
            if (EditableHeaderUtilities.isPlaceHolderToBeShown(editableHeaderMetaData)) {
                return editableHeaderMetaData.renderPlaceHolder(context,
                                                                blockWidth,
                                                                rowHeight);
            }

            return ((EditableHeaderMetaData) headerRowMetaData).render(context,
                                                                       blockWidth,
                                                                       rowHeight);
        }
        return defaultHeaderContentSupplier.get();
    }
}
