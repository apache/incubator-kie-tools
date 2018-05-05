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

package org.kie.workbench.common.stunner.client.widgets.canvas.actions;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.google.gwt.event.dom.client.KeyCodes;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.uberfire.mvp.Command;

public abstract class AbstractTextEditorBox implements TextEditorBoxView.Presenter {

    private AbstractCanvasHandler canvasHandler;
    private TextPropertyProviderFactory textPropertyProviderFactory;
    private CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;
    private Command closeCallback;
    private Element<? extends Definition> element;
    private String value;

    protected abstract TextEditorBoxView getView();

    @PostConstruct
    public void setup() {
        getView().init(this);
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
    public boolean isVisible() {
        return getView().isVisible();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void show(final Element element) {
        this.element = element;
        final TextPropertyProvider textPropertyProvider = textPropertyProviderFactory.getProvider(element);
        final String name = textPropertyProvider.getText(element);
        getView().show(name);
    }

    @Override
    public void hide() {
        getView().hide();
        this.value = null;
    }

    public void onChangeName(final String name) {
        this.value = name;
    }

    // TODO: Check command result.
    @Override
    public void onSave() {
        flush();
        getView().hide();
        fireCloseCallback();
    }

    @Override
    public void flush() {
        if (null != this.value) {
            final TextPropertyProvider textPropertyProvider = textPropertyProviderFactory.getProvider(element);
            textPropertyProvider.setText(canvasHandler,
                                         commandManagerProvider.getCommandManager(),
                                         element,
                                         value);
        }
    }

    @Override
    public void onClose() {
        this.hide();
        fireCloseCallback();
    }

    @PreDestroy
    public void destroy() {
        canvasHandler = null;
        textPropertyProviderFactory = null;
        commandManagerProvider = null;
        closeCallback = null;
        element = null;
        value = null;
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
        return getView().getElement();
    }

    @Override
    public void onKeyPress(final int keyCode,
                           final boolean shiftKeyPressed,
                           final String value) {
        processKey(keyCode,
                   shiftKeyPressed,
                   value);
    }

    private void processKey(final int keyCode,
                            boolean shiftKeyPressed,
                            final String value) {
        this.value = value;
        // Enter key produces save.
        if ((KeyCodes.KEY_ENTER == keyCode) && (!shiftKeyPressed)) {
            onSave();
        }
    }

    @Override
    public void onKeyDown(final int keyCode,
                          final String value) {
        this.value = value;
        // Tab key produces save.
        if (KeyCodes.KEY_TAB == keyCode) {
            onSave();
        }
    }
}
