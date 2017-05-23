/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.backend.server.indexing;

import java.util.Collection;
import java.util.Collections;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.Resource;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GuidedDecisionTableModelIndexVisitorTest {

    @Mock
    private Project project;

    @Mock
    private Package pkg;

    private GuidedDecisionTable52 model;
    private DefaultIndexBuilder builder;
    private GuidedDecisionTableModelIndexVisitor visitor;

    @Before
    public void setup() {
        this.model = GuidedDecisionTableFactory.makeTableWithAttributeCol("packageName",
                                                                          Collections.emptyList(),
                                                                          "tableName");
        this.builder = new DefaultIndexBuilder("fileName",
                                               project,
                                               pkg);
        this.visitor = new GuidedDecisionTableModelIndexVisitor(builder,
                                                                model);
    }

    @Test
    public void checkRuleNamesAreExtracted() {
        visitor.visit();

        final Collection<Resource> resources = visitor.getResources();
        assertNotNull(resources);
        assertEquals(1,
                     resources.size());
        final Resource resource = resources.iterator().next();
        assertEquals("packageName.Row 1 tableName",
                     resource.getResourceFQN());
        assertEquals(ResourceType.RULE,
                     resource.getResourceType());
    }
}
