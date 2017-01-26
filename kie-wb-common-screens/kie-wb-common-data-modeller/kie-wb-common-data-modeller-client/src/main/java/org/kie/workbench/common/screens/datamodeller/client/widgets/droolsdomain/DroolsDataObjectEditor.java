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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommandBuilder;
import org.kie.workbench.common.screens.datamodeller.client.handlers.DomainHandlerRegistry;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.ObjectEditor;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldCreatedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldDeletedEvent;
import org.kie.workbench.common.screens.datamodeller.model.droolsdomain.DroolsDomainAnnotations;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.mvp.Command;

@Dependent
public class DroolsDataObjectEditor
        extends ObjectEditor
        implements DroolsDataObjectEditorView.Presenter {

    private ValidatorService validatorService;

    private DroolsDataObjectEditorView view;

    @Inject
    public DroolsDataObjectEditor( DroolsDataObjectEditorView view,
            DomainHandlerRegistry handlerRegistry,
            Event<DataModelerEvent> dataModelerEvent,
            DataModelCommandBuilder commandBuilder,
            ValidatorService validatorService ) {
        super( handlerRegistry, dataModelerEvent, commandBuilder );
        this.validatorService = validatorService;
        this.view = view;
        view.init( this );
    }

    @PostConstruct
    protected void init() {

        List<Pair<String, String>> roleOptions = new ArrayList<Pair<String, String>>( );
        roleOptions.add( new Pair<String, String>( "EVENT", "EVENT" ) );
        view.initRoleList( roleOptions, true );

        List<Pair<String, String>> typeSafeOptions = new ArrayList<Pair<String, String>>( );
        typeSafeOptions.add( new Pair<String, String>( "false", "false" ) );
        typeSafeOptions.add( new Pair<String, String>( "true", "true" ) );
        view.initTypeSafeList( typeSafeOptions, true );

        view.initTimeStampFieldList( new ArrayList<Pair<String, String>>(), true );

        view.initDurationFieldList( new ArrayList<Pair<String, String>>(), true );

        setReadonly( true );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public String getName() {
        return "DROOLS_OBJECT_EDITOR";
    }

    @Override
    public String getDomainName() {
        return DroolsDomainEditor.DROOLS_DOMAIN;
    }

    public void setReadonly( boolean readonly ) {
        super.setReadonly( readonly );
        view.setReadonly( readonly );
    }

    protected void loadDataObject( DataObject dataObject ) {
        clear();
        setReadonly( true );
        if ( dataObject != null ) {
            this.dataObject = dataObject;

            Annotation annotation = dataObject.getAnnotation( DroolsDomainAnnotations.ROLE_ANNOTATION );
            if ( annotation != null ) {
                String value = annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ) != null ? annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ).toString() : UIUtil.NOT_SELECTED;
                view.setRole( value );
            }

            annotation = dataObject.getAnnotation( DroolsDomainAnnotations.PROPERTY_REACTIVE_ANNOTATION );
            if ( annotation != null ) {
                view.setPropertyReactive( Boolean.TRUE );
            }

            annotation = dataObject.getAnnotation( DroolsDomainAnnotations.CLASS_REACTIVE_ANNOTATION );
            if ( annotation != null ) {
                view.setClassReactive( Boolean.TRUE );
            }

            annotation = dataObject.getAnnotation( DroolsDomainAnnotations.TYPE_SAFE_ANNOTATION );
            if ( annotation != null ) {
                String value = annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ) != null ? annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ).toString() : UIUtil.NOT_SELECTED;
                view.setTypeSafe( value );
            }

            annotation = dataObject.getAnnotation( DroolsDomainAnnotations.EXPIRES_ANNOTATION );
            if ( annotation != null ) {
                view.setExpires( annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ).toString() );
            }

            annotation = dataObject.getAnnotation( DroolsDomainAnnotations.JAXB_XML_ROOT_ELEMENT_ANNOTATION );
            if ( annotation == null ) {
                // look for old style annotation.
                annotation = dataObject.getAnnotation( DroolsDomainAnnotations.REMOTABLE_ANNOTATION );
            }
            if ( annotation != null ) {
                view.setRemotable( Boolean.TRUE );
            }

            loadDuration( dataObject );

            loadTimestamp( dataObject );

            setReadonly( getContext() == null || getContext().isReadonly() );
        }
    }

    private void loadDuration( DataObject dataObject ) {
        Annotation annotation;
        loadDurationSelector( dataObject );
        annotation = dataObject.getAnnotation( DroolsDomainAnnotations.DURATION_ANNOTATION );
        if ( annotation != null ) {
            String value = annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ) != null ? annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ).toString() : UIUtil.NOT_SELECTED;
            view.setDurationField( value );
        }
    }

    private void loadTimestamp( DataObject dataObject ) {
        Annotation annotation;
        loadTimestampSelector( dataObject );
        annotation = dataObject.getAnnotation( DroolsDomainAnnotations.TIMESTAMP_ANNOTATION );
        if ( annotation != null ) {
            String value = annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ) != null ? annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ).toString() : UIUtil.NOT_SELECTED;
            view.setTimeStampField( value );
        }
    }

    // Event observers

    private void onDataObjectFieldCreated( @Observes DataObjectFieldCreatedEvent event ) {
        updateFieldDependentSelectors( event, event.getCurrentDataObject(), event.getCurrentField() );
    }

    private void onDataObjectFieldChange( @Observes DataObjectFieldChangeEvent event ) {
        updateFieldDependentSelectors( event, event.getCurrentDataObject(), event.getCurrentField() );
    }

    private void onDataObjectFieldDeleted( @Observes DataObjectFieldDeletedEvent event ) {
        updateFieldDependentSelectors( event, event.getCurrentDataObject(), event.getCurrentField() );
    }

    private void updateFieldDependentSelectors( DataModelerEvent event,
                                                DataObject currentDataObject,
                                                ObjectProperty currentField ) {
        if ( event.isFromContext( context != null ? context.getContextId() : null ) && getDataObject() == currentDataObject ) {
            loadDuration( getDataObject() );
            loadTimestamp( getDataObject() );
        }
    }

    @Override
    public void onRoleChange() {
        if ( getDataObject() != null ) {

            final String newRole = UIUtil.NOT_SELECTED.equals( view.getRole() ) ? null : view.getRole();

            commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(), getName(), getDataObject(),
                    DroolsDomainAnnotations.ROLE_ANNOTATION, DroolsDomainAnnotations.VALUE_PARAM, newRole, true ).execute();
        }
    }

    @Override
    public void onTypeSafeChange() {
        if ( getDataObject() != null ) {

            final String newTypeSafeValue = UIUtil.NOT_SELECTED.equals( view.getTypeSafe() ) ? null : view.getTypeSafe();

            commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(), getName(), getDataObject(),
                    DroolsDomainAnnotations.TYPE_SAFE_ANNOTATION, DroolsDomainAnnotations.VALUE_PARAM, newTypeSafeValue, true ).execute();
        }
    }

    @Override
    public void onTimeStampFieldChange() {
        if ( getDataObject() != null ) {

            final String newTimestampValue = UIUtil.NOT_SELECTED.equals( view.getTimeStampField() ) ? null : view.getTimeStampField();

            commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(), getName(), getDataObject(),
                    DroolsDomainAnnotations.TIMESTAMP_ANNOTATION, DroolsDomainAnnotations.VALUE_PARAM, newTimestampValue, true ).execute();
        }
    }

    @Override
    public void onDurationFieldChange() {
        if ( getDataObject() != null ) {

            final String newDurationValue = UIUtil.NOT_SELECTED.equals( view.getDurationField() ) ? null : view.getDurationField();

            commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(), getName(), getDataObject(),
                    DroolsDomainAnnotations.DURATION_ANNOTATION, DroolsDomainAnnotations.VALUE_PARAM, newDurationValue, true ).execute();
        }
    }


    @Override
    public void onPropertyReactiveChange() {
        if ( getDataObject() != null ) {

            final Boolean isChecked = view.getPropertyReactive();
            commandBuilder.buildDataObjectAddOrRemoveAnnotationCommand( getContext(), getName(), getDataObject(),
                    DroolsDomainAnnotations.PROPERTY_REACTIVE_ANNOTATION, isChecked ).execute();

            if ( isChecked ) {
                commandBuilder.buildDataObjectRemoveAnnotationCommand( getContext(), getName(), getDataObject(),
                        DroolsDomainAnnotations.CLASS_REACTIVE_ANNOTATION ).execute();
                view.setClassReactive( false );
            }
        }
    }

    @Override
    public void onClassReactiveChange() {
        if ( getDataObject() != null ) {

            final Boolean isChecked = view.getClassReactive();
            commandBuilder.buildDataObjectAddOrRemoveAnnotationCommand( getContext(), getName(), getDataObject(),
                    DroolsDomainAnnotations.CLASS_REACTIVE_ANNOTATION, isChecked ).execute();

            if ( isChecked ) {
                commandBuilder.buildDataObjectRemoveAnnotationCommand( getContext(), getName(), getDataObject(),
                        DroolsDomainAnnotations.PROPERTY_REACTIVE_ANNOTATION ).execute();
                view.setPropertyReactive( false );
            }
        }
    }

    @Override
    public void onExpiresChange() {
        if ( getDataObject() != null ) {

            view.setExpiresOnError( false );
            final Command afterCloseCommand = new Command() {
                @Override
                public void execute() {
                    view.setExpiresOnError( true );
                    view.selectAllExpiresText();
                }
            };

            final String newValue = view.getExpires();

            // Otherwise validate
            validatorService.isValidTimerInterval( newValue, new ValidatorCallback() {
                @Override
                public void onFailure() {
                    view.showErrorPopup( Constants.INSTANCE.validation_error_invalid_timer_expression( newValue ), null, afterCloseCommand );
                }

                @Override
                public void onSuccess() {

                    commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(), getName(), getDataObject(),
                            DroolsDomainAnnotations.EXPIRES_ANNOTATION, DroolsDomainAnnotations.VALUE_PARAM, DataModelerUtils.nullTrim( newValue ), true ).execute();

                }
            } );
        }
    }

    @Override
    public void onRemotableChange() {
        if ( getDataObject() != null ) {

            final Boolean isChecked = view.getRemotable();
            commandBuilder.buildDataObjectAddOrRemoveAnnotationCommand( getContext(), getName(), getDataObject(),
                    DroolsDomainAnnotations.JAXB_XML_ROOT_ELEMENT_ANNOTATION, isChecked ).execute();
            //remove the old style annotation.
            commandBuilder.buildDataObjectRemoveAnnotationCommand( getContext(), getName(), getDataObject(),
                    DroolsDomainAnnotations.REMOTABLE_ANNOTATION ).execute();
        }
    }

    private void loadDurationSelector( DataObject dataObject ) {
        if ( dataObject == null ) {
            return;
        }

        List<String> types = new ArrayList<String>();
        types.add( "short" );
        types.add( "int" );
        types.add( "long" );
        types.add( "java.lang.Short" );
        types.add( "java.lang.Integer" );
        types.add( "java.lang.Long" );

        String defaultValue = null;
        Annotation annotation = dataObject.getAnnotation( DroolsDomainAnnotations.DURATION_ANNOTATION );
        if ( annotation != null ) {
            defaultValue = AnnotationValueHandler.getStringValue( annotation, DroolsDomainAnnotations.VALUE_PARAM );
        }

        List<Pair<String, String>> propertyOptions = loadPropertyOptions( dataObject, types, defaultValue );
        view.initDurationFieldList( propertyOptions, true );
    }

    private void loadTimestampSelector( DataObject dataObject ) {
        if ( dataObject == null ) {
            return;
        }

        List<String> types = new ArrayList<String>();
        types.add( "long" );
        types.add( "java.lang.Long" );
        types.add( "java.util.Date" );
        types.add( "java.sql.Timestamp" );

        String defaultValue = null;
        Annotation annotation = dataObject.getAnnotation( DroolsDomainAnnotations.TIMESTAMP_ANNOTATION );
        if ( annotation != null ) {
            defaultValue = AnnotationValueHandler.getStringValue( annotation, DroolsDomainAnnotations.VALUE_PARAM );
        }

        List<Pair<String, String>> propertyOptions = loadPropertyOptions( dataObject, types, defaultValue );
        view.initTimeStampFieldList( propertyOptions, true );
    }

    private List<Pair<String, String>> loadPropertyOptions( DataObject dataObject,
                                       List<String> types,
                                       String defaultValue ) {
        List<Pair<String, String>> propertyOptions = new ArrayList<Pair<String, String>>( );

        if ( dataObject != null ) {

            List<ObjectProperty> properties = DataModelerUtils.filterPropertiesByType( dataObject.getProperties(), types, true );
            SortedMap<String, String> propertyNames = new TreeMap<String, String>();

            for ( ObjectProperty property : properties ) {
                propertyNames.put( property.getName(), property.getName() );
            }

            if ( defaultValue != null && !"".equals( defaultValue ) ) {
                propertyNames.put( defaultValue, defaultValue );
            }

            for ( Map.Entry<String, String> propertyName : propertyNames.entrySet() ) {
                propertyOptions.add( new Pair( propertyName.getKey(), propertyName.getValue() ) );
            }
        }
        return propertyOptions;
    }

    public void clear() {
        view.setRole( UIUtil.NOT_SELECTED );
        view.setClassReactive( false );
        view.setPropertyReactive( false );
        view.setClassReactive( false );

        view.setTypeSafe( UIUtil.NOT_SELECTED );

        view.setExpires( null );
        view.setExpiresOnError( false );
        view.setDurationField( UIUtil.NOT_SELECTED );
        view.setTimeStampField( UIUtil.NOT_SELECTED );
        view.setRemotable( false );
    }
}