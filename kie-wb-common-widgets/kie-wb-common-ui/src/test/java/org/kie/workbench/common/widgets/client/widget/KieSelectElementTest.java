/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.widgets.client.widget;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLSelectElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class KieSelectElementTest {

    @Mock
    private KieSelectElement.View view;

    @Mock
    private KieSelectOptionsListPresenter optionsListPresenter;

    private KieSelectElement kieSelectElement;

    @Before
    public void before() {
        kieSelectElement = spy(new KieSelectElement(view, optionsListPresenter));
    }

    @Test
    public void testSetup() {
        final HTMLElement viewRoot = spy(new HTMLElement());
        viewRoot.innerHTML = "bar";
        doReturn(viewRoot).when(view).getElement();

        final HTMLSelectElement selectElement = spy(new HTMLSelectElement());
        doReturn(selectElement).when(view).getSelect();

        final List<KieSelectOption> options =
                singletonList(new KieSelectOption("Label", "Value"));

        kieSelectElement.setup(
                options,
                "Value",
                value -> {
                });

        verify(view).setValue(eq("Value"));
        verify(optionsListPresenter).setup(eq(selectElement), eq(options), any());
    }

    @Test
    public void testOnChange() {
        final AtomicInteger i = new AtomicInteger(0);
        doReturn("Test").when(kieSelectElement).getValue();

        kieSelectElement.onChange = value -> {
            assertEquals("Test", value);
            i.incrementAndGet();
        };

        kieSelectElement.onChange();

        assertEquals(1, i.get());
    }
}