/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.editor.commons.client.htmleditor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.ext.editor.commons.client.EditorTitle;
import org.uberfire.ext.editor.commons.client.resources.i18n.Constants;
import org.uberfire.ext.widgets.common.client.colorpicker.ColorPickerDialog;
import org.uberfire.ext.widgets.common.client.colorpicker.ColorUtils;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;

@Dependent
@Templated
public class HtmlEditorView implements HtmlEditorPresenter.View,
                                       IsElement {

    public static final int HTML_EDITOR_MARGIN = 14;
    @Inject
    @DataField("container")
    Div container;
    @Inject
    @DataField("html-editor")
    Div htmlEditor;
    @Inject
    @DataField("html-editor-toolbar")
    Div toolbar;
    @Inject
    @DataField("bold-action")
    Button boldAction;
    @Inject
    @DataField("italic-action")
    Button italicAction;
    @Inject
    @DataField("underline-action")
    Button underlineAction;
    @Inject
    @DataField("create-link-action")
    Button createLinkAction;
    @Inject
    @DataField("remove-link-action")
    Button removeLinkAction;
    @Inject
    @DataField("insert-image-action")
    Button insertImageAction;
    @Inject
    @DataField("insert-table-action")
    Button insertTableAction;
    @Inject
    @DataField("big-title-action")
    Button bigTitleAction;
    @Inject
    @DataField("medium-title-action")
    Button mediumTitleAction;
    @Inject
    @DataField("small-title-action")
    Button smallTitleAction;
    @Inject
    @DataField("paragraph-action")
    Button paragraphAction;
    @Inject
    @DataField("pre-action")
    Button preAction;
    @Inject
    @DataField("plain-text-action")
    Button plainTextAction;
    @Inject
    @DataField("quote-action")
    Button quoteAction;
    @Inject
    @DataField("code-action")
    Button codeAction;
    @Inject
    @DataField("font-size-action")
    Button fontSizeAction;
    @Inject
    @DataField("font-size-action-x-small")
    Anchor fontSizeActionXSmall;
    @Inject
    @DataField("font-size-action-small")
    Anchor fontSizeActionSmall;
    @Inject
    @DataField("font-size-action-medium")
    Anchor fontSizeActionMedium;
    @Inject
    @DataField("font-size-action-large")
    Anchor fontSizeActionLarge;
    @Inject
    @DataField("font-size-action-x-large")
    Anchor fontSizeActionXLarge;
    @Inject
    @DataField("font-size-action-xx-large")
    Anchor fontSizeActionXXLarge;
    @Inject
    @DataField("font-color-action")
    Button fontColorAction;
    @Inject
    @DataField("background-color-action")
    Button backgroundColorAction;
    @Inject
    @DataField("unordered-list-action")
    Button unorderedListAction;
    @Inject
    @DataField("ordered-list-action")
    Button orderedListAction;
    @Inject
    @DataField("outdent-list-action")
    Button outdentListAction;
    @Inject
    @DataField("indent-list-action")
    Button indentListAction;
    @Inject
    @DataField("align-left-action")
    Button alignLeftAction;
    @Inject
    @DataField("align-right-action")
    Button alignRightAction;
    @Inject
    @DataField("align-center-action")
    Button alignCenterAction;
    @Inject
    @DataField("undo-action")
    Button undoAction;
    @Inject
    @DataField("redo-action")
    Button redoAction;
    @Inject
    @DataField("switch-to-html-action")
    Button switchToHtmlAction;
    @Inject
    @DataField("background-color-button")
    Button backgroundColorButton;
    @Inject
    @DataField("background-color-input")
    Input backgroundColorInput;
    @Inject
    @DataField("font-color-button")
    Button fontColorButton;
    @Inject
    @DataField("font-color-input")
    Input fontColorInput;

    private TranslationService translationService;
    private HtmlEditorPresenter presenter;
    private HtmlEditorLibraryLoader libraryLoader;
    private EditorTitle title;
    private boolean loaded = false;
    private JavaScriptObject jsEditor;
    private String identifier;

    @Inject
    public HtmlEditorView(final TranslationService translationService,
                          final HtmlEditorLibraryLoader libraryLoader) {
        super();
        this.translationService = translationService;
        this.libraryLoader = libraryLoader;
        this.title = new EditorTitle();
    }

    @Override
    public void init(final HtmlEditorPresenter presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void postConstruct() {
        libraryLoader.ensureLibrariesAreAvailable();
        configureToolbarTitles();
        setUpColorEditor(backgroundColorButton, backgroundColorInput);
        setUpColorEditor(fontColorButton, fontColorInput);
    }

    private void setUpColorEditor(final Button button,
                                  final Input input) {
        button.setOnclick(event -> handleColorSelection(event, button, input));
        input.setOnclick(event -> handleColorSelection(event, button, input));
    }

    private void handleColorSelection(MouseEvent event, Button button, Input input) {
        final ColorPickerDialog dlg = new ColorPickerDialog();
        dlg.getElement().getStyle().setZIndex(9999);
        dlg.addDialogClosedHandler(closedEvent -> {
            if (!closedEvent.isCanceled()) {
                int[] rgb = ColorUtils.getRGB(dlg.getColor());
                String color = "#" + dlg.getColor();
                input.setValue("rgb("+rgb[0]+","+rgb[1]+","+rgb[2]+");");
            }
        });

        dlg.showRelativeTo(ElementWrapperWidget.getWidget(button));
        dlg.getElement().getStyle().setBackgroundColor("white");
    }



    @Override
    public void load() {
        if (!loaded) {

            identifier = String.valueOf(System.currentTimeMillis());
            loaded = true;

            configureScreenComponents(getEditorId(), getToolbarId());
            loadEditor(getEditorId(), getToolbarId());
        }
    }

    private String getToolbarId() {
        return "html-editor-toolbar-" + identifier;
    }

    private String getEditorId() {
        return "html-editor-" + identifier;
    }

    @Override
    public void destroy() {
        destroyEditor();

        loaded = false;
    }

    @Override
    public String getContent() {
        synchronizeView();
        return htmlEditor.getInnerHTML();
    }

    @Override
    public void setContent(final String content) {
        htmlEditor.setInnerHTML(content);
    }

    public final native void synchronizeView() /*-{

        var editor = this.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::jsEditor;

        try {
            if (editor.currentView == "source") {
                editor.fire("change_view", "composer");
            }
        } catch (e) {
            // Ignore.
            // `wysihtml` (0.6.0-beta1) introduced new checks to the `editor.fire`.
            // These asserts don't work as expected in some scenarios. See (RHDM-219).
            // If this library was updated, consider to remove it.
        }
    }-*/;

    protected void configureScreenComponents(final String editorId,
                                             final String toolbarId) {
        htmlEditor.setId(editorId);
        toolbar.setId(toolbarId);
    }

    protected void loadEditor(String editorId,
                              String toolbarId) {
        Scheduler.get().scheduleDeferred(() -> {
            initEditor(editorId,
                       toolbarId);
            onResize();
        });
    }

    protected native void initEditor(String editorId,
                                     String toolbarId) /*-{
        var editor = new $wnd.wysihtml.Editor(editorId, {
            toolbar: $wnd.document.getElementById(toolbarId),
            parserRules: $wnd.wysihtmlParserRules
        });

        this.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::jsEditor = editor;
    }-*/;

    protected native void destroyEditor() /*-{

        var editor = this.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::jsEditor;

        editor.destroy();
    }-*/;

    public void docksInteractionEvent(@Observes UberfireDocksInteractionEvent event) {
        Scheduler.get().scheduleDeferred(() -> onResize());
    }

    private void onResize() {
        final String toolbarHeightCss = getOffsetHeight(toolbar);
        final int toolbarHeight = Integer.parseInt(toolbarHeightCss) + HTML_EDITOR_MARGIN;
        container.getStyle().setProperty("width",
                                         "calc(100% - " + HTML_EDITOR_MARGIN + "px)");
    }

    protected native String getOffsetHeight(HTMLElement el) /*-{
        return el.offsetHeight;
    }-*/;

    @Override
    public void alertReadOnly() {
        final String message = translationService.format(Constants.HtmlEditorView_CantSaveReadOnly);
        Window.alert(message);
    }

    @Override
    public void showLoading() {
        final String message = translationService.format(Constants.HtmlEditorView_Loading);
        showBusyIndicator(message);
    }

    @Override
    public void showSaving() {
        final String message = translationService.format(Constants.HtmlEditorView_Saving);
        showBusyIndicator(message);
    }

    @Override
    public EditorTitle getTitleWidget() {
        return title;
    }

    @Override
    public void refreshTitle(final String value) {
        title.setText(value);
    }

    @Override
    public void showBusyIndicator(String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    @Override
    public boolean confirmClose() {
        final String message = translationService.format(Constants.HtmlEditorView_DiscardUnsavedData);
        return Window.confirm(message);
    }

    // Still necessary, in order to be used as an editor view.
    @Override
    public Widget asWidget() {
        return ElementWrapperWidget.getWidget(getElement());
    }

    private void configureToolbarTitles() {
        boldAction.setTitle(translationService.format(Constants.HtmlEditorView_Bold));
        italicAction.setTitle(translationService.format(Constants.HtmlEditorView_Italic));
        underlineAction.setTitle(translationService.format(Constants.HtmlEditorView_Underline));
        createLinkAction.setTitle(translationService.format(Constants.HtmlEditorView_CreateLink));
        removeLinkAction.setTitle(translationService.format(Constants.HtmlEditorView_RemoveLink));
        insertImageAction.setTitle(translationService.format(Constants.HtmlEditorView_InsertImage));
        insertTableAction.setTitle(translationService.format(Constants.HtmlEditorView_InsertTable));
        bigTitleAction.setTitle(translationService.format(Constants.HtmlEditorView_BigTitle));
        mediumTitleAction.setTitle(translationService.format(Constants.HtmlEditorView_MediumTitle));
        smallTitleAction.setTitle(translationService.format(Constants.HtmlEditorView_SmallTitle));
        paragraphAction.setTitle(translationService.format(Constants.HtmlEditorView_Paragraph));
        preAction.setTitle(translationService.format(Constants.HtmlEditorView_Pre));
        plainTextAction.setTitle(translationService.format(Constants.HtmlEditorView_PlainText));
        quoteAction.setTitle(translationService.format(Constants.HtmlEditorView_Quote));
        codeAction.setTitle(translationService.format(Constants.HtmlEditorView_Code));
        fontSizeAction.setTitle(translationService.format(Constants.HtmlEditorView_FontSize));
        fontColorAction.setTitle(translationService.format(Constants.HtmlEditorView_FontColor));
        backgroundColorAction.setTitle(translationService.format(Constants.HtmlEditorView_BackgroundColor));
        unorderedListAction.setTitle(translationService.format(Constants.HtmlEditorView_UnorderedList));
        orderedListAction.setTitle(translationService.format(Constants.HtmlEditorView_OrderedList));
        outdentListAction.setTitle(translationService.format(Constants.HtmlEditorView_OutdentList));
        indentListAction.setTitle(translationService.format(Constants.HtmlEditorView_IndentList));
        alignLeftAction.setTitle(translationService.format(Constants.HtmlEditorView_AlignLeft));
        alignRightAction.setTitle(translationService.format(Constants.HtmlEditorView_AlignRight));
        alignCenterAction.setTitle(translationService.format(Constants.HtmlEditorView_AlignCenter));
        undoAction.setTitle(translationService.format(Constants.HtmlEditorView_Undo));
        redoAction.setTitle(translationService.format(Constants.HtmlEditorView_Redo));
        switchToHtmlAction.setTitle(translationService.format(Constants.HtmlEditorView_SwitchToHtmlView));
    }
}
