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
package org.drools.workbench.screens.guided.dtable.backend.server;

import java.util.Set;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.junit.Test;
import org.kie.soup.project.datamodel.imports.Import;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class GuidedDecisionTableModelVisitorTest {

    @Test
    public void useFullClassNameAndOnlyTheClassName() {
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        model.getImports().addImport(new Import("org.test.AnotherPerson"));
        model.getImports().addImport(new Import("org.test.Person"));
        final Pattern52 pattern52 = mock(Pattern52.class);
        doReturn("Person").when(pattern52).getFactType();

        model.getConditions().add(pattern52);

        final Set<String> consumedModelClasses = new GuidedDecisionTableModelVisitor(model).getConsumedModelClasses();

        assertTrue(consumedModelClasses.contains("org.test.Person"));
    }
}