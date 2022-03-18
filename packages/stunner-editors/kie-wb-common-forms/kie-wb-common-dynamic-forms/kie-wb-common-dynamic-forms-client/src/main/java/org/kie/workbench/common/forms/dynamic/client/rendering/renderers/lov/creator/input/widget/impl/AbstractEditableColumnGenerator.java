/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl;

import com.google.gwt.user.cellview.client.Column;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.CellEditionHandler;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.EditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.TableEntry;
import org.kie.workbench.common.forms.dynamic.client.resources.i18n.FormRenderingConstants;
import org.uberfire.ext.widgets.table.client.UberfirePagedTable;

public abstract class AbstractEditableColumnGenerator<TYPE> implements EditableColumnGenerator<TYPE> {

    protected TranslationService translationService;

    public AbstractEditableColumnGenerator(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public void registerColumn(UberfirePagedTable<TableEntry<TYPE>> table,
                               CellEditionHandler<TYPE> cellEditionHandler,
                               boolean readOnly) {

        Column column;

        if (readOnly) {
            column = getReadOnlyColumn();
        } else {
            column = getEditableColumn(table,
                                       cellEditionHandler);
        }

        table.addColumn(column,
                        translationService.getTranslation(FormRenderingConstants.EditableColumnGeneratorValueHeader));
    }

    protected abstract Column<TableEntry<TYPE>, ?> getEditableColumn(UberfirePagedTable<TableEntry<TYPE>> table,
                                                                     CellEditionHandler<TYPE> cellEditionHandler);

    protected abstract Column<TableEntry<TYPE>, ?> getReadOnlyColumn();
}
