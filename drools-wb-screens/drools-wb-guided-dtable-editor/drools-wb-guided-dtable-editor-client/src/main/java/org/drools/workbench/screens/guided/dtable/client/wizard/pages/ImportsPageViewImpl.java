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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.kie.workbench.common.widgets.client.resources.WizardCellListResources;
import org.uberfire.client.common.popups.errors.ErrorPopup;

/**
 * An implementation of the Imports page
 */
@Dependent
public class ImportsPageViewImpl extends Composite
        implements
        ImportsPageView {

    private Presenter presenter;

    private List<String> availableImports;
    private Set<String> availableImportsSelections;
    private MinimumWidthCellList<String> availableImportsWidget;
    private MultiSelectionModel<String> availableImportsSelectionModel = new MultiSelectionModel<String>();

    private List<String> chosenImports;
    private Set<String> chosenImportSelections;
    private MinimumWidthCellList<String> chosenImportsWidget;
    private MultiSelectionModel<String> chosenImportsSelectionModel = new MultiSelectionModel<String>();

    @UiField
    ScrollPanel availableImportsContainer;

    @UiField
    ScrollPanel chosenImportsContainer;

    @UiField
    PushButton btnAdd;

    @UiField
    PushButton btnRemove;

    interface ImportsPageWidgetBinder
            extends
            UiBinder<Widget, ImportsPageViewImpl> {

    }

    private static ImportsPageWidgetBinder uiBinder = GWT.create( ImportsPageWidgetBinder.class );

    public ImportsPageViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    public void setup() {
        this.availableImportsWidget = new MinimumWidthCellList<String>( new TextCell(),
                                                                        WizardCellListResources.INSTANCE );
        this.chosenImportsWidget = new MinimumWidthCellList<String>( new TextCell(),
                                                                     WizardCellListResources.INSTANCE );
        initialiseAvailableImports();
        initialiseChosenImports();
    }

    private void initialiseAvailableImports() {
        availableImportsContainer.add( availableImportsWidget );
        availableImportsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        availableImportsWidget.setMinimumWidth( 275 );

        final Label lstEmpty = new Label( GuidedDecisionTableConstants.INSTANCE.DecisionTableWizardNoAvailableImports() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        availableImportsWidget.setEmptyListWidget( lstEmpty );

        availableImportsWidget.setSelectionModel( availableImportsSelectionModel );

        availableImportsSelectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange( final SelectionChangeEvent event ) {
                availableImportsSelections = availableImportsSelectionModel.getSelectedSet();
                btnAdd.setEnabled( availableImportsSelections.size() > 0 );
            }

        } );
    }

    private void initialiseChosenImports() {
        chosenImportsContainer.add( chosenImportsWidget );
        chosenImportsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        chosenImportsWidget.setMinimumWidth( 275 );

        final Label lstEmpty = new Label( GuidedDecisionTableConstants.INSTANCE.DecisionTableWizardNoChosenImports() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        chosenImportsWidget.setEmptyListWidget( lstEmpty );

        chosenImportsWidget.setSelectionModel( chosenImportsSelectionModel );

        chosenImportsSelectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange( final SelectionChangeEvent event ) {
                chosenImportSelections = chosenImportsSelectionModel.getSelectedSet();
                btnRemove.setEnabled( chosenImportSelections.size() > 0 );
            }

        } );
    }

    @Override
    public void setAvailableImports( final List<String> imports ) {
        Collections.sort( imports );
        availableImports = new ArrayList<String>( imports );
        availableImportsWidget.setRowCount( availableImports.size(),
                                            true );
        availableImportsWidget.setRowData( availableImports );
    }

    @Override
    public void setChosenImports( final List<String> imports ) {
        Collections.sort( imports );
        chosenImports = new ArrayList<String>( imports );
        chosenImportsWidget.setRowCount( chosenImports.size(),
                                         true );
        chosenImportsWidget.setRowData( chosenImports );
    }

    @Override
    public void init( final Presenter presenter ) {
        this.presenter = presenter;
    }

    @UiHandler(value = "btnAdd")
    public void btnAddClick( final ClickEvent event ) {
        for ( String imp : availableImportsSelections ) {
            availableImports.remove( imp );
            chosenImports.add( imp );
            presenter.addImport( imp );
        }
        refreshImportsWidgets();
        availableImportsSelections.clear();
        availableImportsSelectionModel.clear();

        btnAdd.setEnabled( false );
    }

    @UiHandler(value = "btnRemove")
    public void btnRemoveClick( final ClickEvent event ) {
        boolean allImportsRemoved = true;
        final Iterator<String> itr = chosenImportSelections.iterator();
        while ( itr.hasNext() ) {
            final String imp = itr.next();
            final boolean importRemoved = presenter.removeImport( imp );
            allImportsRemoved = allImportsRemoved && importRemoved;
            if ( importRemoved ) {
                availableImports.add( imp );
                chosenImports.remove( imp );
                chosenImportsSelectionModel.setSelected( imp,
                                                         false );
                itr.remove();
            }
        }

        refreshImportsWidgets();

        if ( !allImportsRemoved ) {
            ErrorPopup.showMessage( GuidedDecisionTableConstants.INSTANCE.DecisionTableWizardCannotRemoveImport() );
        }

        btnRemove.setEnabled( !allImportsRemoved );
    }

    private void refreshImportsWidgets() {
        setAvailableImports( availableImports );
        setChosenImports( chosenImports );
    }

}
