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
package org.kie.workbench.common.stunner.bpmn.project.backend.query;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import javax.inject.Inject;
import javax.inject.Named;

import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindResourcesQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringMapPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.io.IOService;
import org.uberfire.paging.PageResponse;

public abstract class AbstractFindIdsQuery extends FindResourcesQuery implements NamedQuery {

    @Inject
    private BpmnProcessIdsResponseBuilder responseBuilder;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    public abstract String getName();

    protected abstract ResourceType getProcessIdResourceType();

    @Override
    public ResponseBuilder getResponseBuilder() {
        responseBuilder.setIOService(ioService);
        responseBuilder.setProcessIdResourceType(getProcessIdResourceType());
        return responseBuilder;
    }

    @Override
    public void validateTerms(Set<ValueIndexTerm> queryTerms) throws IllegalArgumentException {
        this.checkInvalidAndRequiredTerms(queryTerms,
                                          getName(),
                                          new String[]{ValueResourceIndexTerm.class.getSimpleName()},
                                          new Predicate[]{t -> {
                                              if (!(t instanceof ValueResourceIndexTerm)) {
                                                  return false;
                                              } else {
                                                  return ((ValueResourceIndexTerm) t).getTerm()
                                                          .equals(getProcessIdResourceType().toString());
                                              }
                                          }});
        this.checkTermsSize(1,
                            queryTerms);
    }

    public static class BpmnProcessIdsResponseBuilder implements ResponseBuilder {

        private IOService ioService;

        private ResourceType processIdResourceType;

        public BpmnProcessIdsResponseBuilder() {
        }

        public BpmnProcessIdsResponseBuilder(IOService ioService, ResourceType processIdResourceType) {
            this.ioService = ioService;
            this.processIdResourceType = processIdResourceType;
        }

        public void setIOService(IOService ioService) {
            this.ioService = ioService;
        }

        public void setProcessIdResourceType(ResourceType resourceType) {
            this.processIdResourceType = resourceType;
        }

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
            final List<RefactoringPageRow> result = new ArrayList<RefactoringPageRow>(kObjects.size());
            for (final KObject kObject : kObjects) {
                for (KProperty property : kObject.getProperties()) {
                    if (property.getName().equals(processIdResourceType.toString())) {
                        String bpmnProcessId = (String) property.getValue();
                        final Path path = Paths.convert(ioService.get(URI.create(kObject.getKey())));
                        Map<String, Path> map = new HashMap<String, Path>();
                        map.put(bpmnProcessId,
                                path);
                        RefactoringMapPageRow row = new RefactoringMapPageRow();
                        row.setValue(map);
                        result.add(row);
                    }
                }
            }
            return result;
        }
    }
}
