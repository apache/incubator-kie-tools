/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.client.widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.datamodel.rule.DSLComplexVariableValue;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.DSLVariableValue;
import org.drools.workbench.screens.guided.rule.client.editor.CustomFormPopUp;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.guvnor.common.services.workingset.client.WorkingSetManager;
import org.guvnor.common.services.workingset.client.factconstraints.customform.CustomFormConfiguration;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.ext.widgets.common.client.common.DatePicker;
import org.uberfire.ext.widgets.common.client.common.DropDownValueChanged;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;

/**
 * This displays a widget to edit a DSL sentence.
 */
public class DSLSentenceWidget extends RuleModellerWidget {

    private WorkingSetManager workingSetManager = null;

    private final List<Widget> widgets;
    private final List<DSLDropDown> dropDownWidgets;
    private final DSLSentence sentence;
    private final VerticalPanel layout;
    private HorizontalPanel currentRow;
    private boolean readOnly;

    public DSLSentenceWidget( RuleModeller modeller,
                              EventBus eventBus,
                              DSLSentence sentence,
                              Boolean readOnly ) {
        super( modeller,
               eventBus );
        widgets = new ArrayList<Widget>();
        dropDownWidgets = new ArrayList<DSLDropDown>();
        this.sentence = sentence;

        if ( readOnly == null ) {
            this.readOnly = false;
        } else {
            this.readOnly = readOnly;
        }

        this.layout = new VerticalPanel();
        this.currentRow = new HorizontalPanel();
        this.layout.add( currentRow );
        this.layout.setCellWidth( currentRow,
                                  "100%" );
        this.layout.setWidth( "100%" );

        if ( this.readOnly ) {
            this.layout.addStyleName( "editor-disabled-widget" );
        }

        init();
    }

    private void init() {
        makeWidgets( this.sentence );
        initWidget( this.layout );
    }

    /**
     * This will take a DSL line item, and split it into widget thingamies for
     * displaying. One day, if this is too complex, this will have to be done on
     * the server side.
     */
    public void makeWidgets( DSLSentence sentence ) {

        String dslDefinition = sentence.getDefinition();
        List<DSLVariableValue> dslValues = sentence.getValues();
        int index = 0;

        int startVariable = dslDefinition.indexOf( "{" );
        List<Widget> lineWidgets = new ArrayList<Widget>();

        boolean firstOneIsBracket = ( dslDefinition.indexOf( "{" ) == 0 );

        String startLabel = "";
        if ( startVariable > 0 ) {
            startLabel = dslDefinition.substring( 0,
                                                  startVariable );
        } else if ( !firstOneIsBracket ) {
            // There are no curly brackets in the text. Just print it
            startLabel = dslDefinition;
        }

        Widget label = getLabel( startLabel );
        lineWidgets.add( label );

        while ( startVariable > 0 || firstOneIsBracket ) {
            firstOneIsBracket = false;
            int endVariable = getIndexForEndOfVariable( dslDefinition,
                                                        startVariable );
            String currVariable = dslDefinition.substring( startVariable + 1,
                                                           endVariable );

            DSLVariableValue value = dslValues.get( index );
            Widget varWidget = processVariable( currVariable,
                                                sentence,
                                                value );
            lineWidgets.add( varWidget );
            index++;

            // Parse out the next label between variables
            startVariable = dslDefinition.indexOf( "{",
                                                   endVariable );
            String lbl;
            if ( startVariable > 0 ) {
                lbl = dslDefinition.substring( endVariable + 1,
                                               startVariable );
            } else {
                lbl = dslDefinition.substring( endVariable + 1,
                                               dslDefinition.length() );
            }

            if ( lbl.indexOf( "\\n" ) > -1 ) {
                String[] lines = lbl.split( "\\\\n" );
                for ( int i = 0; i < lines.length; i++ ) {
                    lineWidgets.add( new NewLine() );
                    lineWidgets.add( getLabel( lines[ i ] ) );
                }
            } else {
                Widget currLabel = getLabel( lbl );
                lineWidgets.add( currLabel );
            }

        }

        for ( Widget widg : lineWidgets ) {
            addWidget( widg );
        }

        updateSentence();
    }

