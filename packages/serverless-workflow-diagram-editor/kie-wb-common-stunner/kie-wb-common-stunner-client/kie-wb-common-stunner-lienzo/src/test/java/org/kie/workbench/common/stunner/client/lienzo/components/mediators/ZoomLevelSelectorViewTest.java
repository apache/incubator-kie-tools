/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.client.lienzo.components.mediators;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Event;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import io.crysknife.client.ManagedInstance;
import io.crysknife.ui.translation.api.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.validation.DiagramElementNameProvider;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ZoomLevelSelectorViewTest {

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
    private HTMLElement dropDownText;

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
        tested.onIncreaseLevel(mock(Event.class));
        verify(presenter, times(1)).onIncreaseLevel();
    }

    @Test
    public void testOnDecreaseLevel() {
        tested.init(presenter);
        tested.onDecreaseLevel(mock(Event.class));
        verify(presenter, times(1)).onDecreaseLevel();
    }

    @Test
    public void testOnReset() {
        tested.init(presenter);
        tested.onReset(mock(Event.class));
        verify(presenter, times(1)).onScaleToFitSize();
    }

    @Test
    public void testOnDropDownKeyEvents() {
        tested.init(presenter);
        Event keyDownEvent = mock(Event.class);
        tested.onDropDownKeyDown(keyDownEvent);
        verify(keyDownEvent, times(1)).preventDefault();
        verify(keyDownEvent, times(1)).stopPropagation();
        Event keyUpEvent = mock(Event.class);
        tested.onDropDownKeyUp(keyUpEvent);
        verify(keyUpEvent, times(1)).preventDefault();
        verify(keyUpEvent, times(1)).stopPropagation();
        Event keyPressEvent = mock(Event.class);
        tested.onDropDownKeyPress(keyPressEvent);
        verify(keyPressEvent, times(1)).preventDefault();
        verify(keyPressEvent, times(1)).stopPropagation();
    }

    @Test
    public void testAdd() {
        ZoomLevelSelectorItem item = mock(ZoomLevelSelectorItem.class);
        HTMLElement itemElement = mock(HTMLElement.class);
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
}
