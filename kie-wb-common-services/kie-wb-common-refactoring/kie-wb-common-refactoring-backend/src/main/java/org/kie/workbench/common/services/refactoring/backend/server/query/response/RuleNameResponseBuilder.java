/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.refactoring.backend.server.query.response;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.kie.workbench.common.services.refactoring.model.index.terms.PackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringRuleNamePageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringRuleNamePageRow.RuleName;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.paging.PageResponse;

/**
 * Custom ResponseBuilder to return a String Rule Names
 */
public class RuleNameResponseBuilder
        implements ResponseBuilder {

    @Override
    public PageResponse<RefactoringPageRow> buildResponse(final int pageSize,
                                                          final int startRow,
                                                          final List<KObject> kObjects) {
        final int hits = kObjects.size();
        final PageResponse<RefactoringPageRow> response = new PageResponse<RefactoringPageRow>();
        final List<RefactoringPageRow> result = buildResponse(kObjects);
        response.setTotalRowSize(hits);
        response.setPageRowList(result);
        response.setTotalRowSizeExact(true);
        response.setStartRowIndex(startRow);
        response.setLastPage((pageSize * startRow + 2) >= hits);

        return response;
    }

    @Override
    public List<RefactoringPageRow> buildResponse(final List<KObject> kObjects) {
        //Both "child" rule and "parent" rule (when one extends another) are stored
        //in the index. We therefore need to build a set of unique Rule Names
        final List<RefactoringPageRow> result = new ArrayList<RefactoringPageRow>(kObjects.size());
        final Set<RuleName> uniqueRuleNames = new HashSet<>();
        for (final KObject kObject : kObjects) {
            final Set<RuleName> ruleNames = getRuleNamesFromKObject(kObject);
            uniqueRuleNames.addAll(ruleNames);
        }

        for (RuleName ruleName : uniqueRuleNames) {
            final RefactoringRuleNamePageRow row = new RefactoringRuleNamePageRow();
            row.setValue(ruleName);
            result.add(row);
        }

        return result;
    }

    private Set<RuleName> getRuleNamesFromKObject(final KObject kObject) {
        //Some resources (e.g. Decision Tables etc) contain multiple rule names so add them all
        final Set<RuleName> ruleNames = new HashSet<>();
        if (kObject == null) {
            return ruleNames;
        }

        //Extract KProperties
        final Set<KProperty<?>> kProperties = StreamSupport
                .stream(kObject.getProperties().spliterator(),
                        false)
                .collect(Collectors.toSet());

        //Get Package Name (all Rules for a single Index entry *should* be in a single Package)
        final Optional<KProperty<?>> packageName = kProperties
                .stream()
                .filter((kp) -> kp.getName().equals(PackageNameIndexTerm.TERM))
                .findFirst();

        //Assign Rules to packages
        packageName
                .flatMap((pkg) -> Optional.of(pkg.getValue().toString()))
                .ifPresent((pkgName) -> kProperties
                        .stream()
                        .filter((kp) -> kp.getName().equals(ResourceType.RULE.toString()))
                        .forEach((r) -> ruleNames.add(new RuleName(r.getValue()
                                                                           .toString()
                                                                           .replace(pkgName,
                                                                                    "")
                                                                           .replaceFirst("\\.",
                                                                                         ""),
                                                                   pkgName))));

        return ruleNames;
    }
}
