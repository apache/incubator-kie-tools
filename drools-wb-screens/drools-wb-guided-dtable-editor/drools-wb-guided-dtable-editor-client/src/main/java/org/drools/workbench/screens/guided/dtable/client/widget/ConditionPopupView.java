/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.HasCEPWindow;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52.TableFormat;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.resources.images.GuidedDecisionTableImageResources508;
import org.drools.workbench.screens.guided.rule.client.editor.BindingTextBox;
import org.drools.workbench.screens.guided.rule.client.editor.CEPWindowOperatorsDropdown;
import org.drools.workbench.screens.guided.rule.client.editor.OperatorSelection;
import org.gwtbootstrap3.client.ui.InlineRadio;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.uberfire.ext.widgets.common.client.common.ImageButton;
import org.uberfire.ext.widgets.common.client.common.InfoPopup;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

/**
 * This is a configuration editor for a column in a the guided decision table.
 */
public class ConditionPopupView extends FormStylePopup {

    protected TextBox header;
    private SmallLabel patternLabel = new SmallLabel();
    protected TextBox fieldLabel = getFieldLabel();
    protected TextBox binding = new BindingTextBox();
    private SmallLabel operatorLabel = new SmallLabel();
    private SimplePanel limitedEntryValueWidgetContainer = new SimplePanel();
    private int limitedEntryValueAttributeIndex = -1;
    protected TextBox valueListWidget;
    private SimplePanel defaultValueWidgetContainer = new SimplePanel();
    private int defaultValueWidgetContainerIndex = -1;
    protected ImageButton editField = null;
    protected ImageButton editOp = null;
    protected ImageButton changePattern = null;

    protected InlineRadio literal = new InlineRadio( "constraintValueType",
                                                   GuidedDecisionTableConstants.INSTANCE.LiteralValue() );
    protected InlineRadio formula = new InlineRadio( "constraintValueType",
                                                   GuidedDecisionTableConstants.INSTANCE.Formula() );
    protected InlineRadio predicate = new InlineRadio( "constraintValueType",
                                                     GuidedDecisionTableConstants.INSTANCE.Predicate() );

    private CEPWindowOperatorsDropdown cwo;
    protected TextBox entryPointName;
    private int cepWindowRowIndex;

    private InfoPopup fieldLabelInterpolationInfo = getPredicateHint();

    private final Command cmdOK = new Command() {
        @Override
        public void execute() {
            presenter.applyChanges();
        }
    };
    private final Command cmdCancel = new Command() {
        @Override
        public void execute() {
            hide();
        }
    };
    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( cmdOK,
                                                                                      cmdCancel );

    private ConditionPopup presenter;

    public ConditionPopupView( ConditionPopup presenter ) {
        super( GuidedDecisionTableConstants.INSTANCE.ConditionColumnConfiguration() );

        this.presenter = presenter;

        changePattern = new ImageButton( GuidedDecisionTableImageResources508.INSTANCE.Edit(),
                GuidedDecisionTableImageResources508.INSTANCE.EditDisabled(),
                GuidedDecisionTableConstants.INSTANCE.ChooseAnExistingPatternThatThisColumnAddsTo()
        );

        editField = new ImageButton( GuidedDecisionTableImageResources508.INSTANCE.Edit(),
                GuidedDecisionTableImageResources508.INSTANCE.EditDisabled(),
                GuidedDecisionTableConstants.INSTANCE.EditTheFieldThatThisColumnOperatesOn() );

        editOp = new ImageButton( GuidedDecisionTableImageResources508.INSTANCE.Edit(),
                GuidedDecisionTableImageResources508.INSTANCE.EditDisabled(),
                GuidedDecisionTableConstants.INSTANCE.EditTheOperatorThatIsUsedToCompareDataWithThisField() );

        entryPointName = new TextBox();
        header = new TextBox();
        valueListWidget = new TextBox();
    }

    public void initializeView() {
        initializeChangePattern();
        initializeTableFormatSpecifics();
        initializeFactField();
        initializeOperator();
        initializeCepWindow();
        initializeEntryPoint();
        initializeColumnHeader();
        initializeOptionalValueList();
        initializeBinding();
        initializeHideColumnTick();
        initializeApplyButton();
    }

