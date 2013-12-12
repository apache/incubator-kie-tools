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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.RadioButton;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
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
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.utils.DTCellValueUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.DTCellValueWidgetFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.Validator;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.cells.AvailableFieldCell;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.cells.ConditionCell;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.cells.ConditionPatternCell;
import org.drools.workbench.screens.guided.rule.client.editor.CEPOperatorsDropdown;
import org.drools.workbench.screens.guided.rule.client.editor.OperatorSelection;
import org.kie.workbench.common.widgets.client.resources.WizardCellListResources;
import org.kie.workbench.common.widgets.client.resources.WizardResources;
import org.uberfire.client.callbacks.Callback;

/**
 * An implementation of the Fact Patterns Constraints page
 */
@Dependent
public class FactPatternConstraintsPageViewImpl extends Composite
        implements
        FactPatternConstraintsPageView {

    private Presenter presenter;

    private Validator validator;
    private DTCellValueUtilities cellUtils;

    private List<Pattern52> availablePatterns;
    private Pattern52 availablePatternsSelection;
    private MinimumWidthCellList<Pattern52> availablePatternsWidget;

    private Set<AvailableField> availableFieldsSelections;
    private MinimumWidthCellList<AvailableField> availableFieldsWidget;

    private List<ConditionCol52> chosenConditions;
    private ConditionCol52 chosenConditionsSelection;
    private Set<ConditionCol52> chosenConditionsSelections;
    private MinimumWidthCellList<ConditionCol52> chosenConditionsWidget;

    private boolean isOperatorValid;

    private DTCellValueWidgetFactory factory;

    @UiField
    protected ScrollPanel availablePatternsContainer;

    @UiField
    protected ScrollPanel availableFieldsContainer;

    @UiField
    protected ScrollPanel chosenConditionsContainer;

    @UiField
    protected PushButton btnAdd;

    @UiField
    protected PushButton btnRemove;

    @UiField
    VerticalPanel conditionDefinition;

    @UiField
    HorizontalPanel calculationType;

    @UiField
    RadioButton optLiteral;

    @UiField
    RadioButton optFormula;

    @UiField
    RadioButton optPredicate;

    @UiField
    TextBox txtColumnHeader;

    @UiField
    HorizontalPanel columnHeaderContainer;

    @UiField
    TextBox txtPredicateExpression;

    @UiField
    HorizontalPanel predicateExpressionContainer;

    @UiField
    HorizontalPanel operatorContainer;

    @UiField
    SimplePanel ddOperatorContainer;

    @UiField
    TextBox txtValueList;

    @UiField
    HorizontalPanel msgDuplicateBindings;

    @UiField
    HorizontalPanel msgIncompleteConditions;

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

    @UiField(provided = true)
    PushButton btnMoveUp = new PushButton( AbstractImagePrototype.create( GuidedDecisionTableResources.INSTANCE.images().shuffleUp() ).createImage() );

    @UiField(provided = true)
    PushButton btnMoveDown = new PushButton( AbstractImagePrototype.create( GuidedDecisionTableResources.INSTANCE.images().shuffleDown() ).createImage() );

    @New
    @Inject
    private ConditionPatternCell availableConditionsCell;

    @New
    @Inject
    private ConditionCell chosenConditionsCell;

    interface FactPatternConstraintsPageWidgetBinder
            extends
            UiBinder<Widget, FactPatternConstraintsPageViewImpl> {

    }

    private static FactPatternConstraintsPageWidgetBinder uiBinder = GWT.create( FactPatternConstraintsPageWidgetBinder.class );

    public FactPatternConstraintsPageViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    public void setup() {
        this.availablePatternsWidget = new MinimumWidthCellList<Pattern52>( availableConditionsCell,
                                                                            WizardCellListResources.INSTANCE );
        this.availableFieldsWidget = new MinimumWidthCellList<AvailableField>( new AvailableFieldCell(),
                                                                               WizardCellListResources.INSTANCE );
        this.chosenConditionsWidget = new MinimumWidthCellList<ConditionCol52>( chosenConditionsCell,
                                                                                WizardCellListResources.INSTANCE );
        initialiseAvailablePatterns();
        initialiseAvailableFields();
        initialiseChosenFields();
        initialiseCalculationTypes();
        initialiseColumnHeader();
        initialisePredicateExpression();
        initialiseValueList();
        initialiseShufflers();
    }

    @Override
    public void setValidator( final Validator validator ) {
        this.validator = validator;
        this.availableConditionsCell.setValidator( validator );
        this.chosenConditionsCell.setValidator( validator );
    }

    @Override
    public void setDTCellValueUtilities( final DTCellValueUtilities cellUtils ) {
        this.cellUtils = cellUtils;
    }

    private void initialiseAvailablePatterns() {
        availablePatternsContainer.add( availablePatternsWidget );
        availablePatternsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        availablePatternsWidget.setMinimumWidth( 175 );

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
        chosenConditionsContainer.add( chosenConditionsWidget );
        chosenConditionsWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        chosenConditionsWidget.setMinimumWidth( 175 );

        final Label lstEmpty = new Label( GuidedDecisionTableConstants.INSTANCE.DecisionTableWizardNoChosenFields() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        chosenConditionsWidget.setEmptyListWidget( lstEmpty );

        final MultiSelectionModel<ConditionCol52> selectionModel = new MultiSelectionModel<ConditionCol52>();
        chosenConditionsWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange( final SelectionChangeEvent event ) {
                chosenConditionsSelections = new HashSet<ConditionCol52>();
                final Set<ConditionCol52> selections = selectionModel.getSelectedSet();
                for ( ConditionCol52 c : selections ) {
                    chosenConditionsSelections.add( c );
                }
                chosenConditionsSelected( chosenConditionsSelections );
            }

            private void chosenConditionsSelected( final Set<ConditionCol52> cws ) {
                btnRemove.setEnabled( true );
                if ( cws.size() == 1 ) {
                    chosenConditionsSelection = cws.iterator().next();
                    conditionDefinition.setVisible( true );
                    validateConditionHeader();
                    validateConditionOperator();
                    populateConditionDefinition();
                    enableMoveUpButton();
                    enableMoveDownButton();
                } else {
                    chosenConditionsSelection = null;
                    conditionDefinition.setVisible( false );
                    optLiteral.setEnabled( false );
                    optFormula.setEnabled( false );
                    optPredicate.setEnabled( false );
                    txtColumnHeader.setEnabled( false );
                    txtValueList.setEnabled( false );
                    defaultValueContainer.setVisible( false );
                    btnMoveUp.setEnabled( false );
                    btnMoveDown.setEnabled( false );
                }
            }

            private void displayCalculationTypes( final Pattern52 selectedPattern,
                                                  final ConditionCol52 selectedCondition ) {
                final boolean isPredicate = ( selectedCondition.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE );
                final boolean hasEnum = presenter.hasEnum( selectedPattern,
                                                           selectedCondition );
                calculationType.setVisible( !isPredicate );
                optLiteral.setEnabled( !isPredicate );
                optLiteral.setVisible( !isPredicate );
                optFormula.setEnabled( !( isPredicate || hasEnum ) );
                optFormula.setVisible( !isPredicate );
                operatorContainer.setVisible( !isPredicate );
                optPredicate.setEnabled( isPredicate );
                optPredicate.setVisible( isPredicate );
                txtPredicateExpression.setEnabled( isPredicate );
                predicateExpressionContainer.setVisible( isPredicate );
            }

            private void populateConditionDefinition() {

                // Fields common to all table formats
                txtColumnHeader.setEnabled( true );
                txtColumnHeader.setText( chosenConditionsSelection.getHeader() );

                presenter.getOperatorCompletions( availablePatternsSelection,
                                                  chosenConditionsSelection,
                                                  new Callback<String[]>() {
                                                      @Override
                                                      public void callback( final String[] ops ) {
                                                          doPopulateConditionDefinition( ops );

                                                      }
                                                  } );
            }

            private void doPopulateConditionDefinition( final String[] ops ) {
                final CEPOperatorsDropdown ddOperator = new CEPOperatorsDropdown( ops,
                                                                                  chosenConditionsSelection );
                ddOperatorContainer.setWidget( ddOperator );

                criteriaExtendedEntry.setVisible( presenter.getTableFormat() == GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY );
                criteriaLimitedEntry.setVisible( presenter.getTableFormat() == GuidedDecisionTable52.TableFormat.LIMITED_ENTRY );

                // Fields specific to the table format
                switch ( presenter.getTableFormat() ) {
                    case EXTENDED_ENTRY:
                        txtValueList.setEnabled( !presenter.requiresValueList( availablePatternsSelection,
                                                                               chosenConditionsSelection ) );
                        txtValueList.setText( chosenConditionsSelection.getValueList() );

                        makeDefaultValueWidget();
                        defaultValueContainer.setVisible( validator.doesOperatorNeedValue( chosenConditionsSelection ) );

                        if ( chosenConditionsSelection.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
                            txtPredicateExpression.setText( chosenConditionsSelection.getFactField() );
                        }

                        ddOperator.addValueChangeHandler( new ValueChangeHandler<OperatorSelection>() {

                            @Override
                            public void onValueChange( ValueChangeEvent<OperatorSelection> event ) {
                                chosenConditionsSelection.setOperator( event.getValue().getValue() );
                                final boolean requiresValueList = presenter.requiresValueList( availablePatternsSelection,
                                                                                               chosenConditionsSelection );
                                txtValueList.setEnabled( requiresValueList );
                                if ( !requiresValueList ) {
                                    txtValueList.setText( "" );
                                } else {
                                    txtValueList.setText( chosenConditionsSelection.getValueList() );
                                }
                                presenter.stateChanged();
                                validateConditionOperator();

                                makeDefaultValueWidget();
                                defaultValueContainer.setVisible( validator.doesOperatorNeedValue( chosenConditionsSelection ) );
                            }

                        } );

                        switch ( chosenConditionsSelection.getConstraintValueType() ) {
                            case BaseSingleFieldConstraint.TYPE_LITERAL:
                                optLiteral.setValue( true );
                                displayCalculationTypes( availablePatternsSelection,
                                                         chosenConditionsSelection );
                                break;
                            case BaseSingleFieldConstraint.TYPE_RET_VALUE:
                                optFormula.setValue( true );
                                displayCalculationTypes( availablePatternsSelection,
                                                         chosenConditionsSelection );
                                break;
                            case BaseSingleFieldConstraint.TYPE_PREDICATE:
                                optPredicate.setValue( true );
                                displayCalculationTypes( availablePatternsSelection,
                                                         chosenConditionsSelection );
                        }
                        break;
                    case LIMITED_ENTRY:
                        calculationType.setVisible( false );
                        makeLimitedValueWidget();

                        // If operator changes the widget used to populate the
                        // value can change
                        ddOperator.addValueChangeHandler( new ValueChangeHandler<OperatorSelection>() {

                            @Override
                            public void onValueChange( ValueChangeEvent<OperatorSelection> event ) {
                                chosenConditionsSelection.setOperator( event.getValue().getValue() );
                                validateConditionOperator();
                                makeLimitedValueWidget();
                                presenter.stateChanged();
                            }

                        } );
                        break;
                }
            }

            private void makeLimitedValueWidget() {
                if ( !( chosenConditionsSelection instanceof LimitedEntryConditionCol52 ) ) {
                    return;
                }
                final LimitedEntryConditionCol52 lec = (LimitedEntryConditionCol52) chosenConditionsSelection;
                boolean doesOperatorNeedValue = validator.doesOperatorNeedValue( chosenConditionsSelection );
                if ( !doesOperatorNeedValue ) {
                    limitedEntryValueContainer.setVisible( false );
                    lec.setValue( null );
                    return;
                }
                limitedEntryValueContainer.setVisible( true );
                if ( lec.getValue() == null ) {
                    lec.setValue( factory.makeNewValue( chosenConditionsSelection ) );
                }
                limitedEntryValueWidgetContainer.setWidget( factory.getWidget( availablePatternsSelection,
                                                                               chosenConditionsSelection,
                                                                               lec.getValue() ) );
            }

        } );
    }

    private void makeDefaultValueWidget() {
        DTCellValue52 defaultValue = chosenConditionsSelection.getDefaultValue();
        if ( defaultValue == null ) {
            defaultValue = factory.makeNewValue( chosenConditionsSelection );
            chosenConditionsSelection.setDefaultValue( defaultValue );
        }

        //Correct comma-separated Default Value if operator does not support it
        if ( !validator.doesOperatorAcceptCommaSeparatedValues( chosenConditionsSelection ) ) {
            cellUtils.removeCommaSeparatedValue( defaultValue );
        }

        defaultValueWidgetContainer.setWidget( factory.getWidget( availablePatternsSelection,
                                                                  chosenConditionsSelection,
                                                                  defaultValue ) );
    }

    private void validateConditionHeader() {
        if ( validator.isConditionHeaderValid( chosenConditionsSelection ) ) {
            columnHeaderContainer.setStyleName( WizardResources.INSTANCE.css().wizardDTableFieldContainerValid() );
        } else {
            columnHeaderContainer.setStyleName( WizardResources.INSTANCE.css().wizardDTableFieldContainerInvalid() );
        }
    }

    private void validateConditionOperator() {
        isOperatorValid = validator.isConditionOperatorValid( chosenConditionsSelection );
        if ( isOperatorValid ) {
            operatorContainer.setStyleName( WizardResources.INSTANCE.css().wizardDTableFieldContainerValid() );
        } else {
            operatorContainer.setStyleName( WizardResources.INSTANCE.css().wizardDTableFieldContainerInvalid() );
        }
    }

    private void initialiseCalculationTypes() {
        optLiteral.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( final ClickEvent w ) {
                chosenConditionsSelection.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
                chosenConditionsWidget.redraw();
                presenter.assertDefaultValue( availablePatternsSelection,
                                              chosenConditionsSelection );
                makeDefaultValueWidget();
            }
        } );

        optFormula.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( final ClickEvent w ) {
                chosenConditionsSelection.setConstraintValueType( BaseSingleFieldConstraint.TYPE_RET_VALUE );
                chosenConditionsWidget.redraw();
                presenter.assertDefaultValue( availablePatternsSelection,
                                              chosenConditionsSelection );
                makeDefaultValueWidget();
            }
        } );
        optPredicate.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( final ClickEvent w ) {
                chosenConditionsSelection.setConstraintValueType( BaseSingleFieldConstraint.TYPE_PREDICATE );
                chosenConditionsWidget.redraw();
                presenter.assertDefaultValue( availablePatternsSelection,
                                              chosenConditionsSelection );
                makeDefaultValueWidget();
            }
        } );

    }

    private void initialiseColumnHeader() {
        txtColumnHeader.addValueChangeHandler( new ValueChangeHandler<String>() {

            @Override
            public void onValueChange( final ValueChangeEvent<String> event ) {
                final String header = txtColumnHeader.getText();
                chosenConditionsSelection.setHeader( header );
                presenter.stateChanged();
                validateConditionHeader();
            }

        } );
    }

    private void initialisePredicateExpression() {
        txtPredicateExpression.addValueChangeHandler( new ValueChangeHandler<String>() {

            @Override
            public void onValueChange( final ValueChangeEvent<String> event ) {
                final String expression = txtPredicateExpression.getText();
                chosenConditionsSelection.setFactField( expression );

                // Redraw list widget that shows Predicate expressions
                chosenConditionsWidget.redraw();

            }

        } );
    }

    private void initialiseValueList() {

        //Copy value back to model
        txtValueList.addValueChangeHandler( new ValueChangeHandler<String>() {

            @Override
            public void onValueChange( final ValueChangeEvent<String> event ) {
                final String valueList = txtValueList.getText();
                chosenConditionsSelection.setValueList( valueList );
                // ValueList is optional, no need to advise of state change
            }

        } );

        //Update Default Value widget if necessary
        txtValueList.addBlurHandler( new BlurHandler() {

            @Override
            public void onBlur( final BlurEvent event ) {
                presenter.assertDefaultValue( availablePatternsSelection,
                                              chosenConditionsSelection );
                makeDefaultValueWidget();
            }
        } );

    }

    private void initialiseShufflers() {
        btnMoveUp.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( final ClickEvent event ) {
                final int index = chosenConditions.indexOf( chosenConditionsSelection );
                final ConditionCol52 c = chosenConditions.remove( index );
                chosenConditions.add( index - 1,
                                      c );
                setChosenConditions( chosenConditions );
                availablePatternsSelection.setChildColumns( chosenConditions );
            }

        } );
        btnMoveDown.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( final ClickEvent event ) {
                final int index = chosenConditions.indexOf( chosenConditionsSelection );
                final ConditionCol52 c = chosenConditions.remove( index );
                chosenConditions.add( index + 1,
                                      c );
                setChosenConditions( chosenConditions );
                availablePatternsSelection.setChildColumns( chosenConditions );
            }

        } );
    }

    private void enableMoveUpButton() {
        if ( chosenConditions == null || chosenConditionsSelection == null ) {
            btnMoveUp.setEnabled( false );
            return;
        }
        int index = chosenConditions.indexOf( chosenConditionsSelection );
        btnMoveUp.setEnabled( index > 0 );
    }

    private void enableMoveDownButton() {
        if ( chosenConditions == null || chosenConditionsSelection == null ) {
            btnMoveDown.setEnabled( false );
            return;
        }
        int index = chosenConditions.indexOf( chosenConditionsSelection );
        btnMoveDown.setEnabled( index < chosenConditions.size() - 1 );
    }

    @Override
    public void init( final FactPatternConstraintsPageView.Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setDTCellValueWidgetFactory( final DTCellValueWidgetFactory factory ) {
        this.factory = factory;
    }

    @Override
    public void setAreConditionsDefined( final boolean areConditionsDefined ) {
        msgIncompleteConditions.setVisible( !areConditionsDefined );
        chosenConditionsWidget.redraw();
        availablePatternsWidget.redraw();
    }

    @Override
    public void setArePatternBindingsUnique( final boolean arePatternBindingsUnique ) {
        msgDuplicateBindings.setVisible( !arePatternBindingsUnique );
        availablePatternsWidget.redraw();
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
                setChosenConditions( new ArrayList<ConditionCol52>() );
                chosenConditionsSelection = null;
                conditionDefinition.setVisible( false );
                msgIncompleteConditions.setVisible( false );
            }
        } else {

            // If no available pattern is selected clear fields
            setAvailableFields( new ArrayList<AvailableField>() );
            setChosenConditions( new ArrayList<ConditionCol52>() );
        }
    }

    @Override
    public void setAvailableFields( final List<AvailableField> fields ) {
        availableFieldsWidget.setRowCount( fields.size(),
                                           true );
        availableFieldsWidget.setRowData( fields );
    }

    @Override
    public void setChosenConditions( final List<ConditionCol52> conditions ) {
        chosenConditions = conditions;
        chosenConditionsWidget.setRowCount( conditions.size(),
                                            true );
        chosenConditionsWidget.setRowData( conditions );
        conditionDefinition.setVisible( conditions.contains( chosenConditionsSelection ) );
        enableMoveUpButton();
        enableMoveDownButton();
        presenter.stateChanged();
    }

    @UiHandler(value = "btnAdd")
    public void btnAddClick( final ClickEvent event ) {
        for ( AvailableField f : availableFieldsSelections ) {
            chosenConditions.add( makeNewConditionColumn( f ) );
        }
        setChosenConditions( chosenConditions );
        availablePatternsSelection.setChildColumns( chosenConditions );
        presenter.stateChanged();
    }

    private ConditionCol52 makeNewConditionColumn( final AvailableField f ) {
        final GuidedDecisionTable52.TableFormat format = presenter.getTableFormat();
        if ( format == GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY ) {
            final ConditionCol52 c = new ConditionCol52();
            c.setFactField( f.getName() );
            c.setFieldType( f.getType() );
            c.setConstraintValueType( f.getCalculationType() );
            return c;
        } else {
            final LimitedEntryConditionCol52 c = new LimitedEntryConditionCol52();
            c.setFactField( f.getName() );
            c.setFieldType( f.getType() );
            c.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
            return c;
        }

    }

    @UiHandler(value = "btnRemove")
    public void btnRemoveClick( final ClickEvent event ) {
        //Don't allow removal if Pattern is used elsewhere and we're removing all constraints
        if ( chosenConditions.size() == chosenConditionsSelections.size() ) {
            if ( !validator.canPatternBeRemoved( availablePatternsSelection ) ) {
                if ( chosenConditions.size() == 1 ) {
                    Window.alert( GuidedDecisionTableConstants.INSTANCE.UnableToDeleteConditionColumn0( chosenConditions.get( 0 ).getHeader() ) );
                } else {
                    Window.alert( GuidedDecisionTableConstants.INSTANCE.UnableToDeleteConditionColumns() );
                }
                return;
            }
        }

        //Otherwise remove constraints
        for ( ConditionCol52 c : chosenConditionsSelections ) {
            chosenConditions.remove( c );
        }
        chosenConditionsSelections.clear();
        setChosenConditions( chosenConditions );
        availablePatternsSelection.setChildColumns( chosenConditions );
        presenter.stateChanged();

        txtColumnHeader.setText( "" );
        txtValueList.setText( "" );
        defaultValueContainer.setVisible( false );
        conditionDefinition.setVisible( false );
        btnRemove.setEnabled( false );
    }

}
