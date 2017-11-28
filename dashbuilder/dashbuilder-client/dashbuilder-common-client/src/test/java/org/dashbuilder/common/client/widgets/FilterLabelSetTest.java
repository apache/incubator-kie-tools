/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.common.client.widgets;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FilterLabelSetTest {

    @Mock
    FilterLabelSet.View view;

    @Mock
    FilterLabel filterLabel;

    @Mock
    SyncBeanDef<FilterLabel> filterLabelBean;

    @Mock
    SyncBeanManager beanManager;

    FilterLabelSet presenter;

    @Before
    public void setUp() {
        when(beanManager.lookupBean(FilterLabel.class)).thenReturn(filterLabelBean);
        when(filterLabelBean.newInstance()).thenReturn(filterLabel);
        presenter = new FilterLabelSet(view, beanManager);
    }

    @Test
    public void testClearAll() {
        presenter.clear();
        verify(view, never()).setClearAllEnabled(true);
        verify(view, never()).addLabel(any());

        presenter.addLabel("a");
        verify(view, never()).setClearAllEnabled(true);
        verify(view, times(1)).addLabel(any());

        presenter.addLabel("b");
        verify(view).setClearAllEnabled(true);
        verify(view, times(2)).addLabel(any());
    }
}