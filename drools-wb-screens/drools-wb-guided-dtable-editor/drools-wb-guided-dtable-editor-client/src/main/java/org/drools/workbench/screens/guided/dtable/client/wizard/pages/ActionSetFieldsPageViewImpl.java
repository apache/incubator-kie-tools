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
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.DTCellValueWidgetFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.Validator;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.cells.ActionSetFieldCell;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.cells.ActionSetFieldPatternCell;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.cells.AvailableFieldCell;
import org.kie.workbench.common.widgets.client.resources.WizardCellListResources;
import org.kie.workbench.common.widgets.client.resources.WizardResources;

/**
 * An implementation of the ActionSetFields page
 */
@Dependent
public class ActionSetFieldsPageViewImpl extends Composite
        implements
        ActionSetFieldsPageView {

    private Presenter presenter;

    private Validator validator;

    private List<Pattern52> availablePatterns;
    private Pattern52 availablePatternsSelection;
    private MinimumWidthCellList<Pattern52> availablePatternsWidget;

    private Set<AvailableField> availableFieldsSelections;
    private MinimumWidthCellList<AvailableField> availableFieldsWidget;

    private List<ActionSetFieldCol52> chosenFields;
    private ActionSetFieldCol52 chosenFieldsSelection;
    private Set<ActionSetFieldCol52> chosenFieldsSelections;
    private MinimumWidthCellList<ActionSetFieldCol52> chosenFieldsWidget;

    private DTCellValueWidgetFactory factory;

    @UiField
    protected ScrollPanel availablePatternsContainer;

    @UiField
    protected ScrollPanel availableFieldsContainer;

    @UiField
    protected ScrollPanel chosenFieldsContainer;

    @UiField
    protected PushButton btnAdd;

    @UiField
    protected PushButton btnRemove;

    @UiField
    VerticalPanel fieldDefinition;

    @UiField
    TextBox txtColumnHeader;

    @UiField
    HorizontalPanel columnHeaderContainer;

    @UiField
    TextBox txtValueList;

    @UiField
    CheckBox chkUpdateEngine;

    @UiField
    HorizontalPanel msgDuplicateBindings;

    @UiField
    HorizontalPanel msgIncompleteActionSetFields;

    @UiField
    VerticalPanel criteriaExtendedEntry;

    @UiField
    VerticalPanel criteriaLimitedEntry;

    @UiField
    HorizontalPanel limitedEntryValueContainer;

    @UiField
    SimplePanel limitedEntryValueWidgetContainer;

    @UiField
    HorizontalPanel defaultValueContainer;

    @UiField
    SimplePanel defaultValueWidgetContainer;

    @New
    @Inject
    private ActionSetFieldPatternCell actionSetFieldPatternCell;

    @New
    @Inject
    private ActionSetFieldCell actionSetFieldCell;

    interface ActionSetFieldPageWidgetBinder
            extends
            UiBinder<Widget, ActionSetFieldsPageViewImpl> {

    }

    private static ActionSetFieldPageWidgetBinder uiBinder = GWT.create( ActionSetFieldPageWidgetBinder.class );

    public ActionSetFieldsPageViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    public void setup() {
        this.availablePatternsWidget = new MinimumWidthCellList<Pattern52>( actionSetFieldPatternCell,
                                                                            WizardCellListResources.INSTANCE );
        this.availableFieldsWidget = new MinimumWidthCellList<AvailableField>( new AvailableFieldCell(),
                                                                               WizardCellListResources.INSTANCE );
        this.chosenFieldsWidget = new MinimumWidthCellList<ActionSetFieldCol52>( actionSetFieldCell,
                                                                                 WizardCellListResources.INSTANCE );
        initialiseAvailablePatterns();
        initialiseAvailableFields();
        initialiseChosenFields();
        initialiseColumnHeader();
        initialiseValueList();
        initialiseUpdateEngine();
    }

    @Override
    public void setValidator( final Validator validator ) {
        this.validator = validator;
        this.actionSetFieldPatternCell.setValidator( validator );
        this.actionSetFieldCell.setValidator( validator );
    }

    private void initialiseAvailablePatterns() {
        availablePatternsContainer.add( availablePatternsWidget );
        availablePatternsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        availablePatternsWidget.setMinimumWidth( 180 );

        final Label lstEmpty = new Label( GuidedDecisionTableConstants.INSTANCE.DecisionTableWizardNoAvailablePatterns() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        availablePatternsWidget.setEmptyListWidget( lstEmpty );

        final SingleSelectionModel<Pattern52> selectionModel = new SingleSelectionModel<Pattern52>();
        availablePatternsWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange( final SelectionChangeEvent event ) {
                availablePatternsSelection = selectionModel.getSelectedObject();
                presenter.selectPattern( availablePatternsSelection );
            }

        } );
    }

    private void initialiseAvailableFields() {
        availableFieldsContainer.add( availableFieldsWidget );
        availableFieldsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        availableFieldsWidget.setMinimumWidth( 175 );

        final Label lstEmpty = new Label( GuidedDecisionTableConstants.INSTANCE.DecisionTableWizardNoAvailableFields() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        availableFieldsWidget.setEmptyListWidget( lstEmpty );

        final MultiSelectionModel<AvailableField> selectionModel = new MultiSelectionModel<AvailableField>();
        availableFieldsWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange( final SelectionChangeEvent event ) {
                availableFieldsSelections = selectionModel.getSelectedSet();
                btnAdd.setEnabled( availableFieldsSelections.size() > 0 );
            }

        } );
    }

    private void initialiseChosenFields() {
        chosenFieldsContainer.add( chosenFieldsWidget );
        chosenFieldsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        chosenFieldsWidget.setMinimumWidth( 175 );

        final Label lstEmpty = new Label( GuidedDecisionTableConstants.INSTANCE.DecisionTableWizardNoChosenFields() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        chosenFieldsWidget.setEmptyListWidget( lstEmpty );

        final MultiSelectionModel<ActionSetFieldCol52> selectionModel = new MultiSelectionModel<ActionSetFieldCol52>();
        chosenFieldsWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange( final SelectionChangeEvent event ) {
                chosenFieldsSelections = selectionModel.getSelectedSet();
                chosenConditionsSelected( chosenFieldsSelections );
            }

            private void chosenConditionsSelected( final Set<ActionSetFieldCol52> cws ) {
                btnRemove.setEnabled( true );
                if ( cws.size() == 1 ) {
                    chosenFieldsSelection = cws.iterator().next();
                    fieldDefinition.setVisible( true );
                    validateFieldHeader();
                    populateFieldDefinition();
                } else {
                    chosenFieldsSelection = null;
                    fieldDefinition.setVisible( false );
                    txtColumnHeader.setEnabled( false );
                    txtValueList.setEnabled( false );
                    defaultValueContainer.setVisible( false );
                    chkUpdateEngine.setEnabled( false );
                }
            }

            private void populateFieldDefinition() {

                // Fields common to all table formats
                txtColumnHeader.setEnabled( true );
                chkUpdateEngine.setEnabled( true );
                txtColumnHeader.setText( chosenFieldsSelection.getHeader() );
                chkUpdateEngine.setValue( chosenFieldsSelection.isUpdate() );

                criteriaExtendedEntry.setVisible( presenter.getTableFormat() == GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
                criteriaLimitedEntry.setVisible( presenter.getTableFormat() == GuidedDecisionTable52.TableFormat.LIMITED_ENTRY );

                // Fields specific to the table format
                switch ( presenter.getTableFormat() ) {
                    case EXTENDED_ENTRY:
                        txtValueList.setEnabled( !presenter.hasEnums( chosenFieldsSelection ) );
                        txtValueList.setText( chosenFieldsSelection.getValueList() );
                        makeDefaultValueWidget();
                        defaultValueContainer.setVisible( true );
                        break;
                    case LIMITED_ENTRY:
                        makeLimitedValueWidget();
                        limitedEntryValueContainer.setVisible( true );
                        break;
                }
            }

            private void makeLimitedValueWidget() {
                if ( !( chosenFieldsSelection instanceof LimitedEntryActionSetFieldCol52 ) ) {
                    return;
                }
                final LimitedEntryActionSetFieldCol52 lea = (LimitedEntryActionSetFieldCol52) chosenFieldsSelection;
                if ( lea.getValue() == null ) {
                    lea.setValue( factory.makeNewValue( availablePatternsSelection,
                                                        chosenFieldsSelection ) );
                }
                limitedEntryValueWidgetContainer.setWidget( factory.getWidget( availablePatternsSelection,
                                                                               chosenFieldsSelection,
                                                                               lea.getValue() ) );
            }

        } );
    }

    private void makeDefaultValueWidget() {
        if ( chosenFieldsSelection.getDefaultValue() == null ) {
            chosenFieldsSelection.setDefaultValue( factory.makeNewValue( availablePatternsSelection,
                                                                         chosenFieldsSelection ) );
        }
        defaultValueWidgetContainer.setWidget( factory.getWidget( availablePatternsSelection,
                                                                  chosenFieldsSelection,
                                                                  chosenFieldsSelection.getDefaultValue() ) );
    }

    private void validateFieldHeader() {
        if ( validator.isActionHeaderValid( chosenFieldsSelection ) ) {
            columnHeaderContainer.setStyleName( WizardResources.INSTANCE.css().wizardDTableFieldContainerValid() );
        } else {
            columnHeaderContainer.setStyleName( WizardResources.INSTANCE.css().wizardDTableFieldContainerInvalid() );
        }
    }

    private void initialiseColumnHeader() {
        txtColumnHeader.addValueChangeHandler( new ValueChangeHandler<String>() {

            @Override
            public void onValueChange( ValueChangeEvent<String> event ) {
                final String header = txtColumnHeader.getText();
                chosenFieldsSelection.setHeader( header );
                presenter.stateChanged();
                validateFieldHeader();
            }

        } );
    }

    private void initialiseValueList() {

        //Copy value back to model
        txtValueList.addValueChangeHandler( new ValueChangeHandler<String>() {

            @Override
            public void onValueChange( final ValueChangeEvent<String> event ) {
                final String valueList = txtValueList.getText();
                chosenFieldsSelection.setValueList( valueList );
                // ValueList is optional, no need to advise of state change
            }

        } );

        //Update Default Value widget if necessary
        txtValueList.addBlurHandler( new BlurHandler() {

            @Override
            public void onBlur( final BlurEvent event ) {
                presenter.assertDefaultValue( availablePatternsSelection,
                                              chosenFieldsSelection );
                makeDefaultValueWidget();
            }

        } );

    }

    private void initialiseUpdateEngine() {
        chkUpdateEngine.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( final ClickEvent event ) {
                chosenFieldsSelection.setUpdate( chkUpdateEngine.getValue() );
            }

        } );
    }

    @Override
    public void init( final ActionSetFieldsPageView.Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setDTCellValueWidgetFactory( final DTCellValueWidgetFactory factory ) {
        this.factory = factory;
    }

    @Override
    public void setArePatternBindingsUnique( final boolean arePatternBindingsUnique ) {
        msgDuplicateBindings.setVisible( !arePatternBindingsUnique );
        availablePatternsWidget.redraw();
    }

    @Override
    public void setAreActionSetFieldsDefined( final boolean areActionSetFieldsDefined ) {
        msgIncompleteActionSetFields.setVisible( !areActionSetFieldsDefined );
        chosenFieldsWidget.redraw();
    }

    @Override
    public void setAvailablePatterns( final List<Pattern52> patterns ) {
        availablePatterns = patterns;
        availablePatternsWidget.setRowCount( availablePatterns.size(),
                                             true );
        availablePatternsWidget.setRowData( availablePatterns );

        if ( availablePatternsSelection != null ) {

            // If the currently selected pattern is no longer available clear selections
            if ( !availablePatterns.contains( availablePatternsSelection ) ) {
                availablePatternsWidget.getSelectionModel().setSelected( availablePatternsSelection,
                                                                         false );
                availablePatternsSelection = null;
                setAvailableFields( new ArrayList<AvailableField>() );
                availableFieldsSelections = null;
                setChosenFields( new ArrayList<ActionSetFieldCol52>() );
                chosenFieldsSelection = null;
                fieldDefinition.setVisible( false );
                msgIncompleteActionSetFields.setVisible( false );
            }
        } else {

            // If no available pattern is selected clear fields
            setAvailableFields( new ArrayList<AvailableField>() );
            setChosenFields( new ArrayList<ActionSetFieldCol52>() );
        }
    }

    @Override
    public void setAvailableFields( final List<AvailableField> fields ) {
        availableFieldsWidget.setRowCount( fields.size(),
                                           true );
        availableFieldsWidget.setRowData( fields );
    }

    @Override
    public void setChosenFields( final List<ActionSetFieldCol52> fields ) {
        chosenFields = fields;
        chosenFieldsWidget.setRowCount( fields.size(),
                                        true );
        chosenFieldsWidget.setRowData( fields );
        fieldDefinition.setVisible( fields.contains( chosenFieldsSelection ) );
        presenter.stateChanged();
    }

    @UiHandler(value = "btnAdd")
    public void btnAddClick( final ClickEvent event ) {
        for ( AvailableField f : availableFieldsSelections ) {
            chosenFields.add( makeNewActionColumn( f ) );
        }
        setChosenFields( chosenFields );
        presenter.stateChanged();
    }

    private ActionSetFieldCol52 makeNewActionColumn( final AvailableField f ) {
        final GuidedDecisionTable52.TableFormat format = presenter.getTableFormat();
        if ( format == GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY ) {
            final ActionSetFieldCol52 a = new ActionSetFieldCol52();
            a.setBoundName( availablePatternsSelection.getBoundName() );
            a.setFactField( f.getName() );
            a.setType( f.getType() );
            return a;
        } else {
            final LimitedEntryActionSetFieldCol52 a = new LimitedEntryActionSetFieldCol52();
            a.setBoundName( availablePatternsSelection.getBoundName() );
            a.setFactField( f.getName() );
            a.setType( f.getType() );
            return a;
        }

    }

    @UiHandler(value = "btnRemove")
    public void btnRemoveClick( final ClickEvent event ) {
        for ( ActionSetFieldCol52 a : chosenFieldsSelections ) {
            chosenFields.remove( a );
        }
        chosenFieldsSelections.clear();
        setChosenFields( chosenFields );
        presenter.stateChanged();

        txtColumnHeader.setText( "" );
        txtValueList.setText( "" );
        defaultValueContainer.setVisible( false );
        fieldDefinition.setVisible( false );
        btnRemove.setEnabled( false );
    }

}
