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
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommandBuilder;
import org.kie.workbench.common.screens.datamodeller.client.handlers.DomainHandlerRegistry;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.FieldEditor;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.model.droolsdomain.DroolsDomainAnnotations;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.uberfire.mvp.Command;

@Dependent
public class DroolsDataObjectFieldEditor
        extends FieldEditor
        implements DroolsDataObjectFieldEditorView.Presenter {

    //https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.11
    private static int MAX_CLASS_FIELDS = 65535;

    private DroolsDataObjectFieldEditorView view;

    @Inject
    public DroolsDataObjectFieldEditor( DroolsDataObjectFieldEditorView view,
            DomainHandlerRegistry handlerRegistry,
            Event<DataModelerEvent> dataModelerEvent,
            DataModelCommandBuilder commandBuilder ) {
        super( handlerRegistry, dataModelerEvent, commandBuilder );
        this.view = view;
        view.init( this );
    }

    @PostConstruct
    protected void init() {
        setReadonly( true );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public String getName() {
        return "DROOLS_FIELD_EDITOR";
    }

    @Override
    public String getDomainName() {
        return DroolsDomainEditor.DROOLS_DOMAIN;
    }

    public void setReadonly( boolean readonly ) {
        super.setReadonly( readonly );
        view.setReadonly( readonly );
    }

    @Override
    public void clear() {
        view.setEquals( false );
        view.setPositionOnError( false );
        view.setPosition( null );
    }

    @Override
    protected void loadDataObjectField( DataObject dataObject,
            ObjectProperty objectField ) {
        clear();
        setReadonly( true );
        if ( dataObject != null && objectField != null ) {
            this.dataObject = dataObject;
            this.objectField = objectField;

            Annotation annotation = objectField.getAnnotation( DroolsDomainAnnotations.KEY_ANNOTATION );
            if ( annotation != null ) {
                view.setEquals( Boolean.TRUE );
            }

            annotation = objectField.getAnnotation( DroolsDomainAnnotations.POSITION_ANNOTATION );
            if ( annotation != null ) {
                Object positionValue = annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM );
                String position = positionValue != null ? positionValue.toString() : "";
                view.setPosition( position );
            }

            setReadonly( getContext() == null || getContext().isReadonly() );
        }
    }

    @Override
    public void onEqualsChange() {
        if ( getObjectField() != null ) {

            final Boolean isChecked = view.getEquals();

            commandBuilder.buildFieldAddOrRemoveAnnotationCommand( getContext(), getName(), getDataObject(),
                    getObjectField(), DroolsDomainAnnotations.KEY_ANNOTATION, isChecked ).execute();
        }
    }

    @Override
    public void onPositionChange() {

        if ( getDataObject() != null ) {
            view.setPositionOnError( false );
            final Command afterCloseCommand = new Command() {
                @Override
                public void execute() {
                    view.setPositionOnError( true );
                    view.selectAllPositionText();
                }
            };

            final String newValue = DataModelerUtils.nullTrim( view.getPosition() );

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
                    view.showErrorPopup( error, null, afterCloseCommand );
                } else {
                    //just proceed to change the position

                    commandBuilder.buildFieldAnnotationValueChangeCommand( getContext(), getName(), getDataObject(),
                            getObjectField(), DroolsDomainAnnotations.POSITION_ANNOTATION, DroolsDomainAnnotations.VALUE_PARAM,
                            newPosition, false ).execute();

                    view.setPosition( newPosition + "" );
                }

            } else {
                commandBuilder.buildFieldAnnotationRemoveCommand( getContext(), getName(), getDataObject(), getObjectField(),
                        DroolsDomainAnnotations.POSITION_ANNOTATION ).execute();
                view.setPosition( null );
            }
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