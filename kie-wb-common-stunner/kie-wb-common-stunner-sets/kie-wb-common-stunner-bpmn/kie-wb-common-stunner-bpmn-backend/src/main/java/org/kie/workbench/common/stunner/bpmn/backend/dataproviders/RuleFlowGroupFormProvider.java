/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.dataproviders;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import org.kie.soup.commons.util.Sets;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindRuleFlowNamesQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueSharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;

public class RuleFlowGroupFormProvider implements SelectorDataProvider {

    @Inject
    protected RefactoringQueryService queryService;

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SelectorData getSelectorData(final FormRenderingContext context) {
        return new SelectorData(getRuleFlowGroupNames(),
                                null);
    }

    @SuppressWarnings("unchecked")
    private Map<Object, String> getRuleFlowGroupNames() {

        List<RefactoringPageRow> results = queryService.query(
                FindRuleFlowNamesQuery.NAME,
                new Sets.Builder<ValueIndexTerm>()
                        .add(new ValueSharedPartIndexTerm("*",
                                                          PartType.RULEFLOW_GROUP,
                                                          ValueIndexTerm.TermSearchType.WILDCARD))

                        .build());

        Map<Object, String> ruleFlowGroupNames = new TreeMap<>();

        for (RefactoringPageRow row : results) {
            ruleFlowGroupNames.put(((Map<String, String>) row.getValue()).get("name"),
                                   ((Map<String, String>) row.getValue()).get("name"));
        }

        return ruleFlowGroupNames;
    }
}
