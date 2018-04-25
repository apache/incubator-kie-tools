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

package org.kie.workbench.common.stunner.client.widgets.menu;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.workbench.model.menu.impl.BaseMenuCustom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MenuUtilsTest {

    @Mock
    private MenuUtils.HasEnabledIsWidget hasEnabledIsWidget;

    @Mock
    private IsWidget isWidget;

    @Mock
    private Widget widget;

    @Mock
    private Button button;

    @Mock
    private ButtonGroup buttonGroup;

    @Before
    public void setUp() {
        when(hasEnabledIsWidget.asWidget()).thenReturn(widget);
        when(isWidget.asWidget()).thenReturn(widget);
    }

    @Test
    public void testBuildItemWithHasEnabledIsWidget() {
        BaseMenuCustom menuItem = (BaseMenuCustom) MenuUtils.buildItem(hasEnabledIsWidget);
        assertEquals(widget,
                     menuItem.build());

        menuItem.setEnabled(true);
        verify(hasEnabledIsWidget,
               times(1)).setEnabled(true);

        menuItem.setEnabled(false);
        verify(hasEnabledIsWidget,
               times(1)).setEnabled(false);

        when(hasEnabledIsWidget.isEnabled()).thenReturn(true);
        assertTrue(menuItem.isEnabled());
    }

    @Test
    public void testBuildItemWithNormalIsWidget() {
        BaseMenuCustom menuItem = (BaseMenuCustom) MenuUtils.buildItem(isWidget);
        assertEquals(widget,
                     menuItem.build());

        menuItem.setEnabled(true);
        assertTrue(menuItem.isEnabled());

        menuItem.setEnabled(false);
        assertFalse(menuItem.isEnabled());
    }

    @Test
    public void testBuildItemWithButtonGroup() {
        final MenuUtils.HasEnabledIsWidget wrappedButtonGroup = MenuUtils.buildHasEnabledWidget(buttonGroup, button);
        final BaseMenuCustom menuItem = (BaseMenuCustom) MenuUtils.buildItem(wrappedButtonGroup);
        assertEquals(buttonGroup,
                     menuItem.build());

        menuItem.setEnabled(true);
        verify(button,
               times(1)).setEnabled(true);

        menuItem.setEnabled(false);
        verify(button,
               times(1)).setEnabled(false);

        when(button.isEnabled()).thenReturn(true);
        assertTrue(menuItem.isEnabled());
    }
}