    private int getIndexForEndOfVariable( String dsl,
                                          int start ) {
        int end = -1;
        int bracketCount = 0;
        if ( start > dsl.length() ) {
            return end;
        }
        for ( int i = start; i < dsl.length(); i++ ) {
            char c = dsl.charAt( i );
            if ( c == '{' ) {
                bracketCount++;
            }
            if ( c == '}' ) {
                bracketCount--;
                if ( bracketCount == 0 ) {
                    end = i;
                    return end;
                }
            }
        }
        return -1;
    }

    static class NewLine extends Widget {

    }

    public Widget processVariable( String currVariable,
                                   DSLSentence sentence,
                                   DSLVariableValue value ) {

        Widget result = null;
        // Formats are: 
        // <varName>:ENUM:<Field.type>
        // <varName>:DATE:<dateFormat>
        // <varName>:BOOLEAN:[checked | unchecked] <-initial value
        // <varName>:CF:<Field.type>
        // Note: <varName> is no longer overwritten with the value; values are stored in DSLSentence.values()

        if ( currVariable.contains( ":" ) ) {
            if ( currVariable.contains( ":" + DSLSentence.ENUM_TAG + ":" ) ) {
                result = getEnumDropdown( currVariable,
                                          sentence,
                                          value );
            } else if ( currVariable.contains( ":" + DSLSentence.DATE_TAG + ":" ) ) {
                result = getDateSelector( value );
            } else if ( currVariable.contains( ":" + DSLSentence.BOOLEAN_TAG + ":" ) ) {
                result = getCheckbox( currVariable,
                                      value );
            } else if ( currVariable.contains( ":" + DSLSentence.CUSTOM_FORM_TAG + ":" ) ) {
                result = getCustomFormEditor( currVariable,
                                              value );
            } else {
                String regex = currVariable.substring( currVariable.indexOf( ":" ) + 1,
                                                       currVariable.length() );
                result = getBox( value,
                                 regex );
            }
        } else {
            result = getBox( value,
                             "" );
        }

        return result;
    }

    private Widget getEnumDropdown( String variableDef,
                                    DSLSentence sentence,
                                    DSLVariableValue value ) {
        final boolean multipleSelect = isMultipleSelect( variableDef,
                                                         sentence );
        DSLDropDown resultWidget = new DSLDropDown( variableDef,
                                                    value,
                                                    multipleSelect );
        dropDownWidgets.add( resultWidget );
        return resultWidget;
    }

    private boolean isMultipleSelect( final String variableDef,
                                      final DSLSentence sentence ) {
        final String variableName = variableDef.substring( 0,
                                                           variableDef.indexOf( ":" ) );
        final String variableNameMarker = "{" + variableName + "}";
        final String drl = sentence.getDrl();
        final String normalizedDrl = drl.trim().replaceAll( "\"", "" ).replaceAll( " +", " " );
        final String normalizedDrlFragment = normalizedDrl.substring( 0,
                                                                      normalizedDrl.indexOf( variableNameMarker ) );
        for ( String listOperator : OperatorsOracle.EXPLICIT_LIST_OPERATORS ) {
            final String listOperatorMarker = " " + listOperator + " ";
            if ( normalizedDrlFragment.indexOf( listOperatorMarker ) == normalizedDrlFragment.length() - listOperatorMarker.length() ) {
                return true;
            }
        }
        return false;
    }

    private Widget getBox( DSLVariableValue variableDef,
                           String regex ) {
        return this.getBox( variableDef,
                            regex,
                            false );
    }

    private Widget getBox( DSLVariableValue variableDef,
                           String regex,
                           boolean readonly ) {

        FieldEditor currentBox = new FieldEditor();
        currentBox.setVisibleLength( variableDef.getValue().length() + 1 );
        currentBox.setValue( variableDef );
        currentBox.setRestriction( regex );

        currentBox.box.setEnabled( !readonly );

        return currentBox;
    }

    private Widget getCheckbox( String variableDef,
                                DSLVariableValue value ) {
        return new DSLCheckBox( variableDef,
                                value );
    }

