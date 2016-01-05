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

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.workbench.panels.DockingWorkbenchPanelView;

@RunWith(MockitoJUnitRunner.class)
public class SimpleWorkbenchPanelPresenterTest extends AbstractDockingWorkbenchPanelPresenterTest {

    @Mock(name="view")
    protected DockingWorkbenchPanelView<SimpleWorkbenchPanelPresenter> view;

    @InjectMocks
    SimpleWorkbenchPanelPresenter presenter;

    @Before
    public void setUp2() {
        presenter.init();
        presenter.setDefinition( panelPresenterPanelDefinition );
    }

    @Override
    AbstractDockingWorkbenchPanelPresenter<?> getPresenterToTest() {
        return presenter;
    }

    @Test
    public void initShouldBindPresenterToView() {
        verify( view ).init( presenter );
    }
}
