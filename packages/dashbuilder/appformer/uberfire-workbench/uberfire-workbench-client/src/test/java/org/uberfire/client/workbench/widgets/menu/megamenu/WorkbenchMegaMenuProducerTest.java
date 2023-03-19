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

package org.uberfire.client.workbench.widgets.menu.megamenu;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;
import org.uberfire.client.workbench.widgets.menu.events.PerspectiveVisibiltiyChangeEvent;
import org.uberfire.client.workbench.widgets.menu.megamenu.brand.MegaMenuBrand;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.ChildContextMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.GroupContextMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.ChildMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.GroupMenuItemPresenter;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchMegaMenuProducerTest {

    @Mock
    private PerspectiveManager perspectiveManager;

    @Mock
    private ActivityManager activityManager;

    @Mock
    private WorkbenchMegaMenuPresenter.View view;

    @Mock
    private WorkbenchMegaMenuPresenter defaultPresenter;

    @Mock
    private WorkbenchMegaMenuStandalonePresenter standalonePresenter;

    @Mock
    private PerspectiveChange perspectiveChangeEvent;

    @Mock
    private PlaceMaximizedEvent placeMaximizedEvent;

    @Mock
    private PlaceMinimizedEvent placeMinimizedEvent;

    @Mock
    private ManagedInstance<MegaMenuBrand> megaMenuBrands;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private ManagedInstance<ChildMenuItemPresenter> childMenuItemPresenters;

    @Mock
    private ManagedInstance<GroupMenuItemPresenter> groupMenuItemPresenters;

    @Mock
    private ManagedInstance<ChildContextMenuItemPresenter> childContextMenuItemPresenters;

    @Mock
    private ManagedInstance<GroupContextMenuItemPresenter> groupContextMenuItemPresenters;

    @Mock
    private Workbench workbench;

    private WorkbenchMegaMenuProducer producer;
    private boolean isStandalone = false;

    @Before
    public void setup() {
        producer = new WorkbenchMegaMenuProducer(perspectiveManager,
                                                 activityManager,
                                                 view,
                                                 megaMenuBrands,
                                                 placeManager,
                                                 childMenuItemPresenters,
                                                 groupMenuItemPresenters,
                                                 childContextMenuItemPresenters,
                                                 groupContextMenuItemPresenters,
                                                 workbench) {
            @Override
            protected boolean isStandalone() {
                return isStandalone;
            }

            @Override
            protected WorkbenchMegaMenuPresenter makeDefaultPresenter() {
                return defaultPresenter;
            }

            @Override
            protected WorkbenchMegaMenuStandalonePresenter makeStandalonePresenter() {
                return standalonePresenter;
            }
        };
    }

    @Test
    public void megaMenuPresenterInstantiationDefaultMode() {
        assertMegaMenuPresenter(false,
                                WorkbenchMegaMenuPresenter.class);
    }

    @Test
    public void megaMenuPresenterInstantiationStandaloneMode() {
        assertMegaMenuPresenter(true,
                                WorkbenchMegaMenuStandalonePresenter.class);
    }

    @Test
    public void checkObservedEventsCallsPresenterDefaultMode() {
        final WorkbenchMegaMenuPresenter presenter = getMegaMenuPresenter(false);
        assertMegaMenuEvents(presenter);
    }

    @Test
    public void checkObservedEventsCallsPresenterStandaloneMode() {
        final WorkbenchMegaMenuPresenter presenter = getMegaMenuPresenter(true);
        assertMegaMenuEvents(presenter);
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
        final WorkbenchMegaMenuPresenter presenter = getMegaMenuPresenter(isStandalone);

        presenter.onPerspectiveVisibilityChange(new PerspectiveVisibiltiyChangeEvent("perspectiveId", false));

        verify(presenter).onPerspectiveVisibilityChange(any());
    }

    private void assertMegaMenuPresenter(final boolean isStandalone,
                                         final Class expectedPresenterType) {
        final WorkbenchMegaMenuPresenter presenter = getMegaMenuPresenter(isStandalone);
        assertEquals(extractContainingClassName(expectedPresenterType.getName()),
                     extractContainingClassName(presenter.getClass().getName()));
    }

    private void assertMegaMenuEvents(final WorkbenchMegaMenuPresenter presenter) {
        presenter.onPerspectiveChange(perspectiveChangeEvent);
        verify(presenter).onPerspectiveChange(eq(perspectiveChangeEvent));
    }

    private WorkbenchMegaMenuPresenter getMegaMenuPresenter(final boolean isStandalone) {
        this.isStandalone = isStandalone;
        return producer.getWorbenchMenu();
    }

    private String extractContainingClassName(final String className) {
        if (className.contains("$")) {
            return className.substring(0,
                                       className.indexOf("$"));
        }
        return className;
    }
}
