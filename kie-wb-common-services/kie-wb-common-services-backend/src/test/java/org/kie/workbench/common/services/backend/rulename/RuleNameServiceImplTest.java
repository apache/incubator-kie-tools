/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.services.backend.rulename;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindRulesByProjectQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringRuleNamePageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RuleNameServiceImplTest {

    private static final String PROJECT_ROOT_URI = "project-root-uri";

    @Mock
    private RefactoringQueryService queryService;

    @Mock
    private KieProjectService projectService;

    @Mock
    private Path path;

    @Mock
    private Path projectRootPath;

    @Mock
    private KieProject project;

    private RuleNameServiceImpl service;

    @Before
    public void setup() {
        this.service = new RuleNameServiceImpl(queryService,
                                               projectService);

        when(projectService.resolveProject(any(Path.class))).thenReturn(project);
        when(project.getRootPath()).thenReturn(projectRootPath);
        when(projectRootPath.toURI()).thenReturn(PROJECT_ROOT_URI);

        when(queryService.query(eq(FindRulesByProjectQuery.NAME),
                                anySetOf(ValueIndexTerm.class))).thenReturn(getResults());
    }

    private List<RefactoringPageRow> getResults() {
        final List<RefactoringPageRow> results = new ArrayList<>();
        results.add(new RefactoringRuleNamePageRow() {{
            setValue(new RuleName("rule1",
                                  "org.kie.test.package"));
        }});
        results.add(new RefactoringRuleNamePageRow() {{
            setValue(new RuleName("rule2",
                                  "org.kie.test.package"));
        }});
        return results;
    }

    @Test
    public void checkGetRuleNames() {
        final Collection<String> ruleNames = service.getRuleNames(path,
                                                                  "");
        assertEquals(2,
                     ruleNames.size());
        assertTrue(ruleNames.stream().filter((r) -> r.equals("rule1")).findFirst().isPresent());
        assertTrue(ruleNames.stream().filter((r) -> r.equals("rule2")).findFirst().isPresent());
    }
}
