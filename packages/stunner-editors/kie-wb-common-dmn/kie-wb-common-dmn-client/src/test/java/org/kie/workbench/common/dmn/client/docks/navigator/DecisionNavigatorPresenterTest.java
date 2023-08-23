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

package org.kie.workbench.common.dmn.client.docks.navigator;

import java.util.ArrayList;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DomGlobal.SetTimeoutCallbackFn;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.common.KogitoChannelHelper;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter.DEFER_DELAY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionNavigatorPresenterTest {

    @Mock
    private DecisionNavigatorPresenter.View view;

    @Mock
    private DecisionNavigatorTreePresenter treePresenter;

    @Mock
    private DecisionComponents decisionComponents;

    @Mock
    private DecisionNavigatorObserver decisionNavigatorObserver;

    @Mock
    private TranslationService translationService;

    @Mock
    private KogitoChannelHelper kogitoChannelHelper;

    @Mock
    private DecisionNavigatorItemsProvider navigatorItemsProvider;

    @Mock
    private DMNDiagramsSession dmnDiagramsSession;

    private DecisionNavigatorPresenter presenter;

    @Before
    public void setup() {
        presenter = spy(new DecisionNavigatorPresenter(view,
                                                       treePresenter,
                                                       decisionComponents,
                                                       decisionNavigatorObserver,
                                                       translationService,
                                                       kogitoChannelHelper,
                                                       navigatorItemsProvider,
                                                       dmnDiagramsSession));
    }

    @Test
    public void testSetup() {

        presenter.setup();

        verify(presenter).initialize();
        verify(presenter).setupView();
    }

    @Test
    public void testInitialize() {

        presenter.initialize();

        verify(view).init(presenter);
        verify(decisionNavigatorObserver).init(presenter);
    }

    @Test
    public void testSetupViewWhenChannelIsVSCodeOrDefault() {
        final DecisionNavigatorTreePresenter.View treeView = mock(DecisionNavigatorTreePresenter.View.class);
        final DecisionComponents.View decisionComponentsView = mock(DecisionComponents.View.class);
        when(kogitoChannelHelper.isIncludedModelEnabled()).thenReturn(true);
        when(treePresenter.getView()).thenReturn(treeView);
        when(decisionComponents.getView()).thenReturn(decisionComponentsView);

        presenter.setupView();

        verify(view).setupMainTree(treeView);
        verify(view, atLeastOnce()).showDecisionComponentsContainer();
        verify(view, atLeastOnce()).setupDecisionComponents(decisionComponentsView);
        verify(view, never()).hideDecisionComponentsContainer();
    }

    @Test
    public void testSetupViewWhenChannelIsNotVSCodeOrDefault() {
        final DecisionNavigatorTreePresenter.View treeView = mock(DecisionNavigatorTreePresenter.View.class);
        final DecisionComponents.View decisionComponentsView = mock(DecisionComponents.View.class);
        when(kogitoChannelHelper.isIncludedModelEnabled()).thenReturn(false);
        when(treePresenter.getView()).thenReturn(treeView);
        when(decisionComponents.getView()).thenReturn(decisionComponentsView);

        presenter.setupView();

        verify(view).setupMainTree(treeView);
        verify(view, never()).showDecisionComponentsContainer();
        verify(view, never()).setupDecisionComponents(decisionComponentsView);
        verify(view, atLeastOnce()).hideDecisionComponentsContainer();
    }

    @Test
    public void testGetView() {
        assertEquals(view, presenter.getView());
    }

    @Test
    public void testGetTreePresenter() {
        assertEquals(treePresenter, presenter.getTreePresenter());
    }

    @Test
    public void testRefreshTreeView() {

        final ArrayList<DecisionNavigatorItem> items = new ArrayList<>();
        doReturn(items).when(presenter).getItems();

        presenter.refreshTreeView();

        verify(treePresenter).setupItems(items);
    }

    @Test
    public void testRefresh() {

        final ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);

        doReturn(true).when(dmnDiagramsSession).isSessionStatePresent();
        doNothing().when(presenter).defer(any());

        presenter.refresh();

        verify(presenter).defer(cmdCaptor.capture());

        cmdCaptor.getValue().execute();

        verify(presenter).refreshTreeView();
        verify(presenter).refreshComponentsView();
    }

    @Test
    public void testRefreshWhenSessionStateIsNotPresent() {

        final ArgumentCaptor<Command> cmdCaptor = ArgumentCaptor.forClass(Command.class);

        doReturn(false).when(dmnDiagramsSession).isSessionStatePresent();
        doNothing().when(presenter).defer(any());

        presenter.refresh();

        verify(presenter, never()).defer(any());
        verify(presenter, never()).refreshTreeView();
        verify(presenter, never()).refreshComponentsView();
    }

    @Test
    public void testRemoveAllElements() {
        presenter.removeAllElements();

        verify(treePresenter).removeAllItems();
        verify(decisionComponents).removeAllItems();
    }

    @Test
    public void testClearSelections() {
        presenter.clearSelections();

        verify(treePresenter).deselectItem();
    }

    @Test
    public void testOnRefreshDecisionComponents() {

        presenter.onRefreshDecisionComponents(mock(RefreshDecisionComponents.class));
        presenter.onRefreshDecisionComponents(mock(RefreshDecisionComponents.class));

        verify(decisionComponents, times(2)).refresh();
    }

    @Test
    public void testOnRefreshDecisionComponents_WhenRefreshComponentsIsSuspended() {

        presenter.setIsRefreshComponentsViewSuspended(true);
        presenter.onRefreshDecisionComponents(mock(RefreshDecisionComponents.class));
        presenter.onRefreshDecisionComponents(mock(RefreshDecisionComponents.class));

        verify(decisionComponents, never()).refresh();
    }

    @Test
    public void testOnRefreshDecisionComponents_WhenSuspendAndResume() {

        presenter.setIsRefreshComponentsViewSuspended(true);
        presenter.onRefreshDecisionComponents(mock(RefreshDecisionComponents.class));
        presenter.onRefreshDecisionComponents(mock(RefreshDecisionComponents.class));

        verify(decisionComponents, never()).refresh();

        presenter.setIsRefreshComponentsViewSuspended(false);
        presenter.onRefreshDecisionComponents(mock(RefreshDecisionComponents.class));
        presenter.onRefreshDecisionComponents(mock(RefreshDecisionComponents.class));

        verify(decisionComponents, times(2)).refresh();
    }

    @Test
    public void testDefer() {

        final Command cmd = mock(Command.class);
        final ArgumentCaptor<SetTimeoutCallbackFn> timeoutCaptor = ArgumentCaptor.forClass(SetTimeoutCallbackFn.class);

        doNothing().when(presenter).clearTimeout(anyInt());
        doReturn(0d).when(presenter).setTimeout(any(), anyInt());

        presenter.defer(cmd);

        verify(presenter).clearTimeout(0d);
        verify(presenter).setTimeout(timeoutCaptor.capture(), eq(DEFER_DELAY));

        timeoutCaptor.getValue().onInvoke();
        verify(cmd).execute();
    }
}
