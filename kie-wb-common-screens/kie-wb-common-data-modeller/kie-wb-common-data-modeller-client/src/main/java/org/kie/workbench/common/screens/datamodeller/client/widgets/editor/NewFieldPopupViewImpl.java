/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.editor;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.shared.event.ModalShownEvent;
import org.gwtbootstrap3.client.shared.event.ModalShownHandler;
import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

@Dependent
public class NewFieldPopupViewImpl
        extends BaseModal
        implements NewFieldPopupView {

    interface Binder extends
            UiBinder<Widget, NewFieldPopupViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField
    FormLabel newPropertyIdLabel;

    @UiField
    TextBox newPropertyId;

    @UiField
    FormLabel newPropertyLabelLabel;

    @UiField
    TextBox newPropertyLabel;

    @UiField
    FormLabel newPropertyTypeLabel;

    @UiField
    Select newPropertyTypeList;

    @UiField
    CheckBox isNewPropertyMultiple;

    @UiField
    Button createButton;

    @UiField
    Button createAndContinue;

    @UiField
    Button cancelButton;

    @UiField
    Alert messageHelpInline;

    private Presenter presenter;

    @Inject
    public NewFieldPopupViewImpl() {
    }

    @PostConstruct
    private void init() {

        setTitle( Constants.INSTANCE.objectBrowser_newFieldTitle() );

        setBody( uiBinder.createAndBindUi( this ) );

        newPropertyTypeList.addValueChangeHandler( e -> presenter.onTypeChange() );

        createButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onCreate();
            }
        } );

        createAndContinue.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onCreateAndContinue();
            }
        } );

        cancelButton.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onCancel();
            }
        } );

        addShownHandler( new ModalShownHandler() {
            @Override
            public void onShown( ModalShownEvent shownEvent ) {
                newPropertyId.setFocus( true );
            }
        } );

        newPropertyId.addKeyUpHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                clearErrorMessage();
            }
        } );
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void initTypeList( List<Pair<String, String>> options, boolean includeEmptyItem ) {
        UIUtil.initList( newPropertyTypeList, options, includeEmptyItem );
    }

    @Override
    public String getSelectedType() {
        return newPropertyTypeList.getValue();
    }

    @Override
    public String getFieldName() {
        return newPropertyId.getText();
    }

    @Override
    public String getFieldLabel() {
        return newPropertyLabel.getText();
    }

    @Override
    public boolean getIsMultiple() {
        return isNewPropertyMultiple.getValue();
    }

    @Override
    public void setIsMultiple( boolean multiple ) {
        isNewPropertyMultiple.setValue( multiple );
    }

    @Override
    public void enableIsMultiple( boolean enabled ) {
        isNewPropertyMultiple.setEnabled( enabled );
    }

    @Override
    public void setErrorMessage( String errorMessage ) {
        messageHelpInline.setVisible( true );
        messageHelpInline.setText( errorMessage );
    }

    @Override
    public void setFocusOnFieldName() {
        Scheduler.get().scheduleDeferred( new Command() {
            @Override
            public void execute() {
                newPropertyId.setFocus( true );
            }
        } );
    }

    @Override
    public void clear() {
        newPropertyId.setText( null );
        newPropertyLabel.setText( null );
        if ( newPropertyTypeList.getValue() != null ) {
            UIUtil.setSelectedValue( newPropertyTypeList, UIUtil.NOT_SELECTED );
        }
        isNewPropertyMultiple.setValue( false );
        isNewPropertyMultiple.setEnabled( true );
        clearErrorMessage();
    }

    private void clearErrorMessage() {
        messageHelpInline.setText( null );
        messageHelpInline.setVisible( false );
    }
}
