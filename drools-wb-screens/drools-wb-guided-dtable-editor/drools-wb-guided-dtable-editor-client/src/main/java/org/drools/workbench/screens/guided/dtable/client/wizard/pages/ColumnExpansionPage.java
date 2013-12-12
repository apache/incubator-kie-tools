/*
 * Copyright 2011 JBoss Inc
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
package org.drools.workbench.screens.guided.dtable.client.wizard.pages;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.events.ConditionsDefinedEvent;
import org.uberfire.client.callbacks.Callback;

/**
 * A page for the guided Decision Table Wizard to define which columns will be
 * expanded when the Decision Table is generated
 */
@Dependent
public class ColumnExpansionPage extends AbstractGuidedDecisionTableWizardPage
        implements
        ColumnExpansionPageView.Presenter {

    @Inject
    private ColumnExpansionPageView view;

    private List<ConditionCol52> columnsToExpand = null;

    @Override
    public String getTitle() {
        return GuidedDecisionTableConstants.INSTANCE.DecisionTableWizardColumnExpansion();
    }

    @Override
    public void initialise() {
        view.init( this );
        view.setValidator( getValidator() );
        view.setChosenColumns( new ArrayList<ConditionCol52>() );
        content.setWidget( view );
        columnsToExpand = null;
    }

    @Override
    public void prepareView() {
        //Setup the available columns, that could have changed each time this page is visited
        final List<ConditionCol52> availableColumns = findAvailableColumnsToExpand();
        view.setAvailableColumns( availableColumns );
        columnsToExpand = availableColumns;
    }

    private List<ConditionCol52> findAvailableColumnsToExpand() {
        final List<ConditionCol52> availableColumns = new ArrayList<ConditionCol52>();
        for ( CompositeColumn<?> cc : model.getPatterns() ) {
            if ( cc instanceof Pattern52 ) {
                final Pattern52 p = (Pattern52) cc;
                for ( ConditionCol52 c : p.getChildColumns() ) {
                    switch ( model.getTableFormat() ) {
                        case EXTENDED_ENTRY:
                            if ( modelUtils.hasValueList( c ) ) {
                                final String[] values = modelUtils.getValueList( c );
                                if ( values != null && values.length > 1 ) {
                                    availableColumns.add( c );
                                }

                            } else if ( oracle.hasEnums( p.getFactType(),
                                                         c.getFactField() ) ) {
                                availableColumns.add( c );
                            }
                            break;
                        case LIMITED_ENTRY:
                            availableColumns.add( c );
                    }
                }
            }
        }
        return availableColumns;
    }

    @Override
    public void isComplete( final Callback<Boolean> callback ) {
        //Expansion can involve zero or more columns, so the page is always complete
        callback.callback( true );
    }

    public void onConditionsDefined( final @Observes ConditionsDefinedEvent event ) {
        view.setAreConditionsDefined( event.getAreConditionsDefined() );
    }

    @Override
    public void setColumnsToExpand( final List<ConditionCol52> columns ) {
        this.columnsToExpand = columns;
    }

    @Override
    public List<ConditionCol52> getColumnsToExpand() {
        //If the page has not been viewed the default setting is to use all columns
        if ( this.columnsToExpand == null ) {
            return findAvailableColumnsToExpand();
        }

        //Otherwise return those chosen in the UI
        return this.columnsToExpand;
    }

}
