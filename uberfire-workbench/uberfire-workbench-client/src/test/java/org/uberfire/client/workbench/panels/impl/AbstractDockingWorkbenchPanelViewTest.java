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

import org.junit.Before;
import org.mockito.Answers;
import org.mockito.Mock;
import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.listbar.ResizeFlowPanel;

import com.google.gwt.user.client.ui.SimpleLayoutPanel;

/**
 * Contains the setup necessary for testing subclasses of {@link AbstractDockingWorkbenchPanelView} with GWTMockito.
 */
public abstract class AbstractDockingWorkbenchPanelViewTest {

    @Mock(answer=Answers.RETURNS_MOCKS)
    ResizeFlowPanel partViewContainer;

    @Mock(answer=Answers.RETURNS_MOCKS)
    SimpleLayoutPanel topLevelWidget;

    @Mock
    WorkbenchDragAndDropManager dndManager;

    @Mock
    BeanFactory factory;

    @Before
    public void setupAbstractDockingSuperclass() {
        getViewToTest().setupDockingPanel(); // PostConstruct method
    }

    /**
     * Subclasses should return the object being unit tested. It must return a valid result even before the
     * {@code @Setup} method of the subclass has been invoked.
     */
    protected abstract AbstractDockingWorkbenchPanelView<?> getViewToTest();
}
