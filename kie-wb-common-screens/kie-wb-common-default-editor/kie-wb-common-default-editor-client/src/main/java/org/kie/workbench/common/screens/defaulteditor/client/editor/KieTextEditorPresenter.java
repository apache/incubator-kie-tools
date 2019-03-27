/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.defaulteditor.client.editor;

import java.util.function.Supplier;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorContent;
import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorService;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.ext.widgets.core.client.editors.texteditor.TextResourceType;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.category.Others;
import org.uberfire.workbench.model.menu.Menus;

public abstract class KieTextEditorPresenter
        extends KieEditor<String> {

    protected KieTextEditorView view;

    @Inject
    private Others category;

    @Inject
    protected Caller<DefaultEditorService> defaultEditorService;

    @Inject
    protected BusyIndicatorView busyIndicatorView;

    @Inject
    private DefaultFileNameValidator fileNameValidator;

    @Inject
    private PlaceManager placeManager;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Inject
    public KieTextEditorPresenter(final KieTextEditorView baseView) {
        super(baseView);
        view = baseView;
    }

    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        //This causes loadContent() to be called (which for this sub-class loads the Overview not the Text/XML etc)
        super.init(path,
                   place,
                   new TextResourceType(category));

        //This causes the view's content (Text/XML etc) to be loaded, after which we need to get the original HashCode to support "dirty" content
        view.onStartup(path);
        view.setReadOnly(isReadOnly);
    }

    @Override
    protected void makeMenuBar() {
        if (canUpdateProject()) {
            fileMenuBuilder
                    .addSave(versionRecordManager.newSaveMenuItem(this::saveAction))
                    .addCopy(versionRecordManager.getCurrentPath(),
                             getRenameValidator())
                    .addRename(getSaveAndRename())
                    .addDelete(versionRecordManager.getPathToLatest(),
                               assetUpdateValidator);
        }

        addDownloadMenuItem(fileMenuBuilder);

        fileMenuBuilder
                .addNewTopLevelMenu(versionRecordManager.buildMenu())
                .addNewTopLevelMenu(alertsButtonMenuItemBuilder.build());
    }

    @Override
    protected Caller<? extends SupportsSaveAndRename<String, Metadata>> getSaveAndRenameServiceCaller() {
        return defaultEditorService;
    }

    @Override
    protected Supplier<String> getContentSupplier() {
        return view::getContent;
    }

    @Override
    protected void loadContent() {
        defaultEditorService.call(getLoadSuccessCallback(),
                                  getNoSuchFileExceptionErrorCallback()).loadContent(versionRecordManager.getCurrentPath());
    }

    private RemoteCallback<DefaultEditorContent> getLoadSuccessCallback() {
        return new RemoteCallback<DefaultEditorContent>() {
            @Override
            public void callback(final DefaultEditorContent content) {
                resetEditorPages(content.getOverview());
                metadata = content.getOverview().getMetadata();
                setMetadataOriginalHash(metadata.hashCode());

                view.onStartup(versionRecordManager.getCurrentPath());
                view.setReadOnly(isReadOnly);
            }
        };
    }

    //This is called after the View's content has been loaded
    public void onAfterViewLoaded() {
        setOriginalHash(view.getContent().hashCode());
    }

    @Override
    protected void save(String commitMessage) {
        defaultEditorService.call(getSaveSuccessCallback(view.getContent().hashCode()),
                                  new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView)).save(versionRecordManager.getCurrentPath(),
                                                                                                    view.getContent(),
                                                                                                    metadata,
                                                                                                    commitMessage);
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @OnClose
    @Override
    public void onClose() {
        this.versionRecordManager.clear();
        super.onClose();
    }

    @OnMayClose
    public boolean mayClose() {
        return super.mayClose(view.getContent().hashCode());
    }

    /**
     * This allows sub-classes to determine the Mode of the AceEditor.
     * By default the AceEditor assumes the AceEditorMode.TEXT.
     * @return
     */
    public AceEditorMode getAceEditorMode() {
        return AceEditorMode.TEXT;
    }
}