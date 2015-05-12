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
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.ObjectEditor;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectChangeEvent;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.uberfire.ext.properties.editor.client.PropertyEditorWidget;
import org.uberfire.ext.properties.editor.client.fields.BooleanField;
import org.uberfire.ext.properties.editor.client.fields.TextField;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

public class JPADataObjectEditor extends ObjectEditor {

    interface JPADataObjectEditorUIBinder
            extends UiBinder<Widget, JPADataObjectEditor> {

    }

    private static JPADataObjectEditorUIBinder uiBinder = GWT.create( JPADataObjectEditorUIBinder.class );

    private static final String JPA_DATA_OBJECT_EDITOR_EVENT = "JPA_DATA_OBJECT_EDITOR_EVENT";

    private static final String ENTITY_FIELD = "ENTITY_FIELD";

    private static final String TABLE_NAME_FIELD = "TABLE_NAME_FIELD";

    private static final String ENTITY_CATEGORY = "Entity Properties";

    @UiField
    PropertyEditorWidget propertyEditor;

    private static Map<String, DataModelerPropertyEditorFieldInfo> propertyEditorFields = new HashMap<String, DataModelerPropertyEditorFieldInfo>();

    public JPADataObjectEditor() {
        initWidget( uiBinder.createAndBindUi( this ) );

        propertyEditor.setFilterPanelVisible( false );
        propertyEditor.setLastOpenAccordionGroupTitle( ENTITY_CATEGORY );
        loadPropertyEditor();
        setReadonly( true );
    }

    private Project getProject() {
        return getContext() != null ? getContext().getCurrentProject() : null;
    }

    private DataModel getDataModel() {
        return getContext() != null ? getContext().getDataModel() : null;
    }

    protected void setReadonly( boolean readonly ) {
        super.setReadonly( readonly );
    }

