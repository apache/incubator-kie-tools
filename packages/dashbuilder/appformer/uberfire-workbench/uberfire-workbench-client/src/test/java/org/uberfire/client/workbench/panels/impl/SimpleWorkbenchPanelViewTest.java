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

package org.uberfire.client.workbench.panels.impl;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SimpleWorkbenchPanelViewTest extends AbstractSimpleWorkbenchPanelViewTest {

    @InjectMocks
    private SimpleWorkbenchPanelViewUnitTestWrapper view;

    // Not a @Mock or @GwtMock because we want to test the view.init() method
    private SimpleWorkbenchPanelPresenter presenter;

    @Before
    public void setup() {
        super.setup();

        presenter = mock(SimpleWorkbenchPanelPresenter.class);

        view.setup(); // PostConstruct
        view.init(presenter);
    }

    @Override
    protected AbstractDockingWorkbenchPanelView<?> getViewToTest() {
        return view;
    }

    @Test
    public void shouldAddPresenterOnInit() {
        assertEquals(presenter,
                     view.getPresenter());
    }

    @Test
    public void shouldSetupDragAndDropOnListBar() {
        verify(listBar).setDndManager(eq(dndManager));
        verify(listBar).disableDnd();
        verify(listBar).addSelectionHandler(any(SelectionHandler.class));
        verify(listBar).addSelectionHandler(any(SelectionHandler.class));
        verify(listBar).addOnFocusHandler(any(Command.class));
    }

    @Test
    public void shouldPropagateResizeWhenAttached() {

        view.forceAttachedState(true);
        view.setPixelSize(10,
                          10);
        view.onResize();

        // unfortunately, setPixelSize() doesn't have any side effects during unit tests so we can't verify the arguments
        verify(presenter).onResize(any(Integer.class),
                                   any(Integer.class));

        verify(topLevelWidget).onResize();
    }

    @Test
    public void shouldNotPropagateResizeWhenNotAttached() {

        view.forceAttachedState(false);
        view.setPixelSize(10,
                          10);
        view.onResize();

        // unfortunately, setPixelSize() doesn't have any side effects during unit tests so we can't verify the arguments
        verify(presenter,
               never()).onResize(any(Integer.class),
                                 any(Integer.class));

        verify(topLevelWidget).onResize();
    }

    @Test
    public void shouldDisableCloseParts() {
        verify(listBar).disableClosePart();
        verify(listBar,
               never()).enableClosePart();
    }

    @Test(expected = RuntimeException.class)
    public void shouldOnlyHaveOnePart() {
        assertEquals(0,
                     listBar.getPartsSize());

        getViewToTest().addPart(mock(WorkbenchPartPresenter.View.class));
        verify(listBar).addPart(any(WorkbenchPartPresenter.View.class));
        assertEquals(1,
                     listBar.getPartsSize());

        //Second part add is a leak should throw exception
        getViewToTest().addPart(mock(WorkbenchPartPresenter.View.class));
        verify(listBar).addPart(any(WorkbenchPartPresenter.View.class));
        assertEquals(1,
                     listBar.getPartsSize());
    }
}