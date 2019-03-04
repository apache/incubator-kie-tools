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

package org.drools.workbench.screens.testscenario.client.page.audit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@WithClassesToStub(RootPanel.class)
@RunWith(GwtMockitoTestRunner.class)
public class AuditPageTest {

    @Captor
    ArgumentCaptor<List> listArgumentCaptor;
    @Mock
    private AuditPage.AuditPageView auditPageView;
    private AuditPage auditPage;

    @Before
    public void setUp() throws Exception {
        auditPage = new AuditPage(auditPageView);
    }

    @Test
    public void testShowFiredRulesAuditLog() {
        final Set<String> logMessages = new HashSet<String>() {{
            add("rule 1 fired");
            add("rule 2 fired");
        }};

        auditPage.showFiredRulesAuditLog(logMessages);

        verify(auditPageView).showAuditLog(logMessages);
    }

    @Test
    public void testShowFiredRules() {
        final ExecutionTrace executionTrace = new ExecutionTrace();
        final String[] rulesFired = new String[1];
        rulesFired[0] = "rule name";
        executionTrace.setRulesFired(rulesFired);

        auditPage.showFiredRules(executionTrace);

        verify(auditPageView).showFiredRules(listArgumentCaptor.capture());
        final List list = listArgumentCaptor.getValue();
        assertEquals(1, list.size());
        assertEquals("rule name", list.get(0));
    }

    @Test
    public void testShowFiredRulesDontShowIfThereIsNoRules() {
        final ExecutionTrace executionTrace = new ExecutionTrace();
        executionTrace.setRulesFired(null);

        auditPage.showFiredRules(executionTrace);

        verify(auditPageView).showFiredRules(listArgumentCaptor.capture());
        assertTrue(listArgumentCaptor.getValue().isEmpty());
    }
}
