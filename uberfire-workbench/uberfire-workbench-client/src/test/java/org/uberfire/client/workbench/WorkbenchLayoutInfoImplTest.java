/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.workbench;

import org.jboss.errai.common.client.dom.DOMClientRect;
import org.jboss.errai.common.client.dom.Div;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchLayoutInfoImplTest {

    @Mock
    WorkbenchLayoutImpl workbenchLayout;

    @InjectMocks
    WorkbenchLayoutInfoImpl workbenchLayoutInfo;

    @Test
    public void defaultToZeroWhenSomethingGoesWrong() throws Exception {
        assertEquals(0, workbenchLayoutInfo.getHeaderHeight());
    }

    @Test
    public void digTheHeightTheNormalWay() throws Exception {

        final Div div = mock(Div.class);
        final DOMClientRect domClientRect = mock(DOMClientRect.class);

        doReturn(div).when(workbenchLayout).getHeaderPanel();
        doReturn(domClientRect).when(div).getBoundingClientRect();
        doReturn(new Double(12)).when(domClientRect).getHeight();

        assertEquals(12, workbenchLayoutInfo.getHeaderHeight());
    }
}