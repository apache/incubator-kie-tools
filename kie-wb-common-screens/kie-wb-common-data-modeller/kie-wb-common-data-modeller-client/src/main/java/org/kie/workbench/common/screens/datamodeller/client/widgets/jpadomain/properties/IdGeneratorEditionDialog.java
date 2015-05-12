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

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.event.HiddenEvent;
import com.github.gwtbootstrap.client.ui.event.HiddenHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties.PropertyEditionPopup;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class IdGeneratorEditionDialog
        extends BaseModal implements PropertyEditionPopup {

    @UiField
    ListBox generatorType;

    @UiField
    TextBox generatorName;

    @UiField
    ControlGroup generatorControlGroup;

    @UiField
    HelpInline generatorNameInline;

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
        setMaxHeigth( "350px" );
        add( uiBinder.createAndBindUi( this ) );

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

        generatorType.addItem( "NONE", "NONE" );
        generatorType.addItem( "SEQUENCE", "SEQUENCE");
        generatorType.addItem( "TABLE", "TABLE");
        generatorType.addItem( "IDENTITY", "IDENTITY");
        generatorType.addItem( "AUTO", "AUTO");

    }

    private void addHiddlenHandler() {
        addHiddenHandler( new HiddenHandler() {
            @Override
            public void onHidden( HiddenEvent hiddenEvent ) {
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
        String strategy = (String)fieldInfo.getCurrentValue( "strategy" );
        String generator = ( String ) fieldInfo.getCurrentValue( "generator" );

        strategy = strategy != null ? strategy : "NONE";
        generatorType.setSelectedValue( strategy );
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
