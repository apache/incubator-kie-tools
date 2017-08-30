/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.handlers;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.HitPolicyInternationalizer;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.html.Paragraph;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedDecisionTableOptionsTest {

    @Mock
    private HitPolicySelector selector;

    @Captor
    private ArgumentCaptor<Callback<GuidedDecisionTable52.HitPolicy>> callback;

    private GuidedDecisionTableOptions options;

    @Before
    public void setUp() throws Exception {
        options = new GuidedDecisionTableOptions(selector);
        options.hitPolicyDescriptionHeading = mock(Heading.class);
        options.hitPolicyDescriptionText = mock(Paragraph.class);
    }

    @Test
    public void testNoneHitPolicyDescription() throws Exception {
        testHitPolicy(GuidedDecisionTable52.HitPolicy.NONE);
    }

    @Test
    public void testResolvedHitPolicyDescription() throws Exception {
        testHitPolicy(GuidedDecisionTable52.HitPolicy.RESOLVED_HIT);
    }

    @Test
    public void testRuleOrderHitPolicyDescription() throws Exception {
        testHitPolicy(GuidedDecisionTable52.HitPolicy.RULE_ORDER);
    }

    @Test
    public void testUniqueHitPolicyDescription() throws Exception {
        testHitPolicy(GuidedDecisionTable52.HitPolicy.UNIQUE_HIT);
    }

    @Test
    public void testFirstHitPolicyDescription() throws Exception {
        testHitPolicy(GuidedDecisionTable52.HitPolicy.FIRST_HIT);
    }

    private void testHitPolicy(GuidedDecisionTable52.HitPolicy hitPolicy) {
        verify(selector).addValueChangeHandler(callback.capture());

        callback.getValue().callback(hitPolicy);

        verify(options.hitPolicyDescriptionHeading).setText(HitPolicyInternationalizer.internationalize(hitPolicy));
        verify(options.hitPolicyDescriptionText).setText(HitPolicyInternationalizer.internationalizeDescription(hitPolicy));
    }
}
