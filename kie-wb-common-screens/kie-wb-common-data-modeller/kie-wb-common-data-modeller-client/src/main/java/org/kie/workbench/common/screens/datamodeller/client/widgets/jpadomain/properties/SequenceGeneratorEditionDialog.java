/*
 * Copyright 2015 JBoss Inc
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
package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.SequenceGeneratorValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties.PropertyEditionPopup;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class SequenceGeneratorEditionDialog
        extends BaseModal implements PropertyEditionPopup {

    @UiField
    TextBox generatorName;

    @UiField
    TextBox sequenceName;

    @UiField
    TextBox initialValue;

    @UiField
    TextBox allocationSize;

    private Boolean revertChanges = Boolean.TRUE;

    PropertyEditorFieldInfo property;

    Command okCommand;

    interface Binder
            extends
            UiBinder<Widget, SequenceGeneratorEditionDialog> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    public SequenceGeneratorEditionDialog() {
        setTitle( "Sequence Generator" );
        //setMaxHeigth( "450px" );
        add( new ModalBody() {{
            add( uiBinder.createAndBindUi( SequenceGeneratorEditionDialog.this ) );
        }} );

        add( new ModalFooterOKCancelButtons(
                        new Command() {
                            @Override
                            public void execute() {
                                okButton();
                            }
                        },
                        new Command() {
                            @Override
                            public void execute() {
                                cancelButton();
                            }
                        }
                )
        );

    }

    private void addHiddlenHandler() {
        addHiddenHandler( new ModalHiddenHandler() {
            @Override
            public void onHidden( ModalHiddenEvent hiddenEvent ) {
                if ( userPressCloseOrCancel() ) {
                    revertChanges();
                }
            }
        } );
    }

    private void revertChanges() {

    }

    private boolean userPressCloseOrCancel() {
        return revertChanges;
    }

    public void show() {

        DataModelerPropertyEditorFieldInfo fieldInfo = (DataModelerPropertyEditorFieldInfo) property;

        String sequenceName = (String) fieldInfo.getCurrentValue( SequenceGeneratorValueHandler.SEQUENCE_NAME );
        String generatorName = (String) fieldInfo.getCurrentValue( SequenceGeneratorValueHandler.NAME );
        Object initialValue = fieldInfo.getCurrentValue( SequenceGeneratorValueHandler.INITIAL_VALUE );
        Object allocationSize = fieldInfo.getCurrentValue( SequenceGeneratorValueHandler.ALLOCATION_SIZE );

        this.sequenceName.setText( sequenceName );
        this.generatorName.setText( generatorName );
        this.initialValue.setText( initialValue != null ? initialValue.toString() : null );
        this.allocationSize.setText( allocationSize != null ? allocationSize.toString() : null );

        super.show();
    }

    public void setOkCommand( Command okCommand ) {
        this.okCommand = okCommand;
    }

    public void setProperty( PropertyEditorFieldInfo property ) {
        this.property = property;
    }

    void okButton() {

        //TODO add validation in order to establish if the ok operation can be performed. If validation is ok,
        // then new current values can be set.

        DataModelerPropertyEditorFieldInfo fieldInfo = (DataModelerPropertyEditorFieldInfo) property;

        String sequenceName = this.sequenceName.getText();
        String generatorName = this.generatorName.getText();

        fieldInfo.setCurrentValue( SequenceGeneratorValueHandler.SEQUENCE_NAME, sequenceName );
        fieldInfo.setCurrentValue( SequenceGeneratorValueHandler.NAME, generatorName );
        fieldInfo.setCurrentValue( SequenceGeneratorValueHandler.INITIAL_VALUE, getInitialValue() );
        fieldInfo.setCurrentValue( SequenceGeneratorValueHandler.ALLOCATION_SIZE, getAllocationSize() );

        super.hide();
        revertChanges = Boolean.FALSE;
        if ( okCommand != null ) {
            okCommand.execute();
        }

    }

    private Integer getInitialValue() {
        return parseInt( initialValue.getText() );
    }

    private Integer getAllocationSize() {
        return parseInt( allocationSize.getText() );
    }

    private Integer parseInt( String value ) {
        Integer result = null;
        if ( value != null && !"".equals( value.trim() )) {
            try {
                result = Integer.parseInt( value.trim() );
            } catch ( Exception e ) {
            }
        }
        return result;
    }

    void cancelButton() {
        super.hide();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public String getStringValue() {
        //return the value to show in the property editor simple text field.
        String value = generatorName.getText();
        if ( value == null || "".equals( value ) ) value = "NOT_SET";
        return value;
    }

    @Override
    public void setStringValue( String value ) {
        //do nothing
    }
}
