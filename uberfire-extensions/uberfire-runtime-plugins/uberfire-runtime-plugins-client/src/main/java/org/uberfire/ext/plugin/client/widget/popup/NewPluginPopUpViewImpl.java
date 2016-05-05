/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.plugin.client.widget.popup;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class NewPluginPopUpViewImpl extends BaseModal implements NewPluginPopUpView {

    private NewPluginPopUpView.Presenter presenter;

    private PluginType type;

    interface ViewBinder
            extends
            UiBinder<Widget, NewPluginPopUpViewImpl> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    private final Command okCommand = new Command() {
        @Override
        public void execute() {
            onOKButtonClick();
        }
    };

    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            presenter.onCancel();
        }
    };

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( okCommand,
                                                                                      cancelCommand );

    @UiField
    TextBox name;

    @UiField
    HelpBlock nameHelpInline;

    @UiField
    FormGroup nameGroup;

    public void init( NewPluginPopUpView.Presenter presenter ) {
        this.presenter = presenter;

        footer.enableOkButton( true );

        add( new ModalBody() {{
            add( uiBinder.createAndBindUi( NewPluginPopUpViewImpl.this ) );
        }} );
        add( footer );
    }

    public void show( final PluginType type ) {
        this.type = checkNotNull( "type", type );

        name.setText( "" );
        nameHelpInline.setText( "" );
        nameGroup.setValidationState( ValidationState.NONE );

        switch ( this.type ) {
            case PERSPECTIVE:
                setTitle( CommonConstants.INSTANCE.NewPerspectivePopUpTitle() );
                break;
            case PERSPECTIVE_LAYOUT:
                setTitle( CommonConstants.INSTANCE.NewPerspectiveLayoutPopUpTitle() );
                break;
            case SCREEN:
                setTitle( CommonConstants.INSTANCE.NewScreenPopUpTitle() );
                break;
            case EDITOR:
                setTitle( CommonConstants.INSTANCE.NewEditorPopUpTitle() );
                break;
            case SPLASH:
                setTitle( CommonConstants.INSTANCE.NewSplashScreenPopUpTitle() );
                break;
            case DYNAMIC_MENU:
                setTitle( CommonConstants.INSTANCE.NewDynamicMenuPopUpTitle() );
                break;
        }
        super.show();
    }

    private void onOKButtonClick() {
        presenter.onOK( name.getText(), type );
    }

    @Override
    public void handleNameValidationError( String errorMessage ) {
        nameGroup.setValidationState( ValidationState.ERROR );
        nameHelpInline.setText( errorMessage );
    }

    public String emptyName() {
        return "Name is mandatory.";
    }

    public String invalidName() {
        return "Invalid plugin name.";
    }

    public String duplicatedName() {
        return "Plugin name already exists.";
    }
}
