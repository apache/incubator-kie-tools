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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.kindselector;

import java.util.Optional;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class KindPopoverImplTest {

    @Mock
    private KindPopoverView view;

    @Mock
    private TranslationService translationService;

    @Mock
    private HasKindSelectControl hasKindSelectorControl;

    private FunctionDefinition.Kind kind;

    @Captor
    private ArgumentCaptor<FunctionDefinition.Kind[]> kindsArgumentCaptor;

    private KindPopoverImpl popover;

    @Before
    public void setup() {
        this.popover = new KindPopoverImpl(view, translationService);
        this.kind = FunctionDefinition.Kind.JAVA;
    }

    @Test
    public void testInitialization() {
        verify(view).setFunctionKinds(kindsArgumentCaptor.capture());

        final FunctionDefinition.Kind[] values = kindsArgumentCaptor.getValue();

        assertThat(values).hasSize(3);
        assertThat(values).contains(FunctionDefinition.Kind.FEEL,
                                    FunctionDefinition.Kind.JAVA,
                                    FunctionDefinition.Kind.PMML);
    }

    @Test
    public void testShowWhenBound() {
        popover.bind(hasKindSelectorControl, 0, 0);

        popover.show();

        verify(view).show(Optional.ofNullable(popover.getPopoverTitle()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShowWhenNotBound() {
        popover.show();

        verify(view, never()).show(any(Optional.class));
    }

    @Test
    public void testHideWhenBound() {
        popover.bind(hasKindSelectorControl, 0, 0);

        popover.hide();

        verify(view).hide();
    }

    @Test
    public void testHideWhenNotBound() {
        popover.hide();

        verify(view, never()).hide();
    }

    @Test
    public void testOnFunctionKindSelectedWhenBound() {
        popover.bind(hasKindSelectorControl, 0, 0);

        popover.onFunctionKindSelected(kind);

        verify(hasKindSelectorControl).setFunctionKind(eq(FunctionDefinition.Kind.JAVA));
        verify(view).hide();
    }

    @Test
    public void testOnExpressionEditorDefinitionSelectedWhenNotBound() {
        popover.onFunctionKindSelected(kind);

        verify(hasKindSelectorControl, never()).setFunctionKind(any(FunctionDefinition.Kind.class));
        verify(view, never()).hide();
    }
}