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
import javax.enterprise.event.Observes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.util.CascadeType;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.util.FetchMode;
import org.kie.workbench.common.screens.datamodeller.client.util.RelationType;
import org.kie.workbench.common.screens.datamodeller.client.util.RelationshipAnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.util.SequenceGeneratorValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.FieldEditor;
import org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties.IdGeneratorField;
import org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties.RelationshipField;
import org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties.SequenceGeneratorField;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.uberfire.ext.properties.editor.client.PropertyEditorWidget;
import org.uberfire.ext.properties.editor.client.fields.BooleanField;
import org.uberfire.ext.properties.editor.client.fields.TextField;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

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

        propertyEditor.setFilterPanelVisible( false );
        loadPropertyEditor();
        setReadonly( true );
    }

    private DataModel getDataModel() {
        return getContext() != null ? getContext().getDataModel() : null;
    }

    private Project getProject() {
        return getContext() != null ? getContext().getCurrentProject() : null;
    }

    protected void setReadonly( boolean readonly ) {
        super.setReadonly( readonly );
    }

    @Override
    protected void loadDataObjectField( DataObject dataObject, ObjectProperty objectField ) {
        clean();
        setReadonly( true );
        if ( dataObject != null && objectField != null ) {
            this.dataObject = dataObject;
            this.objectField = objectField;

            updateIdentifierField( objectField.getAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_ID_ANNOTATION ) );
            updateColumnFields( objectField.getAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_COLUMN_ANNOTATION ) );
            updateGeneratedValueField( objectField.getAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_GENERATED_VALUE_ANNOTATION ) );
            updateSequenceGeneratorField( objectField.getAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_SEQUENCE_GENERATOR_ANNOTATION ) );
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

        if ( getObjectField() == null ) return;

        Annotation annotation = getObjectField().getAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_ID_ANNOTATION );
        Boolean wasSet = annotation != null;

        final Boolean isSet = Boolean.TRUE.toString().equals( newStringValue );

        if ( wasSet && !isSet ) {
            getObjectField().removeAnnotation( annotation.getClassName() );
        } else if ( !wasSet && isSet ) {
            annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.JAVAX_PERSISTENCE_ID_ANNOTATION ) );
            getObjectField().addAnnotation( annotation );
        }
        if ( wasSet != isSet ) {
            notifyFieldChange( AnnotationDefinitionTO.JAVAX_PERSISTENCE_ID_ANNOTATION, wasSet, isSet );
        }
    }

    private void columnFieldChanged( DataModelerPropertyEditorFieldInfo fieldInfo, String newValue ) {

        if ( getObjectField() == null ) return;

        Annotation annotation = getObjectField().getAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_COLUMN_ANNOTATION );
        String oldValue = null;

        if ( COLUMN_NAME_FIELD.equals( fieldInfo.getKey() ) ) {
            oldValue = AnnotationValueHandler.getStringValue( annotation, "name", null );
            if ( annotation != null ) {
                if ( newValue == null || "".equals( newValue ) ) {
                    annotation.removeValue( "name" );
                } else {
                    annotation.setValue( "name", newValue );
                }
            } else if ( newValue != null && !"".equals( newValue ) ) {
                annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.JAVAX_PERSISTENCE_COLUMN_ANNOTATION ) );
                annotation.setValue( "name", newValue );
                getObjectField().addAnnotation( annotation );
            }

        } else if ( COLUMN_UNIQUE_FIELD.equals( fieldInfo.getKey() ) ) {
            oldValue = AnnotationValueHandler.getStringValue( annotation, "unique", null );
            if ( annotation != null ) {
                if ( newValue == null || "false".equals( newValue ) ) {
                    annotation.removeValue( "unique" );
                } else {
                    annotation.setValue( "unique", newValue );
                }
            } else if ( newValue != null && !"false".equals( newValue ) ) {
                annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.JAVAX_PERSISTENCE_COLUMN_ANNOTATION ) );
                annotation.setValue( "unique", newValue );
                getObjectField().addAnnotation( annotation );
            }

        } else if ( COLUMN_NULLABLE_FIELD.equals( fieldInfo.getKey() ) ) {
            oldValue = AnnotationValueHandler.getStringValue( annotation, "nullable", null );
            if ( annotation != null ) {
                if ( newValue == null || "true".equals( newValue ) ) {
                    annotation.removeValue( "nullable" );
                } else {
                    annotation.setValue( "nullable", newValue );
                }
            } else if ( newValue != null && !"true".equals( newValue ) ) {
                annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.JAVAX_PERSISTENCE_COLUMN_ANNOTATION ) );
                annotation.setValue( "nullable", newValue );
                getObjectField().addAnnotation( annotation );
            }

        } else if ( COLUMN_INSERTABLE_FIELD.equals( fieldInfo.getKey() ) ) {
            oldValue = AnnotationValueHandler.getStringValue( annotation, "insertable", null );
            if ( annotation != null ) {
                if ( newValue == null || "true".equals( newValue ) ) {
                    annotation.removeValue( "insertable" );
                } else {
                    annotation.setValue( "insertable", newValue );
                }
            } else if ( newValue != null && !"true".equals( newValue ) ) {
                annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.JAVAX_PERSISTENCE_COLUMN_ANNOTATION ) );
                annotation.setValue( "insertable", newValue );
                getObjectField().addAnnotation( annotation );
            }

        } else if ( COLUMN_UPDATABLE_FIELD.equals( fieldInfo.getKey() ) ) {
            oldValue = AnnotationValueHandler.getStringValue( annotation, "updatable", null );
            if ( annotation != null ) {
                if ( newValue == null || "true".equals( newValue ) ) {
                    annotation.removeValue( "updatable" );
                } else {
                    annotation.setValue( "updatable", newValue );
                }
            } else if ( newValue != null && !"true".equals( newValue ) ) {
                annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.JAVAX_PERSISTENCE_COLUMN_ANNOTATION ) );
                annotation.setValue( "updatable", newValue );
                getObjectField().addAnnotation( annotation );
            }

        }

        //If the COLUMN annotation just has the by default parameters configured just remove it.
        if ( annotation != null && hasOnlyDefaultValues( annotation ) ) {
            getObjectField().removeAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_COLUMN_ANNOTATION );
        }

        notifyFieldChange( AnnotationDefinitionTO.JAVAX_PERSISTENCE_COLUMN_ANNOTATION, oldValue, newValue );
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
        if ( strValue != null && !"true".equals( true ) ) {
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

        if ( getObjectField() == null ) return;

        String strategy = (String) getField( GENERATED_VALUE_FIELD ).getCurrentValue( "strategy" );
        String generator = (String) getField( GENERATED_VALUE_FIELD ).getCurrentValue( "generator" );
        Annotation annotation = getObjectField().getAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_GENERATED_VALUE_ANNOTATION );

        if ( strategy == null || "NONE".equals( strategy ) ) {
            getObjectField().removeAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_GENERATED_VALUE_ANNOTATION );
        } else {
            if ( annotation == null ) {
                annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.JAVAX_PERSISTENCE_GENERATED_VALUE_ANNOTATION ) );
                getObjectField().addAnnotation( annotation );
            }
            annotation.setValue( "strategy", strategy );
            if ( generator == null ) {
                //uncommon case
                annotation.removeValue( "generator" );
            } else {
                annotation.setValue( "generator", generator );
            }
        }

        //TODO review this
        notifyFieldChange( AnnotationDefinitionTO.JAVAX_PERSISTENCE_GENERATED_VALUE_ANNOTATION, "oldValue", "newValue" );
    }

    private void relationTypeFieldChanged( DataModelerPropertyEditorFieldInfo fieldInfo, String newValue ) {

        if ( getObjectField() == null ) return;

        Annotation oldRelation = getCurrentRelationshipAnnotation( getObjectField() );
        RelationshipAnnotationValueHandler oldRelationHandler = oldRelation != null ? new RelationshipAnnotationValueHandler( oldRelation ) : null;
        Annotation newRelation;

        RelationType newRelationType = ( RelationType ) fieldInfo.getCurrentValue( RelationshipAnnotationValueHandler.RELATION_TYPE );
        CascadeType newCascadeType = ( CascadeType ) fieldInfo.getCurrentValue( RelationshipAnnotationValueHandler.CASCADE );
        FetchMode newFetchMode = ( FetchMode ) fieldInfo.getCurrentValue( RelationshipAnnotationValueHandler.FETCH );
        Boolean newOptional = ( Boolean ) fieldInfo.getCurrentValue( RelationshipAnnotationValueHandler.OPTIONAL );
        String newMappedBy = ( String ) fieldInfo.getCurrentValue( RelationshipAnnotationValueHandler.MAPPED_BY );
        Boolean newOrphanRemoval = ( Boolean ) fieldInfo.getCurrentValue( RelationshipAnnotationValueHandler.ORPHAN_REMOVAL );

        //TODO add more fine grained control to the changes if needed. By now I can just remove the old relation annotation
        //and add the new one. This may alter the annotations order for the given field, but's not a problem.
        if ( oldRelationHandler != null ) getObjectField().removeAnnotation( oldRelationHandler.getClassName() );

        newRelation = RelationshipAnnotationValueHandler.createAnnotation( newRelationType,
                newCascadeType, newFetchMode, newOptional, newMappedBy, newOrphanRemoval,
                getContext().getAnnotationDefinitions() );

        if ( newRelation != null ) {
            getObjectField().addAnnotation( newRelation );
        }

        //TODO review this
        notifyFieldChange( "relationType", oldRelationHandler != null ? oldRelationHandler.getClassName() : null,
                newRelation != null ? newRelation.getClassName() : null );
    }

    private void sequenceGeneratorFieldChanged( DataModelerPropertyEditorFieldInfo fieldInfo, String newValue ) {
        if ( getObjectField() == null ) return;

        Annotation oldGenerator = getObjectField().getAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_SEQUENCE_GENERATOR_ANNOTATION );
        SequenceGeneratorValueHandler oldGeneratorHandler = oldGenerator != null ? new SequenceGeneratorValueHandler( oldGenerator ) : null;
        Annotation newGenerator = null;

        //TODO add more fine grained control to the changes if needed. By now I can just remove the old generator annotation
        //and add the new one. This may alter the annotations order for the given field, but's not a problem.
        if ( oldGeneratorHandler != null ) getObjectField().removeAnnotation( oldGeneratorHandler.getClassName() );

        String name = (String) fieldInfo.getCurrentValue( SequenceGeneratorValueHandler.NAME );
        String sequenceName = (String) fieldInfo.getCurrentValue( SequenceGeneratorValueHandler.SEQUENCE_NAME );
        Integer initialValue = (Integer) fieldInfo.getCurrentValue( SequenceGeneratorValueHandler.INITIAL_VALUE );
        Integer allocationSize = (Integer) fieldInfo.getCurrentValue( SequenceGeneratorValueHandler.ALLOCATION_SIZE );

        if ( name != null && !"".equals( name.trim() ) ) {
            newGenerator = SequenceGeneratorValueHandler.createAnnotation( name,
                    sequenceName,
                    initialValue,
                    allocationSize,
                    getContext().getAnnotationDefinitions() );
            getObjectField().addAnnotation( newGenerator );
        }

        //TODO review this
        notifyFieldChange( "sequenceGenerator", oldGeneratorHandler != null ? oldGeneratorHandler.getName() : null,
                newGenerator != null ? name : null );
    }

    // Event handlers

    protected void clean() {
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

        if ( ( annotation = objectField.getAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_ONE_TO_ONE ) ) != null ) {
            return annotation;
        } else if ( ( annotation = objectField.getAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_ONE_TO_MANY ) ) != null ) {
            return annotation;
        } else if ( ( annotation = objectField.getAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_MANY_TO_ONE ) ) != null ) {
            return annotation;
        } else if ( ( annotation = objectField.getAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_MANY_TO_MANY ) ) != null ) {
            return annotation;
        }
        return null;
    }

    private String getCurrentEditorEventId() {
        //TODO, temporal mecanism to avoid two property editors opened in different workech JPA editors receiving crossed events
        return JPA_DATA_OBJECT_FIELD_EDITOR_EVENT + "-" + this.hashCode();
    }

    private boolean isFromCurrentEditor( String propertyEditorEventId ) {
        return getCurrentEditorEventId().equals( propertyEditorEventId );
    }
}