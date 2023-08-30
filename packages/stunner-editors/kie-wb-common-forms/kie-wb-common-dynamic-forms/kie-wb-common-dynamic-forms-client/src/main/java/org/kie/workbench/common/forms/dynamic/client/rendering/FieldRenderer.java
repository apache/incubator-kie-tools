/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.dynamic.client.rendering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.configError.ConfigErrorDisplayer;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeListener;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;

public abstract class FieldRenderer<F extends FieldDefinition, G extends FormGroup> {

    protected FormRenderingContext renderingContext;
    protected String fieldNS;
    protected F field;
    protected FormFieldImpl formField = null;
    private FormGroup formGroup;
    protected List<FieldChangeListener> fieldChangeListeners = new ArrayList<>();
    private Map<String, IsWidget> partsWidgets = new HashMap<>();

    @Inject
    protected ManagedInstance<G> formGroupsInstance;

    @Inject
    protected ConfigErrorDisplayer errorDisplayer;

    @Inject
    protected FormsElementWrapperWidgetUtil wrapperWidgetUtil;

    public void init(FormRenderingContext renderingContext, F field) {
        this.renderingContext = renderingContext;
        this.field = field;
        this.fieldNS = renderingContext.getNamespace() + FormRenderingContext.NAMESPACE_SEPARATOR + field.getName();
        fieldChangeListeners.clear();
    }

    public IsWidget renderWidget() {
        FieldConfigStatus configStatus = checkFieldConfig();

        if (!configStatus.isWellConfigured()) {
            errorDisplayer.render(configStatus.getConfigErrors());

            return errorDisplayer;
        } else {

            formGroup = getFormGroup(renderingContext.getRenderMode());
            
            Map<String, Widget> formPartsWidgets = formGroup.getPartsWidgets();
            partsWidgets.putAll(formPartsWidgets);

            formField = new FormFieldImpl(field,
                                          formGroup) {
                @Override
                protected void doSetReadOnly(boolean readOnly) {
                    if (renderingContext.getRenderMode().equals(RenderMode.PRETTY_MODE)) {
                        return;
                    }
                    final Object model = renderingContext.getModel();
                    if (model instanceof DynamicReadOnly){
                        final DynamicReadOnly.ReadOnly readonlyMode = ((DynamicReadOnly) model).getReadOnly(field.getName());
                        if (readonlyMode == DynamicReadOnly.ReadOnly.TRUE) {
                            readOnly = true;
                        }
                    }
                    FieldRenderer.this.setReadOnly(readOnly);
                }

                @Override
                public boolean isRequired() {
                    return field.getRequired();
                }

                @Override
                public boolean isContentValid() {
                    return FieldRenderer.this.isContentValid();
                }

                @Override
                public Collection<FieldChangeListener> getChangeListeners() {
                    return getFieldChangeListeners();
                }
            };

            formField.setReadOnly(renderingContext.getRenderMode().equals(RenderMode.READ_ONLY_MODE));

            registerCustomFieldValidators(formField);

            return wrapperWidgetUtil.getWidget(this, formGroup.getElement());
        }
    }

    protected abstract FormGroup getFormGroup(RenderMode renderMode);

    protected String generateUniqueId() {
        return Document.get().createUniqueId();
    }

    public FormField getFormField() {
        return formField;
    }

    public F getField() {
        return field;
    }

    protected FieldConfigStatus checkFieldConfig() {
        return new FieldConfigStatus(getConfigErrors());
    }

    protected List<String> getConfigErrors() {
        return null;
    }

    public abstract String getName();

    protected abstract void setReadOnly(boolean readOnly);

    protected boolean isContentValid() {
        return true;
    }

    protected void registerCustomFieldValidators(FormFieldImpl field) {

    }

    protected void registerFieldRendererPart(IsWidget partWidget) {
        String partId = field.getFieldType().getTypeName();
        this.partsWidgets.put(partId, partWidget);
    }

    private Collection<FieldChangeListener> getFieldChangeListeners() {
        return fieldChangeListeners;
    }

    public class FieldConfigStatus {

        protected List<String> configErrors;

        public FieldConfigStatus(List<String> configErrors) {
            this.configErrors = configErrors;
        }

        public List<String> getConfigErrors() {
            return configErrors;
        }

        public boolean isWellConfigured() {
            return configErrors == null || configErrors.isEmpty();
        }
    }

    @PreDestroy
    public void preDestroy() {
        wrapperWidgetUtil.clear(this);
        partsWidgets.values().forEach(part -> part.asWidget().removeFromParent());
        partsWidgets.clear();
        formGroupsInstance.destroyAll();
        fieldChangeListeners.clear();
        formField = null;
        formGroup = null;
        field = null;
    }
    
    final public Set<String> getFieldParts() {
        return partsWidgets.keySet();
    }
    
    public Widget getFieldPartWidget(String partId) {
        IsWidget partWidget = partsWidgets.get(partId);
        if (partWidget != null) {
            return partWidget.asWidget();
        }
        return null;
    }
}
