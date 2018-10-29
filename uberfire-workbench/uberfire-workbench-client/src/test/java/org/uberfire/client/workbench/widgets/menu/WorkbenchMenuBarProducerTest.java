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

package org.uberfire.client.workbench.widgets.menu;

import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;
import org.uberfire.client.workbench.widgets.menu.events.PerspectiveVisibiltiyChangeEvent;
import org.uberfire.experimental.service.auth.ExperimentalActivitiesAuthorizationManager;
import org.uberfire.security.authz.AuthorizationManager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchMenuBarProducerTest {

    @Mock
    private AuthorizationManager authzManager;

    @Mock
    private PerspectiveManager perspectiveManager;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private ActivityManager activityManager;

    @Mock
    private User identity;

    @Mock
    private WorkbenchMenuBarPresenter.View view;

    @Mock
    private WorkbenchMenuBarPresenter defaultPresenter;

    @Mock
    private WorkbenchMenuBarStandalonePresenter standalonePresenter;

    @Mock
    private ExperimentalActivitiesAuthorizationManager experimentalActivitiesAuthorizationManager;

    @Mock
    private PerspectiveChange perspectiveChangeEvent;

    @Mock
    private PlaceMaximizedEvent placeMaximizedEvent;

    @Mock
    private PlaceMinimizedEvent placeMinimizedEvent;

    private WorkbenchMenuBarProducer producer;
    private boolean isStandalone = false;

    @Before
    public void setup() {
        producer = new WorkbenchMenuBarProducer(authzManager,
                                                perspectiveManager,
                                                placeManager,
                                                activityManager,
                                                identity,
                                                experimentalActivitiesAuthorizationManager, view
        ) {
            @Override
            protected boolean isStandalone() {
                return isStandalone;
            }

            @Override
            protected WorkbenchMenuBarPresenter makeDefaultPresenter() {
                return defaultPresenter;
            }

            @Override
            protected WorkbenchMenuBarPresenter makeStandalonePresenter() {
                return standalonePresenter;
            }
        };
    }

    @Test
    public void menuBarPresenterInstantiationDefaultMode() {
        assertMenuBarPresenter(false,
                               WorkbenchMenuBarPresenter.class);
    }

    @Test
    public void menuBarPresenterInstantiationStandaloneMode() {
        assertMenuBarPresenter(true,
                               WorkbenchMenuBarStandalonePresenter.class);
    }

    @Test
    public void checkObservedEventsCallsPresenterDefaultMode() {
        final WorkbenchMenuBarPresenter presenter = getMenuBarPresenter(false);
        assertMenuBarEvents(presenter);
    }

    @Test
    public void checkObservedEventsCallsPresenterStandaloneMode() {
        final WorkbenchMenuBarPresenter presenter = getMenuBarPresenter(true);
        assertMenuBarEvents(presenter);
    }

    @Test
    public void testNotifyVisibilityChange() {
        testNotifyVisibilityChange(false);
    }

    @Test
    public void testNotifyVisibilityChangeStandaloneMode() {
        testNotifyVisibilityChange(true);
    }

    private void testNotifyVisibilityChange(boolean isStandalone) {
        final WorkbenchMenuBarPresenter presenter = getMenuBarPresenter(isStandalone);

        presenter.onPerspectiveVisibilityChange(new PerspectiveVisibiltiyChangeEvent("perspectiveId", false));

        verify(presenter).onPerspectiveVisibilityChange(any());
    }

    private void assertMenuBarPresenter(final boolean isStandalone,
                                        final Class expectedPresenterType) {
        final WorkbenchMenuBarPresenter presenter = getMenuBarPresenter(isStandalone);
        assertEquals(extractContainingClassName(expectedPresenterType.getName()),
                     extractContainingClassName(presenter.getClass().getName()));
    }

    private void assertMenuBarEvents(final WorkbenchMenuBarPresenter presenter) {
        presenter.onPerspectiveChange(perspectiveChangeEvent);
        verify(presenter).onPerspectiveChange(eq(perspectiveChangeEvent));

        presenter.onPlaceMaximized(placeMaximizedEvent);
        verify(presenter).onPlaceMaximized(eq(placeMaximizedEvent));

        presenter.onPlaceMinimized(placeMinimizedEvent);
        verify(presenter).onPlaceMinimized(eq(placeMinimizedEvent));
    }

    private WorkbenchMenuBarPresenter getMenuBarPresenter(final boolean isStandalone) {
        this.isStandalone = isStandalone;
        return producer.getWorbenchMenu();
    }

    private String extractContainingClassName(final String className) {
        if (className.contains("$$")) {
            return className.substring(0,
                                       className.indexOf("$$"));
        }
        return className;
    }
}