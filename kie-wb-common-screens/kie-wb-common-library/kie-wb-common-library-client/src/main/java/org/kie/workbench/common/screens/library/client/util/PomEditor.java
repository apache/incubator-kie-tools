/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.util;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.client.type.POMResourceType;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.GAVAlreadyExistsException;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.defaulteditor.client.editor.KieTextEditorPresenter;
import org.kie.workbench.common.screens.defaulteditor.client.editor.KieTextEditorView;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.projecteditor.model.InvalidPomException;
import org.kie.workbench.common.screens.projecteditor.service.PomEditorService;
import org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.callbacks.CommandWithThrowableDrivenErrorCallback.CommandWithThrowable;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.widgets.common.client.ace.AceEditorMode;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

import static org.guvnor.common.services.project.service.DeploymentMode.FORCED;
import static org.guvnor.common.services.project.service.DeploymentMode.VALIDATED;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.ERROR;

@WorkbenchEditor(
        identifier = PomEditor.EDITOR_ID,
        supportedTypes = {POMResourceType.class},
        priority = 2)
public class PomEditor extends KieTextEditorPresenter {

    public static final String EDITOR_ID = "PomEditor";
    public final Event<NotificationEvent> notificationEvent;
    public final TranslationService translationService;
    private final Caller<PomEditorService> pomEditorService;
    private final ConflictingRepositoriesPopup conflictingRepositoriesPopup;

    @Inject
    public PomEditor(final KieTextEditorView baseView,
                     final Caller<PomEditorService> pomEditorService,
                     final ConflictingRepositoriesPopup conflictingRepositoriesPopup,
                     final Event<NotificationEvent> notificationEvent,
                     final TranslationService translationService) {
        super(baseView);
        this.pomEditorService = pomEditorService;
        this.conflictingRepositoriesPopup = conflictingRepositoriesPopup;
        this.notificationEvent = notificationEvent;
        this.translationService = translationService;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.onStartup(path,
                        place);
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return super.getMenus();
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return super.getWidget();
    }

    @Override
    public AceEditorMode getAceEditorMode() {
        return AceEditorMode.XML;
    }

    @Override
    protected String getEditorIdentifier() {
        return EDITOR_ID;
    }

    @Override
    protected void save(final String commitMessage) {
        doSave(commitMessage, VALIDATED);
    }

    private void doSave(final String commitMessage,
                        final DeploymentMode mode) {

        //Instantiate a new instance on each "save" operation to pass in commit message
        view.showBusyIndicator(CommonConstants.INSTANCE.Saving());

        pomEditorService.call(getSaveSuccessCallback(view.getContent().hashCode()),
                              new CommandWithThrowableDrivenErrorCallback(busyIndicatorView,
                                                                          saveErrorCallbackConfig(commitMessage)))
                .save(versionRecordManager.getCurrentPath(),
                      view.getContent(),
                      metadata,
                      commitMessage,
                      mode);
    }

    private Map<Class<? extends Throwable>, CommandWithThrowable> saveErrorCallbackConfig(final String commitMessage) {
        return new HashMap<Class<? extends Throwable>, CommandWithThrowable>() {{
            put(GAVAlreadyExistsException.class, t -> {
                view.hideBusyIndicator();
                final GAVAlreadyExistsException e = (GAVAlreadyExistsException) t;
                conflictingRepositoriesPopup.setContent(e.getGAV(), e.getRepositories(), () -> {
                    conflictingRepositoriesPopup.hide();
                    doSave(commitMessage, FORCED);
                });
                conflictingRepositoriesPopup.show();
            });
            put(InvalidPomException.class, t -> {
                view.hideBusyIndicator();

                final InvalidPomException e = (InvalidPomException) t;
                final String message = translationService.format(LibraryConstants.InvalidPom, e.getLineNumber(), e.getColumnNumber());
                notificationEvent.fire(new NotificationEvent(message, ERROR));
            });
        }};
    }
}
