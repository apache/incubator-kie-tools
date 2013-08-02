/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.widgets.configresource.client.widget.unbound;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.widgets.client.popups.footers.ModalFooterOKCancelButtons;
import org.kie.workbench.common.widgets.configresource.client.resources.i18n.ImportConstants;

public class AddImportPopup extends Modal {

    interface AddGlobalPopupBinder
            extends
            UiBinder<Widget, AddImportPopup> {

    }

    private static AddGlobalPopupBinder uiBinder = GWT.create( AddGlobalPopupBinder.class );

    @UiField
    ControlGroup importTypeGroup;

    @UiField
    TextBox importTypeTextBox;

    @UiField
    HelpInline importTypeHelpInline;

    private Command callbackCommand;

    private final Command okCommand = new Command() {
        @Override
        public void execute() {
            onOKButtonClick();
        }
    };

    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            hide();
        }
    };

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( okCommand,
                                                                                      cancelCommand );

    public AddImportPopup() {
        setTitle( ImportConstants.INSTANCE.addImportPopupTitle() );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );

        add( uiBinder.createAndBindUi( this ) );
        add( footer );

        importTypeTextBox.addKeyPressHandler( new KeyPressHandler() {
            @Override
            public void onKeyPress( final KeyPressEvent event ) {
                importTypeGroup.setType( ControlGroupType.NONE );
                importTypeHelpInline.setText( "" );
            }
        } );
    }

    private void onOKButtonClick() {
        boolean hasError = false;
        if ( importTypeTextBox.getText() == null || importTypeTextBox.getText().trim().isEmpty() ) {
            importTypeGroup.setType( ControlGroupType.ERROR );
            importTypeHelpInline.setText( ImportConstants.INSTANCE.importTypeIsMandatory() );
            hasError = true;
        } else {
            importTypeGroup.setType( ControlGroupType.NONE );
        }

        if ( hasError ) {
            return;
        }

        if ( callbackCommand != null ) {
            callbackCommand.execute();
        }
        hide();
    }

    public String getImportType() {
        return importTypeTextBox.getValue();
    }

    public void setCommand( final Command callbackCommand ) {
        this.callbackCommand = callbackCommand;
    }

    @Override
    public void show() {
        importTypeTextBox.setText( "" );
        importTypeGroup.setType( ControlGroupType.NONE );
        importTypeHelpInline.setText( "" );
        super.show();
    }

}