    protected void loadDataObject( DataObject dataObject ) {
        clean();
        setReadonly( true );
        this.dataObject = dataObject;

        if ( dataObject != null ) {
            updateEntityField( dataObject.getAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_ENTITY_ANNOTATION ) );
            updateTableNameField( dataObject.getAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_TABLE_ANNOTATION ) );
        }
        loadPropertyEditor();
    }

    private void updateEntityField( Annotation annotation ) {
        cleanEntityField();
        if ( annotation != null ) {
            updatePropertyEditorField( ENTITY_FIELD, annotation, "true" );
        }
    }

    private void updateTableNameField( Annotation annotation ) {
        cleanTableNameField();
        if ( annotation != null ) {
            String tableName = AnnotationValueHandler.getStringValue( annotation, "name", null );
            updatePropertyEditorField( TABLE_NAME_FIELD, annotation, tableName );
        }
    }

    private void loadPropertyEditor() {
        propertyEditor.handle( new PropertyEditorEvent( getCurrentEditorEventId(), getPropertyEditorCategories() ) );
    }

    private List<PropertyEditorCategory> getPropertyEditorCategories() {

        final List<PropertyEditorCategory> categories = new ArrayList<PropertyEditorCategory>();

        PropertyEditorCategory category = new PropertyEditorCategory( ENTITY_CATEGORY, 1 );
        categories.add( category );

        category.withField( createEntityField() );
        category.withField( createTableNameField() );

        return categories;
    }

    private DataModelerPropertyEditorFieldInfo createEntityField() {
        return createField( "Persistable", ENTITY_FIELD, "false", BooleanField.class, "Persistable", "Mark this entity to be managed as a persistable Data Object." );
    }

    private DataModelerPropertyEditorFieldInfo createTableNameField() {
        return createField( "Table name", TABLE_NAME_FIELD, "", TextField.class, "Table name", "Enter an optional table name to hold Data Object's information." );
    }

    private DataModelerPropertyEditorFieldInfo createField( String label, String key, String currentStringValue, Class<?> customFieldClass) {
        return createField( label, key, currentStringValue, customFieldClass );
    }

    private DataModelerPropertyEditorFieldInfo createField( String label, String key, String currentStringValue, Class<?> customFieldClass, String helpHeading, String helpText ) {
        DataModelerPropertyEditorFieldInfo fieldInfo = propertyEditorFields.get( key );
        if ( fieldInfo == null ) {
            fieldInfo = new DataModelerPropertyEditorFieldInfo( label, currentStringValue, customFieldClass );
            fieldInfo.withKey( key );
            fieldInfo.withHelpInfo( helpHeading, helpText );
            propertyEditorFields.put( key, fieldInfo );
        }
        return fieldInfo;
    }

    @Override
    protected void clean() {
        cleanEntityField();
        cleanTableNameField();
    }

    private void cleanEntityField() {
        updatePropertyEditorField( ENTITY_FIELD, null, "false" );
    }

    private void cleanTableNameField() {
        updatePropertyEditorField( TABLE_NAME_FIELD, null, "" );
    }

    private void updatePropertyEditorField( String fieldId, Annotation currentValue, String currentStringValue ) {
        DataModelerPropertyEditorFieldInfo fieldInfo = propertyEditorFields.get( fieldId );
        fieldInfo.setCurrentValue( currentValue );
        fieldInfo.setCurrentStringValue( currentStringValue );
    }

    // Event handlers

    private void onPropertyEditorChange( @Observes PropertyEditorChangeEvent event ) {
        PropertyEditorFieldInfo property = event.getProperty();

        if ( isFromCurrentEditor( property.getEventId() ) ) {

            DataModelerPropertyEditorFieldInfo fieldInfo = ( DataModelerPropertyEditorFieldInfo ) event.getProperty();

            if ( ENTITY_FIELD.equals( fieldInfo.getKey() ) ) {
                entityFieldChanged( event.getNewValue() );
            } else if ( TABLE_NAME_FIELD.equals( fieldInfo.getKey() ) ) {
                tableNameChanged( event.getNewValue() );
            }
        }
    }

    private void entityFieldChanged( String newValue ) {
        if ( getDataObject() == null ) {
            return;
        }

        Boolean oldValue = null;
        Annotation annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_ENTITY_ANNOTATION );
        oldValue = annotation != null;

        final Boolean isChecked = Boolean.TRUE.toString().equals( newValue );

        if ( annotation != null && !isChecked ) {
            getDataObject().removeAnnotation( annotation.getClassName() );
        } else if ( annotation == null && isChecked ) {
            annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.JAVAX_PERSISTENCE_ENTITY_ANNOTATION ) );
            getDataObject().addAnnotation( annotation );
        }

        notifyObjectChange( AnnotationDefinitionTO.JAVAX_PERSISTENCE_ENTITY_ANNOTATION, oldValue, isChecked );
    }

    private void tableNameChanged( String newValue ) {
        if ( getDataObject() == null ) {
            return;
        }

        String oldValue = null;
        String _label = newValue != null ? newValue.trim() : null;
        Annotation annotation = getDataObject().getAnnotation( AnnotationDefinitionTO.JAVAX_PERSISTENCE_TABLE_ANNOTATION );

        if ( annotation != null ) {
            oldValue = AnnotationValueHandler.getStringValue( annotation, "name" );
            if ( _label != null && !"".equals( _label ) ) {
                annotation.setValue( "name", _label );
            } else {
                getDataObject().removeAnnotation( annotation.getClassName() );
            }
        } else {
            if ( _label != null && !"".equals( _label ) ) {
                annotation = new AnnotationImpl( getContext().getAnnotationDefinitions().get( AnnotationDefinitionTO.JAVAX_PERSISTENCE_TABLE_ANNOTATION ) );
                annotation.setValue( "name", _label );
                getDataObject().addAnnotation( annotation );
            }
        }
        // TODO replace 'label' literal with annotation definition constant
        notifyObjectChange( "tableName", oldValue, _label );
    }

    private void notifyObjectChange( String memberName,
            Object oldValue,
            Object newValue ) {
        DataObjectChangeEvent changeEvent = new DataObjectChangeEvent( getContext().getContextId(), DataModelerEvent.DATA_OBJECT_EDITOR, getDataModel(), getDataObject(), memberName, oldValue, newValue );
        // Notify helper directly
        getContext().getHelper().dataModelChanged( changeEvent );
        dataModelerEvent.fire( changeEvent );
    }

    private String getCurrentEditorEventId() {
        //TODO, temporal mecanism to avoid two property editors opened in different workech JPA editors receiving crossed events
        return JPA_DATA_OBJECT_EDITOR_EVENT + "-" + this.hashCode();
    }

    private boolean isFromCurrentEditor( String propertyEditorEventId ) {
        return getCurrentEditorEventId().equals( propertyEditorEventId );
    }

}