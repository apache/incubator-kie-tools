/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.common.client.breadcrumbs;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Element;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.breadcrumbs.header.UberfireBreadcrumbsContainer;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.BreadcrumbPresenter;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.DefaultBreadcrumbsPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class UberfireBreadcrumbsTest {

    @Mock
    private UberfireBreadcrumbsContainer uberfireBreadcrumbsContainer;

    @Mock
    private ManagedInstance<DefaultBreadcrumbsPresenter> breadcrumbsPresenters;

    @Mock
    private DefaultBreadcrumbsPresenter.View breadcrumbsPresenterView;

    @Mock
    private PlaceManager placeManager;

    private UberfireBreadcrumbs uberfireBreadcrumbs;
    private UberfireBreadcrumbs.View view;

    @Before
    public void setup() {
        doAnswer(invocationOnMock -> new DefaultBreadcrumbsPresenter(breadcrumbsPresenterView)).when(breadcrumbsPresenters).get();

        view = mock(UberfireBreadcrumbs.View.class);
        uberfireBreadcrumbs = spy(new UberfireBreadcrumbs(uberfireBreadcrumbsContainer,
                                                          breadcrumbsPresenters,
                                                          placeManager,
                                                          view) {
        });

        uberfireBreadcrumbs.currentPerspective = "currentPerspective";
    }

    @Test
    public void createBreadcrumbsTest() {
        uberfireBreadcrumbs.createBreadcrumbs();

        verify(uberfireBreadcrumbsContainer).init(any(HTMLElement.class));
    }

    @Test
    public void addToolbar() {
        assertTrue(uberfireBreadcrumbs.breadcrumbsToolBarPerPerspective.isEmpty());

        uberfireBreadcrumbs.currentPerspective = "myperspective";
        uberfireBreadcrumbs.addToolbar("myperspective",
                                       mock(Element.class));

        assertFalse(uberfireBreadcrumbs.breadcrumbsToolBarPerPerspective.isEmpty());
    }

    @Test
    public void addBreadcrumbs() {
        assertTrue(uberfireBreadcrumbs.breadcrumbsPerPerspective.isEmpty());

        uberfireBreadcrumbs.currentPerspective = "myperspective";
        uberfireBreadcrumbs.addBreadCrumb("myperspective",
                                          "label",
                                          new DefaultPlaceRequest("screen"));
        uberfireBreadcrumbs.addBreadCrumb("myperspective",
                                          "label2",
                                          new DefaultPlaceRequest("screen2"));
        uberfireBreadcrumbs.addBreadCrumb("myperspective2",
                                          "label4",
                                          new DefaultPlaceRequest("screen4"));

        assertFalse(uberfireBreadcrumbs.breadcrumbsPerPerspective.isEmpty());
        assertEquals(2,
                     uberfireBreadcrumbs.breadcrumbsPerPerspective.size());
        assertEquals(2,
                     uberfireBreadcrumbs.breadcrumbsPerPerspective.get("myperspective").size());
    }

    @Test
    public void clearBreadcrumbsTest() {
        uberfireBreadcrumbs.currentPerspective = "myperspective";
        uberfireBreadcrumbs.addToolbar("myperspective",
                                       mock(Element.class));
        uberfireBreadcrumbs.addBreadCrumb("myperspective",
                                          "label",
                                          new DefaultPlaceRequest("screen"));

        assertFalse(uberfireBreadcrumbs.breadcrumbsPerPerspective.get("myperspective").isEmpty());
        assertFalse(uberfireBreadcrumbs.breadcrumbsToolBarPerPerspective.isEmpty());

        uberfireBreadcrumbs.clearBreadcrumbs("myperspective");

        assertTrue(uberfireBreadcrumbs.breadcrumbsPerPerspective.get("myperspective").isEmpty());
        assertFalse(uberfireBreadcrumbs.breadcrumbsToolBarPerPerspective.isEmpty());
    }

    @Test
    public void clearBreadcrumbsAndToolBarsTest() {
        uberfireBreadcrumbs.currentPerspective = "myperspective";
        uberfireBreadcrumbs.addToolbar("myperspective",
                                       mock(Element.class));
        uberfireBreadcrumbs.addBreadCrumb("myperspective",
                                          "label",
                                          new DefaultPlaceRequest("screen"));

        assertFalse(uberfireBreadcrumbs.breadcrumbsPerPerspective.get("myperspective").isEmpty());
        assertFalse(uberfireBreadcrumbs.breadcrumbsToolBarPerPerspective.isEmpty());

        uberfireBreadcrumbs.clearBreadcrumbsAndToolBars("myperspective");

        assertTrue(uberfireBreadcrumbs.breadcrumbsPerPerspective.get("myperspective").isEmpty());
        assertTrue(uberfireBreadcrumbs.breadcrumbsToolBarPerPerspective.isEmpty());
    }

    @Test
    public void removeDeepLevelBreadcrumbsTest() {
        uberfireBreadcrumbs.addBreadCrumb("myperspective",
                                          "label",
                                          new DefaultPlaceRequest("screen"));
        uberfireBreadcrumbs
                .addBreadCrumb("myperspective",
                               "label2",
                               new DefaultPlaceRequest("screen2"));
        uberfireBreadcrumbs.addBreadCrumb("myperspective",
                                          "label3",
                                          new DefaultPlaceRequest("screen3"));

        List<BreadcrumbPresenter> breadcrumbs = uberfireBreadcrumbs.breadcrumbsPerPerspective.get("myperspective");

        uberfireBreadcrumbs.removeDeepLevelBreadcrumbsIfNecessary("myperspective",
                                                                  (DefaultBreadcrumbsPresenter) breadcrumbs.get(0));

        assertEquals(1,
                     uberfireBreadcrumbs.breadcrumbsPerPerspective.get("myperspective").size());
    }

    @Test
    public void doesNotRemoveDeepLevelBreadcrumbsTest() {
        uberfireBreadcrumbs.addBreadCrumb("myperspective",
                                          "label",
                                          () -> {
                                          },
                                          false);
        uberfireBreadcrumbs.addBreadCrumb("myperspective",
                                          "label2",
                                          () -> {
                                          },
                                          false);
        uberfireBreadcrumbs.addBreadCrumb("myperspective",
                                          "label3",
                                          () -> {
                                          },
                                          false);

        List<BreadcrumbPresenter> breadcrumbs = uberfireBreadcrumbs.breadcrumbsPerPerspective.get("myperspective");

        uberfireBreadcrumbs.removeDeepLevelBreadcrumbsIfNecessary("myperspective",
                                                                  (DefaultBreadcrumbsPresenter) breadcrumbs.get(0));

        assertEquals(3,
                     uberfireBreadcrumbs.breadcrumbsPerPerspective.get("myperspective").size());
    }

    @Test
    public void generateBreadCrumbSelectCommandTest() {
        DefaultPlaceRequest placeRequest = new DefaultPlaceRequest("screen");
        final Command command = mock(Command.class);
        uberfireBreadcrumbs.addBreadCrumb("myperspective",
                                          "label",
                                          placeRequest);

        List<BreadcrumbPresenter> breadcrumbs = uberfireBreadcrumbs.breadcrumbsPerPerspective.get("myperspective");

        DefaultBreadcrumbsPresenter breadcrumb = (DefaultBreadcrumbsPresenter) breadcrumbs.get(0);

        uberfireBreadcrumbs.generateBreadCrumbSelectCommand("myperspective",
                                                            breadcrumb,
                                                            placeRequest,
                                                            command).execute();

        verify(placeManager).goTo(placeRequest);
        verify(placeManager,
               never()).goTo(eq(placeRequest),
                             any(HasWidgets.class));
        verify(command).execute();
    }

    @Test
    public void getViewShouldAddInnerBreadcrumbsTest() {

        List<BreadcrumbPresenter> breadcrumbs = Arrays
                .asList(mock(DefaultBreadcrumbsPresenter.class),
                        mock(DefaultBreadcrumbsPresenter.class));
        uberfireBreadcrumbs.breadcrumbsPerPerspective.put("myperspective",
                                                          breadcrumbs);
        uberfireBreadcrumbs.breadcrumbsToolBarPerPerspective.put("myperspective",
                                                                 mock(Element.class));

        uberfireBreadcrumbs.getView();

        verify(view).clear();
        verify(view,
               never()).addBreadcrumb(any(UberElemental.class));

        uberfireBreadcrumbs.currentPerspective = "myperspective";

        uberfireBreadcrumbs.getView();

        verify(view,
               times(2)).addBreadcrumb(any(UberElemental.class));
        verify(view,
               times(1)).addBreadcrumbToolbar(any(Element.class));
    }

    @Test
    public void addBreadcrumbAssociatedWithACommandTest() {
        final Command command = mock(Command.class);
        uberfireBreadcrumbs.addBreadCrumb("myperspective",
                                          "label",
                                          command);
        verify(uberfireBreadcrumbs).addBreadCrumb("myperspective",
                                                  "label",
                                                  null,
                                                  command,
                                                  true);
    }

    @Test
    public void addBreadcrumbAssociatedWithAPlaceRequestTest() {
        uberfireBreadcrumbs.addBreadCrumb("myperspective",
                                          "label",
                                          new DefaultPlaceRequest("screen"));
        verify(uberfireBreadcrumbs).addBreadCrumb("myperspective",
                                                  "label",
                                                  new DefaultPlaceRequest("screen"),
                                                  null,
                                                  true);
    }

    @Test
    public void addBreadcrumbAssociatedWithAPlaceRequestWithACommandTest() {
        final Command command = mock(Command.class);
        uberfireBreadcrumbs.addBreadCrumb("myperspective",
                                          "label",
                                          new DefaultPlaceRequest("screen"),
                                          command);
        verify(uberfireBreadcrumbs).addBreadCrumb("myperspective",
                                                  "label",
                                                  new DefaultPlaceRequest("screen"),
                                                  command,
                                                  true);
    }

    @Test
    public void updateBreadcrumbsContainerTest() {
        assertTrue(uberfireBreadcrumbs.breadcrumbsPerPerspective.isEmpty());

        uberfireBreadcrumbs.currentPerspective = "myperspective";
        uberfireBreadcrumbs.addBreadCrumb("myperspective",
                                          "label",
                                          new DefaultPlaceRequest("screen"));

        verify(uberfireBreadcrumbsContainer).enable();
    }

    @Test
    public void updateBreadcrumbsWithNoBreadcrumbsContainerTest() {
        assertTrue(uberfireBreadcrumbs.breadcrumbsPerPerspective.isEmpty());

        uberfireBreadcrumbs.currentPerspective = "myperspective";

        uberfireBreadcrumbs.updateBreadcrumbsContainer();

        verify(uberfireBreadcrumbsContainer).disable();
    }
}