/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.popups.launcher;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherAddEvent;
import org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherRemoveEvent;
import org.kie.workbench.common.widgets.client.popups.launcher.events.AppLauncherUpdatedEvent;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AppLauncherPresenterTest {

    @Spy
    Event<AppLauncherUpdatedEvent> updatedEvent = new EventSourceMock<>();

    @Mock
    AppLauncherPresenter.AppLauncherView view;

    @InjectMocks
    AppLauncherPresenter presenter;

    @Before
    public void setupMocks() {
        doNothing().when(updatedEvent).fire(any(AppLauncherUpdatedEvent.class));
    }

    @Test
    public void testOnAppLauncherRemoveEvent() {
        final String appName = "appName";
        presenter.onAppLauncherAddEvent(new AppLauncherAddEvent(appName, null, null));
        presenter.onAppLauncherRemoveEvent(new AppLauncherRemoveEvent(appName));

        verify(view).removeAllAppLauncher();
        verify(updatedEvent, times(2)).fire(any(AppLauncherUpdatedEvent.class));
        assertTrue(presenter.isAppLauncherEmpty());
    }

    @Test
    public void testOnAppLauncherAddEvent() {
        final String appName = "appName";
        final String url = "url";
        final String css = "fa";
        presenter.onAppLauncherAddEvent(new AppLauncherAddEvent(appName, url, css));

        verify(view).addAppLauncher(appName, url, css);
        verify(updatedEvent).fire(any(AppLauncherUpdatedEvent.class));
        assertFalse(presenter.isAppLauncherEmpty());
    }

    @Test
    public void testIsAppLauncherEmpty() {
        assertTrue(presenter.isAppLauncherEmpty());

        presenter.onAppLauncherAddEvent(new AppLauncherAddEvent(null, null, null));

        assertFalse(presenter.isAppLauncherEmpty());
    }

}