    private void initializeChangePattern() {
        HorizontalPanel patternWidget = new HorizontalPanel();
        patternWidget.add( patternLabel );

        //Pattern selector
        changePattern.addClickHandler(
                new ClickHandler() {
                    public void onClick( ClickEvent w ) {
                        presenter.showChangePattern( w );
                    }
                }
        );
        changePattern.setEnabled( !this.presenter.isReadOnly() );
        patternWidget.add( changePattern );

        addAttribute( GuidedDecisionTableConstants.INSTANCE.Pattern(),
                patternWidget );
    }

    private void initializeTableFormatSpecifics() {
        //Radio buttons for Calculation Type
        switch ( this.presenter.getTableFormat() ) {
            case EXTENDED_ENTRY:
                HorizontalPanel valueTypes = new HorizontalPanel();
                valueTypes.add( literal );
                valueTypes.add( formula );
                valueTypes.add( predicate );
                addAttribute( new StringBuilder( GuidedDecisionTableConstants.INSTANCE.CalculationType() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                        valueTypes );

                switch ( this.presenter.getConstraintValueType() ) {
                    case BaseSingleFieldConstraint.TYPE_LITERAL:
                        literal.setValue( true );
                        break;
                    case BaseSingleFieldConstraint.TYPE_RET_VALUE:
                        formula.setValue( true );
                        break;
                    case BaseSingleFieldConstraint.TYPE_PREDICATE:
                        predicate.setValue( true );
                }

                if ( !this.presenter.isReadOnly() ) {
                    literal.addClickHandler( new ClickHandler() {
                        public void onClick( ClickEvent w ) {
                            presenter.applyConsTypeChange( BaseSingleFieldConstraint.TYPE_LITERAL );
                        }
                    } );

                    formula.addClickHandler( new ClickHandler() {
                        public void onClick( ClickEvent w ) {
                            presenter.applyConsTypeChange( BaseSingleFieldConstraint.TYPE_RET_VALUE );
                        }
                    } );

                    predicate.addClickHandler( new ClickHandler() {
                        public void onClick( ClickEvent w ) {
                            presenter.setFactField( null );
                            presenter.applyConsTypeChange( BaseSingleFieldConstraint.TYPE_PREDICATE );
                        }
                    } );
                }

                break;

            case LIMITED_ENTRY:
                binding.setEnabled( !this.presenter.isReadOnly() );
        }
    }

    private void initializeFactField() {
        //Fact field
        HorizontalPanel field = new HorizontalPanel();
        fieldLabel.setEnabled( !this.presenter.isReadOnly() );
        field.add( fieldLabel );
        field.add( fieldLabelInterpolationInfo );
        editField.addClickHandler(
                new ClickHandler() {
                    public void onClick( ClickEvent w ) {
                        presenter.showFieldChange();
                    }
                }
        );
        editField.setEnabled( !this.presenter.isReadOnly() );
        field.add( editField );
        addAttribute( new StringBuilder( GuidedDecisionTableConstants.INSTANCE.Field() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                field );
    }

    private void initializeOperator() {
        //Operator
        HorizontalPanel operator = new HorizontalPanel();
        operator.add( operatorLabel );
        editOp.addClickHandler(
                new ClickHandler() {
                    public void onClick( ClickEvent w ) {
                        presenter.showOperatorChange();
                    }
                }
        );
        editOp.setEnabled( !this.presenter.isReadOnly() );
        operator.add( editOp );
        addAttribute( GuidedDecisionTableConstants.INSTANCE.Operator(),
                operator );
    }

    private void initializeCepWindow() {
        //Add CEP fields for patterns containing Facts declared as Events
        cepWindowRowIndex = addAttribute( GuidedDecisionTableConstants.INSTANCE.DTLabelOverCEPWindow(),
                createCEPWindowWidget( this.presenter.getEditingPattern() ) ).getIndex();
    }

    private void initializeEntryPoint() {
        //Entry point
        entryPointName.setText( this.presenter.getEditingPattern().getEntryPointName() );
        entryPointName.setEnabled( !this.presenter.isReadOnly() );
        if ( !this.presenter.isReadOnly() ) {
            entryPointName.addChangeHandler( new ChangeHandler() {
                public void onChange( ChangeEvent event ) {
                    ConditionPopupView.this.presenter.getEditingPattern().setEntryPointName( entryPointName.getText() );
                }
            } );
        }
        addAttribute( new StringBuilder( GuidedDecisionTableConstants.INSTANCE.DTLabelFromEntryPoint() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                entryPointName );
    }

    private void initializeColumnHeader() {
        //Column header
        header.setText( this.presenter.getHeader() );
        header.setEnabled( !this.presenter.isReadOnly() );
        if ( !this.presenter.isReadOnly() ) {
            header.addChangeHandler( new ChangeHandler() {
                public void onChange( ChangeEvent event ) {
                    ConditionPopupView.this.presenter.setHeader( header.getText() );
                }
            } );
        }
        addAttribute( GuidedDecisionTableConstants.INSTANCE.ColumnHeaderDescription(),
                header );
    }

    private void initializeOptionalValueList() {
        //Optional value list
        if ( this.presenter.getTableFormat() == TableFormat.EXTENDED_ENTRY ) {
            valueListWidget.setText( this.presenter.getValueList() );
            valueListWidget.setEnabled( !this.presenter.isReadOnly() );
            if ( !this.presenter.isReadOnly() ) {

                //Copy value back to model
                valueListWidget.addChangeHandler( new ChangeHandler() {
                    public void onChange( ChangeEvent event ) {
                        ConditionPopupView.this.presenter.setValueList( valueListWidget.getText() );
                    }
                } );

                //Update Default Value widget if necessary
                valueListWidget.addBlurHandler( new BlurHandler() {
                    public void onBlur( BlurEvent event ) {
                        presenter.assertDefaultValue();
                        presenter.makeDefaultValueWidget();
                    }

                } );

            }
            HorizontalPanel vl = new HorizontalPanel();
            vl.add( valueListWidget );
            vl.add( new InfoPopup( GuidedDecisionTableConstants.INSTANCE.ValueList(),
                    GuidedDecisionTableConstants.INSTANCE.ValueListsExplanation() ) );
            addAttribute( GuidedDecisionTableConstants.INSTANCE.optionalValueList(),
                    vl );
        }
    }

    private void initializeBinding() {
        //Field Binding
        binding.setText( this.presenter.getBinding() );
        if ( !this.presenter.isReadOnly() ) {
            binding.addChangeHandler( new ChangeHandler() {
                public void onChange( ChangeEvent event ) {
                    ConditionPopupView.this.presenter.setBinding( binding.getText() );
                }
            } );
        }
        addAttribute( new StringBuilder( GuidedDecisionTableConstants.INSTANCE.Binding() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                binding );
    }

    private void initializeHideColumnTick() {
        //Hide column tick-box
        addAttribute( new StringBuilder( GuidedDecisionTableConstants.INSTANCE.HideThisColumn() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                DTCellValueWidgetFactory.getHideColumnIndicator( this.presenter.getEditingCol() ) );
    }

    private void initializeApplyButton() {
        //Apply button
        footer.enableOkButton( !this.presenter.isReadOnly() );
        add( footer );
    }

    private TextBox getFieldLabel() {
        final TextBox box = new TextBox();
        box.addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                presenter.setFactField( box.getText() );
            }
        } );
        return box;
    }

    private InfoPopup getPredicateHint() {
        return new InfoPopup( GuidedDecisionTableConstants.INSTANCE.Predicates(),
                              GuidedDecisionTableConstants.INSTANCE.PredicatesInfo() );
    }

    //Widget for CEP 'windows'
    private IsWidget createCEPWindowWidget( final HasCEPWindow c ) {
        HorizontalPanel hp = new HorizontalPanel();
        Label lbl = new Label( GuidedDecisionTableConstants.INSTANCE.OverCEPWindow() );
        lbl.setStyleName( "paddedLabel" );
        hp.add( lbl );

        cwo = new CEPWindowOperatorsDropdown( c,
                                              presenter.isReadOnly() );
        if ( !presenter.isReadOnly() ) {
            cwo.addValueChangeHandler( new ValueChangeHandler<OperatorSelection>() {

                public void onValueChange( ValueChangeEvent<OperatorSelection> event ) {
                    OperatorSelection selection = event.getValue();
                    String selected = selection.getValue();
                    c.getWindow().setOperator( selected );
                }
            } );
        }

        hp.add( cwo );
        return hp;
    }

    public void enableLiteral( boolean enabled ) {
        literal.setEnabled( enabled );
    }

    public void enableFormula( boolean enabled ) {
        formula.setEnabled( enabled );
    }

    public void enablePredicate( boolean enabled ) {
        predicate.setEnabled( enabled );
    }

    public void enableValueListWidget( boolean enabled ) {
        valueListWidget.setEnabled( enabled );
    }

    public void setValueListWidgetText( String text ) {
        valueListWidget.setText( text );
    }

    public void setLimitedEntryVisibility( boolean visibility ) {
        setAttributeVisibility( limitedEntryValueAttributeIndex, visibility );
    }

    public void setDefaultValueVisibility( boolean visibility ) {
        setAttributeVisibility( defaultValueWidgetContainerIndex, visibility );
    }

    public void setLimitedEntryWidget( Widget widget ) {
        limitedEntryValueWidgetContainer.setWidget( widget );
    }

    public void setDefaultValueWidget( Widget widget ) {
        defaultValueWidgetContainer.setWidget( widget );
    }

    public void enableBinding( boolean enabled ) {
        binding.setEnabled( enabled );
    }

    public void enableEditField( boolean enabled ) {
        editField.setEnabled( enabled );
    }

    public void enableEditOperator( boolean enabled ) {
        editOp.setEnabled( enabled );
    }

    public void setFieldLabelText( String text ) {
        fieldLabel.setText( text );
    }

    public void setOperatorLabelText( String text ) {
        operatorLabel.setText( text );
    }

    public void setPatternLabelText( String text ) {
        patternLabel.setText( text );
    }

    public void setFieldLabelDisplayStyle( Style.Display displayStyle ) {
        fieldLabelInterpolationInfo.getWidget().getElement().getStyle().setDisplay( displayStyle );
    }

    public void setEntryPointName( String name ) {
        entryPointName.setText( name );
    }

    public void selectOperator( String operator ) {
        cwo.selectItem( operator );
    }

    public void setCepWindowVisibility( boolean visibility ) {
        setAttributeVisibility( cepWindowRowIndex, visibility );
    }

    public void enableFooter( final boolean enabled ) {
        if ( footer == null ) {
            return;
        }
        footer.enableOkButton( enabled );
        footer.enableCancelButton( enabled );
    }

    public void addLimitedEntryValue() {
        limitedEntryValueAttributeIndex = addAttribute( GuidedDecisionTableConstants.INSTANCE.LimitedEntryValue(),
                limitedEntryValueWidgetContainer ).getIndex();
    }

    public void addDefaultValue() {
        defaultValueWidgetContainerIndex = addAttribute( new StringBuilder( GuidedDecisionTableConstants.INSTANCE.DefaultValue() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                defaultValueWidgetContainer ).getIndex();
    }

    @Override
    public void show() {
        presenter.doPatternLabel();
        presenter.doFieldLabel();
        presenter.doOperatorLabel();
        presenter.displayCEPOperators();
        presenter.doImageButtons();
        presenter.doValueList();
        presenter.doCalculationType();
        presenter.initialiseViewForConstraintValueType();
        presenter.makeDefaultValueWidget();
        presenter.makeLimitedValueWidget();
        super.show();
    }

    public void showFieldChangePopUp() {
        final FormStylePopup pop = new FormStylePopup( GuidedDecisionTableConstants.INSTANCE.Field() );
        final ListBox box = presenter.loadFields();

        pop.addAttribute( new StringBuilder( GuidedDecisionTableConstants.INSTANCE.Field() ).append( GuidedDecisionTableConstants.COLON ).toString(),
                box );

        pop.add( new ModalFooterOKCancelButtons(
                () -> {
                    presenter.confirmFieldChangePopUp( pop, box.getItemText( box.getSelectedIndex() ) );
                },
                () -> {
                    presenter.cancelFieldChangePopUp( pop );
                } ) );
        pop.show();
    }
}
