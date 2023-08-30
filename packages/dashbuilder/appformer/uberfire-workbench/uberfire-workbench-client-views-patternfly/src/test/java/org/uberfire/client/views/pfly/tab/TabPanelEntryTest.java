/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.uberfire.client.views.pfly.tab;

import java.util.function.Consumer;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.ouia.OuiaAttribute;
import org.uberfire.client.workbench.ouia.OuiaComponentIdAttribute;
import org.uberfire.client.workbench.ouia.OuiaComponentTypeAttribute;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class TabPanelEntryTest {

    @Mock
    private TabPanelEntry.DropDownTabListItem tabEntryMock;

    @Mock
    private Consumer<OuiaAttribute> ouiaRendererMock;

    @Mock
    private Widget tabContentMock;

    @Before
    public void setUp() throws Exception {
        GwtMockito.useProviderForType(TabPanelEntry.DropDownTabListItem.class, clazz -> tabEntryMock);

        when(tabEntryMock.ouiaAttributeRenderer()).thenReturn(ouiaRendererMock);
    }

    @Test
    public void testInitialization() {
        final String title = "tab title";
        doCallRealMethod().when(tabEntryMock).initOuiaComponentAttributes();
        doCallRealMethod().when(tabEntryMock).ouiaComponentId();
        doCallRealMethod().when(tabEntryMock).ouiaComponentType();
        when(tabEntryMock.getText()).thenReturn(title);
        when(tabEntryMock.ouiaAttributeRenderer()).thenReturn(ouiaRendererMock);

        new TabPanelEntry(title, tabContentMock);

        verify(tabEntryMock).setText(title);
        verify(tabEntryMock).initOuiaComponentAttributes();
        verify(ouiaRendererMock).accept(eq(new OuiaComponentTypeAttribute("editor-nav-tab")));
        verify(ouiaRendererMock).accept(eq(new OuiaComponentIdAttribute(title)));
    }
}
