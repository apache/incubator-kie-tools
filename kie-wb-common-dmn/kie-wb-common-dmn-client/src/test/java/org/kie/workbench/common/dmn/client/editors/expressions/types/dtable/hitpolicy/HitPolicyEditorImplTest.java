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

import java.util.List;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class HitPolicyEditorImplTest {

    private static final int UI_ROW_INDEX = 0;

    private static final int UI_COLUMN_INDEX = 1;

    @Mock
    private HitPolicyEditorView view;

    @Mock
    private HasHitPolicyControl control;

    @Captor
    private ArgumentCaptor<List<HitPolicy>> hitPoliciesCaptor;

    @Captor
    private ArgumentCaptor<List<BuiltinAggregator>> builtInAggregatorsCaptor;

    @Captor
    private ArgumentCaptor<List<DecisionTableOrientation>> orientationsCaptor;

    @Captor
    private ArgumentCaptor<Command> commandCaptor;

    private HitPolicyEditorView.Presenter editor;

    @Before
    public void setup() {
        this.editor = new HitPolicyEditorImpl(view);

        when(control.getHitPolicy()).thenReturn(null);
        when(control.getBuiltinAggregator()).thenReturn(null);
        when(control.getDecisionTableOrientation()).thenReturn(null);
    }

    @Test
    public void testConstruction() {
        verify(view).init(eq(editor));
        verify(view).initHitPolicies(hitPoliciesCaptor.capture());
        verify(view).initBuiltinAggregators(builtInAggregatorsCaptor.capture());
        verify(view).initDecisionTableOrientations(orientationsCaptor.capture());

        assertThat(hitPoliciesCaptor.getValue()).containsOnly(HitPolicy.values());
        assertThat(builtInAggregatorsCaptor.getValue()).containsOnly(BuiltinAggregator.values());
        assertThat(orientationsCaptor.getValue()).containsOnly(DecisionTableOrientation.values());
    }

    @Test
    public void testGetElement() {
        editor.getElement();

        verify(view).getElement();
    }

    @Test
    public void testBindNullControl() {
        reset(view);

        editor.bind(null,
                    UI_ROW_INDEX,
                    UI_COLUMN_INDEX);

        verifyZeroInteractions(view);
    }

    @Test
    public void testBindNonNullControl() {
        reset(view);

        editor.bind(control,
                    UI_ROW_INDEX,
                    UI_COLUMN_INDEX);

        verify(view).enableHitPolicies(eq(false));
        verify(view).enableBuiltinAggregators(eq(false));
        verify(view).enableDecisionTableOrientation(eq(false));
    }

    @Test
    public void testBindNonNullControlHitPolicy() {
        final HitPolicy hitPolicy = HitPolicy.ANY;

        reset(view);

        when(control.getHitPolicy()).thenReturn(hitPolicy);

        editor.bind(control,
                    UI_ROW_INDEX,
                    UI_COLUMN_INDEX);

        verify(view).enableHitPolicies(eq(true));
        verify(view).initSelectedHitPolicy(eq(hitPolicy));
        verify(view).enableBuiltinAggregators(eq(false));
        verify(view).enableDecisionTableOrientation(eq(false));
    }

    @Test
    public void testBindNonNullControlHitPolicyWithAggregation() {
        final HitPolicy hitPolicy = HitPolicy.COLLECT;
        final BuiltinAggregator aggregator = BuiltinAggregator.COUNT;

        reset(view);

        when(control.getHitPolicy()).thenReturn(hitPolicy);
        when(control.getBuiltinAggregator()).thenReturn(aggregator);

        editor.bind(control,
                    UI_ROW_INDEX,
                    UI_COLUMN_INDEX);

        verify(view).enableHitPolicies(eq(true));
        verify(view).initSelectedHitPolicy(eq(hitPolicy));
        verify(view).enableBuiltinAggregators(eq(true));
        verify(view).initSelectedBuiltinAggregator(eq(aggregator));
        verify(view).enableDecisionTableOrientation(eq(false));
    }

    @Test
    public void testBindNonNullControlOrientation() {
        final DecisionTableOrientation orientation = DecisionTableOrientation.RULE_AS_ROW;

        reset(view);

        when(control.getDecisionTableOrientation()).thenReturn(orientation);

        editor.bind(control,
                    UI_ROW_INDEX,
                    UI_COLUMN_INDEX);

        verify(view).enableHitPolicies(eq(false));
        verify(view).enableBuiltinAggregators(eq(false));
        verify(view).enableDecisionTableOrientation(eq(true));
        verify(view).initSelectedDecisionTableOrientation(eq(orientation));
    }

    @Test
    public void testSetHitPolicyNullControl() {
        editor.setHitPolicy(HitPolicy.ANY);

        verify(control, never()).setHitPolicy(any(HitPolicy.class),
                                              any(Command.class));
    }

    @Test
    public void testSetHitPolicyNonNullControl() {
        final HitPolicy hitPolicy = HitPolicy.ANY;

        when(control.getHitPolicy()).thenReturn(hitPolicy);

        editor.bind(control,
                    UI_ROW_INDEX,
                    UI_COLUMN_INDEX);

        reset(view);

        editor.setHitPolicy(hitPolicy);

        verify(control).setHitPolicy(eq(hitPolicy),
                                     commandCaptor.capture());

        commandCaptor.getValue().execute();

        verify(view).enableHitPolicies(eq(true));
    }

    @Test
    public void testSetBuiltInAggregatorNullControl() {
        editor.setBuiltinAggregator(BuiltinAggregator.COUNT);

        verify(control, never()).setBuiltinAggregator(any(BuiltinAggregator.class));
    }

    @Test
    public void testSetBuiltInAggregatorNonNullControl() {
        final BuiltinAggregator aggregator = BuiltinAggregator.COUNT;

        when(control.getBuiltinAggregator()).thenReturn(aggregator);

        editor.bind(control,
                    UI_ROW_INDEX,
                    UI_COLUMN_INDEX);

        reset(view);

        editor.setBuiltinAggregator(aggregator);

        verify(control).setBuiltinAggregator(eq(aggregator));
    }

    @Test
    public void testSetOrientationNullControl() {
        editor.setDecisionTableOrientation(DecisionTableOrientation.RULE_AS_ROW);

        verify(control, never()).setDecisionTableOrientation(any(DecisionTableOrientation.class));
    }

    @Test
    public void testSetOrientationNonNullControl() {
        final DecisionTableOrientation orientation = DecisionTableOrientation.RULE_AS_ROW;

        when(control.getDecisionTableOrientation()).thenReturn(orientation);

        editor.bind(control,
                    UI_ROW_INDEX,
                    UI_COLUMN_INDEX);

        reset(view);

        editor.setDecisionTableOrientation(orientation);

        verify(control).setDecisionTableOrientation(eq(orientation));
    }

    @Test
    public void testShowNullControl() {
        editor.show();

        verify(view, never()).show();
    }

    @Test
    public void testShowNonNullControl() {
        editor.bind(control,
                    UI_ROW_INDEX,
                    UI_COLUMN_INDEX);

        reset(view);

        editor.show();

        verify(view).show();
    }

    @Test
    public void testHideNullControl() {
        editor.hide();

        verify(view, never()).hide();
    }

    @Test
    public void testHideNonNullControl() {
        editor.bind(control,
                    UI_ROW_INDEX,
                    UI_COLUMN_INDEX);

        reset(view);

        editor.hide();

        verify(view).hide();
    }
}
