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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class StaticWorkbenchPanelPresenterTest {

    @Mock
    StaticWorkbenchPanelView view;
    StaticWorkbenchPanelPresenter presenter;
    @Mock
    private PlaceManager placeManager;

    @Before
    public void setup() {
        presenter = new StaticWorkbenchPanelPresenter(view,
                                                      mock(PerspectiveManager.class),
                                                      placeManager);
        presenter.init();
        presenter.setDefinition(new PanelDefinitionImpl());
    }

    @Test
    public void getDefaultChildTypeTest() {

        assertNull(presenter.getDefaultChildType());
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

        StaticWorkbenchPanelPresenter presenter = new StaticWorkbenchPanelPresenter(view,
                                                                                    mock(PerspectiveManager.class),
                                                                                    placeManager) {
            SinglePartPanelHelper createSinglePartPanelHelper() {
                return singlePartPanelHelper;
            }
        };

        presenter.init();
        presenter.setDefinition(new PanelDefinitionImpl());

        //there is already a part
        when(singlePartPanelHelper.hasNoParts()).thenReturn(false);

        WorkbenchPartPresenter part2 = mock(WorkbenchPartPresenter.class);
        when(part2.getDefinition()).thenReturn(mock(PartDefinition.class));

        presenter.addPart(part2);

        verify(singlePartPanelHelper).closeFirstPartAndAddNewOne(any(Command.class));
    }
}