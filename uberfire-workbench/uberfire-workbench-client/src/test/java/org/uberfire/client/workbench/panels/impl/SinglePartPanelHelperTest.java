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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PartDefinition;

import static org.jgroups.util.Util.assertEquals;
import static org.jgroups.util.Util.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SinglePartPanelHelperTest {

    private SinglePartPanelHelper singlePartHelper;

    @Mock
    private PlaceManager placeManager;

    @Test
    public void thereIsNoPartsTest() {

        singlePartHelper = new SinglePartPanelHelper(new ArrayList<>(),
                                                     placeManager);
        assertTrue(singlePartHelper.hasNoParts());
    }

    @Test
    public void getPlaceFromFirstPartTest() {

        PlaceRequest place = mock(PlaceRequest.class);
        PartDefinition part = mock(PartDefinition.class);
        when(part.getPlace()).thenReturn(place);

        Collection<PartDefinition> parts = Arrays.asList(part);
        singlePartHelper = new SinglePartPanelHelper(parts,
                                                     placeManager);

        assertTrue(!singlePartHelper.hasNoParts());
        assertEquals(place,
                     singlePartHelper.getPlaceFromFirstPart());
    }

    @Test
    public void closeFirstPartAndAddNewOneTest() {

        PlaceRequest place = mock(PlaceRequest.class);
        PartDefinition part = mock(PartDefinition.class);
        Command cmd = mock(Command.class);

        when(part.getPlace()).thenReturn(place);

        Collection<PartDefinition> parts = Arrays.asList(part);
        singlePartHelper = new SinglePartPanelHelper(parts,
                                                     placeManager);

        singlePartHelper.closeFirstPartAndAddNewOne(cmd);
        verify(placeManager).tryClosePlace(place,
                                           cmd);
    }
}