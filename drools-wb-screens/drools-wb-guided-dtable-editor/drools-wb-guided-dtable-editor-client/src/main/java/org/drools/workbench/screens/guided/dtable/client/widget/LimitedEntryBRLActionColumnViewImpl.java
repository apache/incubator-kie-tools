/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryBRLActionColumn;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModellerConfiguration;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.services.shared.rulename.RuleNamesService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.backend.vfs.Path;

/**
 * An editor for a Limited Entry BRL Action Columns
 */
public class LimitedEntryBRLActionColumnViewImpl extends AbstractLimitedEntryBRLColumnViewImpl<IAction, BRLActionVariableColumn>
        implements
        LimitedEntryBRLActionColumnView {

    private Presenter presenter;

    public LimitedEntryBRLActionColumnViewImpl( final Path path,
                                                final GuidedDecisionTable52 model,
                                                final AsyncPackageDataModelOracle oracle,
                                                final Caller<RuleNamesService> ruleNameService,
                                                final LimitedEntryBRLActionColumn column,
                                                final EventBus eventBus,
                                                final boolean isNew,
                                                final boolean isReadOnly ) {
        super( path,
               model,
               oracle,
               ruleNameService,
               column,
               eventBus,
               isNew,
               isReadOnly );
    }

    @Override
    public String getTitle() {
        return GuidedDecisionTableConstants.INSTANCE.ActionBRLFragmentConfiguration();
    }

    protected boolean isHeaderUnique( String header ) {
        for ( ActionCol52 o : model.getActionCols() ) {
            if ( o.getHeader().equals( header ) ) {
                return false;
            }
        }
        return true;
    }

    protected BRLRuleModel getRuleModel( BRLColumn<IAction, BRLActionVariableColumn> column ) {
        BRLRuleModel ruleModel = new BRLRuleModel( model );
        List<IAction> definition = column.getDefinition();
        ruleModel.rhs = definition.toArray( new IAction[ definition.size() ] );
        return ruleModel;
    }

    protected RuleModellerConfiguration getRuleModellerConfiguration() {
        return new RuleModellerConfiguration( true,
                                              false,
                                              true,
                                              true );
    }

    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    protected void doInsertColumn() {
        this.editingCol.setDefinition( Arrays.asList( this.ruleModel.rhs ) );
        presenter.insertColumn( (LimitedEntryBRLActionColumn) this.editingCol );
    }

    @Override
    protected void doUpdateColumn() {
        this.editingCol.setDefinition( Arrays.asList( this.ruleModel.rhs ) );
        presenter.updateColumn( (LimitedEntryBRLActionColumn) this.originalCol,
                                (LimitedEntryBRLActionColumn) this.editingCol );
    }

    @Override
    protected BRLColumn<IAction, BRLActionVariableColumn> cloneBRLColumn( BRLColumn<IAction, BRLActionVariableColumn> col ) {
        LimitedEntryBRLActionColumn clone = new LimitedEntryBRLActionColumn();
        clone.setHeader( col.getHeader() );
        clone.setHideColumn( col.isHideColumn() );
        clone.setDefinition( cloneDefinition( col.getDefinition() ) );
        return clone;
    }

    @Override
    protected boolean isDefined() {
        return this.ruleModel.rhs.length > 0;
    }

    private List<IAction> cloneDefinition( List<IAction> definition ) {
        RuleModelCloneVisitor visitor = new RuleModelCloneVisitor();
        RuleModel rm = new RuleModel();
        for ( IAction action : definition ) {
            rm.addRhsItem( action );
        }
        RuleModel rmClone = visitor.visitRuleModel( rm );
        List<IAction> clone = new ArrayList<IAction>();
        for ( IAction action : rmClone.rhs ) {
            clone.add( action );
        }
        return clone;
    }

}
