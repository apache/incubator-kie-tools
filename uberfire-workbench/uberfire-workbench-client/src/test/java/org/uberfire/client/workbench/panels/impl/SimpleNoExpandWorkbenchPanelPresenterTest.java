/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.workbench.panels.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.DockingWorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PartDefinition;

@RunWith(MockitoJUnitRunner.class)
public class SimpleNoExpandWorkbenchPanelPresenterTest extends AbstractDockingWorkbenchPanelPresenterTest {

    
    @Mock(name = "view")
    protected DockingWorkbenchPanelView<SimpleNoExpandWorkbenchPanelPresenter> view;
    @Mock
    private PlaceManager placeManager;
    @InjectMocks
    SimpleNoExpandWorkbenchPanelPresenter presenter;

    @Before
    public void init() {
        presenter.init();
        presenter.setDefinition(panelPresenterPanelDefinition);
    }
    
    @Override
    AbstractDockingWorkbenchPanelPresenter<?> getPresenterToTest() {
        return presenter;
    }
    
    @Test
    public void viewInitCalledTest() {
        verify(view).init(presenter);
    }
    
    @Test
    public void addPartTest() {
        WorkbenchPartPresenter workbenchPartPresenter = Mockito.mock(WorkbenchPartPresenter.class);
        PartDefinition partDefinition = mock(PartDefinition.class);
        Mockito.when(workbenchPartPresenter.getDefinition()).thenReturn(partDefinition);
        presenter.addPart(workbenchPartPresenter);
        boolean hasPart = panelPresenterPanelDefinition.getParts().stream()
                                                       .filter(part -> part.equals(partDefinition))
                                                       .findFirst().isPresent();
        assertTrue(hasPart);
        verify(placeManager, times(0)).tryClosePlace(any(), any());
        Mockito.when(view.getParts()).thenReturn(Arrays.asList(partDefinition));
        presenter.addPart(workbenchPartPresenter);
        verify(placeManager).tryClosePlace(any(), any());
    }
    
}