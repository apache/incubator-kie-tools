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

package org.kie.workbench.common.screens.datamodeller.client.widgets.editor;

import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.event.ShownEvent;
import com.github.gwtbootstrap.client.ui.event.ShownHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

public class NewFieldPopupViewImpl
        extends BaseModal
        implements NewFieldPopupView {


    interface Binder extends
            UiBinder<Widget, NewFieldPopupViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    private static final String DEFAULT_LABEL_CLASS = "gwt-Label";

    private static final String TEXT_ERROR_CLASS = "text-error";

    @UiField
    Label newPropertyIdLabel = new Label(  );

    @UiField
    com.github.gwtbootstrap.client.ui.TextBox newPropertyId = new TextBox();

    @UiField
    Label newPropertyLabelLabel = new Label(  );

    @UiField
    com.github.gwtbootstrap.client.ui.TextBox newPropertyLabel = new TextBox();

    @UiField
    Label newPropertyTypeLabel = new Label(  );

    @UiField
    com.github.gwtbootstrap.client.ui.ListBox newPropertyTypeList = new ListBox(  );

    @UiField
    CheckBox isNewPropertyMultiple = new CheckBox(  );

    @UiField
    Button createButton;

    @UiField
    Button createAndContinue;

    @UiField
    Button cancelButton;

    @UiField
    HelpInline messageHelpInline;

    private Presenter presenter;

    @Inject
    public NewFieldPopupViewImpl( ) {

        setTitle( "New field" );
        setMaxHeigth( "350px" );
        setWidth( 600 );

        add( uiBinder.createAndBindUi( this ) );

        newPropertyId.getElement().getStyle().setWidth( 180, Style.Unit.PX );
        newPropertyLabel.getElement().getStyle().setWidth( 160, Style.Unit.PX );
        newPropertyTypeList.getElement().getStyle().setWidth( 200, Style.Unit.PX );

        newPropertyTypeList.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                presenter.onTypeChange();
            }
        } );

        createButton.addClickHandler( new ClickHandler() {
            @Override public void onClick( ClickEvent event ) {
                presenter.onCreate();
            }
        } );

        createAndContinue.addClickHandler( new ClickHandler() {
            @Override public void onClick( ClickEvent event ) {
                presenter.onCreateAndContinue();
            }
        } );

        cancelButton.addClickHandler( new ClickHandler() {
            @Override public void onClick( ClickEvent event ) {
                presenter.onCancel();
            }
        } );

        addShownHandler( new ShownHandler() {
            @Override public void onShown( ShownEvent shownEvent ) {
                newPropertyId.setFocus( true );
            }
        } );

        newPropertyId.addKeyUpHandler( new KeyUpHandler() {
            @Override public void onKeyUp( KeyUpEvent event ) {
                clearErrorMessage();
            }
        } );

    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public ListBox getPropertyTypeList() {
        return newPropertyTypeList;
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
        messageHelpInline.setStylePrimaryName( TEXT_ERROR_CLASS );
        messageHelpInline.setText( errorMessage );
    }

    @Override
    public void setFocusOnFieldName() {
        newPropertyId.setFocus( true );
    }

    @Override
    public void clear() {
        newPropertyId.setText( null );
        newPropertyLabel.setText( null );
        if ( newPropertyTypeList.getSelectedValue() != null ) {
            newPropertyTypeList.setSelectedValue( DataModelerUtils.NOT_SELECTED );
        }
        isNewPropertyMultiple.setValue( false );
        isNewPropertyMultiple.setEnabled( true );
        clearErrorMessage();
    }

    private void clearErrorMessage() {
        messageHelpInline.setText( null );
        messageHelpInline.removeStyleName( TEXT_ERROR_CLASS );
    }
}
