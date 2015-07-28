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

package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.RelationshipAnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.handlers.jpadomain.util.SequenceGeneratorValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.FieldEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties.IdGeneratorField;
import org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties.RelationshipField;
import org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties.SequenceGeneratorField;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.CascadeType;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.FetchMode;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.JPADomainAnnotations;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.RelationType;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.uberfire.ext.properties.editor.client.PropertyEditorWidget;
import org.uberfire.ext.properties.editor.client.fields.BooleanField;
import org.uberfire.ext.properties.editor.client.fields.TextField;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

@Dependent
public class JPADataObjectFieldEditor extends FieldEditor {

    interface JPADataObjectFieldEditorUIBinder
            extends UiBinder<Widget, JPADataObjectFieldEditor> {

    }

    private static JPADataObjectFieldEditorUIBinder uiBinder = GWT.create( JPADataObjectFieldEditorUIBinder.class );

    private static final String JPA_DATA_OBJECT_FIELD_EDITOR_EVENT = "JPA_DATA_OBJECT_FIELD_EDITOR_EVENT";

    private static final String IDENTIFIER_FIELD = "IDENTIFIER_FIELD";

    private static final String GENERATED_VALUE_FIELD = "GENERATED_VALUE_FIELD";

    private static final String SEQUENCE_GENERATOR_FIELD = "SEQUENCE_GENERATOR_FIELD";

    private static final String COLUMN_NAME_FIELD = "COLUMN_NAME_FIELD";

    private static final String COLUMN_UNIQUE_FIELD = "COLUMN_UNIQUE_FIELD";

    private static final String COLUMN_NULLABLE_FIELD = "COLUMN_NULLABLE_FIELD";

    private static final String COLUMN_INSERTABLE_FIELD = "COLUMN_INSERTABLE_FIELD";

    private static final String COLUMN_UPDATABLE_FIELD = "COLUMN_UPDATABLE_FIELD";

    private static final String RELATIONSHIP_TYPE_FIELD = "RELATIONSHIP_TYPE_FIELD";

    private static Map<String, DataModelerPropertyEditorFieldInfo> propertyEditorFields = new HashMap<String, DataModelerPropertyEditorFieldInfo>();

    @UiField
    PropertyEditorWidget propertyEditor;

    public JPADataObjectFieldEditor() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    protected void init(){
        propertyEditor.setFilterGroupVisible( false );
        loadPropertyEditor();
        setReadonly( true );
    }

    @Override
    public String getName() {
        return "JPA_FIELD_EDITOR";
    }

    @Override
    public String getDomainName() {
        return JPADomainEditor.JPA_DOMAIN;
    }