    /**
     * If there is an active working-set defining a custom form configuration
     * for the factType and field defined by variableDef, then a button a custom
     * form editor (aka Widget wrapping a button) is returned. Otherwise, the
     * result of
     * {@link #getBox(DSLVariableValue, String) }
     * is returned.
     * @param variableDef
     * @param value
     * @return
     */
    private Widget getCustomFormEditor( String variableDef,
                                        DSLVariableValue value ) {

        //Parse Fact Type and Field for retrieving Custom Form configuration
        //from WorkingSetManager
        //Format for the custom form definition within a DSLSentence is <varName>:<type>:<Fact.field>
        int lastIndex = variableDef.lastIndexOf( ":" );
        String factAndField = variableDef.substring( lastIndex + 1,
                                                     variableDef.length() );
        int dotIndex = factAndField.indexOf( "." );

        String factType = factAndField.substring( 0,
                                                  dotIndex );
        String field = factAndField.substring( dotIndex + 1,
                                               factAndField.length() );

        //is there any custom form configurated for this factType.field?
        final CustomFormConfiguration customFormConfiguration = getWorkingSetManager().getCustomFormConfiguration( this.getModeller().getPath(),
                                                                                                                   factType,
                                                                                                                   field );

        boolean editorReadOnly = this.readOnly;

        if ( !editorReadOnly ) {
            //if no one is forcing us to be in readonly mode, let's see
            //if there is a constraint for the fact type of the custom form
            editorReadOnly = !this.getModeller().getDataModelOracle().isFactTypeRecognized( factType );
        }

        if ( customFormConfiguration != null ) {
            return new DSLCustomFormButton( variableDef,
                                            value,
                                            customFormConfiguration,
                                            editorReadOnly );
        }

        return getBox( value,
                       "",
                       editorReadOnly );
    }

    public Widget getDateSelector( DSLVariableValue value ) {
        //We use the global Date Format settings now
        //String[] parts = variableDef.split( ":" + DSLSentence.DATE_TAG + ":" );
        return new DSLDateSelector( value );
    }

    public Widget getLabel( String labelDef ) {
        SmallLabel label = new SmallLabel();
        label.setText( labelDef.trim() );

        return label;
    }

    private void addWidget( Widget currentBox ) {
        if ( currentBox instanceof NewLine ) {
            currentRow = new HorizontalPanel();
            layout.add( currentRow );
            layout.setCellWidth( currentRow,
                                 "100%" );
        } else {
            currentRow.add( currentBox );
        }
        widgets.add( currentBox );
    }

    /**
     * This will go through the widgets and extract the values
     */
    protected void updateSentence() {
        int iVariable = 0;
        for ( Iterator<Widget> iter = widgets.iterator(); iter.hasNext(); ) {
            Widget wid = iter.next();
            if ( wid instanceof DSLVariableEditor ) {
                sentence.getValues().set( iVariable++,
                                          ( (DSLVariableEditor) wid ).getSelectedValue() );
            }
        }
        this.setModified( true );
    }

    interface DSLVariableEditor {

        DSLVariableValue getSelectedValue();
    }

    class FieldEditor
            extends Composite
            implements DSLVariableEditor {

        private TextBox box;
        private String oldValue = "";
        private DSLVariableValue oldVariableValue;
        private String regex = "";

        public FieldEditor() {
            box = new TextBox();
            box.addChangeHandler( new ChangeHandler() {

                public void onChange( ChangeEvent event ) {
                    TextBox otherBox = (TextBox) event.getSource();

                    if ( !regex.equals( "" ) && !otherBox.getText().matches( regex ) ) {
                        Window.alert( GuidedRuleEditorResources.CONSTANTS.TheValue0IsNotValidForThisField( otherBox.getText() ) );
                        box.setText( oldValue );
                    } else {
                        oldValue = otherBox.getText();
                        updateSentence();
                    }
                }
            } );

            //Wrap widget within a HorizontalPanel to add a space before and after the Widget
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;" ) );
            hp.add( box );
            hp.add( new HTML( "&nbsp;" ) );

            initWidget( hp );
        }

        public void setRestriction( String regex ) {
            this.regex = regex;
        }

        public void setValue( DSLVariableValue value ) {
            this.oldVariableValue = value;
            box.setText( value.getValue() );
        }

        public void setVisibleLength( int l ) {

            ( (InputElement) box.getElement().cast() ).setSize( l );
        }

        public DSLVariableValue getSelectedValue() {
            //if oldVariableValue was of type DSLComplexVariableValue, then return a
            //copy of it with only the 'value' part modified
            if ( oldVariableValue instanceof DSLComplexVariableValue ) {
                return new DSLComplexVariableValue( ( (DSLComplexVariableValue) oldVariableValue ).getId(),
                                                    box.getText() );
            }
            return new DSLVariableValue( box.getText() );
        }

    }

