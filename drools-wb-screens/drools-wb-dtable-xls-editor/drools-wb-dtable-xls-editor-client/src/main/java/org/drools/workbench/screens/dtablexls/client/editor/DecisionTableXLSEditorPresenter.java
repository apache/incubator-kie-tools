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

package org.drools.workbench.screens.dtablexls.client.editor;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionMessage;
import org.drools.workbench.models.guided.dtable.shared.conversion.ConversionResult;
import org.drools.workbench.screens.dtablexls.client.resources.i18n.DecisionTableXLSEditorConstants;
import org.drools.workbench.screens.dtablexls.client.type.DecisionTableXLSResourceType;
import org.drools.workbench.screens.dtablexls.client.widgets.ConversionMessageWidget;
import org.drools.workbench.screens.dtablexls.client.widgets.PopupListWidget;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSService;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.callbacks.DefaultErrorCallback;
import org.kie.uberfire.client.common.BusyIndicatorView;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.kie.workbench.common.widgets.client.popups.validation.DefaultFileNameValidator;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
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
@WorkbenchEditor(identifier = "DecisionTableXLSEditor", supportedTypes = {DecisionTableXLSResourceType.class})
public class DecisionTableXLSEditorPresenter
        extends KieEditor
        implements DecisionTableXLSEditorView.Presenter {

    @Inject
    private Caller<DecisionTableXLSService> decisionTableXLSService;

    @Inject
    private Event<NotificationEvent> notification;

    private DecisionTableXLSEditorView view;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private DecisionTableXLSResourceType type;

    public DecisionTableXLSEditorPresenter() {
    }

    @Inject
    public DecisionTableXLSEditorPresenter(DecisionTableXLSEditorView baseView) {
        super(baseView);
        view = baseView;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
            final PlaceRequest place) {
        super.init(path, place);

        resetEditorPages(null);

        view.setPath(path);
        view.setReadOnly(isReadOnly);
    }

    @Override
    protected void save() {

    }

    @Override
    protected void loadContent() {
        decisionTableXLSService.call(
                new RemoteCallback<Overview>() {
                    @Override
                    public void callback(Overview overview) {

                        resetEditorPages(overview);

                    }
                }
        ).loadContent(versionRecordManager.getCurrentPath());

    }

    @Override public void reload() {

    }

    protected void makeMenuBar() {
        menus = menuBuilder
                .addCopy(versionRecordManager.getCurrentPath(),
                        fileNameValidator)
                .addRename(versionRecordManager.getPathToLatest(),
                        fileNameValidator)
                .addDelete(versionRecordManager.getPathToLatest())
                .addValidate(onValidate())
                .addCommand(DecisionTableXLSEditorConstants.INSTANCE.Convert(),
                        new Command() {

                            @Override
                            public void execute() {
                                convert();
                            }
                        }
                )
                .addNewTopLevelMenu(versionRecordManager.buildMenu())
                .build();

    }

    protected Command onValidate() {
        return new Command() {
            @Override
            public void execute() {
                decisionTableXLSService.call(new RemoteCallback<List<ValidationMessage>>() {
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
        return DecisionTableXLSEditorConstants.INSTANCE.DecisionTableEditorTitle() + " [" + fileName + "]";
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    private void convert() {
        busyIndicatorView.showBusyIndicator(DecisionTableXLSEditorConstants.INSTANCE.Converting());
        decisionTableXLSService.call(new RemoteCallback<ConversionResult>() {
            @Override
            public void callback(final ConversionResult response) {
                busyIndicatorView.hideBusyIndicator();
                if (response.getMessages().size() > 0) {
                    final PopupListWidget popup = new PopupListWidget();
                    for (ConversionMessage message : response.getMessages()) {
                        popup.addListItem(new ConversionMessageWidget(message));
                    }
                    popup.show();
                }
            }
        }).convert(versionRecordManager.getCurrentPath());
    }

}
