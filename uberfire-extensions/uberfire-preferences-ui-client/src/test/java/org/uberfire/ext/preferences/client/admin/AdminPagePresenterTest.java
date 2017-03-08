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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.event.Event;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.preferences.client.admin.category.AdminPageCategoryPresenter;
import org.uberfire.ext.preferences.client.admin.page.AdminPage;
import org.uberfire.ext.preferences.client.admin.page.AdminPageImpl;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Mockito.*;

public class AdminPagePresenterTest {

    private AdminPagePresenter.View view;

    private AdminPage adminPage;

    private ManagedInstance<AdminPageCategoryPresenter> categoryPresenterProvider;

    private Event<NotificationEvent> notification;

    private AdminPagePresenter presenter;

    @Before
    public void setup() {
        view = mock(AdminPagePresenter.View.class);
        adminPage = new AdminPageImpl();
        categoryPresenterProvider = mock(ManagedInstance.class);
        notification = mock(EventSourceMock.class);

        presenter = spy(new AdminPagePresenter(view,
                                               adminPage,
                                               categoryPresenterProvider,
                                               notification));
    }

    @Test
    public void onStartupWithScreenTest() {
        doNothing().when(presenter).init(anyString());

        Map<String, String> params = new HashMap<>();
        params.put("screen",
                   "my-screen");
        PlaceRequest placeRequest = new DefaultPlaceRequest("AdminPagePresenter",
                                                            params);

        presenter.onStartup(placeRequest);

        verify(view).init(presenter);
        verify(notification,
               never()).fire(any(NotificationEvent.class));
        verify(presenter).init("my-screen");
    }

    @Test
    public void onStartupWithoutScreenTest() {
        PlaceRequest placeRequest = new DefaultPlaceRequest("AdminPagePresenter");

        presenter.onStartup(placeRequest);

        verify(view).init(presenter);
        verify(notification).fire(any(NotificationEvent.class));
        verify(presenter,
               never()).init(anyString());
    }

    @Test
    public void initWithNotAddedScreenTest() {
        presenter.init("not-added-screen");

        verify(notification).fire(any(NotificationEvent.class));
    }

    @Test
    public void initWithAddedScreenTest() {
        adminPage.addScreen("added-screen",
                            "Screen title");

        presenter.init("added-screen");

        verify(notification,
               never()).fire(any(NotificationEvent.class));
    }
}
