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

package org.uberfire.ext.preferences.client.admin;

import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.client.admin.category.AdminPageCategoryPresenter;
import org.uberfire.ext.preferences.client.admin.page.AdminPage;
import org.uberfire.ext.preferences.client.admin.page.AdminTool;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

@WorkbenchScreen(identifier = AdminPagePresenter.IDENTIFIER)
public class AdminPagePresenter {

    public static final String IDENTIFIER = "AdminPagePresenter";
    private final View view;
    private final AdminPage adminPage;
    private final ManagedInstance<AdminPageCategoryPresenter> categoryPresenterProvider;
    private final Event<NotificationEvent> notification;
    private String screen;
    private String perspectiveIdentifierToGoBackTo;

    @Inject
    public AdminPagePresenter(final View view,
                              final AdminPage adminPage,
                              final ManagedInstance<AdminPageCategoryPresenter> categoryPresenterProvider,
                              final Event<NotificationEvent> notification) {
        this.view = view;
        this.adminPage = adminPage;
        this.categoryPresenterProvider = categoryPresenterProvider;
        this.notification = notification;
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        screen = placeRequest.getParameter("screen",
                                           adminPage.getDefaultScreen());
        perspectiveIdentifierToGoBackTo = placeRequest.getParameter("perspectiveIdentifierToGoBackTo",
                                                                    null);

        view.init(this);

        if (screen == null) {
            notification.fire(new NotificationEvent(view.getNoScreenParameterError(),
                                                    NotificationEvent.NotificationType.ERROR));
        } else {
            init(screen);
        }
    }

    public void init(final String screen) {
        this.screen = screen;

        final Map<String, List<AdminTool>> toolsByCategory = adminPage.getToolsByCategory(screen);

        if (toolsByCategory != null) {
            toolsByCategory.forEach((category, adminTools) -> {
                AdminPageCategoryPresenter categoryPresenter = categoryPresenterProvider.get();
                categoryPresenter.setup(adminTools,
                                        screen,
                                        perspectiveIdentifierToGoBackTo);
                view.add(categoryPresenter.getView());
            });
        } else {
            notification.fire(new NotificationEvent(view.getNoScreenFoundError(screen),
                                                    NotificationEvent.NotificationType.ERROR));
        }
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return view.getTitle();
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }

    public AdminPage getAdminPage() {
        return adminPage;
    }

    public String getScreen() {
        return screen;
    }

    public interface View extends UberElement<AdminPagePresenter> {

        void add(final AdminPageCategoryPresenter.View categoryView);

        String getTitle();

        String getNoScreenParameterError();

        String getNoScreenFoundError(String screen);
    }
}
