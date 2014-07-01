/*
 * Copyright 2014 JBoss Inc
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

package org.drools.workbench.screens.guided.rule.client.editor;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.type.GuidedRuleDRLResourceType;
import org.drools.workbench.screens.guided.rule.client.type.GuidedRuleDSLRResourceType;
import org.drools.workbench.screens.guided.rule.service.GuidedRuleEditorService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.socialscreen.client.OverviewScreenPresenter;
import org.kie.workbench.common.screens.socialscreen.client.OverviewScreenView;
import org.kie.workbench.common.screens.socialscreen.client.discussion.VersionMenuBuilder;
import org.kie.workbench.common.screens.socialscreen.model.Overview;
import org.kie.workbench.common.widgets.client.callbacks.CommandBuilder;
import org.kie.workbench.common.widgets.client.callbacks.CommandDrivenErrorCallback;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.workbench.type.ClientTypeRegistry;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.type.FileNameUtil;

@Dependent
@WorkbenchEditor(identifier = "GuidedRuleEditor", supportedTypes = {GuidedRuleDRLResourceType.class, GuidedRuleDSLRResourceType.class}, priority = 102)
public class GuidedRuleEditorOverviewPresenter
        extends OverviewScreenPresenter
        implements OverviewScreenView.Presenter {

    private final ClientTypeRegistry clientTypeRegistry;

    private final Caller<GuidedRuleEditorService> service;
    private final SaveOperationService saveOperationService;

    @Inject
    private GuidedRuleDRLResourceType resourceTypeDRL;

    @Inject
    private GuidedRuleDSLRResourceType resourceTypeDSL;

    @Inject
    private Event<NotificationEvent> notification;

    private Menus menus;
    private VersionMenuBuilder versionMenuBuilder;
    private Overview<RuleModel> overview;

    @Inject
    public GuidedRuleEditorOverviewPresenter(
            final ClientTypeRegistry clientTypeRegistry,
            final Caller<GuidedRuleEditorService> service,
            final VersionMenuBuilder versionMenuBuilder,
            final SaveOperationService saveOperationService,
            final OverviewScreenView view) {
        super(view);

        view.setPresenter(this);

        this.clientTypeRegistry = clientTypeRegistry;
        this.service = service;
        this.versionMenuBuilder = versionMenuBuilder;
        this.saveOperationService = saveOperationService;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
            final PlaceRequest place) {

        super.onStartup(path, place);

        makeMenuBar();
    }

    @Override
    protected void loadContent() {
        view.showLoadingIndicator();

        service.call(getModelSuccessCallback(),
                new CommandDrivenErrorCallback(view,
                        new CommandBuilder().addNoSuchFileException(
                                view,
                                new Callback<IsWidget>() {
                                    @Override
                                    public void callback(IsWidget result) {
                                        view.showFileNotFound(result);
                                    }
                                }).build()
                )).loadOverview(path);
    }

    private RemoteCallback<Overview<RuleModel>> getModelSuccessCallback() {
        return new RemoteCallback<Overview<RuleModel>>() {

            @Override
            public void callback(final Overview<RuleModel> overview) {
                //Path is set to null when the Editor is closed (which can happen before async calls complete).
                if (path == null) {
                    return;
                }

                GuidedRuleEditorOverviewPresenter.this.overview = overview;

                view.setPreview(overview.getPreview());

                view.setResourceType(clientTypeRegistry.resolve(path));

                view.setProject(overview.getProjectName());

                versionMenuBuilder.setVersions(version, overview.getMetadata().getVersion());

                view.setDescription(overview.getMetadata().getDescription());
                view.setLastModified(overview.getMetadata().getLastContributor(), overview.getMetadata().getLastModified());
                view.setCreated(overview.getMetadata().getCreator(), overview.getMetadata().getDateCreated());

                view.hideBusyIndicator();
            }
        };
    }

    private void makeMenuBar() {
        if (isReadOnly) {
            menus = versionMenuBuilder.buildRestoreMenu(path);
        } else {
            menus = versionMenuBuilder.buildBasic(
                    new Command() {
                        @Override
                        public void execute() {
                            onSave();
                        }
                    });
        }
    }

    private void onSave() {
        saveOperationService.save(path,
                new CommandWithCommitMessage() {
                    @Override
                    public void execute(final String commitMessage) {
                        view.showSavingIndicator();
                        service.call(
                                getSaveSuccessCallback(),
                                new HasBusyIndicatorDefaultErrorCallback(view)).save(
                                path,
                                overview.getModel(),
                                overview.getMetadata(),
                                commitMessage);
                    }
                });
    }

    private RemoteCallback<Object> getSaveSuccessCallback() {
        return new RemoteCallback<Object>() {
            @Override
            public void callback(Object o) {
                view.hideBusyIndicator();
                notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemSavedSuccessfully()));
            }
        };
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        if (resourceTypeDRL.accept(path)) {
            return view.getTitle(
                    FileNameUtil.removeExtension(path, resourceTypeDRL),
                    resourceTypeDRL.getDescription());
        } else if (resourceTypeDSL.accept(path)) {
            return view.getTitle(
                    FileNameUtil.removeExtension(path, resourceTypeDSL),
                    resourceTypeDRL.getDescription());
        } else {
            return path.getFileName();
        }
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    @Override
    public void onDescriptionEdited(String description) {
        overview.getMetadata().setDescription(description);
    }
}
