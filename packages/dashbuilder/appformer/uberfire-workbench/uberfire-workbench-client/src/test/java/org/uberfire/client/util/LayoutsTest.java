/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.client.util;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.workbench.model.PanelDefinition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class LayoutsTest {

    @Mock
    PanelDefinition panelDef;

    @Test
    public void widthOrDefault() {
        when(panelDef.getWidthAsInt()).thenReturn(42);
        Integer width = Layouts.widthOrDefault(panelDef);

        assertEquals(42,
                     width.intValue());

        when(panelDef.getWidthAsInt()).thenReturn(-1);
        width = Layouts.widthOrDefault(panelDef);

        assertEquals(Layouts.DEFAULT_CHILD_SIZE,
                     width.intValue());
    }

    @Test
    public void heightOrDefault() {
        when(panelDef.getHeightAsInt()).thenReturn(42);
        Integer height = Layouts.heightOrDefault(panelDef);

        assertEquals(42,
                     height.intValue());

        when(panelDef.getHeightAsInt()).thenReturn(-1);
        height = Layouts.heightOrDefault(panelDef);

        assertEquals(Layouts.DEFAULT_CHILD_SIZE,
                     height.intValue());
    }
}
