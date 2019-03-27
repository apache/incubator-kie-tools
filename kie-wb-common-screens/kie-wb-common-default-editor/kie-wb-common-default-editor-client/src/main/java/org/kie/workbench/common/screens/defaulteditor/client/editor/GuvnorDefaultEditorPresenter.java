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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorContent;
import org.kie.workbench.common.screens.defaulteditor.service.DefaultEditorService;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.workbench.type.AnyResourceType;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.category.Others;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

/**
 * A text based editor for Domain Specific Language definitions
 */
@Dependent
@WorkbenchEditor(identifier = GuvnorDefaultEditorPresenter.EDITOR_ID, supportedTypes = {AnyResourceType.class}, priority = -1)
public class GuvnorDefaultEditorPresenter
        extends KieEditor<String> {

    public static final String EDITOR_ID = "GuvnorDefaultFileEditor";

    private final GuvnorDefaultEditorView view;

    @Inject
    private Caller<DefaultEditorService> defaultEditorService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private DefaultFileNameValidator fileNameValidator;

    @Inject
    private Others category;

    @Inject
    public GuvnorDefaultEditorPresenter(final GuvnorDefaultEditorView baseView) {
        super(baseView);
        view = baseView;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.init(path,
                   place,
                   new AnyResourceType(category));
        view.onStartup(path);
    }

    @Override
    protected void makeMenuBar() {
        if (canUpdateProject()) {
            fileMenuBuilder
                    .addCopy(versionRecordManager.getCurrentPath(),
                             assetUpdateValidator)
                    .addRename(versionRecordManager.getCurrentPath(),
                               assetUpdateValidator)
                    .addDelete(versionRecordManager.getCurrentPath(),
                               assetUpdateValidator);
        }

        addDownloadMenuItem(fileMenuBuilder);

        fileMenuBuilder
                .addNewTopLevelMenu(versionRecordManager.buildMenu())
                .addNewTopLevelMenu(alertsButtonMenuItemBuilder.build());
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @Override
    protected String getEditorIdentifier() {
        return EDITOR_ID;
    }

    @OnClose
    @Override
    public void onClose() {
        versionRecordManager.clear();
        super.onClose();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @Override
    protected void loadContent() {
        view.showLoading();
        defaultEditorService.call(getLoadSuccessCallback(),
                                  getNoSuchFileExceptionErrorCallback()).loadContent(versionRecordManager.getCurrentPath());
    }

    private RemoteCallback<DefaultEditorContent> getLoadSuccessCallback() {
        return new RemoteCallback<DefaultEditorContent>() {
            @Override
            public void callback(final DefaultEditorContent content) {
                resetEditorPages(content.getOverview());
                view.hideBusyIndicator();
            }
        };
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }
}
