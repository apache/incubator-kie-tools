/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.refactoring.client.usages;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.service.AssetsUsageService;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShowAssetUsagesDisplayerTest {

    private static final String RESOURCE_PART = "field";
    private static final String RESOURCE_FQN = "RQN.java";

    private static final String RESOURCE_TYPE = "resource";

    @Mock
    private HTMLElement htmlElement;

    @Mock
    private ShowAssetUsagesDisplayerView view;

    @Mock
    private TranslationService translationService;

    private List<Path> queryResults = new ArrayList<>();

    @Mock
    private AssetsUsageService assetsUsageService;

    @Mock
    private ResourceTypeDefinition resourceTypeDefinition;

    @Mock
    private Path currentAssetPath;

    @Mock
    private Command okCommand;

    @Mock
    private Command cancelCommand;

    @Mock
    private Path resultPath;

    private ShowAssetUsagesDisplayer displayer;

    @Before
    public void init() {
        when(view.getDefaultMessageContainer()).thenReturn(htmlElement);

        when(assetsUsageService.getAssetUsages(anyString(),
                                               any(),
                                               any())).thenReturn(queryResults);
        when(assetsUsageService.getAssetPartUsages(anyString(),
                                                   anyString(),
                                                   any(),
                                                   any())).thenReturn(queryResults);

        when(resourceTypeDefinition.getSuffix()).thenReturn(RESOURCE_TYPE);
        when(resourceTypeDefinition.getShortName()).thenReturn(RESOURCE_TYPE);

        when(translationService.format(anyString(),
                                       any())).thenReturn("");

        displayer = new ShowAssetUsagesDisplayer(view,
                                                 translationService,
                                                 new CallerMock<>(assetsUsageService)) {
            {
                registerResourceTypeDefinition(resourceTypeDefinition);
            }
        };

        verify(view).init(displayer);
    }

    @Test
    public void testGetAssetType() {
        Path path = mock(Path.class);

        String fileName = "file.";

        when(path.getFileName()).thenReturn(fileName + RESOURCE_TYPE);

        String resourceType = displayer.getAssetType(path);

        assertEquals(RESOURCE_TYPE,
                     resourceType);

        when(path.getFileName()).thenReturn(fileName + "fail");

        resourceType = displayer.getAssetType(path);

        assertEquals(ShowAssetUsagesDisplayer.UNKNOWN_ASSET_TYPE,
                     resourceType);
        assertNotEquals(RESOURCE_TYPE,
                        resourceType);
    }

    @Test
    public void testShowAssetUsagesWithoutResponse() {
        displayer.showAssetUsages(currentAssetPath,
                                  RESOURCE_FQN,
                                  ResourceType.JAVA,
                                  okCommand,
                                  cancelCommand);

        verify(translationService).format(anyString(),
                                          anyString());
        verify(view).getDefaultMessageContainer();
        verify(htmlElement).setInnerHTML(anyString());
        verify(assetsUsageService).getAssetUsages(anyString(),
                                                  any(),
                                                  any());
        verify(view,
               never()).show(htmlElement,
                             queryResults);
        verify(okCommand).execute();
    }

    @Test
    public void testShowAssetUsagesWithResponseAndAccept() {
        testAssetUsagesWithResponse(true);
    }

    @Test
    public void testShowAssetUsagesWithResponseAndCancel() {
        testAssetUsagesWithResponse(false);
    }

    protected void testAssetUsagesWithResponse(boolean pressOk) {
        queryResults.add(resultPath);

        displayer.showAssetUsages(currentAssetPath,
                                  RESOURCE_FQN,
                                  ResourceType.JAVA,
                                  okCommand,
                                  cancelCommand);

        verify(translationService).format(anyString(),
                                          anyString());
        verify(view).getDefaultMessageContainer();
        verify(htmlElement).setInnerHTML(anyString());
        verify(assetsUsageService).getAssetUsages(anyString(),
                                                  any(),
                                                  any());
        verify(view).show(htmlElement,
                          queryResults);
        verify(okCommand,
               never()).execute();

        if (pressOk) {
            displayer.onOk();
        } else {
            displayer.onCancel();
        }

        displayer.onClose();

        verifyClose(pressOk);
    }

    @Test
    public void testShowAssetPartUsagesWithoutResponse() {

        displayer.showAssetPartUsages(currentAssetPath,
                                      RESOURCE_FQN,
                                      RESOURCE_PART,
                                      PartType.FIELD,
                                      okCommand,
                                      cancelCommand);

        verify(translationService).format(anyString(),
                                          anyString());
        verify(view).getDefaultMessageContainer();
        verify(htmlElement).setInnerHTML(anyString());
        verify(assetsUsageService).getAssetPartUsages(anyString(),
                                                      anyString(),
                                                      any(),
                                                      any());
        verify(view,
               never()).show(htmlElement,
                             queryResults);
        verify(okCommand).execute();
    }

    @Test
    public void testShowAssetPartUsagesWithResponseAndAccept() {
        testAssetPartUsagesWithResponse(true);
    }

    @Test
    public void testShowAssetPartUsagesWithResponseAndCancel() {
        testAssetPartUsagesWithResponse(false);
    }

    protected void testAssetPartUsagesWithResponse(boolean pressOk) {
        queryResults.add(resultPath);

        displayer.showAssetPartUsages(currentAssetPath,
                                      RESOURCE_FQN,
                                      RESOURCE_PART,
                                      PartType.FIELD,
                                      okCommand,
                                      cancelCommand);

        verify(translationService).format(anyString(),
                                          anyString());
        verify(view).getDefaultMessageContainer();
        verify(htmlElement).setInnerHTML(anyString());
        verify(assetsUsageService).getAssetPartUsages(anyString(),
                                                      anyString(),
                                                      any(),
                                                      any());
        verify(view).show(htmlElement,
                          queryResults);
        verify(okCommand,
               never()).execute();

        if (pressOk) {
            displayer.onOk();
        } else {
            displayer.onCancel();
        }

        displayer.onClose();

        verifyClose(pressOk);
    }

    private void verifyClose(boolean pressOk) {
        if (pressOk) {
            verify(okCommand).execute();
            verify(cancelCommand,
                   never()).execute();
        } else {
            verify(cancelCommand).execute();
            verify(okCommand,
                   never()).execute();
        }
    }
}
