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

package org.kie.workbench.common.widgets.client.handlers;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.kie.workbench.common.widgets.client.resources.i18n.NewItemPopupConstants;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

@ApplicationScoped
public class NewResourceView extends BaseModal implements NewResourcePresenter.View {

    interface NewResourceViewBinder
            extends
            UiBinder<Widget, NewResourceView> {

    }

    private static NewResourceViewBinder uiBinder = GWT.create( NewResourceViewBinder.class );

    private NewResourcePresenter presenter;

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

    @UiField
    FormGroup fileNameGroup;

    @UiField
    FormLabel fileTypeLabel;

    @UiField
    TextBox fileNameTextBox;

    @UiField
    HelpBlock fileNameHelpInline;

    @UiField
    FormGroup handlerExtensionsGroup;

    @UiField
    FlowPanel handlerExtensions;

    public NewResourceView() {
        footer.enableOkButton( true );

        setBody( uiBinder.createAndBindUi( NewResourceView.this ) );
        add( footer );
    }

    @Override
    public void init( final NewResourcePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void show() {
        //Clear previous resource name
        fileNameTextBox.setText( "" );
        fileNameGroup.setValidationState( ValidationState.NONE );
        fileNameHelpInline.setText( "" );
        super.show();
    }

    @Override
    public void setActiveHandler( final NewResourceHandler handler ) {
        final List<Pair<String, ? extends IsWidget>> extensions = handler.getExtensions();
        final boolean showExtensions = !( extensions == null || extensions.isEmpty() );
        fileTypeLabel.setText( handler.getDescription() );

        handlerExtensions.clear();
        handlerExtensionsGroup.setVisible( showExtensions );
        if ( showExtensions ) {
            for ( Pair<String, ? extends IsWidget> extension : extensions ) {
                handlerExtensions.add( extension.getK2() );
            }
        }
    }

    void onOKButtonClick() {
        //Generic validation
        final String fileName = fileNameTextBox.getText();
        if ( fileName == null || fileName.trim().isEmpty() ) {
            fileNameGroup.setValidationState( ValidationState.ERROR );
            fileNameHelpInline.setText( NewItemPopupConstants.INSTANCE.fileNameIsMandatory() );
            return;
        }

        //Specialized validation
        presenter.validate( fileName,
                            new ValidatorWithReasonCallback() {

                                @Override
                                public void onSuccess() {
                                    fileNameGroup.setValidationState( ValidationState.NONE );
                                    presenter.makeItem( fileName );
                                }

                                @Override
                                public void onFailure() {
                                    fileNameGroup.setValidationState( ValidationState.ERROR );
                                }

                                @Override
                                public void onFailure( final String reason ) {
                                    fileNameGroup.setValidationState( ValidationState.ERROR );
                                    fileNameHelpInline.setText( reason );
                                }

                            } );
    }

    @Override
    public void setTitle( String title ) {
        super.setTitle( title );
    }

}
