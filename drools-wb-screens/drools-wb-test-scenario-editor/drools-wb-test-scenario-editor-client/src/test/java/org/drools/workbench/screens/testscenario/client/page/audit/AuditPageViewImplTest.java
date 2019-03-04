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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class AuditPageViewImplTest {

    @Mock
    private HTMLDivElement root;

    @Mock
    private AuditTable firedRulesTable;

    @Mock
    private AuditTable auditLogTable;

    @Captor
    private ArgumentCaptor<List<String>> collectionCaptor;

    private AuditPageViewImpl testedView;

    @Before
    public void setUp() throws Exception {
        testedView = new AuditPageViewImpl(root, firedRulesTable, auditLogTable);
    }

    @Test
    public void testAuditLog() throws Exception {
        final Set<String> logMessages = Collections.singleton("log 1");

        testedView.showAuditLog(logMessages);

        verify(auditLogTable).showItems(logMessages);
    }

    @Test
    public void testFiredRules() throws Exception {

        ArrayList<String> rulesFired = new ArrayList<>();
        rulesFired.add("rule 1");
        rulesFired.add("rule 2");
        testedView.showFiredRules(rulesFired);

        verify(firedRulesTable).showItems(collectionCaptor.capture());
        Assertions.assertThat(collectionCaptor.getValue()).containsAll(rulesFired);
    }
}
