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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined;

import java.util.Optional;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridCell;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

public class UndefinedExpressionCell extends DMNGridCell<String> {

    private final ListSelectorView.Presenter listSelector;

    public UndefinedExpressionCell(final ListSelectorView.Presenter listSelector,
                                   final TranslationService translationService) {
        super(new UndefinedExpressionCellValue(translationService.getTranslation(DMNEditorConstants.UndefinedExpressionEditor_SelectExpression)));
        this.listSelector = listSelector;
    }

    private static class UndefinedExpressionCellValue extends BaseGridCellValue<String> {

        public UndefinedExpressionCellValue(final String placeHolder) {
            super(null, placeHolder);
        }
    }

    @Override
    public Optional<Editor> getEditor() {
        return Optional.of(listSelector);
    }

    @Override
    public GridCellEditAction getSupportedEditAction() {
        return GridCellEditAction.SINGLE_CLICK;
    }
}
