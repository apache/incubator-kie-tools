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

package org.kie.workbench.common.forms.editor.client.editor.properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.adf.DynamicFormModelGenerator;
import org.kie.workbench.common.forms.editor.client.editor.properties.binding.DataBindingEditor;
import org.kie.workbench.common.forms.editor.client.editor.properties.binding.DynamicFormModel;
import org.kie.workbench.common.forms.editor.client.editor.properties.binding.StaticFormModel;
import org.kie.workbench.common.forms.editor.service.shared.FieldPropertiesService;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.model.DynamicModel;
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

    private Caller<FieldPropertiesService> propertiesService;

    private DynamicFormModelGenerator dynamicFormModelGenerator;

    private DataBindingEditor staticDataBindingEditor;

    private DataBindingEditor dynamicDataBindingEditor;

    @Inject
    public FieldPropertiesRenderer(FieldPropertiesRendererView view,
                                   Caller<FieldPropertiesService> propertiesService,
                                   DynamicFormModelGenerator dynamicFormModelGenerator,
                                   @StaticFormModel DataBindingEditor staticDataBindingEditor,
                                   @DynamicFormModel DataBindingEditor dynamicDataBindingEditor) {
        this.view = view;
        this.propertiesService = propertiesService;
        this.dynamicFormModelGenerator = dynamicFormModelGenerator;
        this.staticDataBindingEditor = staticDataBindingEditor;
        this.dynamicDataBindingEditor = dynamicDataBindingEditor;
    }

    @PostConstruct
    protected void init() {
        view.setPresenter(this);
    }

    public void render(final FieldPropertiesRendererHelper helper) {

        FormRenderingContext context = dynamicFormModelGenerator.getContextForModel(helper.getCurrentField());
        if (context != null) {
            FormEditorRenderingContext renderingContext = new FormEditorRenderingContext(helper.getPath());
            renderingContext.setRootForm(context.getRootForm());
            renderingContext.getAvailableForms().putAll(context.getAvailableForms());
            renderingContext.setModel(helper.getCurrentField());
            doRender(helper,
                     renderingContext);
        } else {
            propertiesService.call(new RemoteCallback<FormEditorRenderingContext>() {
                @Override
                public void callback(FormEditorRenderingContext renderingContext) {
                    renderingContext.setModel(helper.getCurrentField());
                    renderingContext.setParentContext(helper.getCurrentRenderingContext());

                    doRender(helper,
                             renderingContext);
                }
            }).getFieldPropertiesRenderingContext(helper.getCurrentField(),
                                                  helper.getPath());
        }
    }

    protected void doRender(FieldPropertiesRendererHelper helper,
                            FormEditorRenderingContext context) {
        FormModel roodFormModel = helper.getCurrentRenderingContext().getRootForm().getModel();
        DataBindingEditor editor = roodFormModel instanceof DynamicModel ? dynamicDataBindingEditor : staticDataBindingEditor;
        editor.init(helper);
        view.render(helper,
                    context,
                    editor);
    }

    public FieldPropertiesRendererView getView() {
        return view;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
