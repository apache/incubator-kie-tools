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

package org.kie.workbench.common.stunner.bpmn.integration.service;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.util.EqualsAndHashCodeTestUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

@RunWith(MockitoJUnitRunner.class)
public class MigrateResultTest {

    @Mock
    private Path path1;

    @Mock
    private Path path2;

    private IntegrationService.ServiceError error1 = IntegrationService.ServiceError.JBPM_DESIGNER_PROCESS_ALREADY_EXIST;

    private IntegrationService.ServiceError error2 = IntegrationService.ServiceError.STUNNER_PROCESS_ALREADY_EXIST;

    @Test
    public void testEqualsAndHashCode() {
        EqualsAndHashCodeTestUtils.TestCaseBuilder.newTestCase()
                .addTrueCase(new MigrateResult(path1), new MigrateResult(path1))
                .addTrueCase(new MigrateResult(path1, error1, "key1", new ArrayList<>()),
                             new MigrateResult(path1, error1, "key1", new ArrayList<>()))
                .addFalseCase(new MigrateResult(path1), new MigrateResult(path2))
                .addFalseCase(new MigrateResult(path1, error1, "key1", new ArrayList<>()),
                              new MigrateResult(path2, error1, "key1", new ArrayList<>()))
                .addFalseCase(new MigrateResult(path1, error2, "key1", new ArrayList<>()),
                              new MigrateResult(path1, error1, "key1", new ArrayList<>()))
                .addFalseCase(new MigrateResult(path1, error1, "key1", new ArrayList<>()),
                              new MigrateResult(path1, error1, "key2", new ArrayList<>()))
                .test();
    }
}
