/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dashbuilder.client.editor.external;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.external.model.ExternalComponent;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ExternalComponentGroupProviderTest {

    @Mock
    ExternalDragComponent drag1;
    @Mock
    SyncBeanDef<ExternalDragComponent> c1Bean;
    @Mock
    ExternalComponent c1;

    @Mock
    ExternalDisplayerDragComponent drag2;
    @Mock
    SyncBeanDef<ExternalDisplayerDragComponent> c2Bean;
    @Mock
    ExternalComponent c2;

    @Mock
    SyncBeanManager beanManager;

    @InjectMocks
    ExternalComponentGroupProvider externalComponentGroupProvider;

    @Test
    public void testProduceDragComponent() {
        when(c1Bean.getInstance()).thenReturn(drag1);
        when(beanManager.lookupBean(eq(ExternalDragComponent.class))).thenReturn(c1Bean);

        when(c2Bean.getInstance()).thenReturn(drag2);
        when(beanManager.lookupBean(eq(ExternalDisplayerDragComponent.class))).thenReturn(c2Bean);

        when(c1.isNoData()).thenReturn(false);
        when(c2.isNoData()).thenReturn(true);

        externalComponentGroupProvider.produceDragComponent(c1);
        externalComponentGroupProvider.produceDragComponent(c2);

        verify(beanManager).lookupBean(eq(ExternalDisplayerDragComponent.class));
        verify(beanManager).lookupBean(eq(ExternalDragComponent.class));

    }
}