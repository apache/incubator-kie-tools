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

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.client.callbacks.Callback;

public abstract class BaseConditionColumnDefinitionBuilder extends BaseColumnDefinitionBuilder {

    public BaseConditionColumnDefinitionBuilder( final Caller<GuidedDecisionTableEditorService> service ) {
        super( service );
    }

    protected void generateConditionColumn( final GuidedDecisionTableView.Presenter dtPresenter,
                                            final BaseColumn column,
                                            final Callback<String> afterGenerationCallback ) {
        final GuidedDecisionTable52 existingModel = dtPresenter.getModel();
        final ConditionCol52 cc = (ConditionCol52) column;

        if ( !isConditionPartOfPattern( existingModel,
                                        cc ) ) {
            return;
        }

        final GuidedDecisionTable52 partialModel = new GuidedDecisionTable52();
        final ColumnUtilities columnUtilities = new ColumnUtilities( existingModel,
                                                                     dtPresenter.getDataModelOracle() );
        final Pattern52 p = existingModel.getPattern( cc ).clonePattern();
        p.getChildColumns().clear();
        p.getChildColumns().add( cc );
        partialModel.getConditions().add( p );
        partialModel.getData().add( makeRowData( columnUtilities,
                                                 cc ) );

        generateDefinitionOnServer( partialModel,
                                    dtPresenter.getCurrentPath(),
                                    ( String drl ) -> afterGenerationCallback.callback( getLHS( drl ) ) );
    }

    private boolean isConditionPartOfPattern( final GuidedDecisionTable52 model,
                                              final ConditionCol52 cc ) {
        for ( Pattern52 p : model.getPatterns() ) {
            if ( p.getChildColumns().contains( cc ) ) {
                return true;
            }
        }
        return false;
    }

}
