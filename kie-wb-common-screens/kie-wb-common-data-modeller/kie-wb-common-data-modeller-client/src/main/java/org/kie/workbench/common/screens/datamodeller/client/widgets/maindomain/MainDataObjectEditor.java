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

import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommand;
import org.kie.workbench.common.screens.datamodeller.model.maindomain.MainDomainAnnotations;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.ObjectEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.packageselector.PackageSelector;
import org.kie.workbench.common.screens.datamodeller.client.widgets.refactoring.ShowUsagesPopup;
import org.kie.workbench.common.screens.datamodeller.client.widgets.superselector.SuperclassSelector;
import org.kie.workbench.common.screens.datamodeller.events.ChangeType;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

public class MainDataObjectEditor extends ObjectEditor {

    interface MainDataObjectEditorUIBinder
            extends UiBinder<Widget, MainDataObjectEditor> {

    }

    private static final String DEFAULT_LABEL_CLASS = "gwt-Label";

    private static final String TEXT_ERROR_CLASS = "text-error";

    @UiField
    TextBox name;

    @UiField
    Label nameLabel;

    @UiField
    TextBox label;

    @UiField
    TextArea description;

    @UiField
    Label packageNameLabel;

    @UiField
    SimplePanel packageSelectorPanel;

    @Inject
    PackageSelector packageSelector;

    @UiField
    Label superclassLabel;

    @UiField
    SuperclassSelector superclassSelector;

    @Inject
    private Caller<DataModelerService> modelerService;

    @Inject
    private ValidatorService validatorService;

    private static MainDataObjectEditorUIBinder uiBinder = GWT.create( MainDataObjectEditorUIBinder.class );

