/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.Validator;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.cells.ConditionCell;
import org.kie.workbench.common.widgets.client.resources.WizardCellListResources;

/**
 * An implementation of the Column Expansion page
 */
@Dependent
public class ColumnExpansionPageViewImpl extends Composite
        implements
        ColumnExpansionPageView {

    private Presenter presenter;

    private List<ConditionCol52> availableColumns;
    private Set<ConditionCol52> availableColumnsSelections;
    private MinimumWidthCellList<ConditionCol52> availableColumnsWidget;

    private List<ConditionCol52> chosenColumns = new ArrayList<ConditionCol52>();
    private Set<ConditionCol52> chosenColumnsSelections;
    private MinimumWidthCellList<ConditionCol52> chosenColumnsWidget;

    private MultiSelectionModel<ConditionCol52> availableSelectionModel = new MultiSelectionModel<ConditionCol52>();
    private MultiSelectionModel<ConditionCol52> chosenSelectionModel = new MultiSelectionModel<ConditionCol52>();

    private boolean isFullyExpanded = true;

    @UiField
    CheckBox chkExpandInFull;

    @UiField
    HorizontalPanel columnSelectorContainer;

    @UiField
    ScrollPanel availableColumnsContainer;

    @UiField
    ScrollPanel chosenColumnsContainer;

    @UiField
    PushButton btnAdd;

    @UiField
    PushButton btnRemove;

    @UiField
    HorizontalPanel msgIncompleteConditions;

    @New
    @Inject
    private ConditionCell availableConditionsCell;

    @New
    @Inject
    private ConditionCell chosenConditionsCell;

    interface ColumnExpansionPageWidgetBinder
            extends
            UiBinder<Widget, ColumnExpansionPageViewImpl> {

    }

    private static ColumnExpansionPageWidgetBinder uiBinder = GWT.create( ColumnExpansionPageWidgetBinder.class );

    public ColumnExpansionPageViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    public void setup() {
        this.availableColumnsWidget = new MinimumWidthCellList<ConditionCol52>( availableConditionsCell,
                                                                                WizardCellListResources.INSTANCE );
        this.chosenColumnsWidget = new MinimumWidthCellList<ConditionCol52>( chosenConditionsCell,
                                                                             WizardCellListResources.INSTANCE );
        initialiseAvailableColumns();
        initialiseChosenColumns();
        initialiseExpandInFull();
    }

    @Override
    public void setValidator( final Validator validator ) {
        this.availableConditionsCell.setValidator( validator );
        this.chosenConditionsCell.setValidator( validator );
    }

    private void initialiseAvailableColumns() {
        availableColumnsContainer.add( availableColumnsWidget );
        availableColumnsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        availableColumnsWidget.setMinimumWidth( 275 );

        final Label lstEmpty = new Label( GuidedDecisionTableConstants.INSTANCE.DecisionTableWizardNoAvailableColumns() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        availableColumnsWidget.setEmptyListWidget( lstEmpty );

        availableColumnsWidget.setSelectionModel( availableSelectionModel );

        availableSelectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange( final SelectionChangeEvent event ) {
                availableColumnsSelections = availableSelectionModel.getSelectedSet();
                btnAdd.setEnabled( availableColumnsSelections.size() > 0 );
            }

        } );
    }

    private void initialiseChosenColumns() {
        chosenColumnsContainer.add( chosenColumnsWidget );
        chosenColumnsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        chosenColumnsWidget.setMinimumWidth( 275 );

        final Label lstEmpty = new Label( GuidedDecisionTableConstants.INSTANCE.DecisionTableWizardNoChosenColumns() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        chosenColumnsWidget.setEmptyListWidget( lstEmpty );

        chosenColumnsWidget.setSelectionModel( chosenSelectionModel );

        chosenSelectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange( final SelectionChangeEvent event ) {
                chosenColumnsSelections = chosenSelectionModel.getSelectedSet();
                btnRemove.setEnabled( chosenColumnsSelections.size() > 0 );
            }

        } );
    }

    private void initialiseExpandInFull() {
        chkExpandInFull.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( final ClickEvent event ) {
                isFullyExpanded = chkExpandInFull.getValue();
                columnSelectorContainer.setVisible( !isFullyExpanded );
                presenter.setColumnsToExpand( getColumnsToExpand() );
            }

        } );
    }

    @Override
    public void setChosenColumns( final List<ConditionCol52> columns ) {
        chosenColumnsWidget.setRowCount( columns.size(),
                                         true );
        chosenColumnsWidget.setRowData( columns );
    }

    @Override
    public void setAvailableColumns( final List<ConditionCol52> columns ) {
        availableColumns = columns;
        availableColumns.removeAll( chosenColumns );
        availableColumnsWidget.setRowCount( columns.size(),
                                            true );
        availableColumnsWidget.setRowData( columns );
    }

    @Override
    public void init( final ColumnExpansionPageView.Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setAreConditionsDefined( final boolean areConditionsDefined ) {
        msgIncompleteConditions.setVisible( !areConditionsDefined );
        availableColumnsWidget.redraw();
        chosenColumnsWidget.redraw();
    }

    private List<ConditionCol52> getColumnsToExpand() {
        final List<ConditionCol52> columns = new ArrayList<ConditionCol52>();
        if ( isFullyExpanded ) {
            columns.addAll( availableColumns );
        }
        columns.addAll( chosenColumns );
        return columns;
    }

    @UiHandler(value = "btnAdd")
    public void btnAddClick( final ClickEvent event ) {
        for ( ConditionCol52 column : availableColumnsSelections ) {
            chosenColumns.add( column );
            availableColumns.remove( column );
        }
        availableSelectionModel.clear();
        availableColumnsSelections.clear();
        setChosenColumns( chosenColumns );
        setAvailableColumns( availableColumns );
        presenter.setColumnsToExpand( getColumnsToExpand() );
        btnAdd.setEnabled( false );
    }

    @UiHandler(value = "btnRemove")
    public void btnRemoveClick( final ClickEvent event ) {
        for ( ConditionCol52 column : chosenColumnsSelections ) {
            chosenColumns.remove( column );
            availableColumns.add( column );
        }
        chosenSelectionModel.clear();
        chosenColumnsSelections.clear();
        setChosenColumns( chosenColumns );
        setAvailableColumns( availableColumns );
        presenter.setColumnsToExpand( getColumnsToExpand() );
        btnRemove.setEnabled( false );
    }

}
