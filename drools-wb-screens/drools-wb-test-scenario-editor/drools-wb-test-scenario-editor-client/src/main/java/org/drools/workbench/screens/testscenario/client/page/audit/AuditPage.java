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

package org.drools.workbench.screens.testscenario.client.page.audit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.views.pfly.multipage.PageImpl;

@Dependent
public class AuditPage extends PageImpl {

    public interface AuditPageView extends UberElemental<AuditPage> {

        void showFiredRules(final List<String> ruleNames);

        void showAuditLog(final Set<String> auditLogMessages);
    }
    
    private AuditPageView auditPageView;

    @Inject
    public AuditPage(final AuditPageView auditPageView) {
        super(ElementWrapperWidget.getWidget(auditPageView.getElement()), TestScenarioConstants.INSTANCE.AuditLog());
        this.auditPageView = auditPageView;
    }

    public void showFiredRulesAuditLog(final Set<String> logMessages) {
        auditPageView.showAuditLog(logMessages);
    }

    public void showFiredRules(final ExecutionTrace executionTrace) {
        auditPageView.showFiredRules(getRuleNames(executionTrace));
    }

    private List<String> getRuleNames(final ExecutionTrace executionTrace) {
        if (executionTrace.getRulesFired() == null) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(executionTrace.getRulesFired());
        }
    }
}