    public MainDataObjectEditor() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    void init() {

        superclassSelector.getSuperclassList().addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                superClassChanged( event );
            }
        } );

        packageSelectorPanel.add( packageSelector );
        packageSelector.getPackageList().addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                packageChanged( event );
            }
        } );
        setReadonly( true );
    }

    @Override
    public String getName() {
        return "MAIN_OBJECT_EDITOR";
    }

    @Override
    public String getDomainName() {
        return MainDomainEditor.MAIN_DOMAIN;
    }

    public void setContext( DataModelerContext context ) {
        super.setContext( context );
        packageSelector.setContext( context );
        superclassSelector.setContext( context );
    }

    private DataModel getDataModel() {
        return getContext() != null ? getContext().getDataModel() : null;
    }

    public void refreshTypeList( boolean keepSelection ) {
        superclassSelector.refreshList( keepSelection );
    }

    public void setReadonly( boolean readonly ) {
        super.setReadonly( readonly );
        boolean value = !readonly;

        name.setEnabled( value );
        label.setEnabled( value );
        description.setEnabled( value );
        packageSelector.setEnabled( value );
        superclassSelector.setEnabled( value );
    }

    protected void loadDataObject( DataObject dataObject ) {
        clean();
        setReadonly( true );
        if ( dataObject != null ) {
            this.dataObject = dataObject;

            name.setText( dataObject.getName() );

            Annotation annotation = dataObject.getAnnotation( MainDomainAnnotations.LABEL_ANNOTATION );
            if ( annotation != null ) {
                label.setText( annotation.getValue( MainDomainAnnotations.VALUE_PARAM ).toString() );
            }

            annotation = dataObject.getAnnotation( MainDomainAnnotations.DESCRIPTION_ANNOTATION );
            if ( annotation != null ) {
                description.setText( annotation.getValue( MainDomainAnnotations.VALUE_PARAM ).toString() );
            }

            packageSelector.setDataObject( dataObject );

            superclassSelector.setDataObject( dataObject );

            setReadonly( getContext() == null || getContext().isReadonly() );
        }
    }

    // Event handlers

    @UiHandler("name")
    void nameChanged( final ValueChangeEvent<String> event ) {
        if ( getDataObject() == null ) {
            return;
        }

        // Set widgets to error popup for styling purposes etc.
        nameLabel.setStyleName( DEFAULT_LABEL_CLASS );

        final String packageName = getDataObject().getPackageName();
        final String oldValue = getDataObject().getName();
        final String newValue = name.getValue();

        final String originalClassName = getContext() != null ? getContext().getEditorModelContent().getOriginalClassName() : null;
        final String fieldName = oldValue;
        final Path currentPath = getContext() != null && getContext().getEditorModelContent() != null ? getContext().getEditorModelContent().getPath() : null;

        if ( originalClassName != null ) {
            modelerService.call( new RemoteCallback<List<Path>>() {

                @Override
                public void callback( List<Path> paths ) {

                    if ( paths != null && paths.size() > 0 ) {
                        //If usages for this field were detected in project assets
                        //show the confirmation message to the user.

                        ShowUsagesPopup showUsagesPopup = ShowUsagesPopup.newUsagesPopupForRenaming(
                                Constants.INSTANCE.modelEditor_confirm_renaming_of_used_class( originalClassName ),
                                paths,
                                new org.uberfire.mvp.Command() {
                                    @Override
                                    public void execute() {
                                        doClassNameChange( packageName, oldValue, newValue );
                                    }
                                },
                                new org.uberfire.mvp.Command() {
                                    @Override
                                    public void execute() {
                                        //do nothing.
                                        name.setValue( oldValue );
                                    }
                                }
                        );

                        showUsagesPopup.setCloseVisible( false );
                        showUsagesPopup.show();

                    } else {
                        //no usages, just proceed with the class name change.
                        doClassNameChange( packageName, oldValue, newValue );
                    }
                }
            } ).findClassUsages( currentPath, originalClassName );
        } else {
            doClassNameChange( packageName, oldValue, fieldName );
        }
    }

    private void doClassNameChange( final String packageName,
            final String oldValue,
            final String newValue ) {

        final Command afterCloseCommand = new Command() {
            @Override
            public void execute() {
                nameLabel.setStyleName( TEXT_ERROR_CLASS );
                name.selectAll();
            }
        };

        // In case an invalid name (entered before), was corrected to the original value, don't do anything but reset the label style
        if ( oldValue.equals( newValue ) ) {
            nameLabel.setStyleName( DEFAULT_LABEL_CLASS );
            return;
        }
        // Otherwise validate
        validatorService.isValidIdentifier( newValue, new ValidatorCallback() {
            @Override
            public void onFailure() {
                ErrorPopup.showMessage( Constants.INSTANCE.validation_error_invalid_object_identifier( newValue ), null, afterCloseCommand );
            }

            @Override
            public void onSuccess() {
                validatorService.isUniqueEntityName( packageName, newValue, getDataModel(), new ValidatorCallback() {
                    @Override
                    public void onFailure() {
                        ErrorPopup.showMessage( Constants.INSTANCE.validation_error_object_already_exists( newValue, packageName ), null, afterCloseCommand );
                    }

                    @Override
                    public void onSuccess() {
                        nameLabel.setStyleName( DEFAULT_LABEL_CLASS );
                        dataObject.setName( newValue );

                        notifyChange( createDataObjectChangeEvent( ChangeType.CLASS_NAME_CHANGE )
                                .withOldValue( oldValue )
                                .withNewValue( newValue ) );

                    }
                } );
            }
        } );

    }

    @UiHandler("label")
    void labelChanged( final ValueChangeEvent<String> event ) {
        if ( getDataObject() != null ) {
            String value = DataModelerUtils.nullTrim( label.getValue() );
            DataModelCommand command = commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(),
                    getName(), getDataObject(), MainDomainAnnotations.LABEL_ANNOTATION, "value", value, true );
            command.execute();
        }
    }

    @UiHandler("description")
    void descriptionChanged( final ValueChangeEvent<String> event ) {
        if ( getDataObject() != null ) {
            String value = DataModelerUtils.nullTrim( description.getValue() );
            DataModelCommand command = commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(),
                    getName(), getDataObject(), MainDomainAnnotations.DESCRIPTION_ANNOTATION, "value", value, true );
            command.execute();
        }
    }

    private void packageChanged( ChangeEvent event ) {
        if ( getDataObject() == null ) {
            return;
        }

        // Set widgets to error popup for styling purposes etc.
        packageNameLabel.setStyleName( DEFAULT_LABEL_CLASS );

        final String originalClassName = getContext() != null ? getContext().getEditorModelContent().getOriginalClassName() : null;
        final String newPackageName = packageSelector.isValueSelected() ? packageSelector.getPackageList().getValue() : null;
        final String oldPackageName = getContext().getDataObject().getPackageName();
        final Path currentPath = getContext() != null && getContext().getEditorModelContent() != null ? getContext().getEditorModelContent().getPath() : null;

        if ( ( oldPackageName != null && !oldPackageName.equals( newPackageName ) ) ||
                ( oldPackageName == null && newPackageName != null ) ) {
            //the user is trying to change the package name

            modelerService.call( new RemoteCallback<List<Path>>() {

                @Override
                public void callback( List<Path> paths ) {

                    if ( paths != null && paths.size() > 0 ) {
                        //If usages for this class were detected in project assets
                        //show the confirmation message to the user.

                        ShowUsagesPopup showUsagesPopup = ShowUsagesPopup.newUsagesPopupForChanging(
                                Constants.INSTANCE.modelEditor_confirm_package_change_of_used_class( originalClassName ),
                                paths,
                                new org.uberfire.mvp.Command() {
                                    @Override
                                    public void execute() {
                                        doPackageChange( oldPackageName, newPackageName );
                                    }
                                },
                                new org.uberfire.mvp.Command() {
                                    @Override
                                    public void execute() {
                                        //do nothing.
                                        packageSelector.getPackageList().setSelectedValue( oldPackageName );
                                    }
                                }
                        );

                        showUsagesPopup.setCloseVisible( false );
                        showUsagesPopup.show();

                    } else {
                        //no usages, just proceed with the package change.
                        doPackageChange( oldPackageName, newPackageName );
                    }
                }
            } ).findClassUsages( currentPath, originalClassName );
        } else {
            doPackageChange( oldPackageName, newPackageName );
        }
    }

    private void doPackageChange( String oldPackageName,
            String newPackageName ) {
        getDataObject().setPackageName( newPackageName );

        notifyChange( createDataObjectChangeEvent( ChangeType.PACKAGE_NAME_CHANGE )
                .withOldValue( oldPackageName )
                .withNewValue( newPackageName ) );
    }

    private void superClassChanged( ChangeEvent event ) {
        if ( getDataObject() == null ) {
            return;
        }

        // Set widgets to error popup for styling purposes etc.
        superclassLabel.setStyleName( DEFAULT_LABEL_CLASS );

        final String newSuperClass = superclassSelector.getSuperclassList().getValue();
        final String oldSuperClass = getDataObject().getSuperClassName();

        // No notification needed
        if ( ( ( "".equals( newSuperClass ) || SuperclassSelector.NOT_SELECTED.equals( newSuperClass ) ) && oldSuperClass == null ) ||
                newSuperClass.equals( oldSuperClass ) ) {
            superclassLabel.setStyleName( DEFAULT_LABEL_CLASS );
            return;
        }

        if ( newSuperClass != null && !"".equals( newSuperClass ) && !SuperclassSelector.NOT_SELECTED.equals( newSuperClass ) ) {
            validatorService.canExtend( getContext(), getDataObject().getClassName(), newSuperClass, new ValidatorCallback() {
                @Override
                public void onFailure() {
                    ErrorPopup.showMessage( Constants.INSTANCE.validation_error_cyclic_extension( getDataObject().getClassName(), newSuperClass ), null, new Command() {
                        @Override
                        public void execute() {
                            superclassLabel.setStyleName( TEXT_ERROR_CLASS );
                            superclassSelector.getSuperclassList().setFocus( true );
                        }
                    } );
                }

                @Override
                public void onSuccess() {
                    getDataObject().setSuperClassName( newSuperClass );

                    // Remove former extension refs if superclass has changed
                    if ( oldSuperClass != null && !"".equals( oldSuperClass ) ) {
                        getContext().getHelper().dataObjectExtended( oldSuperClass, getDataObject().getClassName(), false );
                    }
                    getContext().getHelper().dataObjectExtended( newSuperClass, getDataObject().getClassName(), true );
                    notifyChange( createDataObjectChangeEvent( ChangeType.SUPER_CLASS_NAME_CHANGE )
                            .withOldValue( oldSuperClass )
                            .withNewValue( newSuperClass ) );
                }
            } );
        } else {
            getDataObject().setSuperClassName( null );
            getContext().getHelper().dataObjectExtended( oldSuperClass, getDataObject().getClassName(), false );
            notifyChange(  createDataObjectChangeEvent( ChangeType.SUPER_CLASS_NAME_CHANGE )
                    .withOldValue( oldSuperClass )
                    .withNewValue( null ) );
        }
    }

    public void clean() {
        nameLabel.setStyleName( DEFAULT_LABEL_CLASS );
        name.setText( null );
        label.setText( null );
        description.setText( null );
        //packageNameLabel.setStyleName(DEFAULT_LABEL_CLASS);
        packageSelector.setDataObject( null );
        // TODO superclassLabel when its validation is put in place
        superclassSelector.setDataObject( null );
    }
}