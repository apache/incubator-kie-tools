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

import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.DomGlobal;
import elemental2.dom.Event;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.KeyboardEvent;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jsinterop.base.Js;
import org.gwtproject.core.client.Scheduler;
import org.kie.j2cl.tools.di.core.IsElement;
import org.kie.j2cl.tools.di.ui.templates.client.annotation.DataField;
import org.kie.j2cl.tools.di.ui.templates.client.annotation.EventHandler;
import org.kie.j2cl.tools.di.ui.templates.client.annotation.ForEvent;
import org.kie.j2cl.tools.di.ui.templates.client.annotation.Templated;
import org.kie.j2cl.tools.di.ui.translation.client.TranslationService;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants;
import org.kie.workbench.common.stunner.core.client.canvas.controls.inlineeditor.InlineTextEditorBox;
import org.uberfire.mvp.Command;

@Templated(value = "InlineTextEditorBox.html", stylesheet = "InlineTextEditorBox.css")
@InlineTextEditorBox
@Dependent
public class InlineTextEditorBoxViewImpl
        extends AbstractInlineTextEditorBoxView
        implements InlineEditorBoxView,
        IsElement {

    @Inject
    @DataField
    private HTMLDivElement nameField;

    public static final String CARET_RETURN = "<br>";
    public static final String TEXT_ALIGN_CENTER = "text-align: center;";
    public static final String TEXT_ALIGN_LEFT = "text-align: left;";
    public static final String ALIGN_MIDDLE_STYLE = "margin: 0;" +
            "top: 50%;" +
            TEXT_ALIGN_CENTER +
            "-ms-transform: translateY(-50%);" +
            "transform: translateY(-50%);";
    public static final String ALIGN_LEFT_STYLE = "margin: 0;" +
            "top: 50%;" +
            TEXT_ALIGN_LEFT +
            "-ms-transform: translateY(-50%);" +
            "transform: translateY(-50%);";
    public static final String ALIGN_MIDDLE = "MIDDLE";
    public static final String ALIGN_LEFT = "LEFT";
    public static final String ALIGN_TOP = "TOP";
    public static final String DEFAULT_FONT_FAMILY = "Open Sans";
    public static final double DEFAULT_FONT_SIZE = 16d;

    private String textBoxAlignment;
    private String placeholder;
    private String fontFamily;
    private boolean isMultiline;
    private double fontSize;

    @Inject
    public InlineTextEditorBoxViewImpl(final TranslationService translationService) {
        super();
        this.translationService = translationService;
    }

    public InlineTextEditorBoxViewImpl(final TranslationService translationService,
                                       final HTMLDivElement editNameBox,
                                       final HTMLDivElement nameField,
                                       final Command showCommand,
                                       final Command hideCommand) {
        super(showCommand, hideCommand);
        this.translationService = translationService;
        this.nameField = nameField;
        super.editNameBox = editNameBox;
    }

    @PostConstruct
    @Override
    public void initialize() {
        textBoxAlignment = ALIGN_MIDDLE;
        isMultiline = true;
        placeholder = translationService.getTranslation(StunnerWidgetsConstants.NameEditBoxWidgetViewImp_name);
        fontSize = DEFAULT_FONT_SIZE;
        fontFamily = DEFAULT_FONT_FAMILY;
    }

    @Override
    public void init(Presenter presenter) {
        super.presenter = presenter;
    }

    @Override
    public void setTextBoxInternalAlignment(final String alignment) {
        this.textBoxAlignment = alignment;
    }

    @Override
    public void setMultiline(final boolean isMultiline) {
        this.isMultiline = isMultiline;
    }

    @Override
    public void setPlaceholder(final String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public void setFontSize(final double size) {
        this.fontSize = size;
    }

    @Override
    public void setFontFamily(final String fontFamily) {
        this.fontFamily = fontFamily;
    }

    @Override
    public void show(final String name, final double width, final double height) {
        editNameBox.style.cssText = ("width: " + width + "px;" +
                                                  "height: " + height + "px;");
        nameField.setAttribute("style", buildStyle(width, height));
        presenter.onChangeName(name);
        final CSSStyleDeclaration style = ((HTMLElement) editNameBox.parentElement).style;

        Scheduler.get().scheduleDeferred(() -> {
            style.removeProperty("z-index");
            style.setProperty("z-index", "0");
        });
        presenter.flush();
        nameField.textContent = name;
        nameField.setAttribute("data-text", placeholder);

        setVisible();
        scheduleDeferredCommand(() -> {
            nameField.focus();
            selectText(nameField);
        });
    }

    public void selectText(HTMLDivElement node) {
        DomGlobal.window.getSelection().selectAllChildren(Js.cast(node));
    }

    String buildStyle(final double width, final double height) {
        StringBuilder style = new StringBuilder();

        if (textBoxAlignment.equalsIgnoreCase(ALIGN_MIDDLE)) {
            style.append(ALIGN_MIDDLE_STYLE);
        } else if (textBoxAlignment.equalsIgnoreCase(ALIGN_LEFT)) {
            style.append(ALIGN_LEFT_STYLE);
        } else if (textBoxAlignment.equalsIgnoreCase(ALIGN_TOP)) {
            style.append(TEXT_ALIGN_CENTER);
        }

        style.append("max-width: " + width + "px;" +
                             "max-height: " + height + "px;" +
                             "width: " + width + "px;");
        style.append("font-family: \"" + fontFamily + "\";" +
                             "font-size: " + fontSize + "px;");

        return style.toString();
    }

    @EventHandler("nameField")
    void onChangeName(@ForEvent({"keydown", "keyup", "keypress", "blur"}) Event event) {
        KeyboardEvent e = Js.uncheckedCast(event);
        if (isVisible()) {
            e.stopPropagation();
            if (e.type.equals("blur")) {
                saveChanges();
            } else if (e.type.equals("keydown")) {
                if (e.key.equals("Enter")&& !e.shiftKey) {
                    e.preventDefault();
                    saveChanges();
                } else if ((!isMultiline && e.key.equals("Enter") && e.shiftKey) ||
                        e.key.equals("Tab")) {
                    e.preventDefault();
                } else if (e.key.equals("Escape")) {
                    rollback();
                }
            }
        }
    }

    private void saveChanges() {
        presenter.onChangeName(getTextContent());
        presenter.onSave();
    }

    private String getTextContent() {
        String text = nameField.innerHTML;

        // Handle specific browser caret return <br> (e.g. Firefox)
        if (text.contains(CARET_RETURN)) {
            if (text.endsWith(CARET_RETURN)) {
                text = text.substring(0, text.length() - CARET_RETURN.length());
            }
            return text.replace(CARET_RETURN, "\n");
        }
        return nameField.textContent;
    }

    @Override
    public void rollback() {
        presenter.onClose();
    }
}
