/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.forms.editor.client.editor.rendering;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Modal;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldLayoutComponent;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.editor.client.editor.FormEditorHelper;
import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorContextRequest;
import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorContextResponse;
import org.kie.workbench.common.forms.editor.client.editor.properties.FieldPropertiesRenderer;
import org.kie.workbench.common.forms.editor.client.editor.properties.FieldPropertiesRendererHelper;
import org.kie.workbench.common.forms.editor.service.FormEditorRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.*;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;

@Dependent
public class EditorFieldLayoutComponent extends FieldLayoutComponent implements HasDragAndDropSettings,
        HasModalConfiguration {

    public final String[] SETTINGS_KEYS = new String[] { FORM_ID, FIELD_ID};

    @Inject
    protected FieldPropertiesRenderer propertiesRenderer;

    @Inject
    protected LayoutDragComponentHelper layoutDragComponentHelper;

    boolean showProperties = false;

    @Inject
    protected Event<FormEditorContextRequest> fieldRequest;

    @Inject
    protected Event<ComponentDropEvent> fieldDroppedEvent;

    @Inject
    protected Event<ComponentRemovedEvent> fieldRemovedEvent;

    protected FormEditorHelper editorHelper;

    private FieldPropertiesRendererHelper propertiesRendererHelper;

    private ModalConfigurationContext configContext;

    private String fieldId;

    private String formId;

    @Override
    public void init( FormRenderingContext renderingContext, FieldDefinition field ) {
        super.init( renderingContext, field );
        initPropertiesConfig();
    }

    protected void initPropertiesConfig() {
        propertiesRendererHelper = new FieldPropertiesRendererHelper() {

            @Override
            public FormRenderingContext getCurrentRenderingContext() {
                return renderingContext;
            }

            @Override
            public FieldDefinition getCurrentField() {
                return field;
            }

            @Override
            public List<String> getAvailableFields() {
                return  editorHelper.getCompatibleFieldCodes( field );
            }

            @Override
            public Collection<String> getCompatibleFieldTypes() {
                return editorHelper.getCompatibleFieldTypes( field );
            }

            @Override
            public void onClose() {
                renderContent();
                showProperties = false;
                if ( configContext != null) {
                    configContext.getComponentProperties().put( FORM_ID, formId );
                    configContext.getComponentProperties().put( FIELD_ID, field.getId() );
                    configContext.configurationFinished();
                    configContext = null;
                }
            }

            @Override
            public void onFieldTypeChange( String newType ) {
                switchToFieldType( newType );
            }

            @Override
            public void onFieldBindingChange( String newBinding ) {
                switchToField( newBinding );
            }

            @Override
            public Path getPath() {
                return ((FormEditorRenderingContext)renderingContext).getFormPath();
            }
        };
    }

    @Override
    public String[] getSettingsKeys() {
        return SETTINGS_KEYS;
    }

    @Override
    public void setSettingValue( String key, String value ) {
        if ( FORM_ID.equals( key )) {
            formId = value;
        } else if (FIELD_ID.equals( key )) {
            fieldId = value;
        }
    }

    @Override
    public String getSettingValue( String key ) {
        if ( FORM_ID.equals( key )) {
            if ( renderingContext != null ) {
                return renderingContext.getRootForm().getId();
            }
            return formId;
        }
        else if (FIELD_ID.equals( key )) {
            if ( field != null ) {
                return field.getId();
            }
            return fieldId;
        }
        return null;
    }

    @Override
    public Map<String, String> getMapSettings() {
        Map<String, String> settings = new HashMap<>();
        settings.put( FORM_ID, getSettingValue( FORM_ID ) );
        settings.put( FIELD_ID, getSettingValue( FIELD_ID ) );
        return settings;
    }

    @Override
    public Modal getConfigurationModal( final ModalConfigurationContext ctx ) {

        showProperties = true;

        configContext = ctx;

        if (field == null) {
            getEditionContext( ctx.getComponentProperties() );
        } else {
            propertiesRenderer.render( propertiesRendererHelper );

        }

        return propertiesRenderer.getView().getPropertiesModal();
    }

    @Override
    protected IsWidget generateContent( RenderingContext ctx ) {
        if ( fieldRenderer != null) {
            renderContent();
        } else {
            getEditionContext( ctx.getComponent().getProperties() );
        }
        return content;
    }

    protected void getEditionContext( Map<String, String> properties ) {
        if (field != null) return;

        if (fieldId == null) {
            fieldId = properties.get(FIELD_ID);
        }

        if (formId == null) {
            formId = properties.get( FORM_ID );
        }

        fieldRequest.fire( new FormEditorContextRequest( formId, fieldId ) );
    }

    public void onFieldResponse(@Observes FormEditorContextResponse response) {
        if ( !response.getFormId().equals( formId ) ) {
            return;
        } else if ( field != null && !response.getFieldId().equals( fieldId )) {
            return;
        }

        editorHelper = response.getEditorHelper();

        init( editorHelper.getRenderingContext(), editorHelper.getFormField( response.getFieldId() ) );

        renderContent();

        if ( showProperties ) {
            propertiesRenderer.render( propertiesRendererHelper );
        }
    }

    public List<String> getCompatibleFields() {
        return editorHelper.getCompatibleFieldCodes( field );
    }

    public Collection<String> getCompatibleFieldTypes() {
        return editorHelper.getCompatibleFieldTypes( field );
    }

    public void switchToField(String bindingExpression) {
        if (field.getBinding().equals( bindingExpression )) return;

        FieldDefinition destField = editorHelper.switchToField( field, bindingExpression );

        if ( destField == null ) return;

        LayoutComponent component = layoutDragComponentHelper.getLayoutComponent( this );

        fieldRemovedEvent.fire( new ComponentRemovedEvent( component ) );

        fieldId = destField.getId();
        field = destField;

        component = layoutDragComponentHelper.getLayoutComponent( this );
        fieldDroppedEvent.fire( new ComponentDropEvent( component ) );

        if ( showProperties ) {
            propertiesRenderer.render( propertiesRendererHelper );
        }

        fieldRenderer.init( renderingContext, field );

        renderContent();
    }

    public void switchToFieldType( String typeCode ) {
        if ( field.getCode().equals(typeCode) ) return;

        field = editorHelper.switchToFieldType( field, typeCode);

        initComponent();

        if ( showProperties ) {
            propertiesRenderer.render( propertiesRendererHelper );
        }

        renderContent();
    }
}
