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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.event.ShownEvent;
import com.github.gwtbootstrap.client.ui.event.ShownHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.commons.data.Pair;
import org.kie.workbench.common.services.shared.validation.ValidatorWithReasonCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.NewItemPopupConstants;
import org.uberfire.client.common.popups.footers.ModalFooterOKCancelButtons;

@ApplicationScoped
public class NewResourceView extends Modal implements NewResourcePresenter.View {

    interface NewResourceViewBinder
            extends
            UiBinder<Widget, NewResourceView> {

    }

    private static NewResourceViewBinder uiBinder = GWT.create( NewResourceViewBinder.class );

    private NewResourcePresenter presenter;

    private final Map<NewResourceHandler, RadioButton> handlerToWidgetMap = new HashMap<NewResourceHandler, RadioButton>();

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

    private Widget activeHandlerWidget = null;

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons( okCommand,
                                                                                      cancelCommand );

    @UiField
    ControlGroup fileNameGroup;

    @UiField
    TextBox fileNameTextBox;

    @UiField
    HelpInline fileNameHelpInline;

    @UiField
    VerticalPanel handlerExtensions;

    public NewResourceView() {
        setMaxHeigth( ( Window.getClientHeight() * 0.75 ) + "px" );
        setBackdrop( BackdropType.STATIC );
        setKeyboard( true );
        setAnimation( true );
        setDynamicSafe( true );

        footer.enableOkButton( true );

        add( uiBinder.createAndBindUi( this ) );
        add( footer );

        addShownHandler( new ShownHandler() {
            @Override
            public void onShown( ShownEvent shownEvent ) {
                if ( activeHandlerWidget != null ) {
                    activeHandlerWidget.getElement().scrollIntoView();
                }
            }
        } );
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
        handlerExtensions.clear();
        handlerExtensions.setVisible( !( extensions == null || extensions.isEmpty() ) );
        if ( extensions != null ) {
            for ( Pair<String, ? extends IsWidget> extension : extensions ) {
                handlerExtensions.add( extension.getK2() );
            }
        }

        //Select handler
        final RadioButton option = handlerToWidgetMap.get( handler );
        activeHandlerWidget = option;
        if ( option != null ) {
            option.setValue( true,
                             true );
        }

    }

    @Override
    public void setHandlers( final List<NewResourceHandler> handlers ) {
        //Sort handlers by description
        Collections.sort( handlers, new Comparator<NewResourceHandler>() {
            @Override
            public int compare( final NewResourceHandler o1,
                                final NewResourceHandler o2 ) {
                return o1.getDescription().compareToIgnoreCase( o2.getDescription() );
            }
        } );

    }

    @Override
    public void enableHandler( final NewResourceHandler handler,
                               final boolean enabled ) {
        final RadioButton handlerOption = this.handlerToWidgetMap.get( handler );
        if ( handlerOption == null ) {
            return;
        }
        handlerOption.setEnabled( enabled );
    }

    private RadioButton makeOption( final NewResourceHandler handler ) {
        final RadioButton option = new RadioButton( "handlers",
                                                    handler.getDescription() );
        option.addValueChangeHandler( new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                if ( event.getValue() == true ) {
                    selectNewResourceHandler( handler );
                }
            }
        } );

        return option;
    }

    private void selectNewResourceHandler( final NewResourceHandler handler ) {
        setActiveHandler( handler );
        footer.enableOkButton( true );
        presenter.setActiveHandler( handler );
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
    public void setTitle(String title){
        super.setTitle(title);
    }

}
