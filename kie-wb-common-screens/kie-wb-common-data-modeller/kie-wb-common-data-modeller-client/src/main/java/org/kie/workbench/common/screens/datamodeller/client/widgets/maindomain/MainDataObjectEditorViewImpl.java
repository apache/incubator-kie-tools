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

package org.kie.workbench.common.screens.datamodeller.client.widgets.maindomain;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.kie.workbench.common.screens.datamodeller.client.widgets.packageselector.PackageSelector;
import org.kie.workbench.common.screens.datamodeller.client.widgets.packageselector.PackageSelectorView;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

@Dependent
public class MainDataObjectEditorViewImpl
        extends MainEditorAbstractView<MainDataObjectEditorView.Presenter>
        implements MainDataObjectEditorView {

    interface MainDataObjectEditorViewImplUiBinder
            extends UiBinder<Widget, MainDataObjectEditorViewImpl> {

    }

    private static MainDataObjectEditorViewImplUiBinder uiBinder = GWT.create( MainDataObjectEditorViewImplUiBinder.class );

    @UiField
    TextBox name;

    @UiField
    FormLabel nameLabel;

    @UiField
    FormGroup nameFormGroup;

    @UiField
    TextBox label;

    @UiField
    TextArea description;

    @UiField
    FormLabel packageNameLabel;

    @UiField
    FormGroup packageFormGroup;

    @UiField
    FlowPanel packageSelectorPanel;

    @Inject
    PackageSelector packageSelector;

    @UiField
    FormGroup superclassGroup;

    @UiField
    Select superclassSelector;

    public MainDataObjectEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    protected void init() {

        superclassSelector.addValueChangeHandler(e -> presenter.onSuperClassChange());

        packageSelectorPanel.add( packageSelector );

        packageSelector.addPackageSelectorHandler( new PackageSelectorView.PackageSelectorHandler() {

            @Override
            public void onPackageChange( String packageName ) {
                presenter.onPackageChange();
            }

            @Override
            public void onPackageAdded( String packageName ) {
                presenter.onPackageAdded();
            }
        } );

        name.addValueChangeHandler( new ValueChangeHandler<String>() {
            @Override
            public void onValueChange( ValueChangeEvent<String> event ) {
                presenter.onNameChange();
            }
        } );

        label.addValueChangeHandler( new ValueChangeHandler<String>() {
            @Override
            public void onValueChange( ValueChangeEvent<String> event ) {
                presenter.onLabelChange();
            }
        } );

        description.addValueChangeHandler( new ValueChangeHandler<String>() {
            @Override
            public void onValueChange( ValueChangeEvent<String> event ) {
                presenter.onDescriptionChange();
            }
        } );

        setReadonly( true );
    }

    @Override
    public void setName( String name ) {
        this.name.setText( name );
    }

    @Override
    public String getName() {
        return name.getText();
    }

    @Override
    public void setLabel( String label ) {
        this.label.setText( label );
    }

    @Override
    public void setDescription( String description ) {
        this.description.setText( description );
    }

    @Override
    public String getDescription() {
        return this.description.getText();
    }

    @Override
    public String getLabel() {
        return label.getText();
    }

    @Override
    public void setSuperClass( String superClass ) {
        UIUtil.setSelectedValue( superclassSelector, superClass );
    }

    @Override
    public String getSuperClass() {
        return superclassSelector.getValue();
    }

    @Override
    public void setPackageName( String packageName ) {
        packageSelector.setCurrentPackage( packageName );
    }

    @Override
    public String getPackageName() {
        return packageSelector.getPackage();
    }

    @Override
    public String getNewPackageName() {
        return packageSelector.getNewPackage();
    }

    @Override
    public void setReadonly( boolean readonly ) {
        boolean value = !readonly;

        name.setEnabled( value );
        label.setEnabled( value );
        description.setEnabled( value );
        packageSelector.setEnabled( value );
        superclassSelector.setEnabled( value );
    }

    @Override
    public void setSuperClassOnError( boolean onError ) {
        superclassGroup.setValidationState( onError ? ValidationState.ERROR : ValidationState.NONE );
    }

    @Override
    public void setPackageNameOnError( boolean onError ) {
        packageFormGroup.setValidationState( onError ? ValidationState.ERROR : ValidationState.NONE );
    }

    @Override
    public void setNameOnError( boolean onError ) {
        nameFormGroup.setValidationState( onError ? ValidationState.ERROR : ValidationState.NONE );
    }

    @Override
    public void setAllNameNameText() {
        name.selectAll();
    }

    @Override
    public void showErrorPopup( String errorMessage, final Command afterShow,
            final Command afterClose ) {
        ErrorPopup.showMessage( errorMessage, afterShow, afterClose );
    }

    @Override
    public void setSuperClassOnFocus() {
        superclassSelector.setFocus( true );
    }

    @Override
    public void initSuperClassList( List<Pair<String, String>> values, String selectedValue ) {
        UIUtil.initList( superclassSelector, values, selectedValue, true );
    }

    @Override
    public void clearSuperClassList() {
        superclassSelector.clear();
        UIUtil.refreshSelect( superclassSelector );
    }

    @Override
    public void initPackageSelector( DataModelerContext context ) {
        packageSelector.setContext( context );
    }

    @Override
    public void clearPackageList() {
        packageSelector.clear();
    }

}
