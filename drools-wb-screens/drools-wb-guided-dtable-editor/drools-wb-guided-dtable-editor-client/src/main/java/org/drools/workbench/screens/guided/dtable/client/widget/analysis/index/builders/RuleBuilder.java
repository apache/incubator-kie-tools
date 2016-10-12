/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.builders;

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.index.Index;
import org.drools.workbench.services.verifier.api.client.index.Pattern;
import org.drools.workbench.services.verifier.api.client.index.Rule;
import org.uberfire.commons.validation.PortablePreconditions;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.builders.Utils.*;

public class RuleBuilder {

    private final Index index;
    private final GuidedDecisionTable52 model;
    private final List<DTCellValue52> row;
    private final ColumnUtilities utils;
    private final Rule rule;
    private final AnalyzerConfiguration configuration;

    public RuleBuilder( final Index index,
                        final GuidedDecisionTable52 model,
                        final Integer rowIndex,
                        final List<DTCellValue52> row,
                        final ColumnUtilities utils,
                        final AnalyzerConfiguration configuration ) {
        this.index = PortablePreconditions.checkNotNull( "index",
                                                         index );
        this.model = PortablePreconditions.checkNotNull( "model",
                                                         model );
        this.row = PortablePreconditions.checkNotNull( "row",
                                                       row );
        this.utils = PortablePreconditions.checkNotNull( "utils",
                                                         utils );
        this.rule = new Rule( PortablePreconditions.checkNotNull( "rowIndex",
                                                                  rowIndex ),
                              configuration );
        this.configuration = PortablePreconditions.checkNotNull( "configuration",
                                                                 configuration );
    }

    public Rule build() {

        resolvePatterns();

        return rule;
    }

    private void resolvePatterns() {

        for ( final CompositeColumn<? extends BaseColumn> column : model.getConditions() ) {
            if ( column instanceof Pattern52 ) {

                final Pattern52 pattern52 = (Pattern52) column;

                final Pattern pattern = resolvePattern( index,
                                                        rule,
                                                        pattern52,
                                                        configuration );

                new FieldConditionsBuilder( index,
                                            model,
                                            rule,
                                            row,
                                            utils,
                                            pattern,
                                            configuration ).buildConditions( ( (Pattern52) column ).getChildColumns() );
            } else if ( column instanceof BRLConditionColumn ) {
                new BRLConditionsBuilder( index,
                                          model,
                                          rule,
                                          row,
                                          utils,
                                          configuration ).buildConditions( ( (BRLConditionColumn) column ).getChildColumns() );
            }
        }


        for ( final ActionCol52 actionCol52 : model.getActionCols() ) {
            new ActionBuilder( index,
                               model,
                               rule,
                               row,
                               actionCol52,
                               configuration ).build();
        }


    }

}
