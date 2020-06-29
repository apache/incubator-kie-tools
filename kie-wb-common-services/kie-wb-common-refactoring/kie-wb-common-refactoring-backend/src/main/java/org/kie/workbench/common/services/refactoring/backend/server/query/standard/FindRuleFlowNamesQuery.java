/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.refactoring.backend.server.query.standard;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.lucene.search.Query;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.SharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueBranchNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueModuleNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueSharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringMapPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.paging.PageResponse;

@ApplicationScoped
public class FindRuleFlowNamesQuery extends AbstractFindQuery implements NamedQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindRuleFlowNamesQuery.class);

    private final IOService ioService;

    @Inject
    public FindRuleFlowNamesQuery(@Named("ioStrategy") IOService ioService) {
        this.ioService = ioService;
    }

    private RuleFlowNamesResponseBuilder responseBuilder = new RuleFlowNamesResponseBuilder();

    public static final String NAME = FindRuleFlowNamesQuery.class.getSimpleName();
    public static final String SHARED_TERM = SharedPartIndexTerm.TERM + ":" + PartType.RULEFLOW_GROUP.toString();

    public static boolean isSharedRuleFlowGroup(String parameter) {
        return SHARED_TERM.equals(parameter);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ResponseBuilder getResponseBuilder() {
        responseBuilder.setIOService(ioService);
        return responseBuilder;
    }

    /* (non-Javadoc)
     * @see org.kie.workbench.common.services.refactoring.backend.server.query.IndexQuery#toQuery(java.util.Set)
     */
    @Override
    public Query toQuery(Set<ValueIndexTerm> terms) {
        return buildFromSingleTerm(terms);
    }

    private static final ValueSharedPartIndexTerm ruleFlowTerm = new ValueSharedPartIndexTerm("not-used", PartType.RULEFLOW_GROUP);

    @Override
    public void validateTerms(Set<ValueIndexTerm> queryTerms) throws IllegalArgumentException {
        checkNotNullAndNotEmpty(queryTerms);

        checkInvalidAndRequiredTerms(queryTerms,
                                     NAME,
                                     new String[]{
                                             null, null,  // not required
                                             ruleFlowTerm.getTerm()
                                     },
                                     (t) -> (t instanceof ValueModuleNameIndexTerm),
                                     (t) -> (t instanceof ValueBranchNameIndexTerm),
                                     (t) -> (t.getTerm().equals(ruleFlowTerm.getTerm())));
    }

    private static class RuleFlowNamesResponseBuilder implements ResponseBuilder {

        private IOService ioService;

        public void setIOService(IOService ioService) {
            this.ioService = ioService;
        }

        @Override
        public PageResponse<RefactoringPageRow> buildResponse(final int pageSize,
                                                              final int startRow,
                                                              final List<KObject> kObjects) {
            final int hits = kObjects.size();
            final PageResponse<RefactoringPageRow> response = new PageResponse();
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
            final List<RefactoringPageRow> result = new ArrayList(kObjects.size());

            for (final KObject kObject : kObjects) {
                final List<Map<String, String>> ruleFlowGroups = getRuleFlowGroupsFromKObject(kObject);
                for (Map<String, String> group : ruleFlowGroups) {
                    RefactoringMapPageRow row = new RefactoringMapPageRow();
                    row.setValue(group);
                    result.add(row);
                }
            }

            return result;
        }

        private List<Map<String, String>> getRuleFlowGroupsFromKObject(final KObject kObject) {
            final List<Map<String, String>> ruleFlowGroups = new ArrayList<>();
            if (kObject == null) {
                return ruleFlowGroups;
            }
            for (KProperty<?> property : kObject.getProperties()) {
                if (SHARED_TERM.equals(property.getName())) {
                    Path path = getPath(kObject);
                    if (path != null) {
                        Map<String, String> group = new HashMap<>();
                        group.put("name", property.getValue().toString());
                        group.put("filename", path.getFileName());
                        group.put("pathuri", path.toURI());
                        ruleFlowGroups.add(group);
                    }
                }
            }

            return ruleFlowGroups;
        }

        private Path getPath(KObject kObject) {
            try {
                return Paths.convert(ioService.get(URI.create(kObject.getKey())));
            } catch (FileSystemNotFoundException ex) {
                LOGGER.error(ex.toString());
                return null;
            }
        }
    }
}

