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

package org.kie.workbench.common.forms.dynamic.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.databinding.client.api.Converter;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.IsFormView;
import org.kie.workbench.common.forms.dynamic.client.init.FormHandlerGeneratorManager;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldLayoutComponent;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.RequiresValueConverter;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.dynamic.service.shared.adf.DynamicFormModelGenerator;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.forms.processing.engine.handling.Form;
import org.kie.workbench.common.forms.processing.engine.handling.FormHandler;
import org.uberfire.mvp.Command;

@Dependent
public class DynamicFormRenderer implements IsWidget,
                                            IsFormView {

    public interface DynamicFormRendererView extends IsWidget {

        void setPresenter(DynamicFormRenderer presenter);

        void render(FormRenderingContext context);

        void bind();

        FieldLayoutComponent getFieldLayoutComponentForField(FieldDefinition field);

        void clear();
    }

    private DynamicFormRendererView view;

    private FormHandler formHandler;

    private FormRenderingContext context;

    private FormHandlerGeneratorManager formHandlerGenerator;

    private DynamicFormModelGenerator dynamicFormModelGenerator;

    @Inject
    public DynamicFormRenderer(DynamicFormRendererView view,
                               FormHandlerGeneratorManager formHandlerGenerator,
                               DynamicFormModelGenerator dynamicFormModelGenerator) {
        this.view = view;
        this.formHandlerGenerator = formHandlerGenerator;
        this.dynamicFormModelGenerator = dynamicFormModelGenerator;
    }

    @PostConstruct
    protected void init() {
        view.setPresenter(this);
    }

    public void renderDefaultForm(final Object model) {
        renderDefaultForm(model,
                          RenderMode.EDIT_MODE,
                          null);
    }

    public void renderDefaultForm(final Object model,
                                  RenderMode renderMode) {
        renderDefaultForm(model,
                          renderMode,
                          null);
    }

    public void renderDefaultForm(final Object model,
                                  final Command callback) {
        renderDefaultForm(model,
                          RenderMode.EDIT_MODE,
                          callback);
    }

    public void renderDefaultForm(final Object model,
                                  final RenderMode renderMode,
                                  final Command callback) {
        PortablePreconditions.checkNotNull("model",
                                           model);
        FormRenderingContext context = dynamicFormModelGenerator.getContextForModel(model);
        if (context != null) {
            doRenderDefaultForm(context,
                                model,
                                renderMode,
                                callback);
        }
    }

    protected void doRenderDefaultForm(FormRenderingContext context,
                                       final Object model,
                                       final RenderMode renderMode,
                                       final Command callback) {
        if (renderMode != null) {
            context.setRenderMode(renderMode);
        }
        if (context.getModel() == null) {
            context.setModel(model);
        }
        render(context);
        if (callback != null) {
            callback.execute();
        }
    }

    public void render(FormRenderingContext context) {
        Assert.notNull("FormRenderingContext must not be null",
                       context);

        unBind();

        this.context = context;

        formHandler = formHandlerGenerator.getFormHandler(context);

        view.render(context);
        if (context.getModel() != null) {
            bind(context.getModel());
        }
    }

    public void bind(Object model) {
        if (context != null && model != null) {
            formHandler.setUp(model);
            context.setModel(model);
            view.bind();
        }
    }

    protected void bind(FieldRenderer renderer) {
        doBind(renderer);
    }

    protected void doBind(FieldRenderer renderer) {
        if (isInitialized() && renderer.getFormField() != null) {
            if (renderer instanceof RequiresValueConverter) {
                Converter valueConverter = ((RequiresValueConverter) renderer).getConverter();
                formHandler.registerInput(renderer.getFormField(),
                                          valueConverter);
            } else {
                formHandler.registerInput(renderer.getFormField());
            }
        }
    }

    public void addFieldChangeHandler(FieldChangeHandler handler) {
        addFieldChangeHandler(null,
                              handler);
    }

    public void addFieldChangeHandler(String fieldName,
                                      FieldChangeHandler handler) {
        if (context != null && isInitialized()) {
            if (fieldName != null) {
                FieldDefinition field = context.getRootForm().getFieldByName(fieldName);
                if (field == null) {
                    throw new IllegalArgumentException("Form doesn't contain any field identified by: '" + fieldName + "'");
                } else {
                    formHandler.addFieldChangeHandler(fieldName,
                                                      handler);
                }
            } else {
                formHandler.addFieldChangeHandler(handler);
            }
        }
    }

    public void unBind() {
        if (isInitialized()) {
            formHandler.clear();
            view.clear();
            formHandler = null;
            context = null;
        }
    }

    public void setModel(Object model) {
        bind(model);
    }

    public Object getModel() {
        if (isInitialized()) {
            return formHandler.getModel();
        }
        return null;
    }

    public void switchToMode(RenderMode renderMode) {
        Assert.notNull("RenderMode cannot be null",
                       renderMode);
        RenderMode currentMode = context.getRenderMode();
        if (context != null && isInitialized() && !currentMode.equals(renderMode)) {

            context.setRenderMode(renderMode);

            if (currentMode.equals(RenderMode.PRETTY_MODE)) {
                render(context);
            } else if (renderMode.equals(RenderMode.PRETTY_MODE)) {
                render(context);
            } else {
                formHandler.setReadOnly(RenderMode.READ_ONLY_MODE.equals(renderMode));
            }
        }
    }

    public boolean isValid() {
        return isInitialized() && formHandler.validate();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public Form getCurrentForm() {
        if (isInitialized()) {
            return formHandler.getForm();
        }

        return null;
    }

    public void flush() {
        if (isInitialized()) {
            formHandler.maybeFlush();
        }
    }

    public boolean isInitialized() {
        return context != null && formHandler != null;
    }
}
