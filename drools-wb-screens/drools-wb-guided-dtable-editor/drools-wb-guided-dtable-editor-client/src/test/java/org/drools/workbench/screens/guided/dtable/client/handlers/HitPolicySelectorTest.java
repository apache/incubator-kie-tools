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

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HitPolicySelectorTest {

    @Mock
    private HitPolicySelectorView view;

    private HitPolicySelector selector;

    @Before
    public void setUp() throws
                        Exception {
        selector = new HitPolicySelector( view );
    }

    @Test
    public void allModesSetUp() throws
                                Exception {
        final GuidedDecisionTable52.HitPolicy[] values = GuidedDecisionTable52.HitPolicy.values();

        verify( view,
                times( values.length ) ).addHitPolicyOption( any() );

        for ( final GuidedDecisionTable52.HitPolicy value : values ) {
            verify( view ).addHitPolicyOption( value );
        }
    }

    @Test
    public void defaultIsSelected() throws
                                    Exception {
        verify( view ).setSelection( GuidedDecisionTable52.HitPolicy.getDefault() );
    }

    @Test
    public void addActiveHitPolicyValueChangeListener() throws
                                                        Exception {
        final Callback<GuidedDecisionTable52.HitPolicy> callback = mock( Callback.class );
        selector.addValueChangeHandler( callback );

        selector.onHitPolicySelected( GuidedDecisionTable52.HitPolicy.RULE_ORDER );

        verify( callback ).callback( GuidedDecisionTable52.HitPolicy.RULE_ORDER );
    }

}