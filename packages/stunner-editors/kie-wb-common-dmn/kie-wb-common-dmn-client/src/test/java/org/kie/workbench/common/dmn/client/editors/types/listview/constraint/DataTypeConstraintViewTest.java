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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeConstraintView_AddConstraints;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeConstraintView_ConstraintsTooltip;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintViewTest {

    @Mock
    private HTMLAnchorElement constraintsAnchorContainer;

    @Mock
    private HTMLDivElement constraintsLabelContainer;

    @Mock
    private HTMLElement constraintsAnchorText;

    @Mock
    private HTMLElement constraintsLabelText;

    @Mock
    private HTMLElement constraintsTooltip;

    @Mock
    private TranslationService translationService;

    @Mock
    private DataTypeConstraint presenter;

    private DataTypeConstraintView view;

    @Before
    public void setup() {
        view = spy(new DataTypeConstraintView(constraintsAnchorContainer, constraintsLabelContainer, constraintsAnchorText, constraintsLabelText, constraintsTooltip, translationService));
        view.init(presenter);
    }

    @Test
    public void testSetup() {

        final String constraintsTooltipText = "ConstraintsTooltip";
        final JSONObject properties = mock(JSONObject.class);
        final JavaScriptObject propertiesJSONObject = mock(JavaScriptObject.class);

        doReturn(properties).when(view).properties();
        when(properties.getJavaScriptObject()).thenReturn(propertiesJSONObject);
        when(translationService.format(DataTypeConstraintView_ConstraintsTooltip)).thenReturn(constraintsTooltipText);

        view.setup();

        verify(constraintsTooltip).setAttribute("title", constraintsTooltipText);
        verify(view).setupTooltip(propertiesJSONObject);
    }

    @Test
    public void testOnConstraintsClick() {

        view.onConstraintsClick(mock(ClickEvent.class));

        verify(presenter).openModal();
    }

    @Test
    public void testShowAnchor() {

        constraintsLabelContainer.classList = mock(DOMTokenList.class);
        constraintsAnchorContainer.classList = mock(DOMTokenList.class);
        constraintsTooltip.classList = mock(DOMTokenList.class);

        view.showAnchor();

        verify(constraintsLabelContainer.classList).add(HIDDEN_CSS_CLASS);
        verify(constraintsAnchorContainer.classList).remove(HIDDEN_CSS_CLASS);
        verify(constraintsTooltip.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideAnchorWhenLabelTextIsConstraints() {

        final String addConstraints = "Add Constraints";
        when(translationService.format(DataTypeConstraintView_AddConstraints)).thenReturn(addConstraints);

        constraintsLabelContainer.classList = mock(DOMTokenList.class);
        constraintsAnchorContainer.classList = mock(DOMTokenList.class);
        constraintsTooltip.classList = mock(DOMTokenList.class);

        constraintsLabelText.textContent = addConstraints;

        view.hideAnchor();

        verify(constraintsLabelContainer.classList).add(HIDDEN_CSS_CLASS);
        verify(constraintsAnchorContainer.classList).add(HIDDEN_CSS_CLASS);
        verify(constraintsTooltip.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testHideAnchorWhenLabelTextIsNotConstraints() {

        final String addConstraints = "Add Constraints";
        when(translationService.format(DataTypeConstraintView_AddConstraints)).thenReturn(addConstraints);

        constraintsLabelContainer.classList = mock(DOMTokenList.class);
        constraintsAnchorContainer.classList = mock(DOMTokenList.class);
        constraintsTooltip.classList = mock(DOMTokenList.class);

        constraintsLabelText.textContent = "1, 2, 3";

        view.hideAnchor();

        verify(constraintsLabelContainer.classList).remove(HIDDEN_CSS_CLASS);
        verify(constraintsAnchorContainer.classList).add(HIDDEN_CSS_CLASS);
        verify(constraintsTooltip.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testSetTextWhenTextIsNotBlank() {

        final String expectedText = "text";

        constraintsAnchorText.textContent = "something...";
        constraintsLabelText.textContent = "something...";

        view.setText(expectedText);

        assertEquals(expectedText, constraintsAnchorText.textContent);
        assertEquals(expectedText, constraintsLabelText.textContent);
    }

    @Test
    public void testSetTextWhenTextIsBlank() {

        final String expectedText = "Add Constraints";
        when(translationService.format(DataTypeConstraintView_AddConstraints)).thenReturn(expectedText);

        constraintsAnchorText.textContent = "something...";
        constraintsLabelText.textContent = "something...";

        view.setText("");

        assertEquals(expectedText, constraintsAnchorText.textContent);
        assertEquals(expectedText, constraintsLabelText.textContent);
    }

    @Test
    public void testProperties() {

        final JSONObject jsonObject = mock(JSONObject.class);

        doReturn(jsonObject).when(view).makeJsonObject();

        view.properties();

        verify(jsonObject).put(eq("container"), eq(new JSONString("body")));
    }

    @Test
    public void testEnable() {

        final HTMLElement element = mock(HTMLElement.class);
        element.classList = mock(DOMTokenList.class);

        doReturn(element).when(view).getElement();

        view.enable();

        element.classList.remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testDisable() {

        final HTMLElement element = mock(HTMLElement.class);
        element.classList = mock(DOMTokenList.class);

        doReturn(element).when(view).getElement();

        view.disable();

        element.classList.add(HIDDEN_CSS_CLASS);
    }
}
