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

import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.backend.server.conversion.util.BRLColumnUtil;
import org.drools.workbench.screens.guided.dtable.backend.server.conversion.util.ColumnContext;

import static org.drools.workbench.screens.guided.dtable.backend.server.conversion.util.BRLColumnUtil.canThisColumnBeSplitToMultiple;

public class BRLColumnSubHeaderProvider {

    private SubHeaderBuilder subHeaderBuilder;
    private ColumnContext columnContext;

    public BRLColumnSubHeaderProvider(final SubHeaderBuilder subHeaderBuilder,
                                      final ColumnContext columnContext) {
        this.subHeaderBuilder = subHeaderBuilder;
        this.columnContext = columnContext;
    }

    public void getBRLColumnSubHeaderBuilder(final GuidedDecisionTable52 dtable,
                                             final BRLConditionColumn brlColumn) {
        final BRLColumnSubHeaderBuilder builder = getBuilder(dtable,
                                                             brlColumn);

        builder.buildBrlConditions(brlColumn);
    }

    public void getBRLColumnSubHeaderBuilder(final GuidedDecisionTable52 dtable,
                                             final BRLActionColumn brlActionColumn) {
        final BRLColumnSubHeaderBuilder builder = getBuilder(dtable,
                                                             brlActionColumn);

        builder.buildBrlActions(brlActionColumn);
    }

    private BRLColumnSubHeaderBuilder getBuilder(final GuidedDecisionTable52 dtable,
                                                 final BRLConditionColumn brlColumn) {
        if (canThisColumnBeSplitToMultiple(brlColumn)) {
            return new BRLColumnSubHeaderBuilderByPatterns(subHeaderBuilder,
                                                           columnContext,
                                                           dtable);
        } else {
            return new BRLColumnSubHeaderBuilderDefault(subHeaderBuilder,
                                                        columnContext,
                                                        dtable);
        }
    }

    private BRLColumnSubHeaderBuilder getBuilder(final GuidedDecisionTable52 dtable,
                                                 final BRLActionColumn brlColumn) {
        if (BRLColumnUtil.canThisColumnBeSplitToMultiple(brlColumn)) {
            return new BRLColumnSubHeaderBuilderByPatterns(subHeaderBuilder,
                                                           columnContext,
                                                           dtable);
        } else {
            return new BRLColumnSubHeaderBuilderDefault(subHeaderBuilder,
                                                        columnContext,
                                                        dtable);
        }
    }
}
