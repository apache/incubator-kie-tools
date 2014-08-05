/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.dsltext.client.editor;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.dsltext.client.resources.i18n.DSLTextEditorConstants;
import org.drools.workbench.screens.dsltext.client.type.DSLResourceType;
import org.drools.workbench.screens.dsltext.model.DSLTextEditorContent;
import org.drools.workbench.screens.dsltext.service.DSLTextEditorService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.callbacks.DefaultErrorCallback;
import org.kie.uberfire.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.uberfire.client.common.MultiPageEditor;
import org.kie.workbench.common.widgets.client.callbacks.CommandBuilder;
import org.kie.workbench.common.widgets.client.callbacks.CommandDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.editor.GuvnorEditor;
import org.kie.workbench.common.widgets.client.editor.GuvnorEditorView;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.popups.validation.DefaultFileNameValidator;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.widget.OverviewWidgetPresenter;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.type.FileNameUtil;

/**
 * DSL Editor Presenter.
 */
@Dependent
@WorkbenchEditor(identifier = "DSLEditor", supportedTypes = {DSLResourceType.class})
public class DSLEditorPresenter
        extends GuvnorEditor {

    @Inject
    private Caller<DSLTextEditorService> dslTextEditorService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private PlaceManager placeManager;

    private DSLEditorView view;

    @Inject
    private OverviewWidgetPresenter overview;

    @Inject
    private MultiPageEditor multiPage;

    @Inject
    private DSLResourceType type;

    @Inject
    private DefaultFileNameValidator fileNameValidator;

    private Metadata metadata;

    @Inject
    public DSLEditorPresenter(DSLEditorView baseView) {
        super(baseView);
        view = baseView;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
            final PlaceRequest place) {
        this.init(path, place);
    }

    protected void loadContent() {
        dslTextEditorService.call(getModelSuccessCallback(),
                new CommandDrivenErrorCallback(view,
                        new CommandBuilder().addNoSuchFileException(view,
                                multiPage,
                                menus).build()
                )).loadContent(versionRecordManager.getCurrentPath());
    }

    private RemoteCallback<DSLTextEditorContent> getModelSuccessCallback() {
        return new RemoteCallback<DSLTextEditorContent>() {

            @Override
            public void callback(final DSLTextEditorContent content) {
                //Path is set to null when the Editor is closed (which can happen before async calls complete).
                if (versionRecordManager.getCurrentPath() == null) {
                    return;
                }

                multiPage.clear();
                
                multiPage.addWidget(overview,
                        CommonConstants.INSTANCE.Overview());
                overview.setContent(content.getOverview(), versionRecordManager.getCurrentPath());

                versionRecordManager.setVersions(content.getOverview().getMetadata().getVersion());

                metadata = content.getOverview().getMetadata();

                multiPage.addWidget(view,
                        DSLTextEditorConstants.INSTANCE.DSL());

                view.setContent(content.getModel());
                view.hideBusyIndicator();
            }
        };
    }

    protected Command onValidate() {
        return new Command() {
            @Override
            public void execute() {
                dslTextEditorService.call(new RemoteCallback<List<ValidationMessage>>() {
                    @Override
                    public void callback(final List<ValidationMessage> results) {
                        if (results == null || results.isEmpty()) {
                            notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemValidatedSuccessfully(),
                                    NotificationEvent.NotificationType.SUCCESS));
                        } else {
                            ValidationPopup.showMessages(results);
                        }
                    }
                }, new DefaultErrorCallback()).validate(versionRecordManager.getCurrentPath(),
                        view.getContent());
            }
        };
    }

    protected void save() {
        new SaveOperationService().save(versionRecordManager.getCurrentPath(),
                new CommandWithCommitMessage() {
                    @Override
                    public void execute(final String commitMessage) {
                        view.showBusyIndicator(CommonConstants.INSTANCE.Saving());
                        dslTextEditorService.call(getSaveSuccessCallback(),
                                new HasBusyIndicatorDefaultErrorCallback(view)).save(versionRecordManager.getCurrentPath(),
                                view.getContent(),
                                metadata,
                                commitMessage);
                    }
                }
        );

        concurrentUpdateSessionInfo = null;
    }

    @IsDirty
    public boolean isDirty() {
        return view.isDirty();
    }

    @OnClose
    public void onClose() {
        this.versionRecordManager.clear();
    }

    @OnMayClose
    public boolean checkIfDirty() {
        if (isDirty()) {
            return view.confirmClose();
        }
        return true;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        String fileName = FileNameUtil.removeExtension(versionRecordManager.getCurrentPath(),
                type);
        if (versionRecordManager.getVersion() != null) {
            fileName = fileName + " v" + versionRecordManager.getVersion();
        }
        return DSLTextEditorConstants.INSTANCE.DslEditorTitle() + " [" + fileName + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
