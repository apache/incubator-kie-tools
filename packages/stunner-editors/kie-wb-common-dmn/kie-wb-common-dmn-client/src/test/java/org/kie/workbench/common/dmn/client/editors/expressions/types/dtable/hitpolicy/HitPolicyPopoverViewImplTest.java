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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.client.views.pfly.widgets.Select;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class HitPolicyPopoverViewImplTest {

    @Mock
    private Select lstHitPolicies;

    @Mock
    private HTMLElement lstHitPoliciesElement;

    @Mock
    private Select lstBuiltinAggregator;

    @Mock
    private HTMLElement lstBuiltinAggregatorElement;

    @Mock
    private HTMLElement element;

    @Mock
    private Div popoverElement;

    @Mock
    private Div popoverContentElement;

    @Mock
    private Span hitPolicyLabel;

    @Mock
    private Span builtinAggregatorLabel;

    @Mock
    private JQueryProducer.JQuery<Popover> jQueryProducer;

    @Mock
    private Popover popover;

    @Mock
    private TranslationService translationService;

    private HitPolicyPopoverViewImpl view;

    private BuiltinAggregatorUtils builtinAggregatorUtils;

    @Before
    public void setUp() throws Exception {
        doReturn(lstHitPoliciesElement).when(lstHitPolicies).getElement();
        doReturn(lstBuiltinAggregatorElement).when(lstBuiltinAggregator).getElement();

        builtinAggregatorUtils = new BuiltinAggregatorUtils(translationService);
        view = spy(new HitPolicyPopoverViewImpl(lstHitPolicies,
                                                lstBuiltinAggregator,
                                                builtinAggregatorUtils,
                                                popoverElement,
                                                popoverContentElement,
                                                hitPolicyLabel,
                                                builtinAggregatorLabel,
                                                jQueryProducer,
                                                translationService));

        doReturn(element).when(view).getElement();
        when(jQueryProducer.wrap(element)).thenReturn(popover);

        doAnswer((i) -> i.getArguments()[0].toString()).when(translationService).getTranslation(Mockito.<String>any());
    }

    @Test
    public void testInitHitPolicies() throws Exception {
        view.initHitPolicies(Arrays.asList(HitPolicy.values()));

        Stream.of(HitPolicy.values()).forEach(policy -> verify(lstHitPolicies).addOption(policy.value()));
    }

    @Test
    public void testInitAggregator() throws Exception {
        final List<BuiltinAggregator> aggregators = builtinAggregatorUtils.getAllValues();
        view.initBuiltinAggregators(aggregators);

        aggregators.stream().forEach(agg -> verify(lstBuiltinAggregator).addOption(builtinAggregatorUtils.toString(agg)));
    }

    @Test
    public void testEnableHitPolicies() throws Exception {
        view.enableHitPolicies(true);

        verify(lstHitPolicies).enable();
    }

    @Test
    public void testDisableHitPolicies() throws Exception {
        view.enableHitPolicies(false);

        verify(lstHitPolicies).disable();
    }

    @Test
    public void testEnableAggregator() throws Exception {
        view.enableBuiltinAggregators(true);

        verify(lstBuiltinAggregator).enable();
    }

    @Test
    public void testDisableAggregator() throws Exception {
        view.enableBuiltinAggregators(false);

        verify(lstBuiltinAggregator).disable();
    }

    @Test
    public void testShow() {
        view.show(Optional.empty());

        verify(popover).show();
    }

    @Test
    public void testHideBeforeShown() {
        view.hide();

        verify(popover, never()).hide();
        verify(popover, never()).destroy();
    }

    @Test
    public void testHideAfterShown() {
        view.show(Optional.empty());
        view.hide();

        verify(popover).hide();
        verify(popover).destroy();
    }
}
