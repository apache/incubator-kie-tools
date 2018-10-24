/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.types.listview;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.HIDDEN_CSS_CLASS;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeConstraintView_ConstraintsTooltip;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintViewTest {

    @Mock
    private HTMLInputElement constraintToggle;

    @Mock
    private HTMLInputElement constraintValue;

    @Mock
    private HTMLElement constraintsTooltip;

    @Mock
    private TranslationService translationService;

    private DataTypeConstraintView view;

    @Before
    public void setup() {
        view = spy(new DataTypeConstraintView(translationService, constraintToggle, constraintValue, constraintsTooltip));
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
    public void testOnConstraintToggleChangeWhenConstraintToggleIsChecked() {

        final ChangeEvent event = mock(ChangeEvent.class);

        doNothing().when(view).enableConstraint();

        constraintToggle.checked = true;

        view.onConstraintToggleChange(event);

        verify(view).enableConstraint();
        verify(constraintValue).select();
    }

    @Test
    public void testOnConstraintToggleChangeWhenConstraintToggleIsNotChecked() {

        final ChangeEvent event = mock(ChangeEvent.class);

        doNothing().when(view).disableConstraint();

        constraintToggle.checked = false;
        constraintValue.value = "value";

        view.onConstraintToggleChange(event);

        verify(view).disableConstraint();
        assertEquals("", constraintValue.value);
    }

    @Test
    public void testEnableConstraint() {

        constraintToggle.checked = false;
        constraintValue.classList = mock(DOMTokenList.class);

        view.enableConstraint();

        assertTrue(constraintToggle.checked);
        verify(constraintValue.classList).remove(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testDisableConstraint() {

        constraintToggle.checked = true;
        constraintValue.classList = mock(DOMTokenList.class);

        view.disableConstraint();

        assertFalse(constraintToggle.checked);
        verify(constraintValue.classList).add(HIDDEN_CSS_CLASS);
    }

    @Test
    public void testGetConstraintValue() {

        final String expectedValue = "value";
        constraintValue.value = expectedValue;

        final String actualValue = view.getConstraintValue();

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testSetConstraintValue() {

        final String expectedValue = "newValue";
        constraintValue.value = "oldValue";

        view.setConstraintValue(expectedValue);

        final String actualValue = constraintValue.value;

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testProperties() {

        final JSONObject jsonObject = mock(JSONObject.class);

        doReturn(jsonObject).when(view).makeJsonObject();

        view.properties();

        verify(jsonObject).put(eq("container"), eq(new JSONString("body")));
    }
}
