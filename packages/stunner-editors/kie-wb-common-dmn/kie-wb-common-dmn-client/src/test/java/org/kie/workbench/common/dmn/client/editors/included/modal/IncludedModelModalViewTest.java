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

package org.kie.workbench.common.dmn.client.editors.included.modal;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class IncludedModelModalViewTest {

    @Mock
    private HTMLDivElement header;

    @Mock
    private HTMLDivElement body;

    @Mock
    private HTMLDivElement footer;

    @Mock
    private HTMLDivElement dropdown;

    @Mock
    private HTMLInputElement modelNameInput;

    @Mock
    private HTMLButtonElement includeButton;

    @Mock
    private HTMLButtonElement cancelButton;

    @Mock
    private IncludedModelModal presenter;

    private IncludedModelModalView view;

    @Before
    public void setup() {
        view = new IncludedModelModalView(header, body, footer, dropdown, modelNameInput, includeButton, cancelButton);
        view.init(presenter);
    }

    @Test
    public void testInitialize() {
        modelNameInput.value = "something";
        view.initialize();
        assertEquals("", modelNameInput.value);
    }

    @Test
    public void testGetHeader() {

        final String expectedHeader = "expectedHeader";
        header.textContent = expectedHeader;

        final String actualHeader = view.getHeader();

        assertEquals(expectedHeader, actualHeader);
    }

    @Test
    public void testGetBody() {
        assertEquals(body, view.getBody());
    }

    @Test
    public void testGetFooter() {
        assertEquals(footer, view.getFooter());
    }

    @Test
    public void testGetModelNameInput() {

        final String expected = "name";
        modelNameInput.value = expected;

        final String actual = view.getModelNameInput();

        assertEquals(expected, actual);
    }

    @Test
    public void testSetupAssetsDropdown() {

        final HTMLElement dropdownElement = mock(HTMLElement.class);

        view.setupAssetsDropdown(dropdownElement);

        verify(dropdown).appendChild(dropdownElement);
    }

    @Test
    public void testDisableIncludeButton() {
        includeButton.disabled = false;

        view.disableIncludeButton();

        assertTrue(includeButton.disabled);
    }

    @Test
    public void testEnableIncludeButton() {
        includeButton.disabled = true;

        view.enableIncludeButton();

        assertFalse(includeButton.disabled);
    }

    @Test
    public void testOnModelNameInputChanged() {
        view.onModelNameInputChanged(mock(KeyUpEvent.class));
        verify(presenter).onValueChanged();
    }

    @Test
    public void testOnIncludeButtonClick() {
        view.onIncludeButtonClick(mock(ClickEvent.class));
        verify(presenter).include();
    }

    @Test
    public void testOnCancelButtonClick() {
        view.onCancelButtonClick(mock(ClickEvent.class));
        verify(presenter).hide();
    }
}
