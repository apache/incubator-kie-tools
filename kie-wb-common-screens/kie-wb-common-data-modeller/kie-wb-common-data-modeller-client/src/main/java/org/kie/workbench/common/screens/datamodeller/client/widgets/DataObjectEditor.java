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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
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
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectDeletedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldCreatedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldDeletedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectSelectedEvent;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.ObjectPropertyTO;
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
    CheckBox classReactiveSelector;

    @UiField
    CheckBox propertyReactiveSelector;

    @UiField
    Icon roleHelpIcon;

    @UiField
    Icon classReactiveHelpIcon;

    @UiField
    Icon propertyReactiveHelpIcon;

    @UiField
    Label typeSafeLabel;

    @UiField
    Icon typeSafeHelpIcon;

    @UiField
    ListBox typeSafeSelector;

    @UiField
    Label timestampLabel;

    @UiField
    Icon timestampHelpIcon;

    @UiField
    ListBox timestampFieldSelector;

    @UiField
    Label durationLabel;

    @UiField
    ListBox durationFieldSelector;

    @UiField
    Icon durationHelpIcon;

    @UiField
    Label expiresLabel;

    @UiField
    Icon expiresHelpIcon;

    @UiField
    TextBox expires;

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

        //roleHelpIcon.getElement().getStyle().setPaddingLeft( 4, Style.Unit.PX );
        roleHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        //classReactiveHelpIcon.getElement().getStyle().setPaddingLeft( 4, Style.Unit.PX );
        classReactiveHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        //propertyReactiveHelpIcon.getElement().getStyle().setPaddingLeft( 4, Style.Unit.PX );
        propertyReactiveHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        typeSafeHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        timestampHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        durationHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        expires.getElement().getStyle().setCursor( Style.Cursor.POINTER );
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
        typeSafeSelector.addChangeHandler( new ChangeHandler() {
            @Override public void onChange( ChangeEvent event ) {
                typeSafeChanged( event );
            }
        } );
        timestampFieldSelector.addChangeHandler( new ChangeHandler() {
            @Override public void onChange( ChangeEvent event ) {
                timestampChanged( event );
            }
        } );
        durationFieldSelector.addChangeHandler( new ChangeHandler() {
            @Override public void onChange( ChangeEvent event ) {
                durationChanged( event );
            }
        } );

        // TODO Change this when necessary (for now hardcoded here)
        roleSelector.addItem( "", NOT_SELECTED );
        roleSelector.addItem( "EVENT", "EVENT" );
        roleSelector.setSelectedValue( NOT_SELECTED );

        typeSafeSelector.addItem( "", NOT_SELECTED );
        typeSafeSelector.addItem( "false", "false" );
        typeSafeSelector.addItem( "true", "true" );

        timestampFieldSelector.addItem( "", NOT_SELECTED );
        durationFieldSelector.addItem( "", NOT_SELECTED );

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
        propertyReactiveSelector.setEnabled( value );
        classReactiveSelector.setEnabled( value );
        typeSafeSelector.setEnabled( value );
        expires.setEnabled( value );
        durationFieldSelector.setEnabled( value );
        timestampFieldSelector.setEnabled( value );
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

            annotation = dataObject.getAnnotation( AnnotationDefinitionTO.PROPERTY_REACTIVE_ANNOTATION );
            if ( annotation != null ) {
                propertyReactiveSelector.setValue( Boolean.TRUE );
            }

            annotation = dataObject.getAnnotation( AnnotationDefinitionTO.CLASS_REACTIVE_ANNOTATION );
            if ( annotation != null ) {
                classReactiveSelector.setValue( Boolean.TRUE );
            }

            annotation = dataObject.getAnnotation( AnnotationDefinitionTO.TYPE_SAFE_ANNOTATION );
            if ( annotation != null ) {
                String value = annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ) != null ? annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ).toString() : NOT_SELECTED;
                typeSafeSelector.setSelectedValue( value );
            }

            annotation = dataObject.getAnnotation( AnnotationDefinitionTO.EXPIRES_ANNOTATION );
            if ( annotation != null ) {
                expires.setText( annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ).toString() );
            }

            loadDuration( dataObject );

            loadTimestamp( dataObject );

            setReadonly( false );
        }
    }

    private void loadDuration( DataObjectTO dataObject ) {
        AnnotationTO annotation;
        loadDurationSelector( dataObject );
        annotation = dataObject.getAnnotation( AnnotationDefinitionTO.DURATION_ANNOTATION );
        if ( annotation != null ) {
            String value = annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ) != null ? annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ).toString() : NOT_SELECTED;
            durationFieldSelector.setSelectedValue( value );
        }
    }

    private void loadTimestamp( DataObjectTO dataObject ) {
        AnnotationTO annotation;
        loadTimestampSelector( dataObject );
        annotation = dataObject.getAnnotation( AnnotationDefinitionTO.TIMESTAMP_ANNOTATION );
        if ( annotation != null ) {
            String value = annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ) != null ? annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ).toString() : NOT_SELECTED;
            timestampFieldSelector.setSelectedValue( value );
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

    private void onDataObjectFieldCreated(@Observes DataObjectFieldCreatedEvent event) {
        updateFieldDependentSelectors( event, event.getCurrentDataObject(), event.getCurrentField() );
    }

    private void onDataObjectFieldChange(@Observes DataObjectFieldChangeEvent event) {
        updateFieldDependentSelectors( event, event.getCurrentDataObject(), event.getCurrentField() );
    }

    private void onDataObjectFieldDeleted(@Observes DataObjectFieldDeletedEvent event) {
        updateFieldDependentSelectors(event, event.getCurrentDataObject(), event.getCurrentField());
    }

    private void updateFieldDependentSelectors( DataModelerEvent event, DataObjectTO currentDataObject, ObjectPropertyTO currentField ) {
        if ( event.isFrom( getDataModel() ) && getDataObject() == currentDataObject) {
            loadDuration( getDataObject() );
            loadTimestamp( getDataObject() );
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
        if ( oldValue.equals( newValue ) ) {
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

    void typeSafeChanged( final ChangeEvent event ) {
        if ( getDataObject() == null ) {
            return;
        }

        final String _typeSaveValue = typeSafeSelector.getValue();
        AnnotationTO annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.TYPE_SAFE_ANNOTATION );

        String oldValue = null;
        if ( annotation != null ) {
            oldValue = AnnotationValueHandler.getInstance().getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
            if ( _typeSaveValue != null && !NOT_SELECTED.equals( _typeSaveValue ) ) {
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _typeSaveValue );
            } else {
                getDataObject().removeAnnotation( annotation );
            }
        } else {
            if ( _typeSaveValue != null && !NOT_SELECTED.equals( _typeSaveValue ) ) {
                getDataObject().addAnnotation( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.TYPE_SAFE_ANNOTATION ), AnnotationDefinitionTO.VALUE_PARAM, _typeSaveValue );
            }
        }
        notifyObjectChange( AnnotationDefinitionTO.TYPE_SAFE_ANNOTATION, oldValue, _typeSaveValue );
    }

    void timestampChanged( final ChangeEvent event ) {
        if ( getDataObject() == null ) {
            return;
        }

        final String _timestampValue = timestampFieldSelector.getValue();
        AnnotationTO annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.TIMESTAMP_ANNOTATION );

        String oldValue = null;
        if ( annotation != null ) {
            oldValue = AnnotationValueHandler.getInstance().getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
            if ( _timestampValue != null && !NOT_SELECTED.equals( _timestampValue ) ) {
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _timestampValue );
            } else {
                getDataObject().removeAnnotation( annotation );
            }
        } else {
            if ( _timestampValue != null && !NOT_SELECTED.equals( _timestampValue ) ) {
                getDataObject().addAnnotation( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.TIMESTAMP_ANNOTATION ), AnnotationDefinitionTO.VALUE_PARAM, _timestampValue );
            }
        }
        notifyObjectChange( AnnotationDefinitionTO.TIMESTAMP_ANNOTATION, oldValue, _timestampValue );
    }

    void durationChanged( final ChangeEvent event ) {
        if ( getDataObject() == null ) {
            return;
        }

        final String _durationValue = durationFieldSelector.getValue();
        AnnotationTO annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.DURATION_ANNOTATION );

        String oldValue = null;
        if ( annotation != null ) {
            oldValue = AnnotationValueHandler.getInstance().getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
            if ( _durationValue != null && !NOT_SELECTED.equals( _durationValue ) ) {
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _durationValue );
            } else {
                getDataObject().removeAnnotation( annotation );
            }
        } else {
            if ( _durationValue != null && !NOT_SELECTED.equals( _durationValue ) ) {
                getDataObject().addAnnotation( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.DURATION_ANNOTATION ), AnnotationDefinitionTO.VALUE_PARAM, _durationValue );
            }
        }
        notifyObjectChange( AnnotationDefinitionTO.DURATION_ANNOTATION, oldValue, _durationValue );
    }

    @UiHandler("propertyReactiveSelector")
    void propertyReactiveChanged(final ClickEvent event) {
        if ( getDataObject() == null ) return;

        Boolean oldValue = null;
        AnnotationTO annotation = getDataObject().getAnnotation(AnnotationDefinitionTO.PROPERTY_REACTIVE_ANNOTATION);
        oldValue = annotation != null;

        final Boolean isChecked = propertyReactiveSelector.getValue();

        if ( annotation != null && !isChecked ) {
            getDataObject().removeAnnotation( annotation );
        } else if ( annotation == null && isChecked ) {
            getDataObject().addAnnotation( new AnnotationTO( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.PROPERTY_REACTIVE_ANNOTATION ) ) );
        }

        if ( isChecked ) {
            getDataObject().removeAnnotation( new AnnotationTO( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.CLASS_REACTIVE_ANNOTATION ) ) );
            classReactiveSelector.setValue( false );
        }
        //TODO check if this event is needed and add validation, this annotation cannot coexist with the ClassReactiveAnnotation
        notifyObjectChange( AnnotationDefinitionTO.PROPERTY_REACTIVE_ANNOTATION, oldValue, isChecked );
    }

    @UiHandler("classReactiveSelector")
    void classReactiveChanged(final ClickEvent event) {
        if ( getDataObject() == null ) return;

        Boolean oldValue = null;
        AnnotationTO annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.CLASS_REACTIVE_ANNOTATION );
        oldValue = annotation != null;

        final Boolean isChecked = classReactiveSelector.getValue();

        if ( annotation != null && !isChecked ) {
            getDataObject().removeAnnotation( annotation );
        } else if ( annotation == null && isChecked ) {
            getDataObject().addAnnotation( new AnnotationTO( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.CLASS_REACTIVE_ANNOTATION ) ) );
        }

        if ( isChecked )  {
            getDataObject().removeAnnotation( new AnnotationTO( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.PROPERTY_REACTIVE_ANNOTATION ) ) );
            propertyReactiveSelector.setValue( false );
        }
        //TODO check if this event is needed and add validation, this annotation cannot coexist with the PropertyReactiveAnnotation
        notifyObjectChange( AnnotationDefinitionTO.CLASS_REACTIVE_ANNOTATION, oldValue, isChecked );
    }

    @UiHandler("expires")
    void expiresChanged( final ValueChangeEvent<String> event ) {
        if ( getDataObject() == null ) {
            return;
        }

        // Set widgets to error popup for styling purposes etc.
        expiresLabel.setStyleName(DEFAULT_LABEL_CLASS);
        final Command afterCloseCommand = new Command() {
            @Override
            public void execute() {
                expiresLabel.setStyleName(TEXT_ERROR_CLASS);
                expires.selectAll();
            }
        };

        final AnnotationTO annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.EXPIRES_ANNOTATION );
        final String oldValue = annotation != null ? AnnotationValueHandler.getInstance().getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM ) : null;
        final String newValue = expires.getText();

        // In case an invalid expression (entered before), was corrected to the original value, don't do anything but reset the label style
        if ( oldValue != null && oldValue.equals( newValue ) ) {
            nameLabel.setStyleName(DEFAULT_LABEL_CLASS);
            return;
        }

        // Otherwise validate
        validatorService.isValidTimerInterval( newValue, new ValidatorCallback() {
            @Override
            public void onFailure() {
                ErrorPopup.showMessage( Constants.INSTANCE.validation_error_invalid_timer_expression( newValue ), null, afterCloseCommand );
            }

            @Override
            public void onSuccess() {
                if ( annotation != null ) {
                    getDataObject().removeAnnotation( annotation );
                }
                if ( newValue != null && !"".equals( newValue ) ) {
                    getDataObject().addAnnotation( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.EXPIRES_ANNOTATION ), AnnotationDefinitionTO.VALUE_PARAM, newValue );
                }

                notifyObjectChange( AnnotationDefinitionTO.EXPIRES_ANNOTATION, oldValue, newValue );
            }
        } );
    }

    private void loadDurationSelector(DataObjectTO dataObject) {
        if (dataObject == null) return;

        List<String> types = new ArrayList<String>( );
        types.add( "short" );
        types.add( "int" );
        types.add( "long" );
        types.add( "java.lang.Short" );
        types.add( "java.lang.Integer" );
        types.add( "java.lang.Long" );

        String defaultValue = null;
        AnnotationTO annotation = dataObject.getAnnotation( AnnotationDefinitionTO.DURATION_ANNOTATION );
        if (annotation != null) {
            defaultValue = AnnotationValueHandler.getInstance().getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
        }

        loadPropertySelector( durationFieldSelector, dataObject, types, defaultValue  );
    }

    private void loadTimestampSelector(DataObjectTO dataObject) {
        if (dataObject == null) return;

        List<String> types = new ArrayList<String>( );
        types.add( "long" );
        types.add( "java.lang.Long" );
        types.add( "java.util.Date" );
        types.add( "java.sql.Timestamp" );

        String defaultValue = null;
        AnnotationTO annotation = dataObject.getAnnotation( AnnotationDefinitionTO.TIMESTAMP_ANNOTATION );
        if (annotation != null) {
            defaultValue = AnnotationValueHandler.getInstance().getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
        }

        loadPropertySelector( timestampFieldSelector, dataObject, types, defaultValue  );
    }

    private void loadPropertySelector(ListBox selector, DataObjectTO dataObject, List<String> types, String defaultValue) {
        if (dataObject == null) return;

        List<ObjectPropertyTO> properties = DataModelerUtils.filterPropertiesByType(dataObject.getProperties(), types);
        SortedMap<String, String> propertyNames = new TreeMap<String, String>();
        for (ObjectPropertyTO property : properties) {
            propertyNames.put( property.getName(), property.getName() );
        }

        if (defaultValue != null && !"".equals( defaultValue ) ) {
            propertyNames.put( defaultValue, defaultValue );
        }

        selector.clear();
        selector.addItem( "", NOT_SELECTED );
        for (Map.Entry<String, String> propertyName : propertyNames.entrySet()) {
            selector.addItem(propertyName.getKey(), propertyName.getValue());
        }
        selector.setSelectedValue( NOT_SELECTED );
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
        classReactiveSelector.setValue( false );
        propertyReactiveSelector.setValue( false );
        typeSafeSelector.setSelectedValue( NOT_SELECTED );
        expires.setText( null );
        durationFieldSelector.setSelectedValue( NOT_SELECTED );
        timestampFieldSelector.setSelectedValue( NOT_SELECTED );
    }
}