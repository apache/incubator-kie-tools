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

package org.kie.workbench.common.dmn.client.editors.included.grid;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageState;
import org.kie.workbench.common.dmn.client.editors.included.grid.empty.DMNCardsEmptyStateView;
import org.kie.workbench.common.widgets.client.cards.CardsGridComponent;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNCardsGridComponentTest {

    @Mock
    private ManagedInstance<DMNCardComponent> dmnCardComponent;

    @Mock
    private CardsGridComponent cardsGridComponent;

    @Mock
    private IncludedModelsPageState pageState;

    @Mock
    private DMNCardsEmptyStateView emptyStateView;

    private DMNCardsGridComponent grid;

    @Before
    public void setup() {
        grid = new DMNCardsGridComponent(dmnCardComponent, cardsGridComponent, pageState, emptyStateView);
    }

    @Test
    public void testInit() {

        final HTMLElement element = mock(HTMLElement.class);
        when(emptyStateView.getElement()).thenReturn(element);

        grid.init();

        verify(cardsGridComponent).setEmptyState(element);
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);
        when(cardsGridComponent.getElement()).thenReturn(expectedElement);

        final HTMLElement actualElement = grid.getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testRefresh() {

        final IncludedModel includedModel1 = mock(IncludedModel.class);
        final IncludedModel includedModel2 = mock(IncludedModel.class);
        final DMNCardComponent card1 = mock(DMNCardComponent.class);
        final DMNCardComponent card2 = mock(DMNCardComponent.class);
        final List<IncludedModel> includedModels = asList(includedModel1, includedModel2);

        when(pageState.generateIncludedModels()).thenReturn(includedModels);
        when(dmnCardComponent.get()).thenReturn(card1, card2);

        grid.refresh();

        verify(card1).setup(grid, includedModel1);
        verify(card2).setup(grid, includedModel2);
        verify(cardsGridComponent).setupCards(asList(card1, card2));
    }
}
