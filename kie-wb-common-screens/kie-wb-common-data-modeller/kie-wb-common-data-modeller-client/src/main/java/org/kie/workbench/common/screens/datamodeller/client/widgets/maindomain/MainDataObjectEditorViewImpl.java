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

package org.kie.workbench.common.screens.datamodeller.client.widgets.maindomain;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.widgets.packageselector.PackageSelector;
import org.kie.workbench.common.screens.datamodeller.client.widgets.superselector.SuperclassSelector;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

import static org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils.*;

public class MainDataObjectEditorViewImpl
        extends Composite
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
    SuperclassSelector superclassSelector;

    private Presenter presenter;

    public MainDataObjectEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    void init() {

        superclassSelector.getSuperclassList().addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                presenter.onSuperClassChanged();
            }
        } );

        packageSelectorPanel.add( packageSelector );
        packageSelector.getPackageList().addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                presenter.onPackageChanged();
            }
        } );
        setReadonly( true );
    }

    @Override
    public void setPresenter( Presenter presenter ) {
        this.presenter = presenter;
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
        setSelectedValue( this.superclassSelector.getSuperclassList(), superClass );
    }

    @Override
    public String getSuperClass() {
        return superclassSelector.getSuperclassList().getValue();
    }

    @Override
    public void setPackageName( String packageName ) {
        packageSelector.setCurrentPackage( packageName );
    }

    @Override
    public String getPackageName() {
        return packageSelector.getPackageList().getValue();
    }

    @Override
    public boolean isPackageSelected() {
        return packageSelector.isValueSelected();
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
    public void setNameSelected() {
        name.selectAll();
    }

    @Override
    public void showErrorPopup( String errorMessage, final Command afterShow,
                                final Command afterClose ) {
        ErrorPopup.showMessage( errorMessage, afterShow, afterClose );
    }

    @Override
    public void setSuperClassOnFocus() {
        superclassSelector.getSuperclassList().setFocus( true );
    }

    @Override
    public void initSuperClassList( List<Pair<String, String>> values, String selectedValue ) {
        superclassSelector.initList( values, selectedValue );
    }

    @Override
    public void cleanSuperClassList() {
        superclassSelector.clean();
    }

    @Override
    public void initPackageSelector( DataModelerContext context ) {
        packageSelector.setContext( context );
    }

    @Override
    public void cleanPackageList() {
        packageSelector.clean();
    }

    // Handlers

    @UiHandler( "name" )
    void nameChanged( final ValueChangeEvent<String> event ) {
        presenter.onNameChanged();
    }

    @UiHandler( "label" )
    void labelChanged( final ValueChangeEvent<String> event ) {
        presenter.onLabelChanged();
    }

    @UiHandler( "description" )
    void descriptionChanged( final ValueChangeEvent<String> event ) {
        presenter.onDescriptionChanged();
    }

}