    @Override
    protected void loadDataObjectField( DataObject dataObject, ObjectProperty objectField ) {
        clean();
        setReadonly( true );
        if ( dataObject != null && objectField != null ) {
            this.dataObject = dataObject;
            this.objectField = objectField;

            updateIdentifierField( objectField.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_ID_ANNOTATION ) );
            updateColumnFields( objectField.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_COLUMN_ANNOTATION ) );
            updateGeneratedValueField( objectField.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_GENERATED_VALUE_ANNOTATION ) );
            updateSequenceGeneratorField( objectField.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_SEQUENCE_GENERATOR_ANNOTATION ) );
            updateRelationshipField( getCurrentRelationshipAnnotation( objectField ) );

            setReadonly( getContext() == null || getContext().isReadonly() );
        }
        loadPropertyEditor();
    }

    protected void loadPropertyEditor() {
        propertyEditor.handle( new PropertyEditorEvent( getCurrentEditorEventId(), getPropertyEditorCategories() ) );
    }

    protected List<PropertyEditorCategory> getPropertyEditorCategories() {

        final List<PropertyEditorCategory> categories = new ArrayList<PropertyEditorCategory>();

        PropertyEditorCategory category = new PropertyEditorCategory( "Identifier Properties", 1 );
        categories.add( category );

        category.withField( createIdentifierField() );
        category.withField( createGeneratedValueField() );
        category.withField( createSequenceGeneratorField() );

        category = new PropertyEditorCategory( "Column Properties", 2 );
        categories.add( category );

        category.withField( createColumnNameField() );
        category.withField( createColumnUniqueField() );
        category.withField( createColumnNullableField() );
        category.withField( createColumnInsertableField() );
        category.withField( createColumnUpdatableField() );

        category = new PropertyEditorCategory( "Relationship Properties", 3 );
        categories.add( category );

        category.withField( createRelationShipTypeField() );

        return categories;
    }

    private DataModelerPropertyEditorFieldInfo createIdentifierField() {
        return createField( "Is Identifier", IDENTIFIER_FIELD, "false", BooleanField.class );
    }

    private DataModelerPropertyEditorFieldInfo createGeneratedValueField() {
        return createField( "Generation strategy", GENERATED_VALUE_FIELD, "NONE", IdGeneratorField.class );
    }

    private DataModelerPropertyEditorFieldInfo createSequenceGeneratorField() {
        return createField( "Sequence Generator", SEQUENCE_GENERATOR_FIELD, "NONE", SequenceGeneratorField.class );
    }

    private DataModelerPropertyEditorFieldInfo createColumnNameField() {
        return createField( "Column name", COLUMN_NAME_FIELD, "", TextField.class );
    }

    private DataModelerPropertyEditorFieldInfo createColumnUniqueField() {
        return createField( "Unique", COLUMN_UNIQUE_FIELD, "false", BooleanField.class );
    }

    private DataModelerPropertyEditorFieldInfo createColumnNullableField() {
        return createField( "Nullable", COLUMN_NULLABLE_FIELD, "true", BooleanField.class );
    }

    private DataModelerPropertyEditorFieldInfo createColumnInsertableField() {
        return createField( "Insertable", COLUMN_INSERTABLE_FIELD, "true", BooleanField.class );
    }

    private DataModelerPropertyEditorFieldInfo createColumnUpdatableField() {
        return createField( "Updatable", COLUMN_UPDATABLE_FIELD, "true", BooleanField.class );
    }

    private DataModelerPropertyEditorFieldInfo createRelationShipTypeField() {
        return createField( "Relationship Type", RELATIONSHIP_TYPE_FIELD, "Relation not set", RelationshipField.class );
    }

    private DataModelerPropertyEditorFieldInfo createField( String label, String key, String currentStringValue, Class<?> customFieldClass ) {
        DataModelerPropertyEditorFieldInfo fieldInfo = propertyEditorFields.get( key );
        if ( fieldInfo == null ) {
            fieldInfo = new DataModelerPropertyEditorFieldInfo( label, currentStringValue, customFieldClass );
            fieldInfo.withKey( key );
            propertyEditorFields.put( key, fieldInfo );
        }
        return fieldInfo;
    }

    private void updatePropertyEditorField( String fieldId, Annotation currentValue, String currentStringValue ) {
        DataModelerPropertyEditorFieldInfo fieldInfo = propertyEditorFields.get( fieldId );
        fieldInfo.setCurrentValue( currentValue );
        fieldInfo.setCurrentStringValue( currentStringValue );
    }

    private DataModelerPropertyEditorFieldInfo getField( String fieldId ) {
        return propertyEditorFields.get( fieldId );
    }

    private void updateIdentifierField( Annotation annotation ) {
        cleanIdentifierField();
        if ( annotation != null ) {
            updatePropertyEditorField( IDENTIFIER_FIELD, annotation, "true" );
        }
    }

    private void updateColumnFields( Annotation annotation ) {

        cleanColumnFields();
        if ( annotation != null ) {

            String currentStringValue = AnnotationValueHandler.getStringValue( annotation, "name", "" );
            updatePropertyEditorField( COLUMN_NAME_FIELD, annotation, currentStringValue );

            currentStringValue = AnnotationValueHandler.getStringValue( annotation, "unique", "false" );
            updatePropertyEditorField( COLUMN_UNIQUE_FIELD, annotation, currentStringValue );

            currentStringValue = AnnotationValueHandler.getStringValue( annotation, "nullable", "true" );
            updatePropertyEditorField( COLUMN_NULLABLE_FIELD, annotation, currentStringValue );

            currentStringValue = AnnotationValueHandler.getStringValue( annotation, "insertable", "true" );
            updatePropertyEditorField( COLUMN_INSERTABLE_FIELD, annotation, currentStringValue );

            currentStringValue = AnnotationValueHandler.getStringValue( annotation, "updatable", "true" );
            updatePropertyEditorField( COLUMN_UPDATABLE_FIELD, annotation, currentStringValue );
        }
    }

    private void updateGeneratedValueField( Annotation annotation ) {

        cleanGeneratedValueField();
        if ( annotation != null ) {

            DataModelerPropertyEditorFieldInfo fieldInfo = getField( GENERATED_VALUE_FIELD );
            String strategy = AnnotationValueHandler.getStringValue( annotation, "strategy", "NONE" );
            String generator = AnnotationValueHandler.getStringValue( annotation, "generator", null );

            fieldInfo.setCurrentValue( "strategy", strategy );
            fieldInfo.setCurrentValue( "generator", generator );

            updatePropertyEditorField( GENERATED_VALUE_FIELD, annotation, strategy );
        }
    }

    private void updateSequenceGeneratorField( Annotation annotation ) {

        cleanSequenceGeneratorField();
        if ( annotation != null ) {

            SequenceGeneratorValueHandler valueHandler = new SequenceGeneratorValueHandler( annotation );
            DataModelerPropertyEditorFieldInfo fieldInfo = getField( SEQUENCE_GENERATOR_FIELD );

            fieldInfo.setCurrentValue( SequenceGeneratorValueHandler.NAME, valueHandler.getName() );
            fieldInfo.setCurrentValue( SequenceGeneratorValueHandler.SEQUENCE_NAME, valueHandler.getSequenceName() );
            fieldInfo.setCurrentValue( SequenceGeneratorValueHandler.INITIAL_VALUE, valueHandler.getInitialValue() );
            fieldInfo.setCurrentValue( SequenceGeneratorValueHandler.ALLOCATION_SIZE, valueHandler.getAllocationSize() );

            updatePropertyEditorField( SEQUENCE_GENERATOR_FIELD, annotation, valueHandler.getName() );
        }
    }

    private void updateRelationshipField( Annotation annotation ) {

        cleanRelationshipField();
        if ( annotation != null ) {

            RelationshipAnnotationValueHandler valueHandler = new RelationshipAnnotationValueHandler( annotation );
            DataModelerPropertyEditorFieldInfo fieldInfo =  getField( RELATIONSHIP_TYPE_FIELD );

            fieldInfo.setCurrentValue( RelationshipAnnotationValueHandler.RELATION_TYPE, valueHandler.getRelationType() );
            fieldInfo.setCurrentValue( RelationshipAnnotationValueHandler.CASCADE, valueHandler.getCascade() );
            fieldInfo.setCurrentValue( RelationshipAnnotationValueHandler.FETCH, valueHandler.getFetch() );
            fieldInfo.setCurrentValue( RelationshipAnnotationValueHandler.OPTIONAL, valueHandler.getOptional() );
            fieldInfo.setCurrentValue( RelationshipAnnotationValueHandler.MAPPED_BY, valueHandler.getMappedBy() );
            fieldInfo.setCurrentValue( RelationshipAnnotationValueHandler.ORPHAN_REMOVAL, valueHandler.getOrphanRemoval() );

            if ( valueHandler.isOneToMany() ) {
                fieldInfo.removeCurrentValue( RelationshipAnnotationValueHandler.OPTIONAL );
            } else if ( valueHandler.isManyToOne() ) {
                fieldInfo.removeCurrentValue( RelationshipAnnotationValueHandler.MAPPED_BY );
                fieldInfo.removeCurrentValue( RelationshipAnnotationValueHandler.ORPHAN_REMOVAL );
            } else if ( valueHandler.isManyToMany() ) {
                fieldInfo.removeCurrentValue( RelationshipAnnotationValueHandler.OPTIONAL );
                fieldInfo.removeCurrentValue( RelationshipAnnotationValueHandler.ORPHAN_REMOVAL );
            }

            updatePropertyEditorField( RELATIONSHIP_TYPE_FIELD, annotation, valueHandler.getRelationType().name() );
        }
    }

    // Event notifications

    public void onPropertyEditorChange( @Observes PropertyEditorChangeEvent event ) {
        PropertyEditorFieldInfo property = event.getProperty();

        if ( isFromCurrentEditor( property.getEventId() ) ) {

            DataModelerPropertyEditorFieldInfo fieldInfo = ( DataModelerPropertyEditorFieldInfo ) event.getProperty();

            if ( IDENTIFIER_FIELD.equals( fieldInfo.getKey() ) ) {
                identifierFieldChanged( event.getNewValue() );
            } else if ( COLUMN_NAME_FIELD.equals( fieldInfo.getKey() ) ||
                    COLUMN_UNIQUE_FIELD.equals( fieldInfo.getKey() ) ||
                    COLUMN_NULLABLE_FIELD.equals( fieldInfo.getKey() ) ||
                    COLUMN_INSERTABLE_FIELD.equals( fieldInfo.getKey() ) ||
                    COLUMN_UPDATABLE_FIELD.equals( fieldInfo.getKey() ) ) {
                columnFieldChanged( fieldInfo, event.getNewValue() );
            } else if ( GENERATED_VALUE_FIELD.equals( fieldInfo.getKey() ) ) {
                generatedValueFieldChanged( fieldInfo, event.getNewValue() );
            } else if ( SEQUENCE_GENERATOR_FIELD.equals( fieldInfo.getKey() ) ) {
                sequenceGeneratorFieldChanged( fieldInfo, event.getNewValue()  );
            } else if ( RELATIONSHIP_TYPE_FIELD.equals( fieldInfo.getKey() ) ) {
                relationTypeFieldChanged( fieldInfo, event.getNewValue() );
            }
        }
    }

    private void identifierFieldChanged( String newStringValue ) {

        if ( getObjectField() != null ) {

            Boolean doAdd = Boolean.TRUE.toString().equals( newStringValue );
            commandBuilder.buildFieldAddOrRemoveAnnotationCommand( getContext(),
                    getName(), getDataObject(), getObjectField(),
                    JPADomainAnnotations.JAVAX_PERSISTENCE_ID_ANNOTATION, doAdd ).execute();
        }
    }

    private void columnFieldChanged( DataModelerPropertyEditorFieldInfo fieldInfo, String newValue ) {

        if ( getObjectField() != null ) {

            if ( COLUMN_NAME_FIELD.equals( fieldInfo.getKey() ) ) {

                //TODO add column name validator
                String value = DataModelerUtils.nullTrim( newValue );
                commandBuilder.buildFieldAnnotationValueChangeCommand( getContext(), getName(), getDataObject(), getObjectField(),
                        JPADomainAnnotations.JAVAX_PERSISTENCE_COLUMN_ANNOTATION, "name", value, false ).execute();

            } else if ( COLUMN_UNIQUE_FIELD.equals( fieldInfo.getKey() ) ) {

                commandBuilder.buildFieldAnnotationValueChangeCommand( getContext(), getName(), getDataObject(), getObjectField(),
                        JPADomainAnnotations.JAVAX_PERSISTENCE_COLUMN_ANNOTATION, "unique", newValue, false ).execute();

            } else if ( COLUMN_NULLABLE_FIELD.equals( fieldInfo.getKey() ) ) {

                commandBuilder.buildFieldAnnotationValueChangeCommand( getContext(), getName(), getDataObject(), getObjectField(),
                        JPADomainAnnotations.JAVAX_PERSISTENCE_COLUMN_ANNOTATION, "nullable", newValue, false ).execute();

            } else if ( COLUMN_INSERTABLE_FIELD.equals( fieldInfo.getKey() ) ) {

                commandBuilder.buildFieldAnnotationValueChangeCommand( getContext(), getName(), getDataObject(), getObjectField(),
                        JPADomainAnnotations.JAVAX_PERSISTENCE_COLUMN_ANNOTATION, "insertable", newValue, false ).execute();

            } else if ( COLUMN_UPDATABLE_FIELD.equals( fieldInfo.getKey() ) ) {

                commandBuilder.buildFieldAnnotationValueChangeCommand( getContext(), getName(), getDataObject(), getObjectField(),
                        JPADomainAnnotations.JAVAX_PERSISTENCE_COLUMN_ANNOTATION, "updatable", newValue, false ).execute();

            }

            Annotation annotation = getObjectField().getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_COLUMN_ANNOTATION );

            //If the COLUMN annotation just has the by default parameters configured just remove it.
            if ( annotation != null && hasOnlyDefaultValues( annotation ) ) {
                commandBuilder.buildFieldAddOrRemoveAnnotationCommand( getContext(), getName(), getDataObject(), getObjectField(),
                        JPADomainAnnotations.JAVAX_PERSISTENCE_COLUMN_ANNOTATION, false ).execute();

            }
        }
    }

    boolean hasOnlyDefaultValues( Annotation columnAnnotation ) {

        String strValue;

        strValue = ( String ) columnAnnotation.getValue( "name" );
        if ( strValue != null && !"".equals( strValue ) ) {
            return false;
        }

        strValue = AnnotationValueHandler.getStringValue( columnAnnotation, "unique", null );
        if ( strValue != null && !"false".equals( strValue ) ) {
            return false;
        }

        strValue = AnnotationValueHandler.getStringValue( columnAnnotation, "nullable", null );
        if ( strValue != null && !"true".equals( strValue ) ) {
            return false;
        }

        strValue = AnnotationValueHandler.getStringValue( columnAnnotation, "insertable", null );
        if ( strValue != null && !"true".equals( strValue ) ) {
            return false;
        }

        strValue = AnnotationValueHandler.getStringValue( columnAnnotation, "updatable", null );
        if ( strValue != null && !"true".equals( strValue ) ) {
            return false;
        }

        return true;
    }

    private void generatedValueFieldChanged( DataModelerPropertyEditorFieldInfo fieldInfo, String newStringValue ) {

        if ( getObjectField() != null ) {

            String strategy = DataModelerUtils.nullTrim( ( String ) getField( GENERATED_VALUE_FIELD ).getCurrentValue( "strategy" ) );
            String generator = DataModelerUtils.nullTrim( ( String ) getField( GENERATED_VALUE_FIELD ).getCurrentValue( "generator" ) );

            if ( strategy == null || "NONE".equals( strategy ) ) {
                commandBuilder.buildFieldAnnotationRemoveCommand( getContext(), getName(), getDataObject(), getObjectField(),
                        JPADomainAnnotations.JAVAX_PERSISTENCE_GENERATED_VALUE_ANNOTATION ).execute();
            } else {

                commandBuilder.buildFieldAnnotationValueChangeCommand( getContext(), getName(), getDataObject(), getObjectField(),
                        JPADomainAnnotations.JAVAX_PERSISTENCE_GENERATED_VALUE_ANNOTATION, "strategy", strategy, false ).execute();

                commandBuilder.buildFieldAnnotationValueChangeCommand( getContext(), getName(), getDataObject(), getObjectField(),
                        JPADomainAnnotations.JAVAX_PERSISTENCE_GENERATED_VALUE_ANNOTATION, "generator", generator, false ).execute();
            }
        }
    }

    private void relationTypeFieldChanged( DataModelerPropertyEditorFieldInfo fieldInfo, String newValue ) {

        if ( getObjectField() != null ) {

            Annotation oldRelation = getCurrentRelationshipAnnotation( getObjectField() );
            RelationshipAnnotationValueHandler oldRelationHandler = oldRelation != null ? new RelationshipAnnotationValueHandler( oldRelation ) : null;
            Annotation newRelation;

            RelationType newRelationType = ( RelationType ) fieldInfo.getCurrentValue( RelationshipAnnotationValueHandler.RELATION_TYPE );
            List<CascadeType> newCascadeTypes = ( List<CascadeType> ) fieldInfo.getCurrentValue( RelationshipAnnotationValueHandler.CASCADE );
            FetchMode newFetchMode = ( FetchMode ) fieldInfo.getCurrentValue( RelationshipAnnotationValueHandler.FETCH );
            Boolean newOptional = ( Boolean ) fieldInfo.getCurrentValue( RelationshipAnnotationValueHandler.OPTIONAL );
            String newMappedBy = DataModelerUtils.nullTrim( ( String ) fieldInfo.getCurrentValue( RelationshipAnnotationValueHandler.MAPPED_BY ) );
            Boolean newOrphanRemoval = ( Boolean ) fieldInfo.getCurrentValue( RelationshipAnnotationValueHandler.ORPHAN_REMOVAL );

            //TODO add more fine grained control for the changes if needed. By now I can just remove the old relation annotation
            //and add the new one. This may alter the annotations order for the given field, but it's not a problem.
            if ( oldRelationHandler != null ) {
                commandBuilder.buildFieldAnnotationRemoveCommand( getContext(), getName(), getDataObject(),
                        getObjectField(), oldRelationHandler.getClassName() ).execute();
            }

            newRelation = RelationshipAnnotationValueHandler.createAnnotation( newRelationType,
                    newCascadeTypes, newFetchMode, newOptional, newMappedBy, newOrphanRemoval,
                    getContext().getAnnotationDefinitions() );

            if ( newRelation != null ) {
                getObjectField().addAnnotation( newRelation );
                commandBuilder.buildFieldAnnotationAddCommand( getContext(), getName(), getDataObject(),
                        getObjectField(), newRelation ).execute();
            }
        }
    }

    private void sequenceGeneratorFieldChanged( DataModelerPropertyEditorFieldInfo fieldInfo, String newValue ) {
        if ( getObjectField() != null ) {

            Annotation oldGenerator = getObjectField().getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_SEQUENCE_GENERATOR_ANNOTATION );
            SequenceGeneratorValueHandler oldGeneratorHandler = oldGenerator != null ? new SequenceGeneratorValueHandler( oldGenerator ) : null;
            Annotation newGenerator = null;

            //TODO add more fine grained control to the changes if needed. By now I can just remove the old generator annotation
            //and add the new one. This may alter the annotations order for the given field, but it's not a problem.
            if ( oldGeneratorHandler != null ) {
                commandBuilder.buildFieldAnnotationRemoveCommand( getContext(), getName(), getDataObject(),
                        getObjectField(), oldGeneratorHandler.getClassName() ).execute();
            }

            String name = DataModelerUtils.nullTrim( ( String ) fieldInfo.getCurrentValue( SequenceGeneratorValueHandler.NAME ) );
            String sequenceName = DataModelerUtils.nullTrim( ( String ) fieldInfo.getCurrentValue( SequenceGeneratorValueHandler.SEQUENCE_NAME ) );
            Integer initialValue = ( Integer ) fieldInfo.getCurrentValue( SequenceGeneratorValueHandler.INITIAL_VALUE );
            Integer allocationSize = ( Integer ) fieldInfo.getCurrentValue( SequenceGeneratorValueHandler.ALLOCATION_SIZE );

            if ( name != null && !"".equals( name.trim() ) ) {
                newGenerator = SequenceGeneratorValueHandler.createAnnotation( name,
                        sequenceName,
                        initialValue,
                        allocationSize,
                        getContext().getAnnotationDefinitions() );

                commandBuilder.buildFieldAnnotationAddCommand( getContext(), getName(), getDataObject(),
                        getObjectField(), newGenerator ).execute();
            }
        }
    }

    // Event handlers

    public void clean() {
        cleanIdentifierField();
        cleanGeneratedValueField();
        cleanSequenceGeneratorField();
        cleanColumnFields();
        cleanRelationshipField();
    }

    protected void cleanIdentifierField() {
        updatePropertyEditorField( IDENTIFIER_FIELD, null, "false" );
    }

    protected void cleanGeneratedValueField() {
        updatePropertyEditorField( GENERATED_VALUE_FIELD, null, "NONE" );
        getField( GENERATED_VALUE_FIELD ).cleanCurrentValues();
    }

    protected void cleanSequenceGeneratorField() {
        updatePropertyEditorField( SEQUENCE_GENERATOR_FIELD, null, "NOT_SET" );
        getField( SEQUENCE_GENERATOR_FIELD ).cleanCurrentValues();
    }

    protected void cleanColumnFields() {
        updatePropertyEditorField( COLUMN_NAME_FIELD, null, null );
        updatePropertyEditorField( COLUMN_UNIQUE_FIELD, null, "false" );
        updatePropertyEditorField( COLUMN_INSERTABLE_FIELD, null, "true" );
        updatePropertyEditorField( COLUMN_NULLABLE_FIELD, null, "true" );
        updatePropertyEditorField( COLUMN_UPDATABLE_FIELD, null, "true" );
    }

    protected void cleanRelationshipField() {
        updatePropertyEditorField( RELATIONSHIP_TYPE_FIELD, null, DataModelerUtils.NOT_SELECTED );
        getField( RELATIONSHIP_TYPE_FIELD ).cleanCurrentValues();
    }

    private Annotation getCurrentRelationshipAnnotation( ObjectProperty objectProperty ) {
        Annotation annotation;

        if ( ( annotation = objectField.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_ONE_TO_ONE ) ) != null ) {
            return annotation;
        } else if ( ( annotation = objectField.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_ONE_TO_MANY ) ) != null ) {
            return annotation;
        } else if ( ( annotation = objectField.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_MANY_TO_ONE ) ) != null ) {
            return annotation;
        } else if ( ( annotation = objectField.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_MANY_TO_MANY ) ) != null ) {
            return annotation;
        }
        return null;
    }

    private String getCurrentEditorEventId() {
        //TODO, temporal mechanism to avoid two property editors opened in different workbench JPA editors receiving crossed events
        return JPA_DATA_OBJECT_FIELD_EDITOR_EVENT + "-" + this.hashCode();
    }

    private boolean isFromCurrentEditor( String propertyEditorEventId ) {
        return getCurrentEditorEventId().equals( propertyEditorEventId );
    }
}