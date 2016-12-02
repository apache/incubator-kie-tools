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

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.datamodeller.client.model.DataModelerPropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.client.PropertyEditorWidget;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorChangeEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

@Dependent
public class JPADataObjectFieldEditorViewImpl
        extends Composite
        implements JPADataObjectFieldEditorView {


    interface JPADataObjectFieldEditorUIBinder
            extends UiBinder<Widget, JPADataObjectFieldEditorViewImpl> {

    }

    private static final String JPA_DATA_OBJECT_FIELD_EDITOR_EVENT = "JPA_DATA_OBJECT_FIELD_EDITOR_EVENT";

    private static JPADataObjectFieldEditorUIBinder uiBinder = GWT.create( JPADataObjectFieldEditorUIBinder.class );

    @UiField
    PropertyEditorWidget propertyEditor;

    private Presenter presenter;

    public JPADataObjectFieldEditorViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @PostConstruct
    protected void init(){
        propertyEditor.setFilterGroupVisible( false );
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void loadPropertyEditorCategories( List<PropertyEditorCategory> categories ) {
        propertyEditor.handle( new PropertyEditorEvent( getCurrentEditorEventId(), categories ) );
    }

    private void onPropertyEditorChange( @Observes PropertyEditorChangeEvent event ) {
        PropertyEditorFieldInfo property = event.getProperty();

        if ( isFromCurrentEditor( property.getEventId() ) ) {

            DataModelerPropertyEditorFieldInfo fieldInfo = ( DataModelerPropertyEditorFieldInfo ) event.getProperty();

            if ( IDENTIFIER_FIELD.equals( fieldInfo.getKey() ) ) {
                presenter.onIdentifierFieldChange( fieldInfo, event.getNewValue() );
            } else if ( COLUMN_NAME_FIELD.equals( fieldInfo.getKey() ) ||
                    COLUMN_UNIQUE_FIELD.equals( fieldInfo.getKey() ) ||
                    COLUMN_NULLABLE_FIELD.equals( fieldInfo.getKey() ) ||
                    COLUMN_INSERTABLE_FIELD.equals( fieldInfo.getKey() ) ||
                    COLUMN_UPDATABLE_FIELD.equals( fieldInfo.getKey() ) ) {
                presenter.onColumnFieldChange( fieldInfo, event.getNewValue() );
            } else if ( GENERATED_VALUE_FIELD.equals( fieldInfo.getKey() ) ) {
                presenter.onGeneratedValueFieldChange( fieldInfo, event.getNewValue() );
            } else if ( SEQUENCE_GENERATOR_FIELD.equals( fieldInfo.getKey() ) ) {
                presenter.onSequenceGeneratorFieldChange( fieldInfo, event.getNewValue()  );
            } else if ( RELATIONSHIP_TYPE_FIELD.equals( fieldInfo.getKey() ) ) {
                presenter.onRelationTypeFieldChange( fieldInfo, event.getNewValue() );
            }
        }
    }

    private String getCurrentEditorEventId() {
        return JPA_DATA_OBJECT_FIELD_EDITOR_EVENT + "-" + this.hashCode();
    }

    private boolean isFromCurrentEditor( String propertyEditorEventId ) {
        return getCurrentEditorEventId().equals( propertyEditorEventId );
    }
}