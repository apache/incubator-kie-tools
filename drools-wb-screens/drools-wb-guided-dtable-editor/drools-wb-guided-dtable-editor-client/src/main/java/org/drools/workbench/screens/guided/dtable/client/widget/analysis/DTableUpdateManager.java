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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.plugin.client.Coordinate;
import org.drools.workbench.services.verifier.plugin.client.api.DeleteColumns;
import org.drools.workbench.services.verifier.plugin.client.api.MakeRule;
import org.drools.workbench.services.verifier.plugin.client.api.NewColumn;
import org.drools.workbench.services.verifier.plugin.client.api.RemoveRule;
import org.drools.workbench.services.verifier.plugin.client.api.SortTable;
import org.drools.workbench.services.verifier.plugin.client.api.Update;
import org.drools.workbench.services.verifier.plugin.client.builders.ModelMetaDataEnhancer;
import org.kie.soup.commons.validation.PortablePreconditions;

public class DTableUpdateManager {

    private Poster poster;
    private FieldTypeProducer fieldTypeProducer;

    public DTableUpdateManager(final Poster poster,
                               final FieldTypeProducer fieldTypeProducer) {
        this.poster = PortablePreconditions.checkNotNull("poster",
                                                         poster);
        this.fieldTypeProducer = PortablePreconditions.checkNotNull("fieldTypeProducer",
                                                                    fieldTypeProducer);
    }

    public void update(final GuidedDecisionTable52 model,
                       final List<Coordinate> coordinates) {
        poster.post(new Update(model,
                               coordinates));
    }

    public void newColumn(final GuidedDecisionTable52 model,
                          final int columnIndex) {
        poster.post(new NewColumn(model,
                                  new ModelMetaDataEnhancer(model).getHeaderMetaData(),
                                  fieldTypeProducer.getFactTypes(),
                                  columnIndex));
    }

    public void deleteColumns(final int firstColumnIndex,
                              final int numberOfColumns) {
        poster.post(new DeleteColumns(firstColumnIndex,
                                      numberOfColumns));
    }

    public void removeRule(final Integer rowDeleted) {
        poster.post(new RemoveRule(rowDeleted));
    }

    public void makeRule(final GuidedDecisionTable52 model,
                         final int index) {
        poster.post(new MakeRule(model,
                                 new ModelMetaDataEnhancer(model).getHeaderMetaData(),
                                 fieldTypeProducer.getFactTypes(),
                                 index));
    }

    public void sort(final List<Integer> rowOrder) {
        poster.post(new SortTable(rowOrder));
    }
}
