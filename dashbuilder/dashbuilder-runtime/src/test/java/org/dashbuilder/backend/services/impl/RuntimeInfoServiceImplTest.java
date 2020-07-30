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

package org.dashbuilder.backend.services.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import org.dashbuilder.shared.model.DashboardInfo;
import org.dashbuilder.shared.model.DashbuilderRuntimeInfo;
import org.dashbuilder.shared.model.RuntimeModel;
import org.dashbuilder.shared.service.RuntimeModelRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.dashbuilder.shared.model.DashbuilderRuntimeMode.MULTIPLE_IMPORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RuntimeInfoServiceImplTest {

    final static String RUNTIME_MODEL_ID = "ID";
    final static String NOT_FOUND_ID = "NOT FOUND";
    final static String LT_NAME = "LAYOUT TEMPLATE";

    @Mock
    RuntimeModel runtimeModel;

    @Mock
    RuntimeModelRegistry registry;

    @InjectMocks
    RuntimeInfoServiceImpl runtimeInfoService;

    @Before
    public void init() {
        LayoutTemplate lt = new LayoutTemplate(LT_NAME);
        when(runtimeModel.getLayoutTemplates()).thenReturn(Arrays.asList(lt));
        when(registry.getMode()).thenReturn(MULTIPLE_IMPORT);
        when(registry.get(RUNTIME_MODEL_ID)).thenReturn(Optional.of(runtimeModel));
        when(registry.get(NOT_FOUND_ID)).thenReturn(Optional.empty());
        when(registry.availableModels()).thenReturn(new HashSet<>(Arrays.asList(RUNTIME_MODEL_ID)));
    }

    @Test
    public void testInfo() {
        DashbuilderRuntimeInfo info = runtimeInfoService.info();

        assertEquals(1, info.getAvailableModels().size());
        assertTrue(info.getAvailableModels().contains(RUNTIME_MODEL_ID));
        assertEquals(MULTIPLE_IMPORT.name(), info.getMode());
        assertFalse(info.isAcceptingNewImports());
    }

    @Test
    public void testDashboardInfoWithFoundModel() {
        Optional<DashboardInfo> dashboardInfoOp = runtimeInfoService.dashboardInfo(RUNTIME_MODEL_ID);
        DashboardInfo dashboardInfo = dashboardInfoOp.get();
        assertEquals(1, dashboardInfo.getPages().size());
        assertTrue(dashboardInfo.getPages().contains(LT_NAME));
        assertEquals(RUNTIME_MODEL_ID, dashboardInfo.getRuntimeModelId());
    }

    @Test
    public void testDashboardInfoWithNotFoundModel() {
        Optional<DashboardInfo> dashboardInfoOp = runtimeInfoService.dashboardInfo(NOT_FOUND_ID);
        assertFalse(dashboardInfoOp.isPresent());
    }

}