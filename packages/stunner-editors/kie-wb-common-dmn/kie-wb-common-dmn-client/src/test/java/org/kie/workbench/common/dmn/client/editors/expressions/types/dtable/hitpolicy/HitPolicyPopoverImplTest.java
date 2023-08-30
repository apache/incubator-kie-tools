/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy;

import java.util.List;
import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class HitPolicyPopoverImplTest {

    private static final int UI_ROW_INDEX = 0;

    private static final int UI_COLUMN_INDEX = 1;

    @Mock
    private HitPolicyPopoverView view;

    @Mock
    private HasHitPolicyControl control;

    @Mock
    private TranslationService translationService;

    @Captor
    private ArgumentCaptor<List<HitPolicy>> hitPoliciesCaptor;

    @Captor
    private ArgumentCaptor<List<BuiltinAggregator>> builtInAggregatorsCaptor;

    @Captor
    private ArgumentCaptor<Command> commandCaptor;

    private HitPolicyPopoverView.Presenter editor;

    private BuiltinAggregatorUtils builtinAggregatorUtils;

    @Before
    public void setup() {
        this.builtinAggregatorUtils = new BuiltinAggregatorUtils(translationService);
        this.editor = new HitPolicyPopoverImpl(view,
                                               translationService,
                                               builtinAggregatorUtils);

        when(control.getHitPolicy()).thenReturn(null);
        when(control.getBuiltinAggregator()).thenReturn(null);

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).format(Mockito.<String>any());
    }

    @Test
    public void testConstruction() {
        verify(view).init(eq(editor));
        verify(view).initHitPolicies(hitPoliciesCaptor.capture());
        verify(view).initBuiltinAggregators(builtInAggregatorsCaptor.capture());

        assertThat(hitPoliciesCaptor.getValue()).containsOnly(HitPolicy.values());
        assertThat(builtInAggregatorsCaptor.getValue()).containsOnlyElementsOf(builtinAggregatorUtils.getAllValues());
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
    }

    @Test
    public void testBindNonNullControlHitPolicyWithAggregation() {
        assertHitPolicyAggregationConfiguration(HitPolicy.COLLECT, BuiltinAggregator.COUNT);
    }

    @Test
    public void testBindNonNullControlHitPolicyWithoutAggregation() {
        assertHitPolicyAggregationConfiguration(HitPolicy.COLLECT, null);
    }

    @Test
    public void testBindNonNullControlHitPolicyNotRequiringAggregation() {
        assertHitPolicyAggregationConfiguration(HitPolicy.ANY, null);
    }

    private void assertHitPolicyAggregationConfiguration(final HitPolicy hitPolicy,
                                                         final BuiltinAggregator builtinAggregator) {
        reset(view);

        when(control.getHitPolicy()).thenReturn(hitPolicy);
        when(control.getBuiltinAggregator()).thenReturn(builtinAggregator);

        editor.bind(control,
                    UI_ROW_INDEX,
                    UI_COLUMN_INDEX);

        verify(view).enableHitPolicies(eq(true));
        verify(view).initSelectedHitPolicy(eq(hitPolicy));
        verify(view).enableBuiltinAggregators(eq(HitPolicy.COLLECT.equals(hitPolicy)));
        verify(view).initSelectedBuiltinAggregator(eq(builtinAggregator));
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
    @SuppressWarnings("unchecked")
    public void testShowNullControl() {
        editor.show();

        verify(view, never()).show(any(Optional.class));
    }

    @Test
    public void testShowNonNullControl() {
        editor.bind(control,
                    UI_ROW_INDEX,
                    UI_COLUMN_INDEX);

        reset(view);

        editor.show();

        verify(view).show(eq(Optional.empty()));
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