    class DSLDropDown extends Composite
            implements
            DSLVariableEditor {

        final AsyncPackageDataModelOracle oracle = getModeller().getDataModelOracle();
        EnumDropDown resultWidget = null;
        String factType;
        String factField;
        DSLVariableValue selectedValue;

        public DSLDropDown( final String variableDef,
                            final DSLVariableValue value,
                            final boolean multipleSelect ) {

            //Parse Fact Type and Field for retrieving DropDown data from Suggestion Completion Engine
            //Format for the drop-down definition within a DSLSentence is <varName>:<type>:<Fact.field>
            int lastIndex = variableDef.lastIndexOf( ":" );
            String factAndField = variableDef.substring( lastIndex + 1,
                                                         variableDef.length() );
            int dotIndex = factAndField.indexOf( "." );
            factType = factAndField.substring( 0,
                                               dotIndex );
            factField = factAndField.substring( dotIndex + 1,
                                                factAndField.length() );
            selectedValue = value;

            //ChangeHandler for drop-down; not called when initialising the drop-down
            DropDownValueChanged handler = new DropDownValueChanged() {

                public void valueChanged( String newText,
                                          String newValue ) {
                    if ( selectedValue.getValue().equals( newValue ) ) {
                        return;
                    }

                    selectedValue = new DSLVariableValue( newValue );

                    //When the value changes we need to reset the content of *ALL* DSLSentenceWidget drop-downs.
                    //An improvement would be to determine the chain of dependent drop-downs and only update
                    //children of the one whose value changes. However in reality DSLSentences only contain
                    //a couple of drop-downs so it's quicker to simply update them all.
                    updateEnumDropDowns( DSLDropDown.this );
                }
            };

            DropDownData dropDownData = getDropDownData();
            resultWidget = new EnumDropDown( value.getValue(),
                                             handler,
                                             dropDownData,
                                             multipleSelect,
                                             modeller.getPath() );

            //Wrap widget within a HorizontalPanel to add a space before and after the Widget
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;" ) );
            hp.add( resultWidget );
            hp.add( new HTML( "&nbsp;" ) );

            initWidget( hp );
        }

        public DSLVariableValue getSelectedValue() {
            return selectedValue;
        }

        public void refreshDropDownData() {
            resultWidget.setDropDownData( selectedValue.getValue(),
                                          getDropDownData() );
        }

        private DropDownData getDropDownData() {
            DropDownData dropDownData = oracle.getEnums( factType,
                                                         factField,
                                                         sentence.getEnumFieldValueMap() );
            return dropDownData;
        }

    }

    class DSLCustomFormButton extends Composite
            implements
            DSLVariableEditor {

        private DSLVariableValue selectedValue;
        private CustomFormConfiguration customFormConfiguration;
        private Button btnCustomForm;

        public DSLCustomFormButton( final String variableDef,
                                    final DSLVariableValue value,
                                    CustomFormConfiguration customFormConfiguration,
                                    boolean readonly ) {

            this.customFormConfiguration = customFormConfiguration;

            this.selectedValue = value;

            this.btnCustomForm = new Button( selectedValue.getValue() );

            this.btnCustomForm.setEnabled( !readonly );

            //for security reasons, only add the handler if we are not in 
            //read-only mode
            if ( !readonly ) {
                this.btnCustomForm.addClickHandler( new ClickHandler() {

                    public void onClick( ClickEvent event ) {
                        final CustomFormPopUp customFormPopUp =
                                new CustomFormPopUp( GuidedRuleEditorImages508.INSTANCE.Wizard(),
                                                     GuidedRuleEditorResources.CONSTANTS.FieldValue(),
                                                     DSLCustomFormButton.this.customFormConfiguration );

                        customFormPopUp.addOkButtonHandler( new ClickHandler() {

                            public void onClick( ClickEvent event ) {
                                String id = customFormPopUp.getFormId();
                                String value = customFormPopUp.getFormValue();
                                btnCustomForm.setText( value );

                                selectedValue = new DSLComplexVariableValue( id,
                                                                             value );

                                updateSentence();
                                customFormPopUp.hide();
                            }
                        } );

                        //if selectedValue is an instance of DSLComplexVariableValue,
                        //then both id and value are passed to the custom form
                        //if not, only the value is passed and "" is passed as id
                        if ( selectedValue instanceof DSLComplexVariableValue ) {
                            DSLComplexVariableValue complexSelectedValue = (DSLComplexVariableValue) selectedValue;
                            customFormPopUp.show( complexSelectedValue.getId(),
                                                  complexSelectedValue.getValue() );
                        } else {
                            customFormPopUp.show( "",
                                                  selectedValue.getValue() );
                        }

                    }
                } );
            }

            //Wrap the button within a HorizontalPanel to add a space before and after the Widget
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;" ) );
            hp.add( btnCustomForm );
            hp.add( new HTML( "&nbsp;" ) );

            initWidget( hp );
        }

        public DSLVariableValue getSelectedValue() {
            return selectedValue;
        }

    }

