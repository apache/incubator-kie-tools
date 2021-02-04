/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.resources;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(GwtMockitoTestRunner.class)
public class HitPolicyInternationalizerTest {

    GuidedDecisionTableConstants constants;

    @Test
    public void internationalize() throws
            Exception {
        assertEquals("RuleOrderHitPolicy",
                     HitPolicyInternationalizer.internationalize(GuidedDecisionTable52.HitPolicy.RULE_ORDER));
        assertEquals("FirstHitPolicy",
                     HitPolicyInternationalizer.internationalize(GuidedDecisionTable52.HitPolicy.FIRST_HIT));
        assertEquals("UniqueHitPolicy",
                     HitPolicyInternationalizer.internationalize(GuidedDecisionTable52.HitPolicy.UNIQUE_HIT));
        assertEquals("ResolvedHitPolicy",
                     HitPolicyInternationalizer.internationalize(GuidedDecisionTable52.HitPolicy.RESOLVED_HIT));
        assertEquals("NoneHitPolicy",
                     HitPolicyInternationalizer.internationalize(GuidedDecisionTable52.HitPolicy.NONE));
    }

    @Test
    public void internationalizeDescription() throws
            Exception {
        assertEquals("RuleOrderHitPolicyDescription",
                     HitPolicyInternationalizer.internationalizeDescription(GuidedDecisionTable52.HitPolicy.RULE_ORDER));
        assertEquals("FirstHitPolicyDescription",
                     HitPolicyInternationalizer.internationalizeDescription(GuidedDecisionTable52.HitPolicy.FIRST_HIT));
        assertEquals("UniqueHitPolicyDescription",
                     HitPolicyInternationalizer.internationalizeDescription(GuidedDecisionTable52.HitPolicy.UNIQUE_HIT));
        assertEquals("ResolvedHitPolicyDescription",
                     HitPolicyInternationalizer.internationalizeDescription(GuidedDecisionTable52.HitPolicy.RESOLVED_HIT));
        assertEquals("NoneHitPolicyDescription",
                     HitPolicyInternationalizer.internationalizeDescription(GuidedDecisionTable52.HitPolicy.NONE));
    }

    @Test
    public void deInternationalize() throws
            Exception {
        assertEquals(GuidedDecisionTable52.HitPolicy.RULE_ORDER,
                     HitPolicyInternationalizer.deInternationalize("RuleOrderHitPolicy"));
        assertEquals(GuidedDecisionTable52.HitPolicy.RESOLVED_HIT,
                     HitPolicyInternationalizer.deInternationalize("ResolvedHitPolicy"));
        assertEquals(GuidedDecisionTable52.HitPolicy.FIRST_HIT,
                     HitPolicyInternationalizer.deInternationalize("FirstHitPolicy"));
        assertEquals(GuidedDecisionTable52.HitPolicy.UNIQUE_HIT,
                     HitPolicyInternationalizer.deInternationalize("UniqueHitPolicy"));
        assertEquals(GuidedDecisionTable52.HitPolicy.NONE,
                     HitPolicyInternationalizer.deInternationalize("NoneHitPolicy"));
    }
}