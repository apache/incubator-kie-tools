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
import java.util.Optional;
import java.util.function.Consumer;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Specializes;
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
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.ComponentDropEvent;
import org.uberfire.ext.layout.editor.client.api.ComponentRemovedEvent;
import org.uberfire.ext.layout.editor.client.api.HasDragAndDropSettings;
import org.uberfire.ext.layout.editor.client.api.HasModalConfiguration;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;

@Specializes
@Dependent
public class EditorFieldLayoutComponent extends FieldLayoutComponent implements HasDragAndDropSettings,
                                                                                HasModalConfiguration {

    public final String[] SETTINGS_KEYS = new String[]{FORM_ID, FIELD_ID};

    protected FieldPropertiesRenderer propertiesRenderer;

    protected LayoutDragComponentHelper layoutDragComponentHelper;

    protected Event<FormEditorContextRequest> fieldRequest;

    protected Event<ComponentDropEvent> fieldDroppedEvent;

    protected Event<ComponentRemovedEvent> fieldRemovedEvent;

    protected FormEditorHelper editorHelper;

    boolean showProperties = false;

    private FieldPropertiesRendererHelper propertiesRendererHelper;

    private ModalConfigurationContext configContext;

    private Optional<String> fieldId = Optional.empty();

    private Optional<String> formId = Optional.empty();

    private boolean disabled = false;

    @Inject
    public EditorFieldLayoutComponent(FieldPropertiesRenderer propertiesRenderer,
                                      LayoutDragComponentHelper layoutDragComponentHelper,
                                      Event<FormEditorContextRequest> fieldRequest,
                                      Event<ComponentDropEvent> fieldDroppedEvent,
                                      Event<ComponentRemovedEvent> fieldRemovedEvent) {
        this.propertiesRenderer = propertiesRenderer;
        this.layoutDragComponentHelper = layoutDragComponentHelper;
        this.fieldRequest = fieldRequest;
        this.fieldDroppedEvent = fieldDroppedEvent;
        this.fieldRemovedEvent = fieldRemovedEvent;
    }

    @Override
    public void init(FormRenderingContext renderingContext,
                     FieldDefinition field) {
        super.init(renderingContext,
                   field);
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
            public List<String> getAvailableModelFields() {
                return editorHelper.getCompatibleModelFields(field);
            }

            @Override
            public Collection<String> getCompatibleFieldTypes() {
                return editorHelper.getCompatibleFieldTypes(field);
            }

            @Override
            public void onClose() {
                renderContent();
                showProperties = false;
                if (configContext != null) {
                    formId.ifPresent(formId -> configContext.getComponentProperties().put(FORM_ID,
                                                                             formId));
                    configContext.getComponentProperties().put(FIELD_ID,
                                                               field.getId());
                    configContext.configurationFinished();
                    configContext = null;
                }
            }

            @Override
            public void onFieldTypeChange(String newType) {
                switchToFieldType(newType);
            }

            @Override
            public void onFieldBindingChange(String newBinding) {
                switchToField(newBinding);
            }

            @Override
            public Path getPath() {
                return ((FormEditorRenderingContext) renderingContext).getFormPath();
            }
        };
    }

    @Override
    public String[] getSettingsKeys() {
        return SETTINGS_KEYS;
    }

    @Override
    public void setSettingValue(String key,
                                String value) {
        if (FORM_ID.equals(key)) {
            formId = Optional.of(value);
        } else if (FIELD_ID.equals(key)) {
            fieldId = Optional.of(value);
        }
    }

    @Override
    public String getSettingValue(String key) {
        if (FORM_ID.equals(key)) {
            if (renderingContext != null) {
                return renderingContext.getRootForm().getId();
            }
            if (formId.isPresent()) {
                return formId.get();
            }
            return formId.isPresent() ? formId.get() : "";
        } else if (FIELD_ID.equals(key)) {
            if (field != null) {
                return field.getId();
            }
            if (fieldId.isPresent()) {
                return fieldId.get();
            }
        }
        return null;
    }

    @Override
    public Map<String, String> getMapSettings() {
        Map<String, String> settings = new HashMap<>();
        settings.put(FORM_ID,
                     getSettingValue(FORM_ID));
        settings.put(FIELD_ID,
                     getSettingValue(FIELD_ID));
        return settings;
    }

    @Override
    public Modal getConfigurationModal(final ModalConfigurationContext ctx) {

        showProperties = true;

        configContext = ctx;

        if (field == null) {
            getEditionContext(ctx.getComponentProperties());
        } else {
            propertiesRenderer.render(propertiesRendererHelper);
        }

        return propertiesRenderer.getView().getPropertiesModal();
    }

    @Override
    protected IsWidget generateContent(RenderingContext ctx) {
        if (fieldRenderer != null) {
            renderContent();
        } else {
            getEditionContext(ctx.getComponent().getProperties());
        }
        return content;
    }

    protected void getEditionContext(Map<String, String> properties) {
        if (field != null) {
            return;
        }

        if (!fieldId.isPresent()) {
            fieldId = Optional.of(properties.get(FIELD_ID));
        }

        if (!formId.isPresent()) {
            formId = Optional.of(properties.get(FORM_ID));
        }

        fieldRequest.fire(new FormEditorContextRequest(formId.get(),
                                                       fieldId.get()));
    }

    public void onFieldResponse(@Observes FormEditorContextResponse response) {
        if(disabled) {
            return;
        } else if (!formId.filter(s -> response.getFormId().equals(s)).isPresent()) {
            return;
        } else if (field != null && !fieldId.filter(s->response.getFieldId().equals(s)).isPresent()) {
            return;
        }

        editorHelper = response.getEditorHelper();

        init(editorHelper.getRenderingContext(),
             editorHelper.getFormField(response.getFieldId()));

        renderContent();

        if (showProperties) {
            propertiesRenderer.render(propertiesRendererHelper);
        }
    }

    public void switchToField(String bindingExpression) {
        if (bindingExpression.equals(field.getBinding())) {
            return;
        }

        FieldDefinition destField = editorHelper.switchToField(field,
                                                               bindingExpression);

        if (destField == null) {
            return;
        }

        LayoutComponent component = layoutDragComponentHelper.getLayoutComponent(this);

        fieldRemovedEvent.fire(new ComponentRemovedEvent(component));

        fieldId = Optional.of(destField.getId());
        field = destField;

        component = layoutDragComponentHelper.getLayoutComponent(this);
        fieldDroppedEvent.fire(new ComponentDropEvent(component));

        if (showProperties) {
            propertiesRenderer.render(propertiesRendererHelper);
        }

        fieldRenderer.init(renderingContext,
                           field);

        renderContent();
    }

    public void switchToFieldType(String typeCode) {
        if (field.getFieldType().getTypeName().equals(typeCode)) {
            return;
        }

        field = editorHelper.switchToFieldType(field,
                                               typeCode);

        initComponent();

        if (showProperties) {
            propertiesRenderer.render(propertiesRendererHelper);
        }

        renderContent();
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
