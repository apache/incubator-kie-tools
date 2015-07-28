/*
* Copyright 2013 JBoss Inc
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
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties.PropertyEditionPopup;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

import static org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils.*;

public class IdGeneratorEditionDialog
        extends BaseModal implements PropertyEditionPopup {

    @UiField
    Select generatorType;

    @UiField
    TextBox generatorName;

    private Boolean revertChanges = Boolean.TRUE;

    PropertyEditorFieldInfo property;

    Command okCommand;

    interface Binder
            extends
            UiBinder<Widget, IdGeneratorEditionDialog> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    public IdGeneratorEditionDialog() {
        setTitle( "Generation Strategy" );
        setBody( uiBinder.createAndBindUi( IdGeneratorEditionDialog.this ) );

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

        generatorType.add( newOption( "NONE", "NONE" ) );
        generatorType.add( newOption( "SEQUENCE", "SEQUENCE" ) );
        generatorType.add( newOption( "TABLE", "TABLE" ) );
        generatorType.add( newOption( "IDENTITY", "IDENTITY" ) );
        generatorType.add( newOption( "AUTO", "AUTO" ) );
        refreshSelect( generatorType );
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
        String strategy = (String) fieldInfo.getCurrentValue( "strategy" );
        String generator = (String) fieldInfo.getCurrentValue( "generator" );

        strategy = strategy != null ? strategy : "NONE";
        setSelectedValue( generatorType, strategy );
        generatorName.setText( generator );

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
        String strategy = generatorType.getValue();
        strategy = "NONE".equals( strategy ) ? null : strategy;
        String generator = generatorName.getText();

        fieldInfo.setCurrentValue( "strategy", strategy );
        fieldInfo.setCurrentValue( "generator", generator );

        super.hide();
        revertChanges = Boolean.FALSE;
        if ( okCommand != null ) {
            okCommand.execute();
        }

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
        return generatorType.getValue();
    }

    @Override
    public void setStringValue( String value ) {
        //do nothing
    }
}
