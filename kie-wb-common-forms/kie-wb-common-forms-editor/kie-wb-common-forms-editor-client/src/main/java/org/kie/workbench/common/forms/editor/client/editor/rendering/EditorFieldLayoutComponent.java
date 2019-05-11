/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.forms.editor.client.editor.rendering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Modal;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldLayoutComponent;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.editor.client.editor.FormEditorContext;
import org.kie.workbench.common.forms.editor.client.editor.FormEditorHelper;
import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorSyncPaletteEvent;
import org.kie.workbench.common.forms.editor.client.editor.properties.FieldPropertiesRenderer;
import org.kie.workbench.common.forms.editor.client.editor.properties.FieldPropertiesRendererHelper;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
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

    protected Event<FormEditorSyncPaletteEvent> syncPaletteEvent;

    protected FieldManager fieldManager;

    protected FormEditorContext formEditorContext;

    protected FormEditorHelper editorHelper;

    boolean showProperties = false;

    private FieldPropertiesRendererHelper propertiesRendererHelper;

    private ModalConfigurationContext configContext;

    private Optional<String> fieldId = Optional.empty();

    private Optional<String> formId = Optional.empty();

    @Inject
    public EditorFieldLayoutComponent(FieldPropertiesRenderer propertiesRenderer,
                                      LayoutDragComponentHelper layoutDragComponentHelper,
                                      FieldManager fieldManager,
                                      FormEditorContext formEditorContext,
                                      Event<FormEditorSyncPaletteEvent> syncPaletteEvent) {
        this.propertiesRenderer = propertiesRenderer;
        this.layoutDragComponentHelper = layoutDragComponentHelper;
        this.fieldManager = fieldManager;
        this.formEditorContext = formEditorContext;
        this.syncPaletteEvent = syncPaletteEvent;
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
            public Set<String> getAvailableModelFields(final FieldDefinition fieldDefinition) {
                return new TreeSet<>(editorHelper.getCompatibleModelFields(fieldDefinition));
            }

            @Override
            public List<String> getCompatibleFieldTypes(FieldDefinition fieldDefinition) {
                return editorHelper.getCompatibleFieldTypes(fieldDefinition);
            }

            @Override
            public void onClose() {
                showProperties = false;
                if (configContext != null) {
                    configContext.configurationCancelled();
                }
            }

            @Override
            public void onPressOk(FieldDefinition fieldCopy) {

                EditorFieldLayoutComponent.this.onPressOk(fieldCopy);
            }

            @Override
            public FieldDefinition onFieldTypeChange(FieldDefinition field,
                                                     String newType) {
                FieldDefinition fieldCopy = fieldManager.getFieldFromProvider(newType, field.getFieldTypeInfo());
                fieldCopy.copyFrom(field);
                fieldCopy.setId(field.getId());
                fieldCopy.setName(field.getName());
                return fieldCopy;
            }

            @Override
            public FieldDefinition onFieldBindingChange(FieldDefinition field,
                                                        String newBinding) {
                return editorHelper.switchToField(field,
                                                  newBinding);
            }

            @Override
            public Path getPath() {
                return ((FormEditorRenderingContext) renderingContext).getFormPath();
            }
        };
    }

    protected void onPressOk(FieldDefinition fieldCopy) {

        editorHelper.saveFormField(field, fieldCopy);

        this.field = fieldCopy;
        this.fieldId = Optional.of(fieldCopy.getId());
        initComponent();
        renderContent();
        showProperties = false;
        if (configContext != null) {
            LayoutComponent layoutComponent = configContext.getLayoutComponent();
            addComponentParts(layoutComponent);
            configContext.getComponentProperties().put(FORM_ID,
                                                       getFormId());
            configContext.getComponentProperties().put(FIELD_ID,
                                                       field.getId());
            configContext.configurationFinished();
            configContext = null;
        }
        syncPaletteEvent.fire(new FormEditorSyncPaletteEvent(getFormId()));
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
            initContent(ctx.getComponentProperties());
        } else {
            propertiesRenderer.render(propertiesRendererHelper);
        }

        return propertiesRenderer.getView().getPropertiesModal();
    }

    @Override
    protected IsWidget generateContent(RenderingContext ctx) {
        LayoutComponent component = ctx.getComponent();
        if (fieldRenderer == null) {
            initContent(component.getProperties());
        } else {
            renderContent();
        }
        addComponentParts(component);
        return content;
    }

    protected void initContent(Map<String, String> properties) {

        if (field != null) {
            return;
        }

        if (!fieldId.isPresent()) {
            fieldId = Optional.ofNullable(properties.get(FIELD_ID));
        }

        if (!formId.isPresent()) {
            formId = Optional.ofNullable(properties.get(FORM_ID));
        }

        editorHelper = getHelperInstance();

        init(editorHelper.getRenderingContext(),
             editorHelper.getFormField(fieldId.get()));

        renderContent();

        if (showProperties) {
            propertiesRenderer.render(propertiesRendererHelper);
        }
    }

    protected FormEditorHelper getHelperInstance() {
        return formEditorContext.getActiveEditorHelper();
    }

    FieldPropertiesRendererHelper getPropertiesRendererHelper() {
        return propertiesRendererHelper;
    }
}
