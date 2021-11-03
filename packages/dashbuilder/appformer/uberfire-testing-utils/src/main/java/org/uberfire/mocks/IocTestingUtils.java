/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.mocks;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Utility class to SyncBeanManager mocking.
 */
public class IocTestingUtils {

    /**
     * Mocks a SyncBeanManager to return a mocked bean when lookupBean is called.
     * @param iocManager
     */
    public static void mockIocManager(SyncBeanManager iocManager) {
        doAnswer(invocationOnMock -> createSyncBeanDef((Class<?>) invocationOnMock.getArguments()[0]))
                .when(iocManager).lookupBean(any(Class.class));
    }

    private static <T> SyncBeanDef<T> createSyncBeanDef(Class<T> clazz) {
        final SyncBeanDef syncBeanDef = mock(SyncBeanDef.class);
        doReturn(mock(clazz)).when(syncBeanDef).getInstance();
        doReturn(mock(clazz)).when(syncBeanDef).newInstance();

        return syncBeanDef;
    }
}
