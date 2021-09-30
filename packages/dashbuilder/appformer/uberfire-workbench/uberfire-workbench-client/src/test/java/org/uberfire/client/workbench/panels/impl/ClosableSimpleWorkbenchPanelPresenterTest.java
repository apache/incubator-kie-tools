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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.DockingWorkbenchPanelView;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PartDefinition;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ClosableSimpleWorkbenchPanelPresenterTest extends AbstractDockingWorkbenchPanelPresenterTest {

    @Mock(name = "view")
    protected DockingWorkbenchPanelView<ClosableSimpleWorkbenchPanelPresenter> view;
    @InjectMocks
    ClosableSimpleWorkbenchPanelPresenter presenter;
    @Mock
    private PlaceManager placeManager;

    @Before
    public void setUp2() {
        presenter.init();
        presenter.setDefinition(panelPresenterPanelDefinition);
    }

    @Override
    AbstractDockingWorkbenchPanelPresenter<?> getPresenterToTest() {
        return presenter;
    }

    @Test
    public void initShouldBindPresenterToView() {
        verify(view).init(presenter);
    }

    @Test
    public void addPartTest() {

        WorkbenchPartPresenter part = mock(WorkbenchPartPresenter.class);
        when(part.getDefinition()).thenReturn(mock(PartDefinition.class));

        presenter.addPart(part);

        verify(view).addPart(any());
    }

    @Test
    public void addPartTwiceShouldCloseOtherPartTest() {

        SinglePartPanelHelper singlePartPanelHelper = mock(SinglePartPanelHelper.class);

        ClosableSimpleWorkbenchPanelPresenter presenter = new ClosableSimpleWorkbenchPanelPresenter(view,
                                                                                                    mock(PerspectiveManager.class),
                                                                                                    placeManager) {
            SinglePartPanelHelper createSinglePartPanelHelper() {
                return singlePartPanelHelper;
            }
        };

        presenter.init();
        presenter.setDefinition(panelPresenterPanelDefinition);

        //there is already a part
        when(singlePartPanelHelper.hasNoParts()).thenReturn(false);

        WorkbenchPartPresenter part2 = mock(WorkbenchPartPresenter.class);
        when(part2.getDefinition()).thenReturn(mock(PartDefinition.class));

        presenter.addPart(part2);

        verify(singlePartPanelHelper).closeFirstPartAndAddNewOne(any(Command.class));
    }
}
