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

package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommand;
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommandBuilder;
import org.kie.workbench.common.screens.datamodeller.client.handlers.DomainHandlerRegistry;
import org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.SequenceGeneratorValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.ObjectEditor;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.JPADomainAnnotations;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.ext.properties.editor.client.fields.BooleanField;
import org.uberfire.ext.properties.editor.client.fields.TextField;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.mvp.Command;

@Dependent
public class JPADataObjectEditor
        extends ObjectEditor
        implements JPADataObjectEditorView.Presenter {

    private static Map<String, DataModelerPropertyEditorFieldInfo> propertyEditorFields = new HashMap<String, DataModelerPropertyEditorFieldInfo>();

    private JPADataObjectEditorView view;

    private static final String EOL = "</BR>";

    private static final String BLANK = "&nbsp;";

    private static final String TWO_BLANKS = BLANK + BLANK;

    @Inject
    public JPADataObjectEditor( JPADataObjectEditorView view,
            DomainHandlerRegistry handlerRegistry,
            Event<DataModelerEvent> dataModelerEvent,
            DataModelCommandBuilder commandBuilder ) {
        super( handlerRegistry, dataModelerEvent, commandBuilder );
        this.view = view;
        view.init( this );
    }

    @PostConstruct
    protected void init() {
        view.setLastOpenAccordionGroupTitle( getEntityCategoryName() );
        loadPropertyEditor();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public String getName() {
        return "JPA_OBJECT_EDITOR";
    }

    @Override
    public String getDomainName() {
        return JPADomainEditor.JPA_DOMAIN;
    }

    @Override
    protected void loadDataObject( DataObject dataObject ) {
        clear();
        setReadonly( true );
        this.dataObject = dataObject;

        if ( dataObject != null ) {
            updateEntityField( dataObject.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_ENTITY_ANNOTATION ) );
            updateTableNameField( dataObject.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_TABLE_ANNOTATION ) );
            setReadonly( getContext() == null || getContext().isReadonly() );
        }
        loadPropertyEditor();
    }

    @Override
    public void onEntityFieldChange( String newValue ) {
        if ( getDataObject() != null ) {

            final Boolean doAdd = Boolean.parseBoolean( newValue );

            if ( doAdd ) {

                if ( !hasIdField( getDataObject() ) ) {
                    List<ObjectProperty> configurableFields = persistenceConfigurableFields( getDataObject() );
                    String message = buildOnSetPersistableRefactoringMessage( false, configurableFields );

                    view.showYesNoCancelPopup( CommonConstants.INSTANCE.Warning(),
                            message,
                            new Command() {
                                @Override
                                public void execute() {
                                    doEntityFieldChange( doAdd, true );
                                }
                            },
                            Constants.INSTANCE.persistence_domain_objectEditor_add_identifier_action(),
                            ButtonType.PRIMARY,
                            new Command() {
                                @Override
                                public void execute() {
                                    doEntityFieldChange( doAdd, false );
                                }
                            },
                            Constants.INSTANCE.persistence_domain_objectEditor_dont_add_identifier_action(),
                            ButtonType.DANGER,
                            new Command() {
                                @Override
                                public void execute() {
                                    updateEntityField( null );
                                    loadPropertyEditor();
                                }
                            },
                            CommonConstants.INSTANCE.Cancel(),
                            ButtonType.DEFAULT );
                } else {
                    doEntityFieldChange( doAdd, false );
                }
            } else {
                doEntityFieldChange( doAdd, false );
            }
        }
    }

    @Override
    public void onTableNameChange( String newValue ) {
        if ( getDataObject() != null ) {

            String value = DataModelerUtils.nullTrim( newValue );
            DataModelCommand command = commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(),
                    getName(), getDataObject(), JPADomainAnnotations.JAVAX_PERSISTENCE_TABLE_ANNOTATION, "name", value, true );
            command.execute();
        }
    }

    //convenient method for facilitating testing
    protected void doEntityFieldChange( boolean isPersistable, boolean generateIdenfitier ) {

        final DataModelCommand command = commandBuilder.buildDataObjectAddOrRemoveAnnotationCommand( getContext(),
                getName(), getDataObject(), JPADomainAnnotations.JAVAX_PERSISTENCE_ENTITY_ANNOTATION, isPersistable );

        if ( generateIdenfitier ) {
            addByDefaultId( dataObject );
        }
        command.execute();

    }

    private void updateEntityField( Annotation annotation ) {
        clearEntityField();
        if ( annotation != null ) {
            updatePropertyEditorField( JPADataObjectEditorView.ENTITY_FIELD, annotation, "true" );
        }
    }

    private void updateTableNameField( Annotation annotation ) {
        clearTableNameField();
        if ( annotation != null ) {
            String tableName = AnnotationValueHandler.getStringValue( annotation, "name", null );
            updatePropertyEditorField( JPADataObjectEditorView.TABLE_NAME_FIELD, annotation, tableName );
        }
    }

    protected void loadPropertyEditor() {
        view.loadPropertyEditorCategories( getPropertyEditorCategories() );
    }

    protected List<PropertyEditorCategory> getPropertyEditorCategories() {

        final List<PropertyEditorCategory> categories = new ArrayList<PropertyEditorCategory>();

        PropertyEditorCategory category = new PropertyEditorCategory( getEntityCategoryName(), 1 );
        categories.add( category );

        category.withField( createEntityField() );
        category.withField( createTableNameField() );

        return categories;
    }

    private DataModelerPropertyEditorFieldInfo createEntityField() {
        return createField( Constants.INSTANCE.persistence_domain_objectEditor_entity_field_label(),
                JPADataObjectEditorView.ENTITY_FIELD,
                "false",
                BooleanField.class,
                Constants.INSTANCE.persistence_domain_objectEditor_entity_field_help_heading(),
                Constants.INSTANCE.persistence_domain_objectEditor_entity_field_help() );
    }

    private DataModelerPropertyEditorFieldInfo createTableNameField() {
        return createField( Constants.INSTANCE.persistence_domain_objectEditor_table_field_label(),
                JPADataObjectEditorView.TABLE_NAME_FIELD,
                "",
                TextField.class,
                Constants.INSTANCE.persistence_domain_objectEditor_table_field_help_heading(),
                Constants.INSTANCE.persistence_domain_objectEditor_table_field_help() );
    }

    private DataModelerPropertyEditorFieldInfo createField( String label, String key, String currentStringValue, Class<?> customFieldClass ) {
        return createField( label, key, currentStringValue, customFieldClass, null, null );
    }

    private DataModelerPropertyEditorFieldInfo createField( String label, String key, String currentStringValue, Class<?> customFieldClass, String helpHeading, String helpText ) {
        DataModelerPropertyEditorFieldInfo fieldInfo = propertyEditorFields.get( key );
        if ( fieldInfo == null ) {
            fieldInfo = new DataModelerPropertyEditorFieldInfo( label, currentStringValue, customFieldClass );
            fieldInfo.withKey( key );
            if ( helpHeading != null ) {
                fieldInfo.withHelpInfo( helpHeading, helpText );
            }
            propertyEditorFields.put( key, fieldInfo );
        }
        return fieldInfo;
    }

    @Override
    public void clear() {
        clearEntityField();
        clearTableNameField();
    }

    private void clearEntityField() {
        updatePropertyEditorField( JPADataObjectEditorView.ENTITY_FIELD, null, "false" );
    }

    private void clearTableNameField() {
        updatePropertyEditorField( JPADataObjectEditorView.TABLE_NAME_FIELD, null, "" );
    }

    private void updatePropertyEditorField( String fieldId, Annotation currentValue, String currentStringValue ) {
        DataModelerPropertyEditorFieldInfo fieldInfo = propertyEditorFields.get( fieldId );
        fieldInfo.setCurrentValue( currentValue );
        fieldInfo.setCurrentStringValue( currentStringValue );
    }

    private String getEntityCategoryName() {
        return Constants.INSTANCE.persistence_domain_objectEditor_entity_category();
    }

    private boolean hasIdField( DataObject dataObject ) {
        for ( ObjectProperty objectProperty : dataObject.getProperties() ) {
            if ( objectProperty.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_ID_ANNOTATION ) != null ) {
                return true;
            }
        }
        return false;
    }

    private List<ObjectProperty> persistenceConfigurableFields( DataObject dataObject ) {
        List<ObjectProperty> result = new ArrayList<ObjectProperty>(  );
        for ( ObjectProperty field : dataObject.getProperties() ) {
            if ( DataModelerUtils.isManagedProperty( field ) && ( field.isMultiple() || !field.isBaseType() ) ) {
                result.add( field );
            }
        }
        return result;
    }

    private void addByDefaultId( DataObject dataObject ) {

        //add the by default id field
        String idFieldName = generateIdFieldName( dataObject );

        commandBuilder.buildAddPropertyCommand(
                getContext(),
                getName(),
                dataObject,
                idFieldName,
                null,
                Long.class.getName(),
                false ).execute();

        ObjectProperty idField = dataObject.getProperty( idFieldName );

        commandBuilder.buildFieldAnnotationAddCommand( getContext(),
                getName(),
                dataObject,
                idField,
                JPADomainAnnotations.JAVAX_PERSISTENCE_ID_ANNOTATION ).execute();

        //set the by default generated value annotation.
        String generatorName = dataObject.getName().toUpperCase() + "_ID_GENERATOR";
        Annotation generatedValue = new AnnotationImpl( context.getAnnotationDefinition(
                JPADomainAnnotations.JAVAX_PERSISTENCE_GENERATED_VALUE_ANNOTATION ) );
        generatedValue.setValue( "generator", generatorName );
        generatedValue.setValue( "strategy", "AUTO" );

        commandBuilder.buildFieldAnnotationAddCommand( getContext(),
                getName(),
                dataObject,
                idField,
                generatedValue ).execute();

        //set by default sequence generator
        String sequenceName = dataObject.getName().toUpperCase() + "_ID_SEQ";
        Annotation sequenceGenerator = SequenceGeneratorValueHandler.createAnnotation( generatorName, sequenceName,
                context.getAnnotationDefinitions() );

        commandBuilder.buildFieldAnnotationAddCommand( getContext(),
                getName(),
                dataObject,
                idField,
                sequenceGenerator ).execute();
    }

    private String generateIdFieldName( DataObject dataObject ) {
        String id = "id";
        int i = 1;
        boolean generated = false;
        while ( !generated ) {
            if ( dataObject.getProperty( id ) == null ) {
                generated = true;
            } else {
                id = "id"+i;
                i++;
            }
        }
        return id;
    }

    private String buildOnSetPersistableRefactoringMessage( boolean hasIdentifier,
            List<ObjectProperty> configurableFields ) {

        StringBuilder message = new StringBuilder();
        message.append( Constants.INSTANCE.persistence_domain_objectEditor_on_make_persistable_message() );
        message.append( EOL );

        if ( !hasIdentifier ) {
            message.append(
                    Constants.INSTANCE.persistence_domain_objectEditor_required_identifier_is_missing_message() );
        }

        if ( configurableFields.size() > 0 ) {
            message.append( Constants.INSTANCE.persistence_domain_objectEditor_review_relationship_fields_message() );
            message.append( TWO_BLANKS );

            boolean isFirst = true;
            for ( ObjectProperty field : configurableFields ) {
                if ( !isFirst ) {
                    message.append( "," + TWO_BLANKS );
                }
                isFirst = false;
                message.append(
                        Constants.INSTANCE.persistence_domain_objectEditor_review_relationship_field_for_review_message(
                                field.getName() ) );
            }
        }

        return message.toString();
    }
}