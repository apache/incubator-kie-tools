/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.webapp.client.dmn;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class KogitoDMNServiceRuntimeImplTest {

    @Mock
    private KogitoResourceContentService resourceContentServiceMock;
    @Mock
    private Path pathMock;
    @Mock
    private RemoteCallback remoteCallbackMock;
    @Mock
    private ErrorCallback errorCallbackMock;

    private KogitoDMNServiceRuntimeImpl kogitoDMNServiceRuntimeImplSpy;

    @Before
    public void setup() {
        when(pathMock.toURI()).thenReturn("uri");
        kogitoDMNServiceRuntimeImplSpy = spy(new KogitoDMNServiceRuntimeImpl() {
            {
                this.resourceContentService = resourceContentServiceMock;
            }
        });
    }

    @Test
    public void getDMNContent() {
        kogitoDMNServiceRuntimeImplSpy.getDMNContent(pathMock, remoteCallbackMock, errorCallbackMock);
        verify(resourceContentServiceMock, times(1)).loadFile(eq("uri"), eq(remoteCallbackMock), eq(errorCallbackMock));
    }
}
