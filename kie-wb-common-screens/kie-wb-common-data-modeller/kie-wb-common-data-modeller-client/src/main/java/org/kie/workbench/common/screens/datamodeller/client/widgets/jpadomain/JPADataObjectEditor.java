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
import org.kie.workbench.common.screens.datamodeller.client.command.DataModelCommand;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.kie.workbench.common.screens.datamodeller.client.util.AnnotationValueHandler;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.widgets.common.domain.ObjectEditor;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.JPADomainAnnotations;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.uberfire.ext.properties.editor.client.PropertyEditorWidget;
import org.uberfire.ext.properties.editor.client.fields.BooleanField;
import org.uberfire.ext.properties.editor.client.fields.TextField;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

@Dependent
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
    }

    @PostConstruct
    protected void init(){
        propertyEditor.setFilterGroupVisible( false );
        propertyEditor.setLastOpenAccordionGroupTitle( ENTITY_CATEGORY );
        loadPropertyEditor();
        setReadonly( true );
    }

    @Override
    public String getName() {
        return "JPA_OBJECT_EDITOR";
    }

    @Override
    public String getDomainName() {
        return JPADomainEditor.JPA_DOMAIN;
    }

    protected void loadDataObject( DataObject dataObject ) {
        clean();
        setReadonly( true );
        this.dataObject = dataObject;

        if ( dataObject != null ) {
            updateEntityField( dataObject.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_ENTITY_ANNOTATION ) );
            updateTableNameField( dataObject.getAnnotation( JPADomainAnnotations.JAVAX_PERSISTENCE_TABLE_ANNOTATION ) );
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
    public void clean() {
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

        if ( getDataObject() != null ) {

            Boolean doAdd = Boolean.TRUE.toString().equals( newValue );
            DataModelCommand command = commandBuilder.buildDataObjectAddOrRemoveAnnotationCommand( getContext(),
                    getName(), getDataObject(), JPADomainAnnotations.JAVAX_PERSISTENCE_ENTITY_ANNOTATION, doAdd );
            command.execute();
        }
    }

    private void tableNameChanged( String newValue ) {

        if ( getDataObject() != null ) {

            String value = DataModelerUtils.nullTrim( newValue );
            DataModelCommand command = commandBuilder.buildDataObjectAnnotationValueChangeCommand( getContext(),
                    getName(), getDataObject(), JPADomainAnnotations.JAVAX_PERSISTENCE_TABLE_ANNOTATION, "name", value, true );
            command.execute();
        }
    }

    private String getCurrentEditorEventId() {
        //Mechanism to avoid two uberfire property editors opened in different workbench JPA editors receiving crossed events
        return JPA_DATA_OBJECT_EDITOR_EVENT + "-" + this.hashCode();
    }

    private boolean isFromCurrentEditor( String propertyEditorEventId ) {
        return getCurrentEditorEventId().equals( propertyEditorEventId );
    }

}