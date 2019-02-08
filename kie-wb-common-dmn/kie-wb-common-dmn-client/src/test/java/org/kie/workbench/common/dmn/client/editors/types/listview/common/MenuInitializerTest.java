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

package org.kie.workbench.common.dmn.client.editors.types.listview.common;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.client.views.pfly.selectpicker.JQuery;
import org.uberfire.client.views.pfly.selectpicker.JQueryEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({JQuery.class})
@RunWith(PowerMockRunner.class)
public class MenuInitializerTest {

    @Mock
    private HTMLDivElement kebabMenu;

    private MenuInitializer initializer;

    @Before
    public void setup() {
        this.initializer = spy(new MenuInitializer(kebabMenu, ".dropdown"));
    }

    @Test
    public void testInit() {

        final JQuery jQuery = mock(JQuery.class);
        final JQuery.CallbackFunction moveDropDownToBody = mock(JQuery.CallbackFunction.class);
        final JQuery.CallbackFunction moveDropDownToKebabContainer = mock(JQuery.CallbackFunction.class);
        final Element dropdown = mock(Element.class);

        doReturn(dropdown).when(initializer).dropdown();
        doReturn(moveDropDownToBody).when(initializer).moveDropDownToBody();
        doReturn(moveDropDownToKebabContainer).when(initializer).moveDropDownToKebabContainer();

        mockStatic(JQuery.class);
        PowerMockito.when(JQuery.$(dropdown)).thenReturn(jQuery);

        initializer.init();

        verify(jQuery).on("show.bs.dropdown", moveDropDownToBody);
        verify(jQuery).on("hidden.bs.dropdown", moveDropDownToKebabContainer);
    }

    @Test
    public void testMoveDropDownToBody() {

        final HTMLElement modalInElement = mock(HTMLElement.class);
        final HTMLElement target = mock(HTMLElement.class);
        final JQuery jQueryModalIn = mock(JQuery.class);
        final JQuery jQueryTarget = mock(JQuery.class);
        final JQuery jQueryCSS = mock(JQuery.class);
        final JQuery jQueryDetach = mock(JQuery.class);
        final JQueryEvent event = mock(JQueryEvent.class);
        final JSONObject jsonObjectProperties = mock(JSONObject.class);
        final JavaScriptObject javaScriptObjectProperties = mock(JavaScriptObject.class);

        event.target = target;
        doReturn(jsonObjectProperties).when(initializer).bodyDropdownProperties(event);
        doReturn(modalInElement).when(initializer).body();
        when(jsonObjectProperties.getJavaScriptObject()).thenReturn(javaScriptObjectProperties);
        when(jQueryTarget.css(javaScriptObjectProperties)).thenReturn(jQueryCSS);
        when(jQueryCSS.detach()).thenReturn(jQueryDetach);

        mockStatic(JQuery.class);
        PowerMockito.when(JQuery.$(modalInElement)).thenReturn(jQueryModalIn);
        PowerMockito.when(JQuery.$(target)).thenReturn(jQueryTarget);

        initializer.moveDropDownToBody().call(event);

        verify(jQueryModalIn).append(jQueryDetach);
    }

    @Test
    public void testMoveDropDownToKebabContainer() {

        final HTMLElement modalInElement = mock(HTMLElement.class);
        final HTMLElement target = mock(HTMLElement.class);
        final JQuery jQueryModalIn = mock(JQuery.class);
        final JQuery jQueryTarget = mock(JQuery.class);
        final JQuery jQueryCSS = mock(JQuery.class);
        final JQuery jQueryDetach = mock(JQuery.class);
        final JQueryEvent event = mock(JQueryEvent.class);
        final JSONObject jsonObjectProperties = mock(JSONObject.class);
        final JavaScriptObject javaScriptObjectProperties = mock(JavaScriptObject.class);

        event.target = target;
        doReturn(jsonObjectProperties).when(initializer).emptyProperties();
        doReturn(modalInElement).when(initializer).body();
        when(jsonObjectProperties.getJavaScriptObject()).thenReturn(javaScriptObjectProperties);
        when(jQueryTarget.css(javaScriptObjectProperties)).thenReturn(jQueryCSS);
        when(jQueryCSS.detach()).thenReturn(jQueryDetach);

        mockStatic(JQuery.class);
        PowerMockito.when(JQuery.$(kebabMenu)).thenReturn(jQueryModalIn);
        PowerMockito.when(JQuery.$(target)).thenReturn(jQueryTarget);

        initializer.moveDropDownToKebabContainer().call(event);

        verify(jQueryModalIn).append(jQueryDetach);
    }

    @Test
    public void testBodyDropdownProperties() {

        final JQueryEvent event = mock(JQueryEvent.class);
        final HTMLElement target = mock(HTMLElement.class);
        final double left = 1;
        final double top = 1;
        final double zIndex = 1051;
        final JSONObject expectedJSONObject = mock(JSONObject.class);

        // Mock "JSONObject" since it relies on a native implementation.
        doReturn(expectedJSONObject).when(initializer).makeJsonObject();

        event.target = target;
        doReturn(left).when(initializer).offsetLeft(target);
        doReturn(top).when(initializer).offsetTop(target);
        doReturn(top).when(initializer).offsetTop(target);

        final JSONObject actualJSONObject = initializer.bodyDropdownProperties(event);

        verify(expectedJSONObject).put("position", new JSONString("absolute"));
        verify(expectedJSONObject).put("left", new JSONNumber(left));
        verify(expectedJSONObject).put("top", new JSONNumber(top));
        verify(expectedJSONObject).put("z-index", new JSONNumber(zIndex));
        assertEquals(expectedJSONObject, actualJSONObject);
    }

    @Test
    public void testEmptyProperties() {

        final JSONObject expectedJSONObject = mock(JSONObject.class);

        // Mock "JSONObject" since it relies on a native implementation.
        doReturn(expectedJSONObject).when(initializer).makeJsonObject();

        final JSONObject actualJSONObject = initializer.emptyProperties();

        verify(expectedJSONObject).put("position", new JSONString(""));
        verify(expectedJSONObject).put("left", new JSONString(""));
        verify(expectedJSONObject).put("top", new JSONString(""));
        verify(expectedJSONObject).put("z-index", new JSONString(""));
        assertEquals(expectedJSONObject, actualJSONObject);
    }
}
