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

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.linkmanager.impl;

import java.util.Set;
import javax.enterprise.context.Dependent;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.GuidedDecisionTable;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.linkmanager.GuidedDecisionTableLinkManager;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridData;

@Dependent
@GuidedDecisionTable
public class DefaultGuidedDecisionTableLinkManager implements GuidedDecisionTableLinkManager {

    @Override
    public void link( final GuidedDecisionTableView.Presenter dtPresenter,
                      final Set<GuidedDecisionTableView.Presenter> otherDecisionTables ) {
        final GuidedDecisionTable52 model = dtPresenter.getModel();
        final GridData uiModel = dtPresenter.getView().getModel();
        final BRLRuleModel helper = new BRLRuleModel( model );

        //Clear existing links
        for ( GridColumn<?> uiColumn : uiModel.getColumns() ) {
            uiColumn.setLink( null );
        }

        //Re-create links to other Decision Tables
        for ( GuidedDecisionTableView.Presenter otherDecisionTable : otherDecisionTables ) {
            final GuidedDecisionTable52 otherModel = otherDecisionTable.getModel();
            for ( CompositeColumn<? extends BaseColumn> otherDecisionTableConditions : otherModel.getConditions() ) {
                if ( otherDecisionTableConditions instanceof Pattern52 ) {
                    final Pattern52 otherDecisionTablePattern = (Pattern52) otherDecisionTableConditions;
                    for ( ConditionCol52 otherDecisionTableCondition : otherDecisionTablePattern.getChildColumns() ) {
                        final String factType = otherDecisionTablePattern.getFactType();
                        final String fieldName = otherDecisionTableCondition.getFactField();
                        final ActionCol52 linkedActionColumn = getLinkedActionColumn( factType,
                                                                                      fieldName,
                                                                                      model,
                                                                                      helper );
                        if ( linkedActionColumn != null ) {
                            final int sourceColumnIndex = model.getExpandedColumns().indexOf( linkedActionColumn );
                            final int targetColumnIndex = otherModel.getExpandedColumns().indexOf( otherDecisionTableCondition );
                            linkColumns( uiModel,
                                         otherDecisionTable.getView().getModel(),
                                         sourceColumnIndex,
                                         targetColumnIndex );
                        }
                    }

                } else if ( otherDecisionTableConditions instanceof BRLConditionColumn ) {
                    final BRLConditionColumn fragment = (BRLConditionColumn) otherDecisionTableConditions;
                    for ( BRLConditionVariableColumn var : fragment.getChildColumns() ) {
                        final String factType = var.getFactType();
                        final String fieldName = var.getFactField();
                        final ActionCol52 linkedActionColumn = getLinkedActionColumn( factType,
                                                                                      fieldName,
                                                                                      model,
                                                                                      helper );
                        if ( linkedActionColumn != null ) {
                            final int sourceColumnIndex = model.getExpandedColumns().indexOf( linkedActionColumn );
                            final int targetColumnIndex = otherModel.getExpandedColumns().indexOf( var );
                            linkColumns( uiModel,
                                         otherDecisionTable.getView().getModel(),
                                         sourceColumnIndex,
                                         targetColumnIndex );
                        }
                    }
                }
            }
        }
    }

    private ActionCol52 getLinkedActionColumn( final String factType,
                                               final String fieldName,
                                               final GuidedDecisionTable52 model,
                                               final BRLRuleModel helper ) {

        for ( ActionCol52 ac : model.getActionCols() ) {
            if ( ac instanceof ActionInsertFactCol52 ) {
                final ActionInsertFactCol52 aif = (ActionInsertFactCol52) ac;
                if ( aif.getFactType().equals( factType ) && aif.getFactField().equals( fieldName ) ) {
                    return ac;
                }
            } else if ( ac instanceof ActionSetFieldCol52 ) {
                final ActionSetFieldCol52 asf = (ActionSetFieldCol52) ac;
                final String binding = asf.getBoundName();
                final String asfFactType = helper.getLHSBindingType( binding );
                if ( asfFactType.equals( factType ) && asf.getFactField().equals( fieldName ) ) {
                    return ac;
                }
            } else if ( ac instanceof BRLActionColumn ) {
                final BRLActionColumn fragment = (BRLActionColumn) ac;
                for ( BRLActionVariableColumn var : fragment.getChildColumns() ) {
                    if ( var.getFactType().equals( factType ) && var.getFactField().equals( fieldName ) ) {
                        return ac;
                    }
                }
            }
        }
        return null;
    }

    void linkColumns( final GridData sourceUiModel,
                      final GridData targetUiModel,
                      final int sourceColumnIndex,
                      final int targetColumnIndex ) {
        sourceUiModel.getColumns().get( sourceColumnIndex ).setLink( targetUiModel.getColumns().get( targetColumnIndex ) );
    }

}
