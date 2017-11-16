/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.services.refactoring.service;

import java.util.List;
import java.util.Set;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.impact.QueryOperationRequest;
import org.uberfire.paging.PageResponse;

@Remote
public interface RefactoringQueryService {

    int queryHitCount(final RefactoringPageRequest request);

    PageResponse<RefactoringPageRow> query(final RefactoringPageRequest request);

    List<RefactoringPageRow> query(final String queryName,
                                   final Set<ValueIndexTerm> queryTerms);

    PageResponse<RefactoringPageRow> queryToPageResponse(final QueryOperationRequest request);

    List<RefactoringPageRow> queryToList(final QueryOperationRequest request);
}
