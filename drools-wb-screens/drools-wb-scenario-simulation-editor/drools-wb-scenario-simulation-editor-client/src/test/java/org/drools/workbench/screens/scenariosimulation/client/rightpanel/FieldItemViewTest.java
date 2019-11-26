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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FACT_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FIELD_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FULL_PROPERTY_PATH;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FieldItemViewTest extends AbstractTestToolsTest {

    @Mock
    private SpanElement fieldNameElementMock;

    @Mock
    private SpanElement checkElementMock;

    @Mock
    private Style checkElementStyleMock;

    @Mock
    private FieldItemPresenter fieldItemPresenter;

    private FieldItemViewImpl fieldItemViewSpy;

    private String INNER_HTML;
    private String ID_ATTRIBUTE;

    @Before
    public void setup() {
        super.setup();
        INNER_HTML = "<a>" + FIELD_NAME + "</a> [" + FACT_MODEL_TREE.getFactName() + "]";
        ID_ATTRIBUTE = "fieldElement-" + FACT_NAME + "-" + FIELD_NAME;
        this.fieldItemViewSpy = spy(new FieldItemViewImpl() {
            {
                this.fieldNameElement = fieldNameElementMock;
                this.fieldElement = lIElementMock;
                this.checkElement = checkElementMock;
            }
        });
        fieldItemViewSpy.setPresenter(fieldItemPresenter);
        when(checkElementMock.getStyle()).thenReturn(checkElementStyleMock);
    }

    @Test
    public void setFieldData() {
        fieldItemViewSpy.setFieldData(FULL_PROPERTY_PATH, FACT_NAME, FIELD_NAME, FACT_MODEL_TREE.getFactName());
        verify(fieldNameElementMock, times(1)).setInnerHTML(eq(INNER_HTML));
        verify(fieldNameElementMock, times(1)).setAttribute(eq("id"), eq(ID_ATTRIBUTE));
        verify(fieldNameElementMock, times(1)).setAttribute(eq("fieldName"), eq(FIELD_NAME));
        verify(fieldNameElementMock, times(1)).setAttribute(eq("className"), eq(FACT_MODEL_TREE.getFactName()));
        verify(fieldNameElementMock, times(1)).setAttribute(eq("fullPath"), eq(FULL_PROPERTY_PATH));
    }

    @Test
    public void onFieldElementSelected() {
        fieldItemViewSpy.onFieldElementSelected();
        verify(lIElementMock, times(1)).addClassName(eq(ConstantHolder.SELECTED));
        verify(fieldItemPresenter, times(1)).onFieldElementClick(eq(fieldItemViewSpy));
    }

    @Test
    public void onFieldElementClicked() {
        InOrder inOrder = inOrder(fieldItemViewSpy, lIElementMock, fieldItemPresenter);
        fieldItemViewSpy.onFieldElementClick();
        inOrder.verify(lIElementMock, times(1)).addClassName(eq(ConstantHolder.SELECTED));
        inOrder.verify(fieldItemViewSpy, times(1)).showCheck(eq(true));
        inOrder.verify(fieldItemPresenter, times(1)).onFieldElementClick(eq(fieldItemViewSpy));
    }

    @Test
    public void showCheck() {
        fieldItemViewSpy.showCheck(true);
        verify(checkElementStyleMock, times(1)).setDisplay(eq(Style.Display.BLOCK));
        //
        reset();
        fieldItemViewSpy.showCheck(false);
        verify(checkElementStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
    }

    @Test
    public void isCheckShown() {
        when(checkElementStyleMock.getDisplay()).thenReturn(Style.Display.NONE.getCssName());
        assertFalse(fieldItemViewSpy.isCheckShown());
        //
        reset();
        when(checkElementStyleMock.getDisplay()).thenReturn(Style.Display.BLOCK.getCssName());
        assertTrue(fieldItemViewSpy.isCheckShown());
    }

    @Test
    public void unselect() {
        fieldItemViewSpy.unselect();
        verify(lIElementMock, times(1)).removeClassName(eq(ConstantHolder.SELECTED));
        verify(checkElementStyleMock, times(1)).setDisplay(eq(Style.Display.NONE));
    }
}