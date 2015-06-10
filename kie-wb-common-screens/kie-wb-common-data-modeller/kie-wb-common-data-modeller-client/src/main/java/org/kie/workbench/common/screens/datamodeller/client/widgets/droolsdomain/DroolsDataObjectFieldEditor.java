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

package org.kie.workbench.common.screens.datamodeller.client.widgets.droolsdomain;

import java.util.List;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.FieldEditor;
import org.kie.workbench.common.screens.datamodeller.events.ChangeType;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

public class DroolsDataObjectFieldEditor extends FieldEditor {

    interface DroolsDataObjectFieldEditorUIBinder
            extends UiBinder<Widget, DroolsDataObjectFieldEditor> {

    }

    //https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.11
    private static int MAX_CLASS_FIELDS = 65535;

    private static DroolsDataObjectFieldEditorUIBinder uiBinder = GWT.create( DroolsDataObjectFieldEditorUIBinder.class );

    private static final String DEFAULT_LABEL_CLASS = "gwt-Label";

    private static final String TEXT_ERROR_CLASS = "text-error";

    @UiField
    CheckBox equalsSelector;

    @UiField
    Icon equalsHelpIcon;

    @UiField
    Label positionLabel;

    @UiField
    Icon positionHelpIcon;

    @UiField
    TextBox position;

    @Inject
    Event<DataModelerEvent> dataModelerEventEvent;

    @Inject
    private ValidatorService validatorService;

    @Inject
    private Caller<DataModelerService> modelerService;

    public DroolsDataObjectFieldEditor() {
        initWidget( uiBinder.createAndBindUi( this ) );

        position.addChangeHandler( new ChangeHandler() {
            @Override public void onChange( ChangeEvent event ) {
                positionChanged( event );
            }
        } );

        positionHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        equalsHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );

        setReadonly( true );
    }

    @Override
    public String getName() {
        return "DROOLS_FIELD_EDITOR";
    }

    public void setReadonly( boolean readonly ) {
        super.setReadonly( readonly );
        boolean value = !readonly;
        equalsSelector.setEnabled( value );
        position.setEnabled( value );
    }

    public void clean() {
        equalsSelector.setValue( Boolean.FALSE );
        positionLabel.setStyleName( DEFAULT_LABEL_CLASS );
        position.setText( null );
    }

    // Event observers

    protected void loadDataObjectField( DataObject dataObject,
            ObjectProperty objectField ) {
        clean();
        setReadonly( true );
        if ( dataObject != null && objectField != null ) {
            this.dataObject = dataObject;
            this.objectField = objectField;

            Annotation annotation = objectField.getAnnotation( AnnotationDefinitionTO.KEY_ANNOTATION );
            if ( annotation != null ) {
                equalsSelector.setValue( Boolean.TRUE );
            }

            annotation = objectField.getAnnotation( AnnotationDefinitionTO.POSITION_ANNOTATION );
            if ( annotation != null ) {
                Object positionValue = annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM );
                String position = positionValue != null ? positionValue.toString() : "";
                this.position.setText( position );
            }

            setReadonly( getContext() == null || getContext().isReadonly() );
        }
    }

    // Event handlers

    @UiHandler("equalsSelector")
    void equalsChanged( final ClickEvent event ) {
        if ( getObjectField() == null ) {
            return;
        }

        Boolean oldEquals = null;
        Annotation annotation = getObjectField().getAnnotation( AnnotationDefinitionTO.KEY_ANNOTATION );
        if ( annotation != null ) {
            Object annotationValue = annotation.getValue( AnnotationDefinitionTO.VALUE_PARAM );
            oldEquals = annotationValue != null ? ( Boolean ) annotationValue : Boolean.FALSE;
        }
        final Boolean setEquals = equalsSelector.getValue();

        if ( annotation != null && !setEquals ) {
            getObjectField().removeAnnotation( annotation.getClassName() );
        } else if ( annotation == null && setEquals ) {
            annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.KEY_ANNOTATION ) );
            getObjectField().addAnnotation( annotation );
        }
        notifyFieldChange( ChangeType.FIELD_ANNOTATION_VALUE_CHANGE, AnnotationDefinitionTO.KEY_ANNOTATION, oldEquals, setEquals );
    }

    private void positionChanged( ChangeEvent event ) {

        positionLabel.setStyleName( DEFAULT_LABEL_CLASS );
        final Command afterCloseCommand = new Command() {
            @Override
            public void execute() {
                positionLabel.setStyleName( TEXT_ERROR_CLASS );
                position.selectAll();
            }
        };

        Annotation annotation = getObjectField().getAnnotation( AnnotationDefinitionTO.POSITION_ANNOTATION );
        final String oldValue = AnnotationValueHandler.getStringValue( annotation, AnnotationDefinitionTO.VALUE_PARAM );
        final String newValue = position.getText() != null ? position.getText().trim() : null;

        boolean notify = false;

        if ( newValue != null && !"".equals( newValue ) ) {
            // validate that entered value is a valid position.
            int newPosition;
            String error = null;
            try {
                newPosition = Integer.parseInt( newValue );
            } catch ( NumberFormatException e ) {
                newPosition = -1;
            }

            if ( newPosition < 0 || newPosition >= MAX_CLASS_FIELDS ) {
                error = Constants.INSTANCE.validation_error_position_greater_or_equal_than_and_lower_than( newValue, "0", MAX_CLASS_FIELDS + "" );
            } else {
                List<ObjectProperty> fieldsUsingPosition = getFieldsUsingPosition( newPosition );
                if ( fieldsUsingPosition.size() > 0 ) {
                    String fieldsUsingPositionNames = listNames( fieldsUsingPosition );
                    error = Constants.INSTANCE.validation_error_position_already_used_by_fields( newPosition + "", fieldsUsingPositionNames );
                }
            }

            if ( error != null ) {
                ErrorPopup.showMessage( error, null, afterCloseCommand );
            } else {
                //just proceed to change the position
                if ( annotation != null ) {
                    annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, newPosition + "" );
                } else {
                    annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.POSITION_ANNOTATION ) );
                    annotation.setValue( AnnotationDefinitionTO.VALUE_PARAM, newPosition + "" );
                    getObjectField().addAnnotation( annotation );
                }
                position.setText( newPosition + "" );
                notify = true;
            }

        } else {
            if ( annotation != null ) {
                getObjectField().removeAnnotation( annotation.getClassName() );
                notify = true;
            }
            position.setText( null );
        }

        if ( notify ) {
            notifyFieldChange( ChangeType.FIELD_ANNOTATION_VALUE_CHANGE ,
                    AnnotationDefinitionTO.POSITION_ANNOTATION, oldValue, newValue );
        }
    }

    private List<ObjectProperty> getFieldsUsingPosition( int position ) {
        return DataModelerUtils.getFieldsUsingPosition( getDataObject(), position, getObjectField().getName() );
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
}