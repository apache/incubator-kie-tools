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
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Command;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommand;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.ObjectEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.refactoring.ShowUsagesPopup;
import org.kie.workbench.common.screens.datamodeller.client.widgets.superselector.SuperclassSelectorHelper;
import org.kie.workbench.common.screens.datamodeller.model.maindomain.MainDomainAnnotations;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

@Dependent
public class MainDataObjectEditor
        extends ObjectEditor
        implements MainDataObjectEditorView.Presenter {

    private MainDataObjectEditorView view;

    @Inject
    private Caller<DataModelerService> modelerService;

    @Inject
    private ValidatorService validatorService;

    public MainDataObjectEditor() {
    }

    @Inject
    public MainDataObjectEditor( MainDataObjectEditorView view ) {

        this.view = view;
        view.setPresenter( this );
        initWidget( view.asWidget() );
    }

    @PostConstruct
    void init() {
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

    public void onContextChange( DataModelerContext context ) {
        this.context = context;
        view.initPackageSelector( context );
        super.onContextChange( context );
    }

    private DataModel getDataModel() {
        return getContext() != null ? getContext().getDataModel() : null;
    }

    public void refreshTypeList( boolean keepSelection ) {
        initSuperClassList( keepSelection );
    }

    public void setReadonly( boolean readonly ) {
        super.setReadonly( readonly );
        view.setReadonly( readonly );
    }

    @Override
    public void onNameChanged() {
        if ( getDataObject() != null ) {

            // Set widgets to error popup for styling purposes etc.
            view.setNameOnError( false );

            final String packageName = getDataObject().getPackageName();
            final String oldValue = getDataObject().getName();
            final String newValue = view.getName();

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
                                            view.setName( oldValue );
                                        }
                                    }
                            );

                            showUsagesPopup.setClosable( false );
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
    }

    @Override
    public void onLabelChanged() {
        if ( getDataObject() != null ) {
            String value = DataModelerUtils.nullTrim( view.getLabel() );
            DataModelCommand command = commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(),
                    getName(), getDataObject(), MainDomainAnnotations.LABEL_ANNOTATION, "value", value, true );
            command.execute();
        }
    }

    @Override
    public void onDescriptionChanged() {
        if ( getDataObject() != null ) {
            String value = DataModelerUtils.nullTrim( view.getDescription() );
            DataModelCommand command = commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(),
                    getName(), getDataObject(), MainDomainAnnotations.DESCRIPTION_ANNOTATION, "value", value, true );
            command.execute();
        }
    }

    @Override
    public void onSuperClassChanged() {
        if ( getDataObject() != null ) {

            // Set widgets to error popup for styling purposes etc.
            view.setSuperClassOnError( false );

            final String newSuperClass = view.getSuperClass();
            final String oldSuperClass = getDataObject().getSuperClassName();

            // No change needed
            if ( ( ( "".equals( newSuperClass ) || DataModelerUtils.NOT_SELECTED.equals( newSuperClass ) ) && oldSuperClass == null ) ||
                    newSuperClass.equals( oldSuperClass ) ) {
                return;
            }

            if ( newSuperClass != null && !"".equals( newSuperClass ) && !DataModelerUtils.NOT_SELECTED.equals( newSuperClass ) ) {
                validatorService.canExtend( getContext(), getDataObject().getClassName(), newSuperClass, new ValidatorCallback() {
                    @Override
                    public void onFailure() {
                        view.showErrorPopup( Constants.INSTANCE.validation_error_cyclic_extension( getDataObject().getClassName(), newSuperClass ), null, new Command() {
                            @Override
                            public void execute() {
                                view.setSuperClassOnError( true );
                                view.setSuperClassOnFocus();
                            }
                        } );
                    }

                    @Override
                    public void onSuccess() {
                        commandBuilder.buildDataObjectSuperClassChangeCommand( getContext(), getName(),
                                getDataObject(), newSuperClass ).execute();

                        getDataObject().setSuperClassName( newSuperClass );
                    }
                } );
            } else {
                commandBuilder.buildDataObjectSuperClassChangeCommand( getContext(), getName(),
                        getDataObject(), null ).execute();
            }
        }
    }

    @Override
    public void onPackageChanged() {

        if ( getDataObject() != null ) {

            // Set widgets to error popup for styling purposes etc.
            view.setPackageNameOnError( false );

            final String originalClassName = getContext() != null ? getContext().getEditorModelContent().getOriginalClassName() : null;
            final String newPackageName = view.isPackageSelected() ? view.getPackageName() : null;
            final String oldPackageName = getDataObject().getPackageName();
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
                                            view.setPackageName( oldPackageName );
                                        }
                                    }
                            );

                            showUsagesPopup.setClosable( false );
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

    }

    protected void loadDataObject( DataObject dataObject ) {
        clean();
        setReadonly( true );
        if ( dataObject != null ) {
            this.dataObject = dataObject;

            view.setName( dataObject.getName() );

            Annotation annotation = dataObject.getAnnotation( MainDomainAnnotations.LABEL_ANNOTATION );
            if ( annotation != null ) {
                view.setLabel( AnnotationValueHandler.getStringValue( annotation, MainDomainAnnotations.VALUE_PARAM ) );
            }

            annotation = dataObject.getAnnotation( MainDomainAnnotations.DESCRIPTION_ANNOTATION );
            if ( annotation != null ) {
                view.setDescription( AnnotationValueHandler.getStringValue( annotation, MainDomainAnnotations.VALUE_PARAM ) );
            }

            view.setPackageName( dataObject.getPackageName() );

            initSuperClassList( false );

            setReadonly( getContext() == null || getContext().isReadonly() );
        }
    }

    private void initSuperClassList( boolean keepSelection ) {
        String currentValue = keepSelection ? view.getSuperClass() : ( dataObject != null ? dataObject.getSuperClassName() : null );
        view.initSuperClassList(
                SuperclassSelectorHelper.buildSuperclassSelectorOptions( getDataModel(), dataObject ),
                currentValue );

    }

    // Event handlers

    private void doClassNameChange( final String packageName,
            final String oldValue,
            final String newValue ) {

        final Command afterCloseCommand = new Command() {
            @Override
            public void execute() {
                view.setNameOnError( true );
                view.setNameSelected();
            }
        };

        // In case an invalid name (entered before), was corrected to the original value, don't do anything but reset the label style
        if ( oldValue.equals( newValue ) ) {
            view.setNameOnError( false );
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
                        view.setNameOnError( false );

                        commandBuilder.buildDataObjectNameChangeCommand( getContext(), getName(),
                                getDataObject(), newValue ).execute();

                    }
                } );
            }
        } );

    }

    private void doPackageChange( String oldPackageName,
            String newPackageName ) {

        commandBuilder.buildDataObjectPackageChangeCommand( getContext(), getName(),
                getDataObject(), newPackageName ).execute();

    }

    public void clean() {
        view.setNameOnError( false );
        view.setName( null );
        view.setLabel( null );
        view.setDescription( null );
        view.setPackageNameOnError( false );

        view.cleanPackageList();
        view.cleanSuperClassList();
    }
}