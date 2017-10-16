/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.drools.workbench.screens.guided.dtable.client.resources;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.kie.soup.commons.validation.PortablePreconditions;

public class HitPolicyInternationalizer {

    public static String internationalize(final GuidedDecisionTable52.HitPolicy hitPolicy) {

        PortablePreconditions.checkNotNull("hitPolicy",
                                           hitPolicy);

        switch (hitPolicy) {
            case NONE:
                return GuidedDecisionTableConstants.INSTANCE.NoneHitPolicy();
            case UNIQUE_HIT:
                return GuidedDecisionTableConstants.INSTANCE.UniqueHitPolicy();
            case FIRST_HIT:
                return GuidedDecisionTableConstants.INSTANCE.FirstHitPolicy();
            case RULE_ORDER:
                return GuidedDecisionTableConstants.INSTANCE.RuleOrderHitPolicy();
            case RESOLVED_HIT:
                return GuidedDecisionTableConstants.INSTANCE.ResolvedHitPolicy();
            default:
                return hitPolicy.name();
        }
    }

    public static String internationalizeDescription(final GuidedDecisionTable52.HitPolicy hitPolicy) {

        PortablePreconditions.checkNotNull("hitPolicy",
                                           hitPolicy);

        switch (hitPolicy) {
            case NONE:
                return GuidedDecisionTableConstants.INSTANCE.NoneHitPolicyDescription();
            case UNIQUE_HIT:
                return GuidedDecisionTableConstants.INSTANCE.UniqueHitPolicyDescription();
            case FIRST_HIT:
                return GuidedDecisionTableConstants.INSTANCE.FirstHitPolicyDescription();
            case RULE_ORDER:
                return GuidedDecisionTableConstants.INSTANCE.RuleOrderHitPolicyDescription();
            case RESOLVED_HIT:
                return GuidedDecisionTableConstants.INSTANCE.ResolvedHitPolicyDescription();
            default:
                return hitPolicy.name();
        }
    }

    public static GuidedDecisionTable52.HitPolicy deInternationalize(final String internalization) {

        PortablePreconditions.checkNotNull("internalization",
                                           internalization);

        for (final GuidedDecisionTable52.HitPolicy hitPolicy : GuidedDecisionTable52.HitPolicy.values()) {
            if (internalization.equals(internationalize(hitPolicy))) {
                return hitPolicy;
            }
        }

        throw new IllegalArgumentException("Could not find hit policy with internalization: " + internalization);
    }
}
