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


package org.kie.workbench.common.stunner.kogito.client.services.util.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.appformer.kogito.bridge.client.resource.interop.ResourceContentOptions;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class WorkItemIconCacheImplTest {

    private static String ICON_1 = "icon1.png";
    private static String ICON_2 = "icon2.png";

    @Mock
    private ResourceContentService resourceContentService;

    private Promises promises;

    private WorkItemIconCacheImpl cache;

    private Promise.PromiseExecutorCallbackFn.ResolveCallbackFn<Object> promiseResolver;

    @Before
    public void init() {
        promises = new SyncPromises();
        when(resourceContentService.get(anyString(), any(ResourceContentOptions.class))).thenAnswer(invocationOnMock -> {
            return promises.create((resolve, reject) -> {
                promiseResolver = resolve;
            });
        });
        cache = new WorkItemIconCacheImpl(resourceContentService);
    }

    @Test
    public void testGetSingleIcon() {
        Promise<String> promise = cache.getIcon(ICON_1);

        verify(resourceContentService).get(anyString(), any());

        Assertions.assertThat(promise).isNotNull();
    }

    @Test
    public void testGetDifferentIcons() {
        Promise<String> promise1 = cache.getIcon(ICON_1);
        verify(resourceContentService).get(anyString(), any());
        Assertions.assertThat(promise1).isNotNull();

        Promise<String> promise2 = cache.getIcon(ICON_2);
        verify(resourceContentService, times(2)).get(anyString(), any());
        Assertions.assertThat(promise2)
                .isNotNull()
                .isNotSameAs(promise1);
    }

    @Test
    public void testGetSameIcon() {
        Promise<String> promise1 = cache.getIcon(ICON_1);
        verify(resourceContentService).get(anyString(), any());
        Assertions.assertThat(promise1).isNotNull();

        Promise<String> promise2 = cache.getIcon(ICON_1);
        verify(resourceContentService, times(1)).get(anyString(), any());
        Assertions.assertThat(promise2)
                .isNotNull()
                .isSameAs(promise1);
    }
}
