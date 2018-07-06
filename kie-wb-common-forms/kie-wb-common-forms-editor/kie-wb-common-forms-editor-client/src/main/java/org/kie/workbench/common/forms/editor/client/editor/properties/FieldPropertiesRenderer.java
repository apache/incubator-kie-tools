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

package org.kie.workbench.common.forms.editor.client.editor.properties;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.databinding.client.BindableProxy;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.adf.DynamicFormModelGenerator;
import org.kie.workbench.common.forms.editor.client.editor.properties.binding.DataBindingEditor;
import org.kie.workbench.common.forms.editor.client.editor.properties.binding.DynamicFormModel;
import org.kie.workbench.common.forms.editor.client.editor.properties.binding.StaticFormModel;
import org.kie.workbench.common.forms.editor.client.editor.properties.util.DeepCloneHelper;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.model.DynamicModel;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormModel;

@Dependent
public class FieldPropertiesRenderer implements IsWidget {

    public interface FieldPropertiesRendererView extends IsWidget {

        void setPresenter(FieldPropertiesRenderer presenter);

        void render(FieldPropertiesRendererHelper helper,
                    FormEditorRenderingContext renderingContext,
                    DataBindingEditor editor);

        Modal getPropertiesModal();
    }

    private FieldPropertiesRendererView view;

    private DynamicFormModelGenerator dynamicFormModelGenerator;

    private DataBindingEditor staticDataBindingEditor;

    private DataBindingEditor dynamicDataBindingEditor;

    protected FieldDefinition originalField;

    protected FieldDefinition fieldCopy;

    protected FieldPropertiesRendererHelper helper;

    private boolean acceptChanges = false;

    @Inject
    public FieldPropertiesRenderer(FieldPropertiesRendererView view,
                                   DynamicFormModelGenerator dynamicFormModelGenerator,
                                   @StaticFormModel DataBindingEditor staticDataBindingEditor,
                                   @DynamicFormModel DataBindingEditor dynamicDataBindingEditor) {
        this.view = view;
        this.dynamicFormModelGenerator = dynamicFormModelGenerator;
        this.staticDataBindingEditor = staticDataBindingEditor;
        this.dynamicDataBindingEditor = dynamicDataBindingEditor;
    }

    @PostConstruct
    protected void init() {
        view.setPresenter(this);
    }

    public void render(final FieldPropertiesRendererHelper helper) {
        this.helper = helper;
        this.originalField = helper.getCurrentField();
        this.fieldCopy = doCopy(originalField);
        this.acceptChanges = false;
        render();
    }

    protected void render() {
        FormRenderingContext context = dynamicFormModelGenerator.getContextForModel(fieldCopy);
        if (context != null) {
            FormEditorRenderingContext renderingContext = new FormEditorRenderingContext("properties", helper.getPath());
            renderingContext.setRootForm(context.getRootForm());
            renderingContext.getAvailableForms().putAll(context.getAvailableForms());
            renderingContext.setModel(fieldCopy);
            doRender(helper, renderingContext);
        }
    }

    @SuppressWarnings("unchecked")
    public FieldDefinition doCopy(final FieldDefinition originalField) {
        return DeepCloneHelper.deepClone(originalField);
    }

    public void onPressOk() {
        acceptChanges = true;
    }

    public void onClose() {
        if (acceptChanges) {
            doAcceptChanges();
        } else {
            doCancel();
        }
    }

    private void doAcceptChanges() {
        helper.onPressOk(unwrap(fieldCopy));
    }

    private void doCancel() {
        helper.onClose();
    }

    public void onFieldTypeChange(final String typeCode) {
        fieldCopy = helper.onFieldTypeChange(unwrap(fieldCopy), typeCode);

        render();
    }

    private FieldDefinition unwrap(FieldDefinition fieldDefinition) {
        if (fieldDefinition instanceof BindableProxy) {
            return ((BindableProxy<FieldDefinition>) fieldDefinition).deepUnwrap();
        }

        return fieldDefinition;
    }

    public void onFieldBindingChange(final String bindingExpression) {
        fieldCopy = helper.onFieldBindingChange(fieldCopy,
                                                bindingExpression);
        render();
    }

    protected void doRender(FieldPropertiesRendererHelper helper,
                            FormEditorRenderingContext context) {
        FormModel roodFormModel = helper.getCurrentRenderingContext().getRootForm().getModel();
        final DataBindingEditor editor = roodFormModel instanceof DynamicModel ? dynamicDataBindingEditor : staticDataBindingEditor;

        editor.init(fieldCopy,
                    this::getAvailableBindings,
                    this::onFieldBindingChange);

        view.render(helper,
                    context,
                    editor);
    }

    private Collection<String> getAvailableBindings() {
        Collection result = helper.getAvailableModelFields(fieldCopy);

        if (originalField.getBinding() != null && !originalField.getBinding().isEmpty()) {
            result.add(originalField.getBinding());
        }

        return result;
    }

    public FieldPropertiesRendererView getView() {
        return view;
    }

    public FieldDefinition getCurrentField() {
        return fieldCopy;
    }

    public List<String> getCompatibleFieldTypes() {
        return helper.getCompatibleFieldTypes(fieldCopy);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
