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

package org.drools.workbench.screens.guided.dtable.client.widget.table.popovers.definitions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.jboss.errai.common.client.api.Caller;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.callbacks.Callback;

public abstract class BaseColumnDefinitionBuilder implements ColumnDefinitionBuilder {

    private static final String WHEN = "when\n";
    private static final String THEN = "then\n";
    private static final String END = "end\n";

    private Caller<GuidedDecisionTableEditorService> service;

    public BaseColumnDefinitionBuilder(final Caller<GuidedDecisionTableEditorService> service) {
        this.service = service;
    }

    protected void generateDefinitionOnServer(final GuidedDecisionTable52 partialModel,
                                              final Path path,
                                              final Callback<String> afterGenerationCallback) {
        service.call((String source) -> afterGenerationCallback.callback(source)).toSource(path,
                                                                                           partialModel);
    }

    protected List<DTCellValue52> makeRowData(final ColumnUtilities columnUtilities,
                                              final BaseColumn column) {
        final List<DTCellValue52> row = new ArrayList<>();
        row.add(new DTCellValue52(1));
        row.add(new DTCellValue52(""));
        row.add(new DTCellValue52("desc"));

        final DataType.DataTypes dataType = columnUtilities.getDataType(column);
        row.add(makeCell(dataType));
        return row;
    }

    protected DTCellValue52 makeCell(final DataType.DataTypes dataType) {
        switch (dataType) {
            case NUMERIC:
            case NUMERIC_BIGDECIMAL:
            case NUMERIC_BIGINTEGER:
            case NUMERIC_BYTE:
            case NUMERIC_DOUBLE:
            case NUMERIC_FLOAT:
            case NUMERIC_INTEGER:
            case NUMERIC_LONG:
            case NUMERIC_SHORT:
                return new DTCellValue52(1);
            case DATE:
                return new DTCellValue52(new Date());
            case BOOLEAN:
                return new DTCellValue52(true);
        }
        return new DTCellValue52("x");
    }

    protected String getLHS(final String drl) {
        return strip(drl,
                     WHEN,
                     THEN);
    }

    protected String getRHS(final String drl) {
        return strip(drl,
                     THEN,
                     END);
    }

    protected String strip(final String drl,
                           final String blockStartTag,
                           final String blockEndTag) {
        final String _drl = drl.toLowerCase();
        final int start = _drl.indexOf(blockStartTag);
        final int end = _drl.indexOf(blockEndTag);
        if (start < 0 || end < 0 || end < start) {
            return drl;
        }
        return drl.substring(start + blockStartTag.length(),
                             end).trim().replaceAll("\t",
                                                    "").replaceAll("\n", "<br/>");
    }
}
