/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.backend.converters.fromstunner;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.activities.CaseManagementReusableSubprocessConverter;
import org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.processes.CaseManagementSubProcessConverter;
import org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.properties.CaseManagementPropertyWriterFactory;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;

import static org.junit.Assert.assertTrue;

public class CaseManagementConverterFactoryTest {

    private CaseManagementConverterFactory tested;

    @Before
    public void setUp() throws Exception {
        CaseManagementPropertyWriterFactory factory = new CaseManagementPropertyWriterFactory();

        tested = new CaseManagementConverterFactory(new DefinitionsBuildingContext(new GraphImpl("x", new GraphNodeStoreImpl()),
                                                                                   CaseManagementDiagram.class),
                                                    factory);
    }

    @Test
    public void testReusableSubprocessConverter() throws Exception {
        assertTrue(CaseManagementReusableSubprocessConverter.class.isInstance(tested.reusableSubprocessConverter()));
    }

    @Test
    public void testSubProcessConverter() throws Exception {
        assertTrue(CaseManagementSubProcessConverter.class.isInstance(tested.subProcessConverter()));
    }
}