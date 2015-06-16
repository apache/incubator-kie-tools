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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.datamodeller.model.droolsdomain.DroolsDomainAnnotations;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.ObjectEditor;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldCreatedEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldDeletedEvent;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

public class DroolsDataObjectEditor extends ObjectEditor {

    interface DroolsDataObjectEditorUIBinder
            extends UiBinder<Widget, DroolsDataObjectEditor> {

    }

    public static final String NOT_SELECTED = "NOT_SELECTED";

    private static final String DEFAULT_LABEL_CLASS = "gwt-Label";

    private static final String TEXT_ERROR_CLASS = "text-error";

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

    @UiField
    Label remotableLabel;

    @UiField
    Icon remotableHelpIcon;

    @UiField
    CheckBox remotableSelector;

    @Inject
    private Caller<DataModelerService> modelerService;

    @Inject
    private ValidatorService validatorService;

    private static DroolsDataObjectEditorUIBinder uiBinder = GWT.create( DroolsDataObjectEditorUIBinder.class );

    public DroolsDataObjectEditor() {
        initWidget( uiBinder.createAndBindUi( this ) );

        roleHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        classReactiveHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        propertyReactiveHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        typeSafeHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        timestampHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        durationHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        expiresHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
        remotableHelpIcon.getElement().getStyle().setCursor( Style.Cursor.POINTER );
    }

