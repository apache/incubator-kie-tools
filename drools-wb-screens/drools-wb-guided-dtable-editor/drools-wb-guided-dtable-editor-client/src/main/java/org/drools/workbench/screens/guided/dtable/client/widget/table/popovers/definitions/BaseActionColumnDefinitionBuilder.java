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

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.client.callbacks.Callback;

public abstract class BaseActionColumnDefinitionBuilder extends BaseColumnDefinitionBuilder {

    public BaseActionColumnDefinitionBuilder( final Caller<GuidedDecisionTableEditorService> service ) {
        super( service );
    }

    protected void generateActionColumn( final GuidedDecisionTableView.Presenter dtPresenter,
                                         final BaseColumn column,
                                         final Callback<String> afterGenerationCallback ) {
        final GuidedDecisionTable52 existingModel = dtPresenter.getModel();
        final GuidedDecisionTable52 partialModel = new GuidedDecisionTable52();
        final ColumnUtilities columnUtilities = new ColumnUtilities( existingModel,
                                                                     dtPresenter.getDataModelOracle() );

        final ActionCol52 ac = (ActionCol52) column;
        partialModel.getActionCols().add( ac );
        partialModel.getData().add( makeRowData( columnUtilities,
                                                 ac ) );

        generateDefinitionOnServer( partialModel,
                                    dtPresenter.getCurrentPath(),
                                    ( String drl ) -> afterGenerationCallback.callback( getRHS( drl ) ) );
    }

}
