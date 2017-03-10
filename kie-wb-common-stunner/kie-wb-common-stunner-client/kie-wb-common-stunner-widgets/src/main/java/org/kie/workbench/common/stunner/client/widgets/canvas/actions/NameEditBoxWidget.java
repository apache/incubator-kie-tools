/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.canvas.actions;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommand;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.mvp.Command;

@Dependent
public class NameEditBoxWidget implements NameEditBoxWidgetView.Presenter {

    private final NameEditBoxWidgetView view;
    private final DefinitionUtils definitionUtils;
    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private AbstractCanvasHandler canvasHandler;
    private CommandManagerProvider<AbstractCanvasHandler> provider;
    private Command closeCallback;
    private Element<? extends Definition> element;
    private String nameValue;

    @Inject
    public NameEditBoxWidget(final DefinitionUtils definitionUtils,
                             final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                             final NameEditBoxWidgetView view) {
        this.definitionUtils = definitionUtils;
        this.canvasCommandFactory = canvasCommandFactory;
        this.view = view;
        this.element = null;
        this.nameValue = null;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    @Override
    public void initialize(final AbstractCanvasHandler canvasHandler,
                           final Command closeCallback) {
        this.canvasHandler = canvasHandler;
        this.closeCallback = closeCallback;
    }

    @Override
    public void setCommandManagerProvider(final CommandManagerProvider<AbstractCanvasHandler> provider) {
        this.provider = provider;
    }

    @Override
    public void show(final Element element) {
        this.element = element;
        final String name = definitionUtils.getName(this.element.getContent().getDefinition());
        view.show(name);
    }

    @Override
    public void hide() {
        view.hide();
        this.nameValue = null;
    }

    public void onChangeName(final String name) {
        this.nameValue = name;
    }

    // TODO: Check command result.
    @Override
    public void onSave() {
        if (null != this.nameValue) {
            final Object def = element.getContent().getDefinition();
            final String nameId = definitionUtils.getNameIdentifier(def);
            if (null != nameId) {
                CanvasCommand<AbstractCanvasHandler> command = canvasCommandFactory.updatePropertyValue(element,
                                                                                                        nameId,
                                                                                                        this.nameValue);
                provider.getCommandManager().execute(canvasHandler,
                                                     command);
            }
        }
        view.hide();
        fireCloseCallback();
    }

    @Override
    public void onKeyPress(final int keyCode,
                           final String value) {
        processKey(keyCode,
                   value);
    }

    @Override
    public void onClose() {
        this.hide();
        fireCloseCallback();
    }

    private void processKey(final int keyCode,
                            final String value) {
        this.nameValue = value;
        // Enter key produces save.
        if (13 == keyCode) {
            onSave();
        }
    }

    private void fireCloseCallback() {
        if (null != closeCallback) {
            closeCallback.execute();
        }
    }

    public String getNameValue() {
        return nameValue;
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
