/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab;

import javax.inject.Inject;

import org.guvnor.structure.contributors.ContributorType;

public class ContributorsSecurityUtils {

    @Inject
    public ContributorsSecurityUtils() {
    }

    public boolean canUserEditContributorOfType(final ContributorType userContributorType,
                                                final ContributorType typeBeingEdited) {
        if (userContributorType.equals(ContributorType.OWNER)) {
            return true;
        } else if (userContributorType.equals(ContributorType.ADMIN)) {
            return typeBeingEdited.equals(ContributorType.ADMIN) || typeBeingEdited.equals(ContributorType.CONTRIBUTOR);
        }

        return false;
    }
}
