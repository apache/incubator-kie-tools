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


package org.kie.workbench.common.stunner.client.widgets.inlineeditor;

import elemental2.dom.HTMLElement;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.uberfire.mvp.Command;

public abstract class AbstractInlineTextEditorBox implements InlineEditorBoxView.Presenter {

    AbstractCanvasHandler canvasHandler;
    TextPropertyProviderFactory textPropertyProviderFactory;
    CommandManagerProvider<AbstractCanvasHandler> commandManagerProvider;
    Command closeCallback;
    Element<? extends Definition> element;
    String value;

    protected abstract InlineEditorBoxView getView();

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
    public void show(final Element element, final double width, final double height) {
        this.element = element;
        final TextPropertyProvider textPropertyProvider = textPropertyProviderFactory.getProvider(element);
        final String name = textPropertyProvider.getText(element);
        getView().show(name, width, height);
    }

    @Override
    public void setTextBoxInternalAlignment(final String alignment) {
        getView().setTextBoxInternalAlignment(alignment);
    }

    @Override
    public void setMultiline(final boolean isMultiline) {
        getView().setMultiline(isMultiline);
    }

    @Override
    public void setPlaceholder(final String placeholder) {
        getView().setPlaceholder(placeholder);
    }

    @Override
    public void setFontSize(final double size) {
        getView().setFontSize(size);
    }

    @Override
    public void setFontFamily(final String fontFamily) {
        getView().setFontFamily(fontFamily);
    }

    @Override
    public void rollback() {
        getView().rollback();
    }

    @Override
    public void hide() {
        getView().hide();
        value = null;
    }

    public void onChangeName(final String value) {
        this.value = value;
    }

    @Override
    public void onSave() {
        flush();
        getView().hide();
        fireCloseCallback();
    }

    @Override
    public void flush() {
        if (null != value) {
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
}
