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

import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.GuidedDecisionTableResources;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.Validator;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.cells.PatternCell;
import org.drools.workbench.screens.guided.rule.client.editor.BindingTextBox;
import org.drools.workbench.screens.guided.rule.client.editor.CEPWindowOperatorsDropdown;
import org.drools.workbench.screens.guided.rule.client.editor.OperatorSelection;
import org.kie.workbench.common.widgets.client.resources.WizardCellListResources;
import org.kie.workbench.common.widgets.client.resources.WizardResources;
import org.uberfire.client.callbacks.Callback;

/**
 * An implementation of the Fact Patterns page
 */
@Dependent
public class FactPatternsPageViewImpl extends Composite
        implements
        FactPatternsPageView {

    private Presenter presenter;

    private Validator validator;

    private Set<String> availableTypesSelections;
    private MinimumWidthCellList<String> availableTypesWidget;

    private List<Pattern52> chosenPatterns;
    private Pattern52 chosenPatternSelection;
    private Set<Pattern52> chosenPatternSelections;
    private MinimumWidthCellList<Pattern52> chosenPatternWidget;

    @UiField
    ScrollPanel availableTypesContainer;

    @UiField
    ScrollPanel chosenPatternsContainer;

    @UiField
    PushButton btnAdd;

    @UiField
    PushButton btnRemove;

    @UiField
    VerticalPanel patternDefinition;

    @UiField
    BindingTextBox txtBinding;

    @UiField
    HorizontalPanel bindingContainer;

    @UiField
    TextBox txtEntryPoint;

    @UiField
    CEPWindowOperatorsDropdown ddCEPWindow;

    @UiField
    HorizontalPanel cepWindowContainer;

    @UiField
    HorizontalPanel msgDuplicateBindings;

    @UiField(provided = true)
    PushButton btnMoveUp = new PushButton( AbstractImagePrototype.create( GuidedDecisionTableResources.INSTANCE.images().shuffleUp() ).createImage() );

    @UiField(provided = true)
    PushButton btnMoveDown = new PushButton( AbstractImagePrototype.create( GuidedDecisionTableResources.INSTANCE.images().shuffleDown() ).createImage() );

    @New
    @Inject
    private PatternCell patternCell;

    interface FactPatternsPageWidgetBinder
            extends
            UiBinder<Widget, FactPatternsPageViewImpl> {

    }

    private static FactPatternsPageWidgetBinder uiBinder = GWT.create( FactPatternsPageWidgetBinder.class );

    public FactPatternsPageViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    public void setup() {
        this.availableTypesWidget = new MinimumWidthCellList<String>( new TextCell(),
                                                                      WizardCellListResources.INSTANCE );
        this.chosenPatternWidget = new MinimumWidthCellList<Pattern52>( patternCell,
                                                                        WizardCellListResources.INSTANCE );
        initialiseAvailableTypes();
        initialiseChosenPatterns();
        initialiseBinding();
        initialiseEntryPoint();
        initialiseCEPWindow();
        initialiseShufflers();
    }

    @Override
    public void setValidator( final Validator validator ) {
        this.validator = validator;
        this.patternCell.setValidator( validator );
    }

    private void initialiseAvailableTypes() {
        availableTypesContainer.add( availableTypesWidget );
        availableTypesWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        availableTypesWidget.setMinimumWidth( 275 );

        final Label lstEmpty = new Label( GuidedDecisionTableConstants.INSTANCE.DecisionTableWizardNoAvailablePatterns() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        availableTypesWidget.setEmptyListWidget( lstEmpty );

        final MultiSelectionModel<String> selectionModel = new MultiSelectionModel<String>();
        availableTypesWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange( final SelectionChangeEvent event ) {
                availableTypesSelections = selectionModel.getSelectedSet();
                btnAdd.setEnabled( availableTypesSelections.size() > 0 );
            }

        } );
    }

    private void initialiseChosenPatterns() {
        chosenPatternsContainer.add( chosenPatternWidget );
        chosenPatternWidget.setKeyboardSelectionPolicy( KeyboardSelectionPolicy.ENABLED );
        chosenPatternWidget.setMinimumWidth( 275 );

        final Label lstEmpty = new Label( GuidedDecisionTableConstants.INSTANCE.DecisionTableWizardNoChosenPatterns() );
        lstEmpty.setStyleName( WizardCellListResources.INSTANCE.cellListStyle().cellListEmptyItem() );
        chosenPatternWidget.setEmptyListWidget( lstEmpty );

        final MultiSelectionModel<Pattern52> selectionModel = new MultiSelectionModel<Pattern52>();
        chosenPatternWidget.setSelectionModel( selectionModel );

        selectionModel.addSelectionChangeHandler( new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange( final SelectionChangeEvent event ) {
                chosenPatternSelections = selectionModel.getSelectedSet();
                chosenTypesSelected( chosenPatternSelections );
            }

            private void chosenTypesSelected( final Set<Pattern52> ps ) {
                btnRemove.setEnabled( true );
                if ( ps.size() == 1 ) {
                    chosenPatternSelection = ps.iterator().next();
                    patternDefinition.setVisible( true );
                    validateBinding();
                    txtBinding.setEnabled( true );
                    txtBinding.setText( chosenPatternSelection.getBoundName() );

                    txtEntryPoint.setEnabled( true );
                    txtEntryPoint.setText( chosenPatternSelection.getEntryPointName() );
                    enableMoveUpButton();
                    enableMoveDownButton();

                    presenter.isPatternEvent( chosenPatternSelection,
                                              new Callback<Boolean>() {
                                                  @Override
                                                  public void callback( final Boolean result ) {
                                                      if ( Boolean.TRUE.equals( result ) ) {
                                                          ddCEPWindow.setCEPWindow( chosenPatternSelection );
                                                          cepWindowContainer.setVisible( true );
                                                      } else {
                                                          cepWindowContainer.setVisible( false );
                                                      }
                                                  }
                                              } );

                } else {
                    chosenPatternSelection = null;
                    patternDefinition.setVisible( false );
                    txtBinding.setEnabled( false );
                    txtBinding.setText( "" );
                    txtEntryPoint.setEnabled( false );
                    txtEntryPoint.setText( "" );
                    btnMoveUp.setEnabled( false );
                    btnMoveDown.setEnabled( false );
                    cepWindowContainer.setVisible( false );
                }
            }

        } );
    }

    private void validateBinding() {
        if ( validator.isPatternBindingUnique( chosenPatternSelection ) ) {
            bindingContainer.setStyleName( WizardResources.INSTANCE.css().wizardDTableFieldContainerValid() );
        } else {
            bindingContainer.setStyleName( WizardResources.INSTANCE.css().wizardDTableFieldContainerInvalid() );
        }
    }

    private void enableMoveUpButton() {
        if ( chosenPatterns == null || chosenPatternSelection == null ) {
            btnMoveUp.setEnabled( false );
            return;
        }
        int index = chosenPatterns.indexOf( chosenPatternSelection );
        btnMoveUp.setEnabled( index > 0 );
    }

    private void enableMoveDownButton() {
        if ( chosenPatterns == null || chosenPatternSelection == null ) {
            btnMoveDown.setEnabled( false );
            return;
        }
        int index = chosenPatterns.indexOf( chosenPatternSelection );
        btnMoveDown.setEnabled( index < chosenPatterns.size() - 1 );
    }

    private void initialiseBinding() {
        txtBinding.addValueChangeHandler( new ValueChangeHandler<String>() {

            @Override
            public void onValueChange( final ValueChangeEvent<String> event ) {
                String binding = txtBinding.getText();
                chosenPatternSelection.setBoundName( binding );
                presenter.stateChanged();
                validateBinding();
            }

        } );
    }

    private void initialiseEntryPoint() {
        txtEntryPoint.addValueChangeHandler( new ValueChangeHandler<String>() {

            @Override
            public void onValueChange( final ValueChangeEvent<String> event ) {
                if ( chosenPatternSelection == null ) {
                    return;
                }
                chosenPatternSelection.setEntryPointName( event.getValue() );
            }

        } );
    }

    private void initialiseCEPWindow() {
        ddCEPWindow.addValueChangeHandler( new ValueChangeHandler<OperatorSelection>() {

            @Override
            public void onValueChange( final ValueChangeEvent<OperatorSelection> event ) {
                if ( chosenPatternSelection == null ) {
                    return;
                }
                OperatorSelection selection = event.getValue();
                String selected = selection.getValue();
                chosenPatternSelection.getWindow().setOperator( selected );
            }

        } );
    }

    private void initialiseShufflers() {
        btnMoveUp.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( final ClickEvent event ) {
                int index = chosenPatterns.indexOf( chosenPatternSelection );
                final Pattern52 p = chosenPatterns.remove( index );
                chosenPatterns.add( index - 1,
                                    p );
                setChosenPatterns( chosenPatterns );
                presenter.setConditionPatterns( chosenPatterns );
            }

        } );
        btnMoveDown.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( final ClickEvent event ) {
                int index = chosenPatterns.indexOf( chosenPatternSelection );
                final Pattern52 p = chosenPatterns.remove( index );
                chosenPatterns.add( index + 1,
                                    p );
                setChosenPatterns( chosenPatterns );
                presenter.setConditionPatterns( chosenPatterns );
            }

        } );
    }

    @Override
    public void setAvailableFactTypes( final List<String> types ) {
        availableTypesWidget.setRowCount( types.size(),
                                          true );
        availableTypesWidget.setRowData( types );
    }

    @Override
    public void setChosenPatterns( final List<Pattern52> types ) {
        chosenPatterns = types;
        chosenPatternWidget.setRowCount( types.size(),
                                         true );
        chosenPatternWidget.setRowData( types );
        enableMoveUpButton();
        enableMoveDownButton();
        presenter.stateChanged();
    }

    @Override
    public void init( final FactPatternsPageView.Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setArePatternBindingsUnique( final boolean arePatternBindingsUnique ) {
        msgDuplicateBindings.setVisible( !arePatternBindingsUnique );
        chosenPatternWidget.redraw();
    }

    @UiHandler(value = "btnAdd")
    public void btnAddClick( final ClickEvent event ) {
        for ( String type : availableTypesSelections ) {
            final Pattern52 pattern = new Pattern52();
            pattern.setFactType( type );
            chosenPatterns.add( pattern );
        }
        setChosenPatterns( chosenPatterns );
        presenter.setConditionPatterns( chosenPatterns );
        presenter.stateChanged();
    }

    @UiHandler(value = "btnRemove")
    public void btnRemoveClick( final ClickEvent event ) {
        boolean allPatternsRemoved = true;
        for ( Pattern52 pattern : chosenPatternSelections ) {
            if ( !validator.canPatternBeRemoved( pattern ) ) {
                allPatternsRemoved = false;
            } else {
                chosenPatterns.remove( pattern );

                //Raise an Event so ActionSetFieldPage can synchronise Patterns
                presenter.signalRemovalOfPattern( pattern );
            }
        }
        if ( !allPatternsRemoved ) {
            Window.alert( GuidedDecisionTableConstants.INSTANCE.UnableToDeletePatterns() );
        }

        chosenPatternSelections.clear();
        setChosenPatterns( chosenPatterns );
        presenter.setConditionPatterns( chosenPatterns );
        presenter.stateChanged();

        txtBinding.setText( "" );
        txtBinding.setEnabled( false );
        txtEntryPoint.setText( "" );
        txtEntryPoint.setEnabled( false );
        btnRemove.setEnabled( false );
        patternDefinition.setVisible( false );
    }

}
