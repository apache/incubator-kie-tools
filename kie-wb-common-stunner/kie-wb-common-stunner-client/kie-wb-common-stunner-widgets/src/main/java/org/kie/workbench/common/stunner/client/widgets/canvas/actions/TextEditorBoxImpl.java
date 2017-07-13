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
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.uberfire.mvp.Command;

@Dependent
public class TextEditorBoxImpl implements TextEditorBoxView.Presenter {

    private final TextEditorBoxView view;

    private AbstractCanvasHandler canvasHandler;
    private TextPropertyProviderFactory textPropertyProviderFactory;
    private CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;
    private Command closeCallback;
    private Element<? extends Definition> element;
    private String value;

    @Inject
    public TextEditorBoxImpl(final TextEditorBoxView view) {
        this.view = view;
        this.element = null;
        this.value = null;
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
        this.textPropertyProviderFactory = canvasHandler.getTextPropertyProviderFactory();
    }

    @Override
    public void setCommandManagerProvider(final CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider) {
        this.commandManagerProvider = commandManagerProvider;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void show(final Element element) {
        this.element = element;
        final TextPropertyProvider textPropertyProvider = textPropertyProviderFactory.getProvider(element);
        final String name = textPropertyProvider.getText(element);
        view.show(name);
    }

    @Override
    public void hide() {
        view.hide();
        this.value = null;
    }

    public void onChangeName(final String name) {
        this.value = name;
    }

    // TODO: Check command result.
    @Override
    public void onSave() {
        if (null != this.value) {
            final TextPropertyProvider textPropertyProvider = textPropertyProviderFactory.getProvider(element);
            textPropertyProvider.setText(canvasHandler,
                                         commandManagerProvider.getCommandManager(),
                                         element,
                                         value);
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
        this.value = value;
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
        return value;
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
