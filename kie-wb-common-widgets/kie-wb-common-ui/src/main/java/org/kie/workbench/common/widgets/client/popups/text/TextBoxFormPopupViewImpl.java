/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.widgets.client.popups.text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class TextBoxFormPopupViewImpl
        extends BaseModal
        implements TextBoxFormPopupView {

    private Presenter presenter;

    interface AddNewKBasePopupViewImplBinder
            extends
            UiBinder<Widget, TextBoxFormPopupViewImpl> {

    }

    private static AddNewKBasePopupViewImplBinder uiBinder = GWT.create( AddNewKBasePopupViewImplBinder.class );

    @UiField
    TextBox nameTextBox;

    @UiField
    FormGroup formGroupName;

    @UiField
    HelpBlock nameHelpBlock;

    public TextBoxFormPopupViewImpl() {
        final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( new Command() {
            @Override
            public void execute() {
                presenter.onOk();
            }
        }, new Command() {
            @Override
            public void execute() {
                hide();
            }
        }
        );

        setBody( uiBinder.createAndBindUi( TextBoxFormPopupViewImpl.this ) );
        add( footer );
        setTitle( CommonConstants.INSTANCE.New() );
    }

    @Override
    public void show() {
        formGroupName.setValidationState( ValidationState.NONE );
        nameHelpBlock.setText( "" );
        super.show();
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public String getName() {
        return nameTextBox.getText();
    }

    @Override
    public void setName( String name ) {
        nameTextBox.setText( name );
    }

    @Override
    public void showFieldEmptyWarning() {
        formGroupName.setValidationState( ValidationState.ERROR );
        nameHelpBlock.setText( CommonConstants.INSTANCE.PleaseSetAName() );
    }

}
