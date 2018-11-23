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

package org.drools.workbench.services.verifier.plugin.client;

import java.util.Set;

import org.drools.verifier.api.reporting.CheckType;
import org.drools.verifier.core.configuration.CheckConfiguration;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CheckConfigurationProviderTest {

    private CheckConfiguration checkConfiguration;

    @Test
    public void getResolvedHitConfiguration() throws
            Exception {

        whenDecisionTableHas(GuidedDecisionTable52.HitPolicy.RESOLVED_HIT);

        thenTheFollowingCheckTypesAreNotIncluded(CheckType.getRowLevelCheckTypes());
    }

    @Test
    public void getRuleOrderConfiguration() throws
            Exception {

        whenDecisionTableHas(GuidedDecisionTable52.HitPolicy.RULE_ORDER);

        thenTheFollowingCheckTypesAreNotIncluded(CheckType.getRowLevelCheckTypes());
    }

    @Test
    public void getFirstHitConfiguration() throws
            Exception {

        whenDecisionTableHas(GuidedDecisionTable52.HitPolicy.FIRST_HIT);

        thenTheFollowingCheckTypesAreNotIncluded(CheckType.getRowLevelCheckTypes());
    }

    @Test
    public void getNoHitPolicySetConfiguration() throws
            Exception {

        whenDecisionTableHas(GuidedDecisionTable52.HitPolicy.NONE);

        thenAllOfTheFollowingChecksAreIncluded(CheckConfiguration.newDefault()
                                                       .getCheckConfiguration());
    }

    @Test
    public void getSetConfiguration() throws
            Exception {

        whenDecisionTableHas(GuidedDecisionTable52.HitPolicy.UNIQUE_HIT);

        thenAllOfTheFollowingChecksAreIncluded(CheckConfiguration.newDefault()
                                                       .getCheckConfiguration());
    }

    private void thenAllOfTheFollowingChecksAreIncluded(final Set<CheckType> checkTypes) {
        for (final CheckType checkType : checkTypes) {
            assertTrue(checkConfiguration.getCheckConfiguration()
                               .contains(checkType));
        }
    }

    private void thenTheFollowingCheckTypesAreNotIncluded(final Set<CheckType> checkTypes) {
        for (final CheckType checkType : checkTypes) {
            assertFalse(checkConfiguration.getCheckConfiguration()
                                .contains(checkType));
        }
    }

    private void whenDecisionTableHas(final GuidedDecisionTable52.HitPolicy hitPolicy) {
        checkConfiguration = CheckConfigurationProvider.get(hitPolicy);
    }
}