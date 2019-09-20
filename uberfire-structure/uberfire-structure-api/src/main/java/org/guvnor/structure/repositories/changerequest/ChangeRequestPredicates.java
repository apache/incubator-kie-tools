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

package org.guvnor.structure.repositories.changerequest;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.guvnor.structure.repositories.changerequest.portable.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatus;

public class ChangeRequestPredicates {

    private ChangeRequestPredicates() {

    }

    public static Predicate<ChangeRequest> matchAll() {
        return elem -> true;
    }

    public static Predicate<ChangeRequest> matchId(final Long id) {
        return elem -> elem.getId() == id;
    }

    public static Predicate<ChangeRequest> matchSearchFilter(final String searchFilter,
                                                             final Function<ChangeRequest, String> searchableElementFunction) {
        return elem -> searchableElementFunction.apply(elem).contains(searchFilter.toLowerCase());
    }

    public static Predicate<ChangeRequest> matchStatus(final ChangeRequestStatus status) {
        return elem -> elem.getStatus() == status;
    }

    public static Predicate<ChangeRequest> matchInStatusList(final List<ChangeRequestStatus> statusList) {
        return elem -> statusList.contains(elem.getStatus());
    }

    public static Predicate<ChangeRequest> matchInTargetBranchList(final List<String> targetBranches) {
        return elem -> targetBranches.contains(elem.getTargetBranch());
    }

    public static Predicate<ChangeRequest> matchSourceBranch(final String branch) {
        return elem -> elem.getSourceBranch().equals(branch);
    }

    public static Predicate<ChangeRequest> matchTargetBranch(final String branch) {
        return elem -> elem.getTargetBranch().equals(branch);
    }

    public static Predicate<ChangeRequest> matchSearchFilterAndStatusList(final String searchFilter,
                                                                          final Function<ChangeRequest, String> searchableElementFunction,
                                                                          final List<ChangeRequestStatus> statusList) {
        return matchSearchFilter(searchFilter,
                                 searchableElementFunction)
                .and(matchInStatusList(statusList));
    }

    public static Predicate<ChangeRequest> matchSourceOrTargetBranch(final String branchName) {
        return matchSourceBranch(branchName)
                .or(matchTargetBranch(branchName));
    }

    public static Predicate<ChangeRequest> matchSourceAndTargetAndStatus(final String sourceBranchName,
                                                                         final String targetBranchName,
                                                                         final ChangeRequestStatus status) {
        return matchSourceBranch(sourceBranchName)
                .and(matchTargetBranch(targetBranchName)
                             .and(matchStatus(status)));
    }

    public static Predicate<ChangeRequest> matchTargetBranchListAndOtherPredicate(final List<String> targetBranches,
                                                                                  final Predicate<ChangeRequest> predicate) {
        return matchInTargetBranchList(targetBranches).and(predicate);
    }
}
