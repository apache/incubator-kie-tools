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
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.FieldEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.refactoring.ShowUsagesPopup;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldDeletedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldSelectedEvent;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

public class MainDataObjectFieldEditor extends FieldEditor {

    interface DataObjectFieldEditorUIBinder
            extends UiBinder<Widget, MainDataObjectFieldEditor> {

    }

    //https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.11
    private static int MAX_CLASS_FIELDS = 65535;

    private static DataObjectFieldEditorUIBinder uiBinder = GWT.create( DataObjectFieldEditorUIBinder.class );

    private static final String DEFAULT_LABEL_CLASS = "gwt-Label";

    private static final String TEXT_ERROR_CLASS = "text-error";

    @UiField
    Label nameLabel;

    @UiField
    TextBox name;

    @UiField
    TextBox label;

    @UiField
    TextArea description;

    @UiField
    ListBox typeSelector;

    @UiField
    CheckBox isTypeMultiple;

    @Inject
    Event<DataModelerEvent> dataModelerEventEvent;

    @Inject
    private ValidatorService validatorService;

    @Inject
    private Caller<DataModelerService> modelerService;

    public MainDataObjectFieldEditor() {
        initWidget( uiBinder.createAndBindUi( this ) );

        typeSelector.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                typeChanged( event );
            }
        } );

        isTypeMultiple.addValueChangeHandler( new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange( ValueChangeEvent<Boolean> event ) {
                typeMultipleChanged( event );
            }
        } );

        setReadonly( true );
    }

    public void setContext( DataModelerContext context ) {
        this.context = context;
        initTypeList();
        isTypeMultiple.setEnabled( false );
    }

    private DataModel getDataModel() {
        return getContext() != null ? getContext().getDataModel() : null;
    }

    private Project getProject() {
        return getContext() != null ? getContext().getCurrentProject() : null;
    }

    protected void setReadonly( boolean readonly ) {
        super.setReadonly( readonly );
        boolean value = !readonly;

        name.setEnabled( value );
        label.setEnabled( value );
        description.setEnabled( value );
        typeSelector.setEnabled( value );
        isTypeMultiple.setEnabled( value );
    }

    // Event notifications

    private void onDataObjectChange( @Observes DataObjectChangeEvent event ) {
        if ( event.isFromContext( context != null ? context.getContextId() : null ) ) {
            if ( "name".equals( event.getPropertyName() ) ||
                    "packageName".equals( event.getPropertyName() ) ||
                    "label".equals( event.getPropertyName() ) ) {

                initTypeList();
            }
        }
    }

    protected void loadDataObjectField( DataObject dataObject,
            ObjectProperty objectField ) {
        clean();
        setReadonly( true );
        if ( dataObject != null && objectField != null ) {
            this.dataObject = dataObject;
            this.objectField = objectField;
            initTypeList();

            name.setText( getObjectField().getName() );

            Annotation annotation = objectField.getAnnotation( AnnotationDefinitionTO.LABEL_ANNOTATION );
            if ( annotation != null ) {
                label.setText( ( String ) annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ) );
            }

            annotation = objectField.getAnnotation( AnnotationDefinitionTO.DESCRIPTION_ANNOTATION );
            if ( annotation != null ) {
                description.setText( ( String ) annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM ) );
            }

            setReadonly( getContext() == null || getContext().isReadonly() );
        } else {
            initTypeList();
        }
    }

    // TODO listen to DataObjectFieldDeletedEvent?

    // Event handlers
    @UiHandler("name")
    void nameChanged( ValueChangeEvent<String> event ) {
        if ( getObjectField() == null ) {
            return;
        }
        // Set widgets to error popup for styling purposes etc.
        nameLabel.setStyleName( DEFAULT_LABEL_CLASS );

        final String oldValue = getObjectField().getName();
        final String newValue = DataModelerUtils.unCapitalize( name.getValue() );

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
                                Constants.INSTANCE.modelEditor_confirm_renaming_of_used_field( oldValue ),
                                paths,
                                new org.uberfire.mvp.Command() {
                                    @Override
                                    public void execute() {
                                        doFieldNameChange( oldValue, newValue );
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
                        //no usages, just proceed with the deletion.
                        doFieldNameChange( oldValue, newValue );
                    }
                }
            } ).findFieldUsages( currentPath, originalClassName, fieldName );
        } else {
            doFieldNameChange( oldValue, fieldName );
        }
    }

    private void doFieldNameChange( final String oldValue,
            final String newValue ) {

        final Command afterCloseCommand = new Command() {
            @Override
            public void execute() {
                nameLabel.setStyleName( TEXT_ERROR_CLASS );
                name.selectAll();
            }
        };

        // In case an invalid name (entered before), was corrected to the original value, don't do anything but reset the label style
        if ( oldValue.equalsIgnoreCase( name.getValue() ) ) {
            name.setText( oldValue );
            nameLabel.setStyleName( DEFAULT_LABEL_CLASS );
            return;
        }

        validatorService.isValidIdentifier( newValue, new ValidatorCallback() {
            @Override
            public void onFailure() {
                ErrorPopup.showMessage( Constants.INSTANCE.validation_error_invalid_object_attribute_identifier( newValue ), null, afterCloseCommand );
            }

            @Override
            public void onSuccess() {
                validatorService.isUniqueAttributeName( newValue, getDataObject(), new ValidatorWithReasonCallback() {

                    @Override
                    public void onFailure() {
                        showFailure( ValidatorService.MANAGED_PROPERTY_EXISTS );
                    }

                    @Override
                    public void onFailure( String reason ) {
                        showFailure( reason );
                    }

                    private void showFailure( String reason ) {
                        if ( ValidatorService.UN_MANAGED_PROPERTY_EXISTS.equals( reason ) ) {
                            ObjectProperty unmanagedProperty = getDataObject().getUnManagedProperty( newValue );
                            ErrorPopup.showMessage( Constants.INSTANCE.validation_error_object_un_managed_attribute_already_exists( unmanagedProperty.getName(), unmanagedProperty.getClassName() ) );
                        } else {
                            ErrorPopup.showMessage( Constants.INSTANCE.validation_error_object_attribute_already_exists( newValue ) );
                        }
                    }

                    @Override
                    public void onSuccess() {
                        nameLabel.setStyleName( DEFAULT_LABEL_CLASS );
                        objectField.setName( newValue );
                        notifyFieldChange( "name", oldValue, newValue );
                    }
                } );
            }
        } );
    }

    @UiHandler("label")
    void labelChanged( final ValueChangeEvent<String> event ) {
        if ( getObjectField() == null ) {
            return;
        }

        String oldValue = null;
        final String _label = label.getValue();
        Annotation annotation = getObjectField().getAnnotation( AnnotationDefinitionTO.LABEL_ANNOTATION );

        if ( annotation != null ) {
            oldValue = AnnotationValueHandler.getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
            if ( _label != null && !"".equals( _label ) ) {
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _label );
            } else {
                getObjectField().removeAnnotation( annotation.getClassName() );
            }
        } else {
            if ( _label != null && !"".equals( _label ) ) {
                annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.LABEL_ANNOTATION ) );
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _label );
                getObjectField().addAnnotation( annotation );
            }
        }
        // TODO replace 'label' literal with annotation definition constant
        notifyFieldChange( "label", oldValue, _label );
    }

    @UiHandler("description")
    void descriptionChanged( final ValueChangeEvent<String> event ) {
        if ( getObjectField() == null ) {
            return;
        }

        String oldValue = null;
        final String _description = description.getValue();
        Annotation annotation = getObjectField().getAnnotation( AnnotationDefinitionTO.DESCRIPTION_ANNOTATION );

        if ( annotation != null ) {
            oldValue = AnnotationValueHandler.getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
            if ( _description != null && !"".equals( _description ) ) {
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _description );
            } else {
                getObjectField().removeAnnotation( annotation.getClassName() );
            }
        } else {
            if ( _description != null && !"".equals( _description ) ) {
                annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.DESCRIPTION_ANNOTATION ) );
                annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, _description );
                getObjectField().addAnnotation( annotation );
            }
        }
        notifyFieldChange( AnnotationDefinitionTO.DESCRIPTION_ANNOTATION, oldValue, _description );
    }

    private void typeChanged( ChangeEvent event ) {
        if ( getObjectField() == null ) {
            return;
        }

        String oldValue = getObjectField().getClassName();
        String type = typeSelector.getValue();
        boolean multiple = isTypeMultiple.getValue();
        typeChanged( oldValue, type, multiple );
    }

    private void typeMultipleChanged( ValueChangeEvent<Boolean> event ) {
        typeChanged( typeSelector.getValue(), typeSelector.getValue(), event.getValue() );
    }

    private void typeChanged( String oldType, String newType, boolean isMultiple ) {
        if ( getObjectField() == null ) {
            return;
        }

        boolean multiple = isMultiple;

        if ( getContext().getHelper().isPrimitiveType( newType ) ) {
            isTypeMultiple.setEnabled( false );
            isTypeMultiple.setValue( false );
            multiple = false;
        } else {
            isTypeMultiple.setEnabled( true );
        }

        getObjectField().setClassName( newType );
        getObjectField().setMultiple( multiple );
        if ( multiple && getObjectField().getBag() == null ) {
            getObjectField().setBag( ObjectPropertyImpl.DEFAULT_PROPERTY_BAG );
        }

        if ( getContext().getHelper().isBaseType( newType ) ) {
            getObjectField().setBaseType( true );
        } else {
            // Un-reference former type reference and set the new one
            getObjectField().setBaseType( false );
            getContext().getHelper().dataObjectUnReferenced( oldType, getDataObject().getClassName() );
            getContext().getHelper().dataObjectReferenced( newType, getDataObject().getClassName() );
        }
        notifyFieldChange( "className", oldType, newType );
    }

    private String listNames( List<ObjectProperty> fields ) {
        StringBuilder names = new StringBuilder();
        boolean first = true;
        for ( ObjectProperty property : fields ) {
            if ( !first ) {
                names.append( ", " );
            }
            names.append( property.getName() );
            first = false;
        }
        return names.toString();
    }

    private void initTypeList() {

        String currentFieldType = null;
        boolean currentFieldTypeMultiple = false;
        isTypeMultiple.setEnabled( true );
        isTypeMultiple.setValue( false );

        if ( getDataModel() != null ) {
            if ( getDataObject() != null && getObjectField() != null ) {
                currentFieldType = getObjectField().getClassName();
                currentFieldTypeMultiple = getObjectField().isMultiple();
                if ( getContext().getHelper().isPrimitiveType( currentFieldType ) ) {
                    isTypeMultiple.setEnabled( false );
                    isTypeMultiple.setValue( false );
                } else {
                    isTypeMultiple.setValue( currentFieldTypeMultiple );
                }
            }
            DataModelerUtils.initTypeList( typeSelector, getContext().getHelper().getOrderedBaseTypes().values(), getDataModel().getDataObjects(), getDataModel().getExternalClasses(), currentFieldType, currentFieldTypeMultiple );
        } else {
            DataModelerUtils.initList( typeSelector, false );
        }
    }

    public void refreshTypeList( boolean keepSelection ) {
        String selectedValue = typeSelector.getValue();
        initTypeList();
        if ( keepSelection && selectedValue != null ) {
            typeSelector.setSelectedValue( selectedValue );
        }
    }

    protected void clean() {
        nameLabel.setStyleName( DEFAULT_LABEL_CLASS );
        name.setText( null );
        label.setText( null );
        description.setText( null );
        typeSelector.setSelectedValue( null );
    }
}