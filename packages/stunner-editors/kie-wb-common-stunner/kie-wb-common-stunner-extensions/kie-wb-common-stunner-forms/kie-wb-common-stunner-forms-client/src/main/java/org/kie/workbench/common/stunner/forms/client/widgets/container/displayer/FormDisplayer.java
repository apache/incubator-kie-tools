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


package org.kie.workbench.common.stunner.forms.client.widgets.container.displayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.databinding.client.BindableProxyFactory;
import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.dynamic.service.shared.adf.DynamicFormModelGenerator;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.StaticModelFormRenderingContext;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.stunner.forms.client.formFilters.FormFiltersProviderFactory;
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.domainChangeHandlers.DomainObjectFieldChangeHandlerRegistry;
import org.kie.workbench.common.stunner.forms.context.PathAwareFormContext;
import org.uberfire.backend.vfs.Path;

@Dependent
public class FormDisplayer implements FormDisplayerView.Presenter,
                                      IsElement {

    private static Logger LOGGER = Logger.getLogger(FormDisplayer.class.getName());

    private final FormDisplayerView view;
    private final DynamicFormRenderer renderer;
    private final DynamicFormModelGenerator modelGenerator;
    private final DomainObjectFieldChangeHandlerRegistry changeHandlerRegistry;

    @Inject
    public FormDisplayer(final FormDisplayerView view,
                         final DynamicFormRenderer renderer,
                         final DynamicFormModelGenerator modelGenerator,
                         final DomainObjectFieldChangeHandlerRegistry changeHandlerRegistry) {
        this.view = view;
        this.renderer = renderer;
        this.modelGenerator = modelGenerator;
        this.changeHandlerRegistry = changeHandlerRegistry;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void render(final String domainObjectUUID,
                       final Object domainObject,
                       final Path diagramPath,
                       final FieldChangeHandler changeHandler,
                       final RenderMode renderMode) {

        LOGGER.fine("Rendering form for element: " + domainObjectUUID);

        doRender(domainObjectUUID, domainObject, diagramPath, changeHandler, renderMode);

        show();
    }

    private void doRender(final String domainObjectUUID,
                          final Object domainObject,
                          final Path diagramPath,
                          final FieldChangeHandler changeHandler,
                          final RenderMode renderMode) {

        final List<String> previousExpandedCollapses = new ArrayList<>();
        if (renderer.isInitialized()) {
            // Collecting expanded collapses from current form to synchronize the new form collapses
            renderer.getCurrentForm().getFields()
                    .stream()
                    .filter(this::checkCollapsibleGroup)
                    .filter(formField -> ((CollapsibleFormGroup) formField.getContainer()).isExpanded())
                    .map(FormField::getFieldName)
                    .collect(Collectors.toCollection(() -> previousExpandedCollapses));

            LOGGER.fine("Clearing previous form");
            renderer.unBind();
        }

        LOGGER.fine("Rendering a new form for element");

        Collection<FormElementFilter> filters = FormFiltersProviderFactory.getFilterForDefinition(domainObjectUUID, domainObject);

        final BindableProxy<?> proxy = (BindableProxy<?>) BindableProxyFactory.getBindableProxy(domainObject);
        final StaticModelFormRenderingContext generatedCtx = modelGenerator.getContextForModel(proxy.deepUnwrap(), filters.stream().toArray(FormElementFilter[]::new));
        final FormRenderingContext<?> pathAwareCtx = new PathAwareFormContext<>(generatedCtx, diagramPath);
        pathAwareCtx.setRenderMode(renderMode);

        renderer.render(pathAwareCtx);

        syncCollapses(previousExpandedCollapses);

        renderer.addFieldChangeHandler(changeHandler);

        changeHandlerRegistry.lookupChangeHandler(domainObject)
                .ifPresent(domainObjectChangeHandler ->{
                    domainObjectChangeHandler.init(domainObject);
                    renderer.addFieldChangeHandler(domainObjectChangeHandler);
                });
    }

    private void syncCollapses(final List<String> expandedCollapses) {

        final Stream<FormField> formFields = renderer.getCurrentForm().getFields().stream()
                .filter(this::checkCollapsibleGroup);

        if (!expandedCollapses.isEmpty()) {
            // There are expanded collapses from a previous form -> Synchronize collapses
            formFields.filter(formField -> expandedCollapses.contains(formField.getFieldName()))
                    .map(this::toFormGroup)
                    .forEach(CollapsibleFormGroup::expand);
        } else {
            // There's no collapses from a previous form -> expanding first collapse group
            formFields.findFirst()
                    .map(this::toFormGroup)
                    .ifPresent(CollapsibleFormGroup::expand);
        }
    }

    private boolean checkCollapsibleGroup(final FormField formField) {
        return formField.getContainer() instanceof CollapsibleFormGroup;
    }

    private CollapsibleFormGroup toFormGroup(final FormField formField) {
        return (CollapsibleFormGroup) formField.getContainer();
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
