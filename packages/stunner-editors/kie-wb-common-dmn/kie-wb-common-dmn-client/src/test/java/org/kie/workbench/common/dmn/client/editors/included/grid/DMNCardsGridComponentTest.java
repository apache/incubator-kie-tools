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

package org.kie.workbench.common.dmn.client.editors.included.grid;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.appformer.kogito.bridge.client.workspace.WorkspaceService;
import org.assertj.core.api.Assertions;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.common.KogitoChannelHelper;
import org.kie.workbench.common.dmn.client.editors.included.BaseIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.DMNIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.DefaultIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageState;
import org.kie.workbench.common.dmn.client.editors.included.PMMLIncludedModelActiveRecord;
import org.kie.workbench.common.dmn.client.editors.included.grid.empty.DMNCardsEmptyStateView;
import org.kie.workbench.common.widgets.client.cards.CardsGridComponent;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNCardsGridComponentTest {

    @Mock
    private ManagedInstance<DMNCardComponent> dmnCardComponent;

    @Mock
    private ManagedInstance<PMMLCardComponent> pmmlCardComponent;

    @Mock
    private ManagedInstance<DefaultCardComponent> defaultCardComponent;

    @Mock
    private CardsGridComponent cardsGridComponent;

    @Mock
    private IncludedModelsPageState pageState;

    @Mock
    private DMNCardsEmptyStateView emptyStateView;

    @Mock
    private WorkspaceService workspaceService;

    @Mock
    private KogitoChannelHelper kogitoChannelHelper;

    private DMNCardsGridComponent grid;

    @Before
    public void setup() {
        grid = new DMNCardsGridComponent(dmnCardComponent,
                                         pmmlCardComponent,
                                         defaultCardComponent,
                                         cardsGridComponent,
                                         pageState,
                                         emptyStateView,
                                         workspaceService,
                                         kogitoChannelHelper);
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

        final DefaultIncludedModelActiveRecord includedModel1 = mock(DefaultIncludedModelActiveRecord.class);
        final DMNIncludedModelActiveRecord includedModel2 = mock(DMNIncludedModelActiveRecord.class);
        final PMMLIncludedModelActiveRecord includedModel3 = mock(PMMLIncludedModelActiveRecord.class);
        final DefaultCardComponent card1 = mock(DefaultCardComponent.class);
        final DMNCardComponent card2 = mock(DMNCardComponent.class);
        final PMMLCardComponent card3 = mock(PMMLCardComponent.class);
        final List<BaseIncludedModelActiveRecord> includedModels = asList(includedModel1, includedModel2, includedModel3);

        when(pageState.generateIncludedModels()).thenReturn(includedModels);
        when(defaultCardComponent.get()).thenReturn(card1);
        when(dmnCardComponent.get()).thenReturn(card2);
        when(pmmlCardComponent.get()).thenReturn(card3);

        grid.refresh();

        verify(card1).setup(grid, includedModel1);
        verify(card2).setup(grid, includedModel2);
        verify(card3).setup(grid, includedModel3);
        verify(cardsGridComponent).setupCards(asList(card1, card2, card3));
    }

    @Test
    public void testPresentPathAsLink() {
        doReturn(true).when(kogitoChannelHelper).isIncludedModelLinkEnabled();
        Assertions.assertThat(grid.presentPathAsLink()).isTrue();

        doReturn(false).when(kogitoChannelHelper).isIncludedModelLinkEnabled();
        Assertions.assertThat(grid.presentPathAsLink()).isFalse();
    }

    @Test
    public void testOpenPathLink() {
        final String file = "file://xyz";
        grid.openPathLink(file);

        verify(workspaceService).openFile(file);
    }
}
