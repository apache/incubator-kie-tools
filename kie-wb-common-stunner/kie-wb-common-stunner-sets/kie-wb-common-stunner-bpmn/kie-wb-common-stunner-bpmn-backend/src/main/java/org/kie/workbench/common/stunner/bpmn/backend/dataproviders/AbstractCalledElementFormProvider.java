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
package org.kie.workbench.common.stunner.bpmn.backend.dataproviders;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.inject.Inject;

import org.kie.soup.commons.util.Sets;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.backend.vfs.Path;

public abstract class AbstractCalledElementFormProvider implements SelectorDataProvider {

    @Inject
    protected RefactoringQueryService queryService;

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    public void setQueryService(RefactoringQueryService queryService) {
        this.queryService = queryService;
    }

    @Override
    public SelectorData getSelectorData(FormRenderingContext context) {
        return new SelectorData(getBusinessProcessIDs(),
                                null);
    }

    public Map<Object, String> getBusinessProcessIDs() {
        final Set<ValueIndexTerm> queryTerms = new Sets.Builder<ValueIndexTerm>()
                .add(new ValueResourceIndexTerm("*",
                                                getProcessIdResourceType(),
                                                ValueIndexTerm.TermSearchType.WILDCARD))
                .build();

        List<RefactoringPageRow> results = queryService.query(
                getQueryName(),
                queryTerms);

        Map<Object, String> businessProcessIDs = new TreeMap<>();

        for (RefactoringPageRow row : results) {
            Map<String, Path> mapRow = (Map<String, Path>) row.getValue();
            for (String rKey : mapRow.keySet()) {
                businessProcessIDs.put(rKey,
                                       rKey);
            }
        }

        return businessProcessIDs;
    }

    protected abstract ResourceType getProcessIdResourceType();

    protected abstract String getQueryName();
}
