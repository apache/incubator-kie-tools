/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.provider.status;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.provider.status.runtime.RuntimePresenter;
import org.guvnor.ala.ui.model.PipelineExecutionTrace;
import org.guvnor.ala.ui.model.PipelineExecutionTraceKey;
import org.guvnor.ala.ui.model.Runtime;
import org.guvnor.ala.ui.model.RuntimeKey;
import org.guvnor.ala.ui.model.RuntimeListItem;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProviderStatusPresenterTest {

    private static int ITEMS_COUNT = 5;

    @Mock
    private ProviderStatusPresenter.View view;

    @Mock
    private ManagedInstance<RuntimePresenter> runtimePresenterInstance;

    private ProviderStatusPresenter presenter;

    private List<RuntimePresenter> runtimePresenters;

    @Before
    public void setUp() {
        runtimePresenters = new ArrayList<>();
        presenter = new ProviderStatusPresenter(view,
                                                runtimePresenterInstance) {
            @Override
            protected RuntimePresenter newRuntimePresenter() {
                RuntimePresenter runtimePresenter = mock(RuntimePresenter.class);
                RuntimePresenter.View view = mock(RuntimePresenter.View.class);
                when(runtimePresenter.getView()).thenReturn(view);
                runtimePresenters.add(runtimePresenter);
                when(runtimePresenterInstance.get()).thenReturn(runtimePresenter);
                return super.newRuntimePresenter();
            }
        };
        presenter.init();
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void testSetupItems() {
        List<RuntimeListItem> items = mockItems(ITEMS_COUNT);
        presenter.setupItems(items);

        verify(runtimePresenterInstance,
               times(ITEMS_COUNT)).get();
        verify(view,
               times(ITEMS_COUNT)).addListItem(any());

        for (int i = 0; i < ITEMS_COUNT; i++) {
            verify(runtimePresenters.get(i),
                   times(1)).setup(items.get(i));
            verify(view,
                   times(1)).addListItem(runtimePresenters.get(i).getView());
        }
    }

    @Test
    public void testRemoveExistingRuntime() {
        RuntimeKey keyToRemove = mock(RuntimeKey.class);
        List<RuntimeListItem> items = mockItems(ITEMS_COUNT);
        //pick an arbitrary element as the existing runtime.
        int index = 1;
        RuntimeListItem item = items.get(index);
        Runtime runtime = mock(Runtime.class);
        when(runtime.getKey()).thenReturn(keyToRemove);
        when(item.isRuntime()).thenReturn(true);
        when(item.getRuntime()).thenReturn(runtime);

        presenter.setupItems(items);
        for (int i = 0; i < ITEMS_COUNT; i++) {
            when(runtimePresenters.get(i).getItem()).thenReturn(items.get(i));
        }
        assertTrue(presenter.removeItem(keyToRemove));
        verify(runtimePresenterInstance,
               times(1)).destroy(runtimePresenters.get(index));
        verify(view,
               times(1)).removeListItem(runtimePresenters.get(index).getView());
    }

    @Test
    public void testRemoveNonExistingRuntime() {
        RuntimeKey keyToRemove = mock(RuntimeKey.class);
        List<RuntimeListItem> items = mockItems(ITEMS_COUNT);

        presenter.setupItems(items);
        for (int i = 0; i < ITEMS_COUNT; i++) {
            when(runtimePresenters.get(i).getItem()).thenReturn(items.get(i));
        }
        assertFalse(presenter.removeItem(keyToRemove));
        verify(runtimePresenterInstance,
               never()).destroy(anyObject());
        verify(view,
               never()).removeListItem(anyObject());
    }

    @Test
    public void testRemoveExistingPipelineExecution() {
        PipelineExecutionTraceKey keyToRemove = mock(PipelineExecutionTraceKey.class);
        List<RuntimeListItem> items = mockItems(ITEMS_COUNT);
        //pick an arbitrary element as the existing pipeline execution.
        int index = 2;
        RuntimeListItem item = items.get(index);
        PipelineExecutionTrace pipelineExecutionTrace = mock(PipelineExecutionTrace.class);
        when(pipelineExecutionTrace.getKey()).thenReturn(keyToRemove);
        when(item.isRuntime()).thenReturn(false);
        when(item.getPipelineTrace()).thenReturn(pipelineExecutionTrace);

        presenter.setupItems(items);
        for (int i = 0; i < ITEMS_COUNT; i++) {
            when(runtimePresenters.get(i).getItem()).thenReturn(items.get(i));
        }
        assertTrue(presenter.removeItem(keyToRemove));
        verify(runtimePresenterInstance,
               times(1)).destroy(runtimePresenters.get(index));
        verify(view,
               times(1)).removeListItem(runtimePresenters.get(index).getView());
    }

    @Test
    public void testRemoveNonExistingPipelineExecution() {
        PipelineExecutionTraceKey keyToRemove = mock(PipelineExecutionTraceKey.class);
        List<RuntimeListItem> items = mockItems(ITEMS_COUNT);

        presenter.setupItems(items);
        for (int i = 0; i < ITEMS_COUNT; i++) {
            when(runtimePresenters.get(i).getItem()).thenReturn(items.get(i));
        }
        assertFalse(presenter.removeItem(keyToRemove));
        verify(runtimePresenterInstance,
               never()).destroy(anyObject());
        verify(view,
               never()).removeListItem(anyObject());
    }

    @Test
    public void testIsEmptyWhenNoItems() {
        presenter.setupItems(new ArrayList<>());
        assertTrue(presenter.isEmpty());
    }

    @Test
    public void testIsEmptyWhenItems() {
        presenter.setupItems(mockItems(ITEMS_COUNT));
        assertFalse(presenter.isEmpty());
    }

    @Test
    public void testClear() {
        List<RuntimeListItem> items = mockItems(ITEMS_COUNT);
        presenter.setupItems(items);

        presenter.clear();
        verify(view,
               times(2)).clear();
        runtimePresenters.forEach(runtimePresenter -> verify(runtimePresenterInstance,
                                                             times(1)).destroy(runtimePresenter));
        verify(runtimePresenterInstance,
               times(ITEMS_COUNT)).destroy(any());
    }

    private List<RuntimeListItem> mockItems(int count) {
        List<RuntimeListItem> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(mock(RuntimeListItem.class));
        }
        return items;
    }
}
