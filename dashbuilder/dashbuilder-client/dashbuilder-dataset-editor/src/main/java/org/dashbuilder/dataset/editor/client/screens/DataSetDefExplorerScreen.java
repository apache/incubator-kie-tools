/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.editor.client.screens;

import java.util.function.Consumer;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.client.widgets.dataset.event.EditDataSetEvent;
import org.dashbuilder.client.widgets.dataset.event.ErrorEvent;
import org.dashbuilder.client.widgets.dataset.explorer.DataSetExplorer;
import org.dashbuilder.client.widgets.resources.i18n.DataSetExplorerConstants;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataset.service.DataSetDefVfsServices;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * @since 0.3.0
 */
@WorkbenchScreen(identifier = "DataSetDefExplorer")
@Dependent
public class DataSetDefExplorerScreen {

    private Menus menu = null;

    @Inject
    PlaceManager placeManager;

    @Inject
    Caller<DataSetDefVfsServices> services;

    @Inject
    Event<NotificationEvent> notification;

    @Inject
    DataSetExplorer explorerWidget;

    @Inject
    ErrorPopupPresenter errorPopupPresenter;

    @OnStartup
    public void onStartup(PlaceRequest placeRequest) {
        this.menu = makeMenuBar();
        explorerWidget.show();
    }

    @OnClose
    public void onClose() {

    }

    @WorkbenchPartTitle
    public String getTitle() {
        return DataSetExplorerConstants.INSTANCE.title();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return explorerWidget;
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(menu);
    }

    private Menus makeMenuBar() {
        return MenuFactory
                .newTopLevelMenu(DataSetExplorerConstants.INSTANCE.newDataSet())
                .respondsWith(this::newDataSet)
                .endMenu()
                .build();
    }

    void newDataSet() {
        placeManager.tryClosePlace(new DefaultPlaceRequest("DataSetDefWizard"), () -> {
            placeManager.goTo("DataSetDefWizard");
        });
    }

    private void showError(final ClientRuntimeError error) {
        final String message = error.getCause() != null ? error.getCause() : error.getMessage();
        showError(message);
    }

    private void showError(final String message) {
        errorPopupPresenter.showMessage(message);
    }

    void onEditDataSetEvent(@Observes EditDataSetEvent event) {
        checkNotNull("event",
                     event);
        services.call(new RemoteCallback<Path>() {
            public void callback(Path path) {
                placeManager.goTo(new PathPlaceRequest(path));
            }
        }).resolve(event.getDef());
    }

    void onErrorEvent(@Observes ErrorEvent errorEvent) {
        checkNotNull("errorEvent",
                     errorEvent);
        if (errorEvent.getClientRuntimeError() != null) {
            showError(errorEvent.getClientRuntimeError());
        } else if (errorEvent.getMessage() != null) {
            showError(errorEvent.getMessage());
        }
    }
}
