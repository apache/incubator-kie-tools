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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.list;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.Span;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.workbench.ouia.OuiaComponentIdAttribute;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ListSelectorTextItemViewImplTest {

    @Mock
    private ListItem listItem;

    @Mock
    private Span text;

    private ListSelectorTextItemViewImpl textItemView;

    @Before
    public void setUp() throws Exception {
        textItemView = spy(new ListSelectorTextItemViewImpl(listItem, text));
    }

    @Test
    public void testSetText() {
        reset(listItem); // We have interacted with listItem during ListSelectorTextItemViewImpl setUp phase
        doReturn("abc").when(text).getTextContent();

        textItemView.setText("abc");
        verify(text).setTextContent("abc");
        verify(listItem).setAttribute(OuiaComponentIdAttribute.COMPONENT_ID, "dmn-grid-context-menu-item-abc");
    }

    @Test
    public void testOuiaComponentTypeAttribute() {
        assertEquals("dmn-grid-context-menu-item", textItemView.ouiaComponentType().getValue());
    }

    @Test
    public void testOuiaComponentIdAttribute() {
        doReturn("xyz").when(text).getTextContent();
        assertEquals("dmn-grid-context-menu-item-xyz", textItemView.ouiaComponentId().getValue());
    }
}
