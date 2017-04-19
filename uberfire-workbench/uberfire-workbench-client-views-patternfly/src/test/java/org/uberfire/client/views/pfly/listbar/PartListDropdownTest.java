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
package org.uberfire.client.views.pfly.listbar;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.uberfire.workbench.model.PartDefinition;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class PartListDropdownTest {

    @Spy
    @InjectMocks
    PartListDropdown dropdown;

    @Test
    public void selectPartShouldMakeItDraggableOnlyIfDnDIsEnable(){
        assertTrue(dropdown.isDndEnabled());
        dropdown.selectPart(mock(PartDefinition.class));
        verify(dropdown).makeDraggable(any(),any());
    }

    @Test
    public void selectPartShouldNeverMakeItDraggableIfDnDIsDisable(){
        dropdown.disableDragAndDrop();
        assertTrue(!dropdown.isDndEnabled());
        dropdown.selectPart(mock(PartDefinition.class));
        verify(dropdown, never()).makeDraggable(any(),any());
    }

}