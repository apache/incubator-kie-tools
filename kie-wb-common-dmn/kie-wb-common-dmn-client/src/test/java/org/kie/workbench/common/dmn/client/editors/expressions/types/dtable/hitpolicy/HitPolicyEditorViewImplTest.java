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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.Select;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class HitPolicyEditorViewImplTest {

    @Mock
    private Select lstHitPolicies;

    @Mock
    private HTMLElement lstHitPoliciesElement;

    @Mock
    private Select lstBuiltinAggregator;

    @Mock
    private HTMLElement lstBuiltinAggregatorElement;

    @Mock
    private Select lstDecisionTableOrientation;

    @Mock
    private HTMLElement lstDecisionTableOrientationElement;

    private HitPolicyEditorViewImpl testedView;

    @Before
    public void setUp() throws Exception {
        doReturn(lstHitPoliciesElement).when(lstHitPolicies).getElement();
        doReturn(lstBuiltinAggregatorElement).when(lstBuiltinAggregator).getElement();
        doReturn(lstDecisionTableOrientationElement).when(lstDecisionTableOrientation).getElement();

        testedView = new HitPolicyEditorViewImpl(lstHitPolicies,
                                                 lstBuiltinAggregator,
                                                 lstDecisionTableOrientation);
    }

    @Test
    public void testInitHitPolicies() throws Exception {
        testedView.initHitPolicies(Arrays.asList(HitPolicy.values()));

        Stream.of(HitPolicy.values()).forEach(policy -> verify(lstHitPolicies).addOption(policy.value()));
    }

    @Test
    public void testInitAggregator() throws Exception {
        final List<BuiltinAggregator> aggregators = Arrays.asList(BuiltinAggregator.values());
        testedView.initBuiltinAggregators(aggregators);

        aggregators.stream().forEach(agg -> verify(lstBuiltinAggregator).addOption(agg.value()));
    }

    @Test
    public void testInitOrientations() throws Exception {
        final List<DecisionTableOrientation> orientations = Arrays.asList(DecisionTableOrientation.values());
        testedView.initDecisionTableOrientations(orientations);

        orientations.stream().forEach(orientation -> verify(lstDecisionTableOrientation).addOption(orientation.value()));
    }

    @Test
    public void testEnableHitPolicies() throws Exception {
        testedView.enableHitPolicies(true);

        verify(lstHitPolicies).enable();
    }

    @Test
    public void testDisableHitPolicies() throws Exception {
        testedView.enableHitPolicies(false);

        verify(lstHitPolicies).disable();
    }

    @Test
    public void testEnableAggregator() throws Exception {
        testedView.enableBuiltinAggregators(true);

        verify(lstBuiltinAggregator).enable();
    }

    @Test
    public void testDisableAggregator() throws Exception {
        testedView.enableBuiltinAggregators(false);

        verify(lstBuiltinAggregator).disable();
    }

    @Test
    public void testEnableOrientations() throws Exception {
        testedView.enableDecisionTableOrientation(true);

        verify(lstDecisionTableOrientation).enable();
    }

    @Test
    public void testDisbleOrientations() throws Exception {
        testedView.enableDecisionTableOrientation(false);

        verify(lstDecisionTableOrientation).disable();
    }
}
