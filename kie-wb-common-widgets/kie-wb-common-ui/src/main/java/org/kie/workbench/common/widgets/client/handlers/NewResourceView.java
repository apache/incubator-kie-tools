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

package org.kie.workbench.common.widgets.client.handlers;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.base.InlineLabel;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.structure.client.validation.ValidatorWithReasonCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.NewItemPopupConstants;
import org.uberfire.commons.data.Pair;
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
    ControlGroup fileNameGroup;

    @UiField
    InlineLabel fileTypeLabel;

    @UiField
    TextBox fileNameTextBox;

    @UiField
    HelpInline fileNameHelpInline;

    @UiField
    ControlGroup handlerExtensionsGroup;

    @UiField
    VerticalPanel handlerExtensions;

    public NewResourceView() {
        footer.enableOkButton( true );

        add( uiBinder.createAndBindUi( this ) );
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
        fileNameGroup.setType( ControlGroupType.NONE );
        fileNameHelpInline.setText( "" );
        super.show();
    }

    @Override
    public void setActiveHandler( final NewResourceHandler handler ) {
        final List<Pair<String, ? extends IsWidget>> extensions = handler.getExtensions();
        final boolean showExtensions = !( extensions == null || extensions.isEmpty() );
        fileTypeLabel.setText( handler.getDescription() );

        handlerExtensions.clear();
        handlerExtensionsGroup.getElement().getStyle().setDisplay( showExtensions ? Style.Display.BLOCK : Style.Display.NONE );
        if ( showExtensions ) {
            for ( Pair<String, ? extends IsWidget> extension : extensions ) {
                final ControlGroup cg = new ControlGroup();
                cg.add( extension.getK2() );
                handlerExtensions.add( cg );
            }
        }
    }

    private void onOKButtonClick() {
        //Generic validation
        final String fileName = fileNameTextBox.getText();
        if ( fileName == null || fileName.trim().isEmpty() ) {
            fileNameGroup.setType( ControlGroupType.ERROR );
            fileNameHelpInline.setText( NewItemPopupConstants.INSTANCE.fileNameIsMandatory() );
            return;
        }

        //Specialized validation
        presenter.validate( fileName,
                            new ValidatorWithReasonCallback() {

                                @Override
                                public void onSuccess() {
                                    fileNameGroup.setType( ControlGroupType.NONE );
                                    presenter.makeItem( fileName );
                                }

                                @Override
                                public void onFailure() {
                                    fileNameGroup.setType( ControlGroupType.ERROR );
                                }

                                @Override
                                public void onFailure( final String reason ) {
                                    fileNameGroup.setType( ControlGroupType.ERROR );
                                    fileNameHelpInline.setText( reason );
                                }

                            } );
    }

    @Override
    public void setTitle( String title ) {
        super.setTitle( title );
    }

}
