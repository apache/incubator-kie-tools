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

package org.kie.workbench.common.stunner.bpmn.project.backend.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.soup.commons.util.Sets;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindRuleFlowNamesQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueSharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.RuleFlowGroup;
import org.kie.workbench.common.stunner.core.util.StringUtils;

@ApplicationScoped
public class RuleFlowGroupQueryService {

    private final RefactoringQueryService queryService;
    private final Function<List<RefactoringPageRow>, List<RuleFlowGroup>> resultToSelectorData;

    //CDI proxy.
    public RuleFlowGroupQueryService() {
        this(null);
    }

    @Inject
    public RuleFlowGroupQueryService(final RefactoringQueryService queryService) {
        this(queryService,
             DEFAULT_RESULT_CONVERTER);
    }

    RuleFlowGroupQueryService(final RefactoringQueryService queryService,
                              final Function<List<RefactoringPageRow>, List<RuleFlowGroup>> resultToSelectorData) {
        this.queryService = queryService;
        this.resultToSelectorData = resultToSelectorData;
    }

    public List<RuleFlowGroup> getRuleFlowGroupNames() {
        List<RefactoringPageRow> queryResult = queryService.query(
                FindRuleFlowNamesQuery.NAME,
                new Sets.Builder<ValueIndexTerm>()
                        .add(new ValueSharedPartIndexTerm("*",
                                                          PartType.RULEFLOW_GROUP,
                                                          ValueIndexTerm.TermSearchType.WILDCARD)).build()
        );
        return resultToSelectorData.apply(queryResult);
    }

    @SuppressWarnings("unchecked")
    private static RuleFlowGroup getValue(final RefactoringPageRow row) {
        Map<String, String> values = (Map<String, String>) row.getValue();
        String name = values.get("name");
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        RuleFlowGroup group = new RuleFlowGroup(name);
        group.setFileName(values.get("filename"));
        group.setPathUri(values.get("pathuri"));

        return group;
    }

    public static final Function<List<RefactoringPageRow>, List<RuleFlowGroup>> DEFAULT_RESULT_CONVERTER =
            rows -> rows.stream()
                    .map(RuleFlowGroupQueryService::getValue)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
}
