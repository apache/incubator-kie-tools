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
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.ext.editor.commons.client.EditorTitle;
import org.uberfire.ext.editor.commons.client.resources.i18n.Constants;
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
    Anchor boldAction;
    @Inject
    @DataField("italic-action")
    Anchor italicAction;
    @Inject
    @DataField("underline-action")
    Anchor underlineAction;
    @Inject
    @DataField("create-link-action")
    Anchor createLinkAction;
    @Inject
    @DataField("remove-link-action")
    Anchor removeLinkAction;
    @Inject
    @DataField("insert-image-action")
    Anchor insertImageAction;
    @Inject
    @DataField("insert-table-action")
    Anchor insertTableAction;
    @Inject
    @DataField("big-title-action")
    Anchor bigTitleAction;
    @Inject
    @DataField("medium-title-action")
    Anchor mediumTitleAction;
    @Inject
    @DataField("small-title-action")
    Anchor smallTitleAction;
    @Inject
    @DataField("paragraph-action")
    Anchor paragraphAction;
    @Inject
    @DataField("pre-action")
    Anchor preAction;
    @Inject
    @DataField("plain-text-action")
    Anchor plainTextAction;
    @Inject
    @DataField("quote-action")
    Anchor quoteAction;
    @Inject
    @DataField("code-action")
    Anchor codeAction;
    @Inject
    @DataField("font-size-action")
    Anchor fontSizeAction;
    @Inject
    @DataField("font-color-action")
    Anchor fontColorAction;
    @Inject
    @DataField("background-color-action")
    Anchor backgroundColorAction;
    @Inject
    @DataField("unordered-list-action")
    Anchor unorderedListAction;
    @Inject
    @DataField("ordered-list-action")
    Anchor orderedListAction;
    @Inject
    @DataField("outdent-list-action")
    Anchor outdentListAction;
    @Inject
    @DataField("indent-list-action")
    Anchor indentListAction;
    @Inject
    @DataField("align-left-action")
    Anchor alignLeftAction;
    @Inject
    @DataField("align-right-action")
    Anchor alignRightAction;
    @Inject
    @DataField("align-center-action")
    Anchor alignCenterAction;
    @Inject
    @DataField("undo-action")
    Anchor undoAction;
    @Inject
    @DataField("redo-action")
    Anchor redoAction;
    @Inject
    @DataField("switch-to-html-action")
    Anchor switchToHtmlAction;
    private TranslationService translationService;
    private HtmlEditorPresenter presenter;
    private HtmlEditorLibraryLoader libraryLoader;
    private EditorTitle title;
    private boolean loaded = false;
    private JavaScriptObject jsEditor;

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
    }

    @Override
    public void load() {
        if (!loaded) {
            final String identifier = String.valueOf(System.currentTimeMillis());
            final String editorId = "html-editor-" + identifier;
            final String toolbarId = "html-editor-toolbar-" + identifier;

            configureScreenComponents(editorId,
                                      toolbarId);
            loadEditor(editorId,
                       toolbarId);

            loaded = true;
        }
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

        if (editor.currentView == "source") {
            editor.fire("change_view", "composer");
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

    public void docksInteractionEvent(@Observes UberfireDocksInteractionEvent event) {
        Scheduler.get().scheduleDeferred(() -> onResize());
    }

    private void onResize() {
        final String toolbarHeightCss = getOffsetHeight(toolbar);
        final int toolbarHeight = Integer.parseInt(toolbarHeightCss) + HTML_EDITOR_MARGIN;
        container.getStyle().setProperty("width",
                                         "calc(100% - " + HTML_EDITOR_MARGIN + "px)");
        htmlEditor.getStyle().setProperty("height",
                                          "calc(100% - " + toolbarHeight + "px)");
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
