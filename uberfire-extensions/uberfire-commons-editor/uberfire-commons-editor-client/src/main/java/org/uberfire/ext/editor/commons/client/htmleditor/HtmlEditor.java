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

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.editor.commons.client.BaseEditor;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.ext.editor.commons.service.htmleditor.HtmlEditorService;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.ext.editor.commons.client.menu.MenuItems.COPY;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.DELETE;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.RENAME;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.SAVE;

@Dependent
@WorkbenchEditor(identifier = "HtmlEditor", supportedTypes = HtmlResourceType.class)
public class HtmlEditor extends BaseEditor<String, DefaultMetadata> {

    private HtmlResourceType htmlResourceType;

    private HtmlEditorPresenter editor;

    private Caller<HtmlEditorService> htmlEditorService;

    @Inject
    public HtmlEditor(final HtmlResourceType htmlResourceType,
                      final HtmlEditorPresenter editor,
                      final Caller<HtmlEditorService> htmlEditorService) {
        super(editor.getView());
        this.htmlResourceType = htmlResourceType;
        this.editor = editor;
        this.htmlEditorService = htmlEditorService;
    }

    @PostConstruct
    public void init() {
        editor.load();
    }

    @Override
    protected Supplier<String> getContentSupplier() {
        return editor::getContent;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        init(path,
             place,
             htmlResourceType,
             SAVE,
             COPY,
             RENAME,
             DELETE);
    }

    @Override
    protected void loadContent() {
        baseView.hideBusyIndicator();
        htmlEditorService.call((String htmlContent) -> {
            editor.setContent(htmlContent);
        }).load(versionRecordManager.getCurrentPath());
    }

    @Override
    protected void save() {
        final String htmlContent = editor.getContent();
        htmlEditorService.call(getSaveSuccessCallback(htmlContent.hashCode())).save(versionRecordManager.getCurrentPath(),
                                                                                    htmlContent,
                                                                                    null,
                                                                                    null);
        concurrentUpdateSessionInfo = null;
    }

    @Override
    protected Caller<? extends SupportsDelete> getDeleteServiceCaller() {
        return htmlEditorService;
    }

    @Override
    protected Caller<? extends SupportsSaveAndRename<String, DefaultMetadata>> getSaveAndRenameServiceCaller() {
        return htmlEditorService;
    }

    @Override
    protected Caller<? extends SupportsCopy> getCopyServiceCaller() {
        return htmlEditorService;
    }

    @Override
    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @Override
    @WorkbenchPartTitle
    public String getTitleText() {
        return CommonConstants.INSTANCE.HtmlEditor() + " [" + versionRecordManager.getCurrentPath().getFileName() + "]";
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return editor.getView();
    }
}
