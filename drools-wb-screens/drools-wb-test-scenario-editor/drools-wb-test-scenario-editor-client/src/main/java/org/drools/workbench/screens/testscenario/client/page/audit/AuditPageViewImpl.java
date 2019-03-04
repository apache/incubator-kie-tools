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

import java.util.List;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class AuditPageViewImpl implements AuditPage.AuditPageView {

    @DataField("root")
    private HTMLDivElement root;

    private AuditPage presenter;

    @DataField("fired-rules-table")
    private AuditTable firedRulesTable;

    @DataField("audit-log-table")
    private AuditTable auditLogTable;

    @Inject
    public AuditPageViewImpl(final HTMLDivElement root,
                             final AuditTable firedRulesTable,
                             final AuditTable auditLogTable) {
        this.root = root;
        this.firedRulesTable = firedRulesTable;
        this.auditLogTable = auditLogTable;

        this.auditLogTable.setTitle(TestScenarioConstants.INSTANCE.AuditLog());
        this.firedRulesTable.setTitle(TestScenarioConstants.INSTANCE.FiredRules());
    }

    @Override
    public HTMLElement getElement() {
        return root;
    }

    @Override
    public void init(AuditPage presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showFiredRules(final List<String> ruleNames) {
        firedRulesTable.showItems(ruleNames);
    }

    @Override
    public void showAuditLog(final Set<String> auditLogMessages) {
        auditLogTable.showItems(auditLogMessages);
    }
}
