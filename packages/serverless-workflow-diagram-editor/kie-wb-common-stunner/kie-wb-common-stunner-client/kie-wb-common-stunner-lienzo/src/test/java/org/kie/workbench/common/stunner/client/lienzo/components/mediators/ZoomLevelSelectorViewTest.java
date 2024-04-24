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


package org.kie.workbench.common.stunner.client.lienzo.components.mediators;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLUListElement;
import elemental2.dom.KeyboardEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.j2cl.tools.di.ui.translation.client.TranslationService;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.theme.StunnerColorTheme;
import org.kie.workbench.common.stunner.core.client.theme.StunnerTheme;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.validation.DiagramElementNameProvider;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ZoomLevelSelectorViewTest {

    @Mock
    private HTMLButtonElement previewButton;

    @Mock
    private HTMLButtonElement decreaseButton;

    @Mock
    private HTMLButtonElement increaseButton;

    @Mock
    private HTMLButtonElement resetButton;

    @Mock
    private HTMLDivElement dropDownPanelGroup;

    @Mock
    private HTMLDivElement dropDownPanel;

    @Mock
    private HTMLButtonElement dropDownButton;

    @Mock
    private HTMLLIElement dropDownText;

    @Mock
    private HTMLUListElement dropDownMenu;

    @Mock
    private ManagedInstance<ZoomLevelSelectorItem> items;

    @Mock
    private ZoomLevelSelector presenter;

    @Mock
    private TranslationService translationService;

    @Mock
    private ManagedInstance<DiagramElementNameProvider> elementNameProviders;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private DefinitionUtils definitionUtils;

    private ZoomLevelSelectorView tested;

    @Before
    public void setUp() {
        tested = new ZoomLevelSelectorView();
        tested.previewButton = previewButton;
        tested.decreaseButton = decreaseButton;
        tested.increaseButton = increaseButton;
        tested.resetButton = resetButton;
        tested.dropDownPanelGroup = dropDownPanelGroup;
        tested.dropDownPanel = dropDownPanel;
        tested.dropDownButton = dropDownButton;
        tested.dropDownText = dropDownText;
        tested.dropDownMenu = dropDownMenu;
        tested.items = items;
        tested.translationService = new ClientTranslationService(translationService, elementNameProviders, sessionManager, definitionUtils);
    }

    @Test
    public void testOnIncreaseLevel() {
        tested.init(presenter);
        tested.onIncreaseLevel(mock(elemental2.dom.Event.class));
        verify(presenter, times(1)).onIncreaseLevel();
    }

    @Test
    public void testOnDecreaseLevel() {
        tested.init(presenter);
        tested.onDecreaseLevel(mock(elemental2.dom.Event.class));
        verify(presenter, times(1)).onDecreaseLevel();
    }

    @Test
    public void testOnReset() {
        tested.init(presenter);
        tested.onReset(mock(elemental2.dom.Event.class));
        verify(presenter, times(1)).onScaleToFitSize();
    }

    @Test
    public void testOnDropDownKeyEvents() {
        tested.init(presenter);
        KeyboardEvent keyDownEvent = mock(elemental2.dom.KeyboardEvent.class);
        tested.onDropDownKeyDown(keyDownEvent);
        verify(keyDownEvent, times(1)).preventDefault();
        verify(keyDownEvent, times(1)).stopPropagation();
        KeyboardEvent keyUpEvent = mock(KeyboardEvent.class);
        tested.onDropDownKeyUp(keyUpEvent);
        verify(keyUpEvent, times(1)).preventDefault();
        verify(keyUpEvent, times(1)).stopPropagation();
        KeyboardEvent keyPressEvent = mock(KeyboardEvent.class);
        tested.onDropDownKeyPress(keyPressEvent);
        verify(keyPressEvent, times(1)).preventDefault();
        verify(keyPressEvent, times(1)).stopPropagation();
    }

    @Test
    public void testAdd() {
        ZoomLevelSelectorItem item = mock(ZoomLevelSelectorItem.class);
        HTMLLIElement itemElement = mock(HTMLLIElement.class);
        when(item.getElement()).thenReturn(itemElement);
        when(items.get()).thenReturn(item);
        Command c = mock(Command.class);
        tested.add("item1", c);
        verify(item, times(1)).setText(eq("item1"));
        verify(item, times(1)).setOnClick(eq(c));
        verify(dropDownMenu, times(1)).appendChild(eq(itemElement));
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(items, times(1)).destroyAll();
    }

    @Test
    public void testApplyThemeLight() {
        StunnerColorTheme theme = mock(StunnerColorTheme.class);
        when(theme.isDarkTheme()).thenReturn(false);
        StunnerTheme.setTheme(theme);

        tested.init(presenter);
        tested.applyTheme();

        assertEquals(ZoomLevelSelectorView.BUTTON_LIGHT_STYLE, tested.previewButton.className);
        assertEquals(ZoomLevelSelectorView.BUTTON_LIGHT_STYLE, tested.resetButton.className);
        assertEquals(ZoomLevelSelectorView.BUTTON_LIGHT_STYLE, tested.increaseButton.className);
        assertEquals(ZoomLevelSelectorView.BUTTON_LIGHT_STYLE, tested.decreaseButton.className);
        assertEquals(ZoomLevelSelectorView.DROPDOWN_BUTTON_LIGHT_STYLE, tested.dropDownButton.className);
        assertEquals(ZoomLevelSelectorView.DROPDOWN_TEXT_LIGHT_STYLE, tested.dropDownText.className);
        assertEquals(ZoomLevelSelectorView.DROPDOWN_PANEL_LIGHT_STYLE, tested.dropDownPanel.className);
        assertEquals(ZoomLevelSelectorView.DROPDOWN_MENU_LIGHT_STYLE, tested.dropDownMenu.className);
    }

    @Test
    public void testApplyThemeDark() {
        StunnerColorTheme theme = mock(StunnerColorTheme.class);
        when(theme.isDarkTheme()).thenReturn(true);
        StunnerTheme.setTheme(theme);

        tested.init(presenter);
        tested.applyTheme();

        assertEquals(ZoomLevelSelectorView.BUTTON_DARK_STYLE, tested.previewButton.className);
        assertEquals(ZoomLevelSelectorView.BUTTON_DARK_STYLE, tested.resetButton.className);
        assertEquals(ZoomLevelSelectorView.BUTTON_DARK_STYLE, tested.increaseButton.className);
        assertEquals(ZoomLevelSelectorView.BUTTON_DARK_STYLE, tested.decreaseButton.className);
        assertEquals(ZoomLevelSelectorView.DROPDOWN_BUTTON_DARK_STYLE, tested.dropDownButton.className);
        assertEquals(ZoomLevelSelectorView.DROPDOWN_TEXT_DARK_STYLE, tested.dropDownText.className);
        assertEquals(ZoomLevelSelectorView.DROPDOWN_PANEL_DARK_STYLE, tested.dropDownPanel.className);
        assertEquals(ZoomLevelSelectorView.DROPDOWN_MENU_DARK_STYLE, tested.dropDownMenu.className);
    }
}