    class DSLCheckBox extends Composite
            implements
            DSLVariableEditor {

        ListBox resultWidget = null;

        public DSLCheckBox( String variableDef,
                            DSLVariableValue value ) {

            resultWidget = new ListBox();
            resultWidget.addItem( "true" );
            resultWidget.addItem( "false" );

            if ( value.getValue().equalsIgnoreCase( "true" ) ) {
                resultWidget.setSelectedIndex( 0 );
            } else {
                resultWidget.setSelectedIndex( 1 );
            }

            resultWidget.addChangeHandler( new ChangeHandler() {

                public void onChange( ChangeEvent event ) {
                    updateSentence();
                }
            } );

            resultWidget.setVisible( true );

            //Wrap widget within a HorizontalPanel to add a space before and after the Widget
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;" ) );
            hp.add( resultWidget );
            hp.add( new HTML( "&nbsp;" ) );
            initWidget( hp );
        }

        public DSLVariableValue getSelectedValue() {
            String value = this.resultWidget.getSelectedIndex() == 0 ? "true" : "false";
            return new DSLVariableValue( value );
        }
    }

    class DSLDateSelector extends Composite
            implements
            DSLVariableEditor {

        private final String DATE_FORMAT = ApplicationPreferences.getDroolsDateFormat();
        private final DateTimeFormat DATE_FORMATTER = DateTimeFormat.getFormat( DATE_FORMAT );

        private final DatePicker datePicker = new DatePicker( false );

        public DSLDateSelector( DSLVariableValue selectedDate ) {
            // Wire up update handler
            datePicker.addValueChangeHandler( new ValueChangeHandler<Date>() {
                @Override
                public void onValueChange( final ValueChangeEvent<Date> event ) {
                    updateSentence();
                }
            } );

            datePicker.setFormat( DATE_FORMAT );
            datePicker.setValue( assertDateValue( selectedDate ) );

            //Wrap widget within a HorizontalPanel to add a space before and after the Widget
            HorizontalPanel hp = new HorizontalPanel();
            hp.add( new HTML( "&nbsp;" ) );
            hp.add( datePicker );
            hp.add( new HTML( "&nbsp;" ) );
            initWidget( hp );
        }

        public DSLVariableValue getSelectedValue() {
            final Date date = datePicker.getValue();
            final String sDate = ( date == null ? null : DATE_FORMATTER.format( datePicker.getValue() ) );
            return new DSLVariableValue( sDate );
        }

        private Date assertDateValue( final DSLVariableValue dslVariableValue ) {
            if ( dslVariableValue.getValue() == null ) {
                return null;
            }
            try {
                return DATE_FORMATTER.parse( dslVariableValue.getValue() );

            } catch ( IllegalArgumentException iae ) {
                return null;
            }
        }
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public boolean isFactTypeKnown() {
        return true;
    }

    //When a value in a drop-down changes we need to reset the content of *ALL* DSLSentenceWidget drop-downs.
    //An improvement would be to determine the chain of dependent drop-downs and only update children of the
    //one whose value changes. However in reality DSLSentences only contain a couple of drop-downs so it's 
    //quicker to simply update them all.
    private void updateEnumDropDowns( final DSLDropDown source ) {

        //Copy selections in UI to data-model, used to drive dependent drop-downs
        updateSentence();

        final int sourceIndex = dropDownWidgets.indexOf( source );
        for ( DSLDropDown dd : dropDownWidgets ) {
            if ( dropDownWidgets.indexOf( dd ) > sourceIndex ) {
                dd.refreshDropDownData();
            }
        }

        //Copy selections in UI to data-model again, as updating the drop-downs
        //can lead to some selected values being cleared when dependent drop-downs
        //are used.
        updateSentence();
    }

    private WorkingSetManager getWorkingSetManager() {
        if ( workingSetManager == null ) {
            workingSetManager = IOC.getBeanManager().lookupBean( WorkingSetManager.class ).getInstance();
        }
        return workingSetManager;
    }

}
