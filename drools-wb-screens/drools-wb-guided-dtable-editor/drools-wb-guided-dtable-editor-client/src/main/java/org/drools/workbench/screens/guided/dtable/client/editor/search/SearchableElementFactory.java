/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.editor.search;

import javax.inject.Inject;

import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.CellUtilities;
import org.drools.workbench.screens.guided.rule.client.util.GWTDateConverter;
import org.kie.soup.project.datamodel.oracle.DateConverter;

public class SearchableElementFactory {

    private final GuidedDecisionTableGridHighlightHelper highlightHelper;

    private final CellUtilities cellUtilities;

    @Inject
    public SearchableElementFactory(final GuidedDecisionTableGridHighlightHelper highlightHelper) {

        this.highlightHelper = highlightHelper;
        this.cellUtilities = new CellUtilities();

        CellUtilities.injectDateConvertor(getDateConverter());
    }

    private DateConverter getDateConverter() {
        return GWTDateConverter.getInstance();
    }

    public GuidedDecisionTableSearchableElement makeSearchableElement(final int row,
                                                                      final int column,
                                                                      final DTCellValue52 cellValue52,
                                                                      final GuidedDecisionTableView widget,
                                                                      final GuidedDecisionTable52 model,
                                                                      final GuidedDecisionTableModellerView.Presenter modeller) {

        final GuidedDecisionTableSearchableElement searchableElement = new GuidedDecisionTableSearchableElement();

        searchableElement.setHighlightHelper(highlightHelper);
        searchableElement.setModeller(modeller);
        searchableElement.setValue(cellUtilities.asString(cellValue52));
        searchableElement.setRow(row);
        searchableElement.setColumn(column);
        searchableElement.setWidget(widget);
        searchableElement.setModel(model);

        return searchableElement;
    }
}
