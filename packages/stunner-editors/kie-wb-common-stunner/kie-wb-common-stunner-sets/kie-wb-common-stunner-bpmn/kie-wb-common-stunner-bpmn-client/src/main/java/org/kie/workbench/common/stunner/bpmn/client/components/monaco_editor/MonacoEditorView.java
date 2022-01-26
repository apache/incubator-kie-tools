/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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


package org.kie.workbench.common.stunner.bpmn.client.components.monaco_editor;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLCollection;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.Node;
import elemental2.dom.NodeList;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
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
    HTMLDivElement rootContainer;

    @Inject
    @DataField("monacoEditor")
    HTMLDivElement monacoEditor;

    @Inject
    @DataField("loadingEditor")
    HTMLDivElement loadingEditor;

    @Inject
    @DataField("monacoLanguageSelector")
    HTMLSelectElement languageSelector;

    MonacoStandaloneCodeEditor editor;
    private MonacoEditorPresenter presenter;
    protected double lastWidth = 0;
    protected ResizeObserver resizeObserver = null;

    @Override
    public void init(final MonacoEditorPresenter presenter) {
        this.presenter = presenter;
        this.languageSelector.title = (StunnerFormsClientFieldsConstants.CONSTANTS.Language());
    }

    @EventHandler("monacoLanguageSelector")
    void onLanguageChanged(@ForEvent("change") final elemental2.dom.Event event) {
        presenter.onLanguageChanged(languageSelector.value);
    }

    void addLanguage(final String text,
                     final String value) {
        languageSelector.add(createOption(text, value));
    }

    void setLanguage(String lang) {
        languageSelector.value = (lang);
    }

    void setValue(String value) {
        if (null != editor) {
            editor.setValue(value);
        }
    }

    void setLanguageReadOnly(boolean readOnly) {
        languageSelector.disabled = (readOnly);
    }

    public String getValue() {
        return editor.getValue();
    }

    public String getLanguage() {
        return languageSelector.value;
    }

    public void load(MonacoEditorOptions options,
                     Runnable callback) {
        load(MonacoEditor.create(monacoEditor,
                                       options.toJavaScriptObject()),
             options.getWidthPx(),
             options.getHeightPx());
        callback.run();
    }

    // Workaround for refreshing Monaco editor and get scrollbars visible when the accordion is expanded
    void attachListenerToPanelTitle() {
        final HTMLCollection<Element> titleNodes = getParentInDepth(rootContainer, DEPTH).getElementsByClassName(PANEL_TITLE);
        for (int i = 0; i < titleNodes.getLength(); i++) {
            (titleNodes.item(i))
                    .addEventListener(EVENT_NAME,
                                      event -> presenter.onLanguageChanged(languageSelector.value),
                                      false);
        }

        if (resizeObserver == null) {
            resizeObserver = new ResizeObserver(event -> onResize());

            if (observeCommand == null) {
                observeCommand = () -> resizeObserver.observe((elemental2.dom.Element) monacoEditor.parentElement.parentNode);
            }
            observeCommand.execute();
        }
    }

    protected void onResize() {
        if (lastWidth == monacoEditor.getBoundingClientRect().width + 2) { // no point in resizing
            return;
        }

        if (presenter == null) {
            resizeObserver.unobserve((elemental2.dom.Element) monacoEditor.parentElement.parentNode);
            resizeObserver = null;
            observeCommand = null;
            return;
        }
        lastWidth = monacoEditor.getBoundingClientRect().width;
        presenter.setWidthPx((int)lastWidth - 2);
        presenter.requestRefresh();
        presenter.onLanguageChanged(languageSelector.value);
    }

    protected org.gwtproject.user.client.Command observeCommand = null;

    static Element getParentInDepth(final Element element, int depth) {
        if (null != element && depth > 0) {
            Element parent = element.parentElement;
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
        this.editor.focus();

        this.editor.onDidBlurEditorWidget(event -> presenter.onValueChanged());
        this.editor.layout(MonacoEditorOptions
                                   .createDimensions(width, height)
                                   .getJavaScriptObject());
    }

    void loadingStarts() {
        monacoEditor.style.setProperty(DISPLAY, NONE);
        loadingEditor.style.removeProperty(DISPLAY);
    }

    void loadingEnds() {
        monacoEditor.style.removeProperty(DISPLAY);
        loadingEditor.style.setProperty(DISPLAY, NONE);
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

    private static void clear(HTMLDivElement div) {
        NodeList childNodes = div.childNodes;
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = (Node)childNodes.item(i);
            div.removeChild(node);
        }
    }

    private static HTMLOptionElement createOption(final String text,
                                                  final String value) {
        final HTMLOptionElement option = (HTMLOptionElement) DomGlobal.document.createElement("option");
        option.textContent = (text);
        option.value = (value);
        return option;
    }
}
