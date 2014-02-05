/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectDeletedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectSelectedEvent;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.services.shared.validation.ValidatorCallback;
import org.uberfire.client.common.popups.errors.ErrorPopup;

public class DataObjectEditor extends Composite {

    interface DataObjectDetailEditorUIBinder
            extends UiBinder<Widget, DataObjectEditor> {

    }

    public static final String NOT_SELECTED = "NOT_SELECTED";

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

    @UiField
    ListBox roleSelector;

    @UiField
    Icon roleHelpIcon;

    @Inject
    Event<DataModelerEvent> dataModelerEvent;

    DataObjectTO dataObject;

    DataModelerContext context;

    @Inject
    private ValidatorService validatorService;

    private boolean readonly = true;

    private static DataObjectDetailEditorUIBinder uiBinder = GWT.create( DataObjectDetailEditorUIBinder.class );

    public DataObjectEditor() {
        initWidget( uiBinder.createAndBindUi( this ) );

        roleHelpIcon.getElement().getStyle().setPaddingLeft( 4, Style.Unit.PX );
        roleHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
    }

    @PostConstruct
    void init() {

        superclassSelector.getSuperclassList().addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                superClassChanged( event );
            }
        } );

        roleSelector.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                roleChanged( event );
            }
        } );
        // TODO Change this when necessary (for now hardcoded here)
        roleSelector.addItem( "", NOT_SELECTED );
        roleSelector.addItem( "EVENT", "EVENT" );
        roleSelector.setSelectedValue( NOT_SELECTED );

        packageSelectorPanel.add( packageSelector );
        packageSelector.getPackageList().addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                packageChanged( event );
            }
        } );
        setReadonly(true);
    }

    public DataObjectTO getDataObject() {
        return dataObject;
    }

    public void setDataObject( DataObjectTO dataObject ) {
        this.dataObject = dataObject;
    }

    public DataModelerContext getContext() {
        return context;
    }

    public void setContext( DataModelerContext context ) {
        this.context = context;
        packageSelector.setContext( context );
        superclassSelector.setContext( context );
    }

    private DataModelTO getDataModel() {
        return getContext() != null ? getContext().getDataModel() : null;
    }

    private void setReadonly(boolean readonly) {
        this.readonly = readonly;
        boolean value = !readonly;

        name.setEnabled( value );
        label.setEnabled( value );
        description.setEnabled( value );
        packageSelector.setEnabled( value );
        superclassSelector.setEnabled( value );
        roleSelector.setEnabled( value );
        roleHelpIcon.setVisible( value );
    }

    private boolean isReadonly() {
        return readonly;
    }

    private void loadDataObject( DataObjectTO dataObject ) {
        clean();
        setReadonly( true );
        if ( dataObject != null ) {
            setDataObject( dataObject );

            name.setText( dataObject.getName() );

            AnnotationTO annotation = dataObject.getAnnotation( AnnotationDefinitionTO.LABEL_ANNOTATION );
            if ( annotation != null ) {
                label.setText( annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ).toString() );
            }

            annotation = dataObject.getAnnotation( AnnotationDefinitionTO.DESCRIPTION_ANNOTATION );
            if ( annotation != null ) {
                description.setText( annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ).toString() );
            }

            packageSelector.setDataObject( dataObject );

            superclassSelector.setDataObject( dataObject );

            annotation = dataObject.getAnnotation( AnnotationDefinitionTO.ROLE_ANNOTATION );
            if ( annotation != null ) {
                String value = annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ) != null ? annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ).toString() : NOT_SELECTED;
                roleSelector.setSelectedValue( value );
            }

            if ( ! dataObject.isExternallyModified() ) {
                setReadonly( false );
            }
        }
    }

    // Event observers

    private void onDataObjectSelected( @Observes DataObjectSelectedEvent event ) {
        if ( event.isFrom( getDataModel() ) ) {
            loadDataObject( event.getCurrentDataObject() );
        }
    }

    private void onDataObjectDeleted( @Observes DataObjectDeletedEvent event ) {
        // When all objects from current model have been deleted clean
        if ( event.isFrom( getDataModel() ) ) {
            if ( getDataModel().getDataObjects().size() == 0 ) {
                clean();
                setDataObject( null );
                setReadonly( true );
            }
            superclassSelector.initList();
        }
    }

    // Event notifications
    private void notifyObjectChange( String memberName,
                                     Object oldValue,
                                     Object newValue ) {
        DataObjectChangeEvent changeEvent = new DataObjectChangeEvent( DataModelerEvent.DATA_OBJECT_EDITOR, getDataModel(), getDataObject(), memberName, oldValue, newValue );
        // Notify helper directly
        getContext().getHelper().dataModelChanged( changeEvent );
        dataModelerEvent.fire( changeEvent );
    }

    // Event handlers

    @UiHandler("name")
    void nameChanged( final ValueChangeEvent<String> event ) {
        if ( getDataObject() == null ) {
            return;
        }

        // Set widgets to errorpopup for styling purposes etc.
        nameLabel.setStyleName(DEFAULT_LABEL_CLASS);
        final Command afterCloseCommand = new Command() {
            @Override
            public void execute() {
                nameLabel.setStyleName(TEXT_ERROR_CLASS);
                name.selectAll();
            }
        };

        final String packageName = getDataObject().getPackageName();
        final String oldValue = getDataObject().getName();
        final String newValue = name.getValue();

        // In case an invalid name (entered before), was corrected to the original value, don't do anything but reset the label style
        if ( oldValue.equalsIgnoreCase( newValue ) ) {
            nameLabel.setStyleName(DEFAULT_LABEL_CLASS);
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
                        nameLabel.setStyleName(DEFAULT_LABEL_CLASS);
                        dataObject.setName( newValue );
                        notifyObjectChange( "name", oldValue, newValue );
                    }
                } );
            }
        } );
    }

    @UiHandler("label")
    void labelChanged( final ValueChangeEvent<String> event ) {
        if ( getDataObject() == null ) {
            return;
        }

        String oldValue = null;
        String _label = label.getValue();
        AnnotationTO annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.LABEL_ANNOTATION );

        if ( annotation != null ) {
            oldValue = AnnotationValueHandler.getInstance().getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
            if ( _label != null && !"".equals( _label ) ) {
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _label );
            } else {
                getDataObject().removeAnnotation( annotation );
            }
        } else {
            if ( _label != null && !"".equals( _label ) ) {
                getDataObject().addAnnotation( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.LABEL_ANNOTATION ), AnnotationDefinitionTO.VALUE_PARAM, _label );
            }
        }
        // TODO replace 'label' literal with annotation definition constant
        notifyObjectChange( "label", oldValue, _label );
    }

    @UiHandler("description")
    void descriptionChanged( final ValueChangeEvent<String> event ) {
        if ( getDataObject() == null ) {
            return;
        }

        String oldValue = null;
        String _description = description.getValue();
        AnnotationTO annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.DESCRIPTION_ANNOTATION );

        if ( annotation != null ) {
            oldValue = AnnotationValueHandler.getInstance().getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
            if ( _description != null && !"".equals( _description ) ) {
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _description );
            } else {
                getDataObject().removeAnnotation( annotation );
            }
        } else {
            if ( _description != null && !"".equals( _description ) ) {
                getDataObject().addAnnotation( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.DESCRIPTION_ANNOTATION ), AnnotationDefinitionTO.VALUE_PARAM, _description );
            }
        }
        notifyObjectChange( AnnotationDefinitionTO.DESCRIPTION_ANNOTATION, oldValue, _description );
    }

    private void packageChanged( ChangeEvent event ) {
        if ( getDataObject() == null ) {
            return;
        }

        // Set widgets to errorpopup for styling purposes etc.
        packageNameLabel.setStyleName(DEFAULT_LABEL_CLASS);

        final String newPackageName = packageSelector.isValueSelected() ? packageSelector.getPackageList().getValue() : null;
        final String oldPackageName = getDataObject().getPackageName();

        // No notification needed

        if ( /*(newPackageName == null && oldPackageName == null) ||*/
                (newPackageName != null && newPackageName.equalsIgnoreCase(oldPackageName)) ) {
            packageNameLabel.setStyleName(DEFAULT_LABEL_CLASS);
            return;

        } else if (newPackageName == null) {

            /*
            ErrorPopup.showMessage( Constants.INSTANCE.validation_error_invalid_package_identifier_null(), null, new Command() {
                @Override
                public void execute() {
                    packageNameLabel.setStyleName( "text-error" );
                    packageSelector.getPackageList().setFocus( true );
                }
            } );
            */

        } else {
            validatorService.isUniqueEntityName( newPackageName, getDataObject().getName(), getDataModel(), new ValidatorCallback() {
                @Override
                public void onFailure() {
                    ErrorPopup.showMessage( Constants.INSTANCE.validation_error_object_already_exists( getDataObject().getName(), newPackageName ), null, new Command() {
                        @Override
                        public void execute() {
                            packageNameLabel.setStyleName(TEXT_ERROR_CLASS);
                            packageSelector.getPackageList().setFocus( true );
                        }
                    } );
                }

                @Override
                public void onSuccess() {
                    packageNameLabel.setStyleName(DEFAULT_LABEL_CLASS);
                    dataObject.setPackageName( newPackageName );
                    notifyObjectChange( "packageName", oldPackageName, newPackageName );
                }
            } );
        }
    }

    private void superClassChanged( ChangeEvent event ) {
        if ( getDataObject() == null ) {
            return;
        }

        // Set widgets to errorpopup for styling purposes etc.
        superclassLabel.setStyleName(DEFAULT_LABEL_CLASS);

        final String newSuperClass = superclassSelector.getSuperclassList().getValue();
        final String oldSuperClass = getDataObject().getSuperClassName();

        // No notification needed
        if ( ( ( "".equalsIgnoreCase( newSuperClass ) || SuperclassSelector.NOT_SELECTED.equals( newSuperClass ) ) && oldSuperClass == null ) ||
                newSuperClass.equalsIgnoreCase( oldSuperClass ) ) {
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
                            superclassLabel.setStyleName(TEXT_ERROR_CLASS);
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
                    notifyObjectChange( "superClassName", oldSuperClass, newSuperClass );
                }
            } );
        } else {
            getDataObject().setSuperClassName( null );
            getContext().getHelper().dataObjectExtended( oldSuperClass, getDataObject().getClassName(), false );
            notifyObjectChange( "superClassName", oldSuperClass, newSuperClass );
        }
    }

    void roleChanged( final ChangeEvent event ) {
        if ( getDataObject() == null ) {
            return;
        }

        final String _role = roleSelector.getValue();
        AnnotationTO annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.ROLE_ANNOTATION );

        String oldValue = null;
        if ( annotation != null ) {
            oldValue = AnnotationValueHandler.getInstance().getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
            if ( _role != null && !NOT_SELECTED.equals( _role ) ) {
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _role );
            } else {
                getDataObject().removeAnnotation( annotation );
            }
        } else {
            if ( _role != null && !NOT_SELECTED.equals( _role ) ) {
                getDataObject().addAnnotation( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.ROLE_ANNOTATION ), AnnotationDefinitionTO.VALUE_PARAM, _role );
            }
        }
        notifyObjectChange( AnnotationDefinitionTO.ROLE_ANNOTATION, oldValue, _role );
    }

    private void clean() {
        nameLabel.setStyleName(DEFAULT_LABEL_CLASS);
        name.setText( null );
        label.setText( null );
        description.setText( null );
        packageNameLabel.setStyleName(DEFAULT_LABEL_CLASS);
        packageSelector.setDataObject( null );
        // TODO superclassLabel when its validation is put in place
        superclassSelector.setDataObject( null );
        roleSelector.setSelectedValue( NOT_SELECTED );
    }
}