    @PostConstruct
    void init() {

        roleSelector.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                roleChanged( event );
            }
        } );
        typeSafeSelector.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                typeSafeChanged( event );
            }
        } );
        timestampFieldSelector.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
                timestampChanged( event );
            }
        } );
        durationFieldSelector.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( ChangeEvent event ) {
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

        setReadonly( true );
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
        boolean value = !readonly;

        roleSelector.setEnabled( value );
        propertyReactiveSelector.setEnabled( value );
        classReactiveSelector.setEnabled( value );
        typeSafeSelector.setEnabled( value );
        expires.setEnabled( value );
        durationFieldSelector.setEnabled( value );
        timestampFieldSelector.setEnabled( value );
        remotableSelector.setEnabled( value );
    }

    protected void loadDataObject( DataObject dataObject ) {
        clean();
        setReadonly( true );
        if ( dataObject != null ) {
            this.dataObject = dataObject;

            Annotation annotation = dataObject.getAnnotation( DroolsDomainAnnotations.ROLE_ANNOTATION );
            if ( annotation != null ) {
                String value = annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ) != null ? annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ).toString() : NOT_SELECTED;
                roleSelector.setSelectedValue( value );
            }

            annotation = dataObject.getAnnotation( DroolsDomainAnnotations.PROPERTY_REACTIVE_ANNOTATION );
            if ( annotation != null ) {
                propertyReactiveSelector.setValue( Boolean.TRUE );
            }

            annotation = dataObject.getAnnotation( DroolsDomainAnnotations.CLASS_REACTIVE_ANNOTATION );
            if ( annotation != null ) {
                classReactiveSelector.setValue( Boolean.TRUE );
            }

            annotation = dataObject.getAnnotation( DroolsDomainAnnotations.TYPE_SAFE_ANNOTATION );
            if ( annotation != null ) {
                String value = annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ) != null ? annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ).toString() : NOT_SELECTED;
                typeSafeSelector.setSelectedValue( value );
            }

            annotation = dataObject.getAnnotation( DroolsDomainAnnotations.EXPIRES_ANNOTATION );
            if ( annotation != null ) {
                expires.setText( annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ).toString() );
            }

            annotation = dataObject.getAnnotation( DroolsDomainAnnotations.REMOTABLE_ANNOTATION );
            if ( annotation != null ) {
                remotableSelector.setValue( Boolean.TRUE );
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
            String value = annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ) != null ? annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ).toString() : NOT_SELECTED;
            durationFieldSelector.setSelectedValue( value );
        }
    }

    private void loadTimestamp( DataObject dataObject ) {
        Annotation annotation;
        loadTimestampSelector( dataObject );
        annotation = dataObject.getAnnotation( DroolsDomainAnnotations.TIMESTAMP_ANNOTATION );
        if ( annotation != null ) {
            String value = annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ) != null ? annotation.getValue( DroolsDomainAnnotations.VALUE_PARAM ).toString() : NOT_SELECTED;
            timestampFieldSelector.setSelectedValue( value );
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

    // Event handlers

    private void roleChanged( final ChangeEvent event ) {
        if ( getDataObject() != null ) {

            final String newRole = NOT_SELECTED.equals( roleSelector.getValue() ) ? null: roleSelector.getValue();

            commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(), getName(), getDataObject(),
                    DroolsDomainAnnotations.ROLE_ANNOTATION, DroolsDomainAnnotations.VALUE_PARAM, newRole, true ).execute();
        }
    }

    private void typeSafeChanged( final ChangeEvent event ) {
        if ( getDataObject() != null ) {

            final String newTypeSafeValue = NOT_SELECTED.equals( typeSafeSelector.getValue() ) ? null : typeSafeSelector.getValue();

            commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(), getName(), getDataObject(),
                    DroolsDomainAnnotations.TYPE_SAFE_ANNOTATION, DroolsDomainAnnotations.VALUE_PARAM, newTypeSafeValue, true ).execute();
        }
    }

    private void timestampChanged( final ChangeEvent event ) {
        if ( getDataObject() != null ) {

            final String newTimestampValue = NOT_SELECTED.equals( timestampFieldSelector.getValue() ) ? null : timestampFieldSelector.getValue();

            commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(), getName(), getDataObject(),
                    DroolsDomainAnnotations.TIMESTAMP_ANNOTATION, DroolsDomainAnnotations.VALUE_PARAM, newTimestampValue, true ).execute();
        }
    }

    private void durationChanged( final ChangeEvent event ) {
        if ( getDataObject() != null ) {

            final String newDurationValue = NOT_SELECTED.equals( durationFieldSelector.getValue() ) ? null : durationFieldSelector.getValue();

            commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(), getName(), getDataObject(),
                    DroolsDomainAnnotations.DURATION_ANNOTATION, DroolsDomainAnnotations.VALUE_PARAM, newDurationValue, true ).execute();
        }
    }

    @UiHandler( "propertyReactiveSelector" )
    void propertyReactiveChanged( final ClickEvent event ) {
        if ( getDataObject() != null ) {

            final Boolean isChecked = propertyReactiveSelector.getValue();
            commandBuilder.buildDataObjectAddOrRemoveAnnotationCommand( getContext(), getName(), getDataObject(),
                    DroolsDomainAnnotations.PROPERTY_REACTIVE_ANNOTATION, isChecked ).execute();

            if ( isChecked ) {
                commandBuilder.buildDataObjectRemoveAnnotationCommand( getContext(), getName(), getDataObject(),
                        DroolsDomainAnnotations.CLASS_REACTIVE_ANNOTATION ).execute();
                classReactiveSelector.setValue( false );
            }
        }
    }

    @UiHandler( "classReactiveSelector" )
    void classReactiveChanged( final ClickEvent event ) {
        if ( getDataObject() != null ) {

            final Boolean isChecked = classReactiveSelector.getValue();
            commandBuilder.buildDataObjectAddOrRemoveAnnotationCommand( getContext(), getName(), getDataObject(),
                    DroolsDomainAnnotations.CLASS_REACTIVE_ANNOTATION, isChecked ).execute();

            if ( isChecked ) {
                commandBuilder.buildDataObjectRemoveAnnotationCommand( getContext(), getName(), getDataObject(),
                        DroolsDomainAnnotations.PROPERTY_REACTIVE_ANNOTATION ).execute();
                propertyReactiveSelector.setValue( false );
            }
        }
    }

    @UiHandler( "expires" )
    void expiresChanged( final ValueChangeEvent<String> event ) {
        if ( getDataObject() != null ) {

            // Set widgets to error popup for styling purposes etc.
            expiresLabel.setStyleName( DEFAULT_LABEL_CLASS );
            final Command afterCloseCommand = new Command() {
                @Override
                public void execute() {
                    expiresLabel.setStyleName( TEXT_ERROR_CLASS );
                    expires.selectAll();
                }
            };

            final String newValue = expires.getText();

            // Otherwise validate
            validatorService.isValidTimerInterval( newValue, new ValidatorCallback() {
                @Override
                public void onFailure() {
                    ErrorPopup.showMessage( Constants.INSTANCE.validation_error_invalid_timer_expression( newValue ), null, afterCloseCommand );
                }

                @Override
                public void onSuccess() {

                    commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(), getName(), getDataObject(),
                            DroolsDomainAnnotations.EXPIRES_ANNOTATION, DroolsDomainAnnotations.VALUE_PARAM, DataModelerUtils.nullTrim( newValue ), true ).execute();

                }
            } );
        }
    }

    @UiHandler( "remotableSelector" )
    void remotableChanged( final ClickEvent event ) {
        if ( getDataObject() != null ) {

            final Boolean isChecked = remotableSelector.getValue();
            commandBuilder.buildDataObjectAddOrRemoveAnnotationCommand( getContext(), getName(), getDataObject(),
                    DroolsDomainAnnotations.REMOTABLE_ANNOTATION, isChecked ).execute();

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

        loadPropertySelector( durationFieldSelector, dataObject, types, defaultValue );
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

        loadPropertySelector( timestampFieldSelector, dataObject, types, defaultValue );
    }

    private void loadPropertySelector( ListBox selector,
            DataObject dataObject,
            List<String> types,
            String defaultValue ) {
        if ( dataObject == null ) {
            return;
        }

        List<ObjectProperty> properties = DataModelerUtils.filterPropertiesByType( dataObject.getProperties(), types, true );
        SortedMap<String, String> propertyNames = new TreeMap<String, String>();
        for ( ObjectProperty property : properties ) {
            propertyNames.put( property.getName(), property.getName() );
        }

        if ( defaultValue != null && !"".equals( defaultValue ) ) {
            propertyNames.put( defaultValue, defaultValue );
        }

        selector.clear();
        selector.addItem( "", NOT_SELECTED );
        for ( Map.Entry<String, String> propertyName : propertyNames.entrySet() ) {
            selector.addItem( propertyName.getKey(), propertyName.getValue() );
        }
        selector.setSelectedValue( NOT_SELECTED );
    }

    public void clean() {
        roleSelector.setSelectedValue( NOT_SELECTED );
        classReactiveSelector.setValue( false );
        propertyReactiveSelector.setValue( false );
        typeSafeSelector.setSelectedValue( NOT_SELECTED );
        expires.setText( null );
        durationFieldSelector.setSelectedValue( NOT_SELECTED );
        timestampFieldSelector.setSelectedValue( NOT_SELECTED );
        remotableSelector.setValue( false );
    }
}