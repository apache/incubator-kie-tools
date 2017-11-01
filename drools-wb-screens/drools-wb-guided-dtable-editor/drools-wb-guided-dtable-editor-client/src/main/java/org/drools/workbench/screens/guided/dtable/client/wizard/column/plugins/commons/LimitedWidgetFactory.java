/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasValueOptionsPage;

public class LimitedWidgetFactory<T extends BaseDecisionTableColumnPlugin & HasValueOptionsPage> extends BaseWidgetFactory<T> {

    public LimitedWidgetFactory(final T plugin) {
        super(plugin);
    }

    public IsWidget create() {
        final LimitedEntryCol limitedEntryCol = (LimitedEntryCol) getPlugin().editingCol();

        if (limitedEntryCol.getValue() == null) {
            limitedEntryCol.setValue(value());
        }

        return getWidget(limitedEntryCol);
    }

    private IsWidget getWidget(LimitedEntryCol limitedEntryCol) {
        if (editingCol() instanceof LimitedEntryActionSetFieldCol52) {

            return factory().getWidget(editingPattern(),
                                       (LimitedEntryActionSetFieldCol52) limitedEntryCol,
                                       limitedEntryCol.getValue());
        } else if (editingCol() instanceof LimitedEntryConditionCol52) {

            return factory().getWidget(editingPattern(),
                                       (LimitedEntryConditionCol52) limitedEntryCol,
                                       limitedEntryCol.getValue());
        } else if (editingCol() instanceof LimitedEntryActionInsertFactCol52) {

            return factory().getWidget((LimitedEntryActionInsertFactCol52) limitedEntryCol,
                                       limitedEntryCol.getValue());
        }

        throw new UnsupportedOperationException("The column type is not supported by the 'LimitedWidget'");
    }

    private DTCellValue52 value() {
        if (editingCol() instanceof LimitedEntryActionSetFieldCol52) {
            return factory().makeNewValue(editingPattern(),
                                          (LimitedEntryActionSetFieldCol52) editingCol());
        } else if (editingCol() instanceof LimitedEntryConditionCol52) {
            return factory().makeNewValue(editingPattern(),
                                          (LimitedEntryConditionCol52) editingCol());
        } else if (editingCol() instanceof LimitedEntryActionInsertFactCol52) {
            return factory().makeNewValue(editingCol());
        }

        throw new UnsupportedOperationException("The column type is not supported by the 'LimitedWidget'");
    }

    private DTColumnConfig52 editingCol() {
        return getPlugin().editingCol();
    }

    private Pattern52 editingPattern() {
        return getPlugin().editingPattern();
    }
}
