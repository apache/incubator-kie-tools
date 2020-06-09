/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.backend.server.conversion;

import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.dtable.backend.server.conversion.util.BRLColumnUtil;
import org.drools.workbench.screens.guided.dtable.backend.server.conversion.util.ColumnContext;

import static org.drools.workbench.screens.guided.dtable.backend.server.conversion.util.BRLColumnUtil.canThisColumnBeSplitToMultiple;

public interface BRLColumnDataBuilder {

    /**
     *
     * @param dataRowBuilder Generic data row builder provides us some build methods.
     * @param brlColumn BRL Column that will be migrated
     * @param columnContext Context of used variables and from where to cell values map to
     * @return Builder for BRL that either builds a single column for several BRL Columns
     *  or splits the columns if possible for better readability in the XLS.
     */
    static BRLColumnDataBuilder make(final DataBuilder.DataRowBuilder dataRowBuilder,
                                     final BRLColumn brlColumn,
                                     final ColumnContext columnContext) {
        if (canTheColumnBeSplitToSeparateColumns(brlColumn)) {
            return new BRLColumnDataBuilderByPatterns(dataRowBuilder,
                                                      columnContext);
        } else {
            return new BRLColumnDataBuilderDefault(dataRowBuilder);
        }
    }

    static boolean canTheColumnBeSplitToSeparateColumns(final BRLColumn brlColumn) {
        return (brlColumn instanceof BRLConditionColumn && canThisColumnBeSplitToMultiple((BRLConditionColumn) brlColumn))
                || (brlColumn instanceof BRLActionColumn && BRLColumnUtil.canThisColumnBeSplitToMultiple((BRLActionColumn) brlColumn));
    }

    void build(final BRLActionColumn baseColumn,
               final List<DTCellValue52> row,
               final Row xlsRow);

    void build(final BRLConditionColumn baseColumn,
               final List<DTCellValue52> row,
               final Row xlsRow);
}
