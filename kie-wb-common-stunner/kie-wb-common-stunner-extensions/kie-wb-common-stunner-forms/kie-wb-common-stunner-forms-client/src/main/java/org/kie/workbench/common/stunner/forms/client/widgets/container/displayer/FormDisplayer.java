/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.forms.client.widgets.container.displayer;

import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.BindableProxyFactory;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.adf.DynamicFormModelGenerator;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.StaticModelFormRenderingContext;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.forms.context.PathAwareFormContext;
import org.uberfire.backend.vfs.Path;

@Dependent
public class FormDisplayer implements FormDisplayerView.Presenter,
                                      IsElement {

    private static Logger LOGGER = Logger.getLogger(FormDisplayer.class.getName());

    private final FormDisplayerView view;
    private final DynamicFormRenderer renderer;
    private final DynamicFormModelGenerator modelGenerator;

    private String currentDefinitionId;

    @Inject
    public FormDisplayer(FormDisplayerView view, DynamicFormRenderer renderer, DynamicFormModelGenerator modelGenerator) {
        this.view = view;
        this.renderer = renderer;
        this.modelGenerator = modelGenerator;

        view.init(this);
    }

    public void render(final Element<? extends Definition<?>> element, final Path diagramPath, final FieldChangeHandler changeHandler) {

        final Object definition = element.getContent().getDefinition();

        String definitionId = getDefinitionId(definition);

        LOGGER.fine("Rendering form for element: " + element.getUUID());

        // If currentDefinitionId is empty or definitionId is different we must render the form.
        // if currentDefinitionId & definitionId are the same means that the form is already rendered so no need to render again
        if (null == currentDefinitionId || !definitionId.equals(currentDefinitionId)) {
            doRender(definitionId, definition, diagramPath, changeHandler);
        } else if (!renderer.isValid()) {
            doRender(definitionId, definition, diagramPath, changeHandler);
        }

        show();
    }

    private void doRender(String definitionId, Object definition, Path diagramPath, FieldChangeHandler changeHandler) {
        if (renderer.isInitialized()) {
            LOGGER.fine("Clearing previous form");
            renderer.unBind();
        }

        LOGGER.fine("Rendering a new form for element");

        final BindableProxy<?> proxy = (BindableProxy<?>) BindableProxyFactory.getBindableProxy(definition);
        final StaticModelFormRenderingContext generatedCtx = modelGenerator.getContextForModel(proxy.deepUnwrap());
        final FormRenderingContext<?> pathAwareCtx = new PathAwareFormContext<>(generatedCtx, diagramPath);

        currentDefinitionId = definitionId;

        renderer.render(pathAwareCtx);

        renderer.addFieldChangeHandler(changeHandler);
    }

    protected String getDefinitionId(Object definition) {
        return BindableAdapterUtils.getDefinitionId(definition.getClass());
    }

    public void show() {
        view.show();
    }

    public void hide() {
        view.hide();
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void dispose() {
        renderer.unBind();
    }

    @Override
    public DynamicFormRenderer getRenderer() {
        return renderer;
    }

    @PreDestroy
    public void destroy() {
        dispose();
    }
}
