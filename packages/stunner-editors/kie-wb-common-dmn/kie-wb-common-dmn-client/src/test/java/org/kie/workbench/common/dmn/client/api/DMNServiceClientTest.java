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

package org.kie.workbench.common.dmn.client.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DMNServiceClientTest {

    @Mock
    private DMNClientServicesProxy proxy;

    @Mock
    private DMNServiceClient client;

    @Before
    public void setup() {
        doCallRealMethod().when(client).callback(any());
        doReturn(proxy).when(client).getClientServicesProxy();
    }

    @Test
    public void testCallback() {

        final Consumer consumer = mock(Consumer.class);
        final ServiceCallback service = client.callback(consumer);

        final List item = mock(List.class);
        service.onSuccess(item);

        verify(consumer, atLeastOnce()).accept(item);

        final ClientRuntimeError error = mock(ClientRuntimeError.class);
        service.onError(error);

        verify(proxy).logWarning(error);
        verify(consumer, atLeastOnce()).accept(any(ArrayList.class));
    }
}