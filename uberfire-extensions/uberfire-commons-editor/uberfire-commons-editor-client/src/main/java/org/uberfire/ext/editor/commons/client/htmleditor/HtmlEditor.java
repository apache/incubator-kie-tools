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
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.editor.commons.client.BaseEditor;
import org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.ext.editor.commons.client.menu.MenuItems.COPY;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.DELETE;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.RENAME;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.SAVE;

@Dependent
@WorkbenchEditor(identifier = "HtmlEditor", supportedTypes = HtmlResourceType.class)
public class HtmlEditor extends BaseEditor {

    private HtmlResourceType htmlResourceType;

    private HtmlEditorPresenter editor;

    private Caller<VFSService> vfsServices;

    private Caller<DeleteService> deleteService;

    private Caller<RenameService> renameService;

    private Caller<CopyService> copyService;

    @Inject
    public HtmlEditor(final HtmlResourceType htmlResourceType,
                      final HtmlEditorPresenter editor,
                      final Caller<VFSService> vfsServices,
                      final Caller<DeleteService> deleteService,
                      final Caller<RenameService> renameService,
                      final Caller<CopyService> copyService) {
        super(editor.getView());
        this.htmlResourceType = htmlResourceType;
        this.editor = editor;
        this.vfsServices = vfsServices;
        this.deleteService = deleteService;
        this.renameService = renameService;
        this.copyService = copyService;
    }

    @PostConstruct
    public void init() {
        editor.load();
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
        vfsServices.call(new RemoteCallback<String>() {
            @Override
            public void callback(final String htmlContent) {
                editor.setContent(htmlContent);
            }
        }).readAllString(versionRecordManager.getCurrentPath());
    }

    @Override
    protected void save() {
        final String htmlContent = editor.getContent();
        vfsServices.call(getSaveSuccessCallback(htmlContent.hashCode())).write(versionRecordManager.getCurrentPath(),
                                                                               htmlContent);
        concurrentUpdateSessionInfo = null;
    }

    @Override
    protected Caller<? extends SupportsDelete> getDeleteServiceCaller() {
        return deleteService;
    }

    @Override
    protected Caller<? extends SupportsRename> getRenameServiceCaller() {
        return renameService;
    }

    @Override
    protected Caller<? extends SupportsCopy> getCopyServiceCaller() {
        return copyService;
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
    public Menus getMenus() {
        return menus;
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return editor.getView();
    }
}
