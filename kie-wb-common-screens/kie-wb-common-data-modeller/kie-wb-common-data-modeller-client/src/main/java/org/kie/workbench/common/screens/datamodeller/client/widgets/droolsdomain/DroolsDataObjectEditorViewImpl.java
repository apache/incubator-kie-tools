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

package org.kie.workbench.common.screens.datamodeller.client.widgets.droolsdomain;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.kie.workbench.common.screens.datamodeller.client.util.ErrorPopupHelper;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.uberfire.commons.data.Pair;
import org.uberfire.mvp.Command;

@Dependent
public class DroolsDataObjectEditorViewImpl
        extends Composite
        implements DroolsDataObjectEditorView {

    interface DroolsDataObjectEditorUIBinder
            extends UiBinder<Widget, DroolsDataObjectEditorViewImpl> {

    }

    @UiField
    Select roleSelector;

    @UiField
    CheckBox classReactiveSelector;

    @UiField
    CheckBox propertyReactiveSelector;

    @UiField
    Select typeSafeSelector;

    @UiField
    Select timestampFieldSelector;

    @UiField
    Select durationFieldSelector;

    @UiField
    FormGroup expiresFormGroup;

    @UiField
    TextBox expires;

    @UiField
    CheckBox remotableSelector;

    private static DroolsDataObjectEditorUIBinder uiBinder = GWT.create( DroolsDataObjectEditorUIBinder.class );

    private Presenter presenter;

    public DroolsDataObjectEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    protected void init() {
        roleSelector.addValueChangeHandler( e -> presenter.onRoleChange() );

        typeSafeSelector.addValueChangeHandler( e -> presenter.onTypeSafeChange() );

        timestampFieldSelector.addValueChangeHandler( e -> presenter.onTimeStampFieldChange() );

        durationFieldSelector.addValueChangeHandler( e -> presenter.onDurationFieldChange() );

        propertyReactiveSelector.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onPropertyReactiveChange();
            }
        } );

        classReactiveSelector.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onClassReactiveChange();
            }
        } );

        expires.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                presenter.onExpiresChange();
            }
        } );

        remotableSelector.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onRemotableChange();
            }
        } );

        setReadonly( true );
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setRole( String role ) {
        UIUtil.setSelectedValue( roleSelector, role );
    }

    @Override
    public String getRole() {
        return roleSelector.getValue();
    }

    @Override
    public void setClassReactive( boolean classReactive ) {
        this.classReactiveSelector.setValue( classReactive );
    }

    @Override
    public boolean getClassReactive() {
        return classReactiveSelector.getValue();
    }

    @Override
    public void setPropertyReactive( boolean propertyReactive ) {
        this.propertyReactiveSelector.setValue( propertyReactive );
    }

    @Override
    public boolean getPropertyReactive() {
        return propertyReactiveSelector.getValue();
    }

    @Override
    public void setTypeSafe( String typeSafe ) {
        UIUtil.setSelectedValue( typeSafeSelector, typeSafe );
    }

    @Override
    public String getTypeSafe() {
        return typeSafeSelector.getValue();
    }

    @Override
    public void setTimeStampField( String timeStampField ) {
        UIUtil.setSelectedValue( timestampFieldSelector, timeStampField );
    }

    @Override
    public String getTimeStampField() {
        return timestampFieldSelector.getValue();
    }

    @Override
    public void setDurationField( String durationField ) {
        UIUtil.setSelectedValue( durationFieldSelector, durationField );
    }

    @Override
    public String getDurationField() {
        return durationFieldSelector.getValue();
    }

    @Override
    public void setExpires( String expires ) {
        this.expires.setText( expires );
    }

    @Override
    public String getExpires() {
        return expires.getText();
    }

    @Override
    public void setExpiresOnError( boolean onError ) {
        expiresFormGroup.setValidationState( onError ? ValidationState.ERROR : ValidationState.NONE );
    }

    @Override
    public void selectAllExpiresText() {
        expires.selectAll();
    }

    @Override
    public void setRemotable( boolean remotable ) {
        this.remotableSelector.setValue( remotable );
    }

    @Override
    public boolean getRemotable() {
        return remotableSelector.getValue();
    }

    public void setReadonly( boolean readonly ) {
        boolean value = !readonly;

        roleSelector.setEnabled( value );
        propertyReactiveSelector.setEnabled( value );
        classReactiveSelector.setEnabled( value );
        typeSafeSelector.setEnabled( value );
        expires.setEnabled( value );
        durationFieldSelector.setEnabled( value );
        timestampFieldSelector.setEnabled( value );
        remotableSelector.setEnabled( value );
    }

    @Override
    public void initRoleList( List<Pair<String, String>> options, boolean includeEmptyOption ) {
        UIUtil.initList( roleSelector, options, includeEmptyOption );
    }

    @Override
    public void initTypeSafeList( List<Pair<String, String>> options, boolean includeEmptyOption ) {
        UIUtil.initList( typeSafeSelector, options, includeEmptyOption );
    }

    @Override
    public void initTimeStampFieldList( List<Pair<String, String>> options, boolean includeEmptyOption ) {
        UIUtil.initList( timestampFieldSelector, options, includeEmptyOption );
    }

    @Override
    public void initDurationFieldList( List<Pair<String, String>> options, boolean includeEmptyOption ) {
        UIUtil.initList( durationFieldSelector, options, includeEmptyOption );
    }

    @Override
    public void showErrorPopup( final String message ) {
        ErrorPopupHelper.showErrorPopup( message );
    }

    @Override
    public void showErrorPopup( final String message, final Command afterShowCommand, final Command afterCloseCommand ) {
        ErrorPopupHelper.showErrorPopup( message, afterShowCommand, afterCloseCommand );
    }

    public void clear() {
        UIUtil.setSelectedValue( roleSelector, UIUtil.NOT_SELECTED );
        classReactiveSelector.setValue( false );
        propertyReactiveSelector.setValue( false );
        UIUtil.setSelectedValue( typeSafeSelector, UIUtil.NOT_SELECTED );
        expires.setText( null );
        UIUtil.setSelectedValue( durationFieldSelector, UIUtil.NOT_SELECTED );
        UIUtil.setSelectedValue( timestampFieldSelector, UIUtil.NOT_SELECTED );
        remotableSelector.setValue( false );
    }
}