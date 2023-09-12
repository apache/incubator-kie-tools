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



package org.kie.workbench.common.stunner.bpmn.client.components.monaco_editor;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import jsinterop.base.Js;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Element;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.common.client.dom.NodeList;
import org.jboss.errai.common.client.dom.Option;
import org.jboss.errai.common.client.dom.Select;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoEditor;
import org.uberfire.client.views.pfly.monaco.jsinterop.MonacoStandaloneCodeEditor;

@Dependent
@Templated
public class MonacoEditorView implements UberElement<MonacoEditorPresenter> {

    static final String DISPLAY = "display";
    static final String NONE = "none";
    static final String PANEL_TITLE = "panel-title";
    static final String EVENT_NAME = "click";
    static final int DEPTH = 14;

    @Inject
    @DataField
    Div rootContainer;

    @Inject
    @DataField("monacoEditor")
    Div monacoEditor;

    @Inject
    @DataField("loadingEditor")
    Div loadingEditor;

    @Inject
    @DataField("monacoLanguageSelector")
    Select languageSelector;

    MonacoStandaloneCodeEditor editor;
    private MonacoEditorPresenter presenter;
    protected Integer lastWidth = 0;
    protected ResizeObserver resizeObserver = null;

    @Override
    public void init(final MonacoEditorPresenter presenter) {
        this.presenter = presenter;
        this.languageSelector.setTitle(StunnerFormsClientFieldsConstants.CONSTANTS.Language());
    }

    @EventHandler("monacoLanguageSelector")
    void onLanguageChanged(@ForEvent("change") final Event event) {
        presenter.onLanguageChanged(languageSelector.getValue());
    }

    void addLanguage(final String text,
                     final String value) {
        languageSelector.add(createOption(text, value));
    }

    void setLanguage(String lang) {
        languageSelector.setValue(lang);
    }

    void setValue(String value) {
        if (null != editor) {
            editor.setValue(value);
        }
    }

    void setLanguageReadOnly(boolean readOnly) {
        languageSelector.setDisabled(readOnly);
    }

    public String getValue() {
        return editor.getValue();
    }

    public String getLanguage() {
        return languageSelector.getValue();
    }

    public void load(MonacoEditorOptions options,
                     Runnable callback) {
        load(MonacoEditor.get().create(Js.uncheckedCast(this.monacoEditor),
                                       options.toJavaScriptObject()),
             options.getWidthPx(),
             options.getHeightPx());
        callback.run();
    }

    // Workaround for refreshing Monaco editor and get scrollbars visible when the accordion is expanded
    void attachListenerToPanelTitle() {
        final NodeList titleNodes = getParentInDepth(rootContainer, DEPTH).getElementsByClassName(PANEL_TITLE);
        for (int i = 0; i < titleNodes.getLength(); i++) {
            titleNodes.item(i)
                    .addEventListener(EVENT_NAME,
                                      event -> presenter.onLanguageChanged(languageSelector.getValue()),
                                      false);
        }

        if (resizeObserver == null) {
            resizeObserver = new ResizeObserver(event -> onResize());

            if (observeCommand == null) {
                observeCommand = () -> resizeObserver.observe((elemental2.dom.Element) monacoEditor.getParentElement().getParentNode());
            }
            observeCommand.execute();
        }
    }

    protected void onResize() {
        if (lastWidth == monacoEditor.getBoundingClientRect().getWidth().intValue() + 2) { // no point in resizing
            return;
        }

        if (presenter == null) {
            resizeObserver.unobserve((elemental2.dom.Element) monacoEditor.getParentElement().getParentNode());
            resizeObserver = null;
            observeCommand = null;
            return;
        }
        lastWidth = monacoEditor.getBoundingClientRect().getWidth().intValue();
        presenter.setWidthPx(lastWidth - 2);
        presenter.requestRefresh();
        presenter.onLanguageChanged(languageSelector.getValue());
    }

    protected com.google.gwt.user.client.Command observeCommand = null;

    static Element getParentInDepth(final Element element, int depth) {
        if (null != element && depth > 0) {
            Element parent = element.getParentElement();
            return null != parent ?
                    getParentInDepth(parent, --depth)
                    :
                    element;
        }
        return element;
    }

    void load(MonacoStandaloneCodeEditor editor,
              int width,
              int height) {
        this.editor = editor;
        this.editor.onDidBlurEditorWidget(event -> presenter.onValueChanged());
        this.editor.layout(MonacoEditorOptions
                                   .createDimensions(width, height)
                                   .getJavaScriptObject());
    }

    void loadingStarts() {
        monacoEditor.getStyle().setProperty(DISPLAY, NONE);
        loadingEditor.getStyle().removeProperty(DISPLAY);
    }

    void loadingEnds() {
        monacoEditor.getStyle().removeProperty(DISPLAY);
        loadingEditor.getStyle().setProperty(DISPLAY, NONE);
    }

    public void dispose() {
        if (null != editor) {
            editor.dispose();
            clear(monacoEditor);
            editor = null;
        }
    }

    @PreDestroy
    public void destroy() {
        dispose();
        clear(rootContainer);
        presenter = null;
    }

    @Override
    public HTMLElement getElement() {
        return rootContainer;
    }

    private static void clear(Div div) {
        NodeList childNodes = div.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            div.removeChild(node);
        }
    }

    private static Option createOption(final String text,
                                       final String value) {
        final Option option = (Option) Window.getDocument().createElement("option");
        option.setTextContent(text);
        option.setValue(value);
        return option;
    }
}
