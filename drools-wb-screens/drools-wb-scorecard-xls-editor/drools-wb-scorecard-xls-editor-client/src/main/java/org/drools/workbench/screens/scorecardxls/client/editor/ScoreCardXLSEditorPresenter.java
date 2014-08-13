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

package org.drools.workbench.screens.scorecardxls.client.editor;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.scorecardxls.client.resources.i18n.ScoreCardXLSEditorConstants;
import org.drools.workbench.screens.scorecardxls.client.type.ScoreCardXLSResourceType;
import org.drools.workbench.screens.scorecardxls.service.ScoreCardXLSContent;
import org.drools.workbench.screens.scorecardxls.service.ScoreCardXLSService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.callbacks.DefaultErrorCallback;
import org.kie.uberfire.client.common.BusyIndicatorView;
import org.kie.workbench.common.widgets.client.popups.validation.DefaultFileNameValidator;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.type.FileNameUtil;

@Dependent
@WorkbenchEditor(identifier = "ScoreCardXLSEditor", supportedTypes = {ScoreCardXLSResourceType.class})
public class ScoreCardXLSEditorPresenter
        extends KieEditor
        implements ScoreCardXLSEditorView.Presenter {

    @Inject
    private Caller<ScoreCardXLSService> scoreCardXLSService;

    @Inject
    private Caller<MetadataService> metadataService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<ChangeTitleWidgetEvent> changeTitleNotification;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ScoreCardXLSEditorView view;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private ScoreCardXLSResourceType type;

    @Inject
    private DefaultFileNameValidator fileNameValidator;

    @Inject
    public ScoreCardXLSEditorPresenter(ScoreCardXLSEditorView baseView) {
        super(baseView);
        view = baseView;

        view.init(this);
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
            final PlaceRequest place) {
        super.init(path, place);
    }

    protected Command onValidate() {
        return new Command() {
            @Override
            public void execute() {
                scoreCardXLSService.call(new RemoteCallback<List<ValidationMessage>>() {
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
                        versionRecordManager.getCurrentPath());
            }
        };
    }

    @OnClose
    public void onClose() {
        this.versionRecordManager.clear();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        String fileName = FileNameUtil.removeExtension(versionRecordManager.getCurrentPath(),
                type);
        if (versionRecordManager.getVersion() != null) {
            fileName = fileName + " v" + versionRecordManager.getVersion();
        }
        return ScoreCardXLSEditorConstants.INSTANCE.ScoreCardXLEditorTitle() + " [" + fileName + "]";
    }

    @Override
    protected void loadContent() {

        scoreCardXLSService.call(new RemoteCallback<ScoreCardXLSContent>() {
            @Override
            public void callback(ScoreCardXLSContent content) {

                resetEditorPages(content.getOverview());

                view.setPath(versionRecordManager.getCurrentPath());
                view.setReadOnly(isReadOnly);
            }
        }).loadContent(versionRecordManager.getCurrentPath());

    }

    @Override
    protected void save() {

    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
