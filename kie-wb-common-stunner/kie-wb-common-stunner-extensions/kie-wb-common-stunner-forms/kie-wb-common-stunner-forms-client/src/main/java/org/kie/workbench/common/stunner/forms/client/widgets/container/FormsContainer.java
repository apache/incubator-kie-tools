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

package org.kie.workbench.common.stunner.forms.client.widgets.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.FormDisplayer;
import org.uberfire.backend.vfs.Path;

@Dependent
public class FormsContainer implements IsElement {

    private static Logger LOGGER = Logger.getLogger(FormsContainer.class.getName());

    private final FormsContainerView view;
    private final ManagedInstance<FormDisplayer> displayersInstance;
    private final Map<FormDisplayerKey, FormDisplayer> formDisplayers;

    private FormDisplayer currentDisplayer;

    @Inject
    public FormsContainer(final FormsContainerView view,
                          final @Any ManagedInstance<FormDisplayer> displayersInstance) {
        this.view = view;
        this.displayersInstance = displayersInstance;
        this.formDisplayers = new HashMap<>();
    }

    public void render(final String graphUuid, final Element<? extends Definition<?>> element, final Path diagramPath, final FieldChangeHandler changeHandler) {

        FormDisplayer displayer = getDisplayer(graphUuid, element);

        displayer.render(element, diagramPath, changeHandler);

        if (null != currentDisplayer && !displayer.equals(currentDisplayer)) {
            currentDisplayer.hide();
        }

        displayer.show();
        currentDisplayer = displayer;
    }

    private FormDisplayer getDisplayer(String graphUuid, Element<? extends Definition<?>> element) {
        FormDisplayerKey key = new FormDisplayerKey(graphUuid, element.getUUID());
        FormDisplayer displayer = formDisplayers.get(key);

        LOGGER.fine("Getting form displayer for : " + key);

        if (displayer != null) {
            return displayer;
        }

        LOGGER.fine("Creating new form displayer for : " + key);

        displayer = displayersInstance.get();
        displayer.hide();
        view.addDisplayer(displayer);

        formDisplayers.put(new FormDisplayerKey(graphUuid, element.getUUID()), displayer);

        return displayer;
    }

    public void clearDiagramDisplayers(String graphUuid) {
        LOGGER.fine("Clearing properties forms for graph: " + graphUuid);
        List<FormDisplayerKey> keys = formDisplayers.keySet()
                .stream()
                .filter(entry -> entry.getGraphUuid().equals(graphUuid)).collect(Collectors.toList());

        keys.forEach(this::clearDisplayer);
        LOGGER.fine("Cleared properties forms for graph: " + graphUuid);
    }

    public void clearFormDisplayer(String graphUuid, String elementUid) {
        formDisplayers.keySet()
                .stream()
                .filter(key -> key.getGraphUuid().equals(graphUuid) && key.getElementUid().equals(elementUid))
                .findAny()
                .ifPresent(this::clearDisplayer);
    }

    private void clearDisplayer(FormDisplayerKey key) {
        FormDisplayer displayer = formDisplayers.remove(key);
        LOGGER.fine("Clearing form displayer for element: " + key.getElementUid());
        view.removeDisplayer(displayer);
        displayer.hide();
        if (displayer.equals(currentDisplayer)) {
            currentDisplayer = null;
        }
        displayersInstance.destroy(displayer);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @PreDestroy
    public void destroyAll() {
        view.clear();
        currentDisplayer = null;
        displayersInstance.destroyAll();
        formDisplayers.clear();
        currentDisplayer = null;
    }
}
