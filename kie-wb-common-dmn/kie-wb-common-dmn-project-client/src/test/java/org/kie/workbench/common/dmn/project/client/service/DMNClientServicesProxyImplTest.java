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

package org.kie.workbench.common.dmn.project.client.service;

import java.util.List;
import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedModelsService;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.api.editors.types.DMNParseService;
import org.kie.workbench.common.dmn.api.editors.types.DMNValidationService;
import org.kie.workbench.common.dmn.api.editors.types.DataObjectsService;
import org.kie.workbench.common.dmn.api.editors.types.TimeZoneService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNClientServicesProxyImplTest {

    @Mock
    private CallerMock<DMNIncludedModelsService> includedModelsServiceCaller;

    @Mock
    private CallerMock<DMNParseService> parseServiceCaller;

    @Mock
    private CallerMock<DMNValidationService> validationServiceCaller;

    @Mock
    private CallerMock<TimeZoneService> timeZoneServiceCaller;

    @Mock
    private Caller<DataObjectsService> dataObjectsServiceCaller;

    @Mock
    private DMNIncludedModelsService includedModelsService;

    @Mock
    private DMNParseService parseService;

    @Mock
    private DMNValidationService validationService;

    @Mock
    private TimeZoneService timeZoneService;

    @Mock
    private WorkspaceProjectContext projectContext;

    @Mock
    private ServiceCallback serviceCallback;

    @Mock
    private WorkspaceProject workspaceProject;

    @Mock
    private Path dmnModelPath;

    @Captor
    private ArgumentCaptor<ClientRuntimeError> clientRuntimeErrorArgumentCaptor;

    private DMNClientServicesProxyImpl clientServicesProxy;

    @Before
    public void setup() {
        clientServicesProxy = spy(new DMNClientServicesProxyImpl(projectContext,
                                                                 includedModelsServiceCaller,
                                                                 parseServiceCaller,
                                                                 validationServiceCaller,
                                                                 timeZoneServiceCaller,
                                                                 dataObjectsServiceCaller));

        when(includedModelsServiceCaller.call(any(RemoteCallback.class),
                                              any(ErrorCallback.class))).thenReturn(includedModelsService);
        when(parseServiceCaller.call(any(RemoteCallback.class),
                                     any(ErrorCallback.class))).thenReturn(parseService);
        when(validationServiceCaller.call(any(RemoteCallback.class),
                                          any(ErrorCallback.class))).thenReturn(validationService);
        when(timeZoneServiceCaller.call(any(RemoteCallback.class),
                                        any(ErrorCallback.class))).thenReturn(timeZoneService);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadModels() {
        final Optional<WorkspaceProject> optionalWorkspaceProject = Optional.of(workspaceProject);

        when(projectContext.getActiveWorkspaceProject()).thenReturn(optionalWorkspaceProject);

        clientServicesProxy.loadModels(dmnModelPath,
                                       serviceCallback);

        verify(includedModelsService).loadModels(eq(dmnModelPath),
                                                 eq(workspaceProject));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadNodesFromImports() {
        final Optional<WorkspaceProject> optionalWorkspaceProject = Optional.of(workspaceProject);
        final DMNIncludedModel includedModel1 = mock(DMNIncludedModel.class);
        final DMNIncludedModel includedModel2 = mock(DMNIncludedModel.class);
        final List<DMNIncludedModel> imports = asList(includedModel1, includedModel2);

        when(projectContext.getActiveWorkspaceProject()).thenReturn(optionalWorkspaceProject);

        clientServicesProxy.loadNodesFromImports(imports, serviceCallback);

        verify(includedModelsService).loadNodesFromImports(workspaceProject, imports);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadPMMLDocumentsFromImports() {
        final Optional<WorkspaceProject> optionalWorkspaceProject = Optional.of(workspaceProject);
        final PMMLIncludedModel includedModel1 = mock(PMMLIncludedModel.class);
        final PMMLIncludedModel includedModel2 = mock(PMMLIncludedModel.class);
        final List<PMMLIncludedModel> imports = asList(includedModel1, includedModel2);

        when(projectContext.getActiveWorkspaceProject()).thenReturn(optionalWorkspaceProject);

        clientServicesProxy.loadPMMLDocumentsFromImports(dmnModelPath, imports, serviceCallback);

        verify(includedModelsService).loadPMMLDocumentsFromImports(dmnModelPath, workspaceProject, imports);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadItemDefinitionsByNamespace() {
        final Optional<WorkspaceProject> optionalWorkspaceProject = Optional.of(workspaceProject);
        final String modelName = "model1";
        final String namespace = "://namespace1";

        when(projectContext.getActiveWorkspaceProject()).thenReturn(optionalWorkspaceProject);

        clientServicesProxy.loadItemDefinitionsByNamespace(modelName, namespace, serviceCallback);

        verify(includedModelsService).loadItemDefinitionsByNamespace(workspaceProject, modelName, namespace);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testParseFEELList() {
        final String source = "source";
        clientServicesProxy.parseFEELList(source, serviceCallback);

        verify(parseService).parseFEELList(eq(source));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testParseRangeValue() {
        final String source = "source";
        clientServicesProxy.parseRangeValue(source, serviceCallback);

        verify(parseService).parseRangeValue(eq(source));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsValidVariableName() {
        final String source = "source";
        clientServicesProxy.isValidVariableName(source, serviceCallback);

        verify(validationService).isValidVariableName(eq(source));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetTimeZones() {
        clientServicesProxy.getTimeZones(serviceCallback);

        verify(timeZoneService).getTimeZones();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnError() {
        doCallRealMethod().when(clientServicesProxy).onError(any(ServiceCallback.class));

        final boolean error = true;
        final Throwable throwable = mock(Throwable.class);
        doNothing().when(clientServicesProxy).warn(any());

        assertFalse(clientServicesProxy.onError(serviceCallback).error(error, throwable));
        verify(serviceCallback).onError(clientRuntimeErrorArgumentCaptor.capture());

        assertEquals(throwable, clientRuntimeErrorArgumentCaptor.getValue().getRootCause());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOnSuccess() {
        doCallRealMethod().when(clientServicesProxy).onSuccess(any(ServiceCallback.class));

        final Object result = new Object();

        clientServicesProxy.onSuccess(serviceCallback).callback(result);

        verify(serviceCallback).onSuccess(eq(result));
    }
}
