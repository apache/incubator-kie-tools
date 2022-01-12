/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.Node;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KieMultipleSelectElementTest {

    @Mock
    private KieMultipleSelectElement.View view;

    @Mock
    private KieSelectOptionsListPresenter optionsListPresenter;

    private KieMultipleSelectElement kieSelectElement;

    @Before
    public void before() {
        kieSelectElement = spy(new KieMultipleSelectElement(view,
                                                            optionsListPresenter,
                                                            new Elemental2DomUtil()));
    }

    @Test
    public void testSetup() {
        final HTMLElement viewRoot = spy(new HTMLElement());
        viewRoot.innerHTML = "bar";
        doReturn(viewRoot).when(view).getElement();

        final HTMLSelectElement selectElement = spy(new HTMLSelectElement());
        doReturn(selectElement).when(view).getSelect();

        final Element container = spy(new Element() {
            @Override
            public Node appendChild(final Node node) {
                if (node instanceof HTMLElement) {
                    this.innerHTML += ((HTMLElement) node).innerHTML;
                }
                return node;
            }
        });

        container.innerHTML = "";

        final List<KieSelectOption> options =
                singletonList(new KieSelectOption("Label", "Value"));

        kieSelectElement.setup(
                container,
                options,
                Arrays.asList("value1", "value2"),
                value -> {
                });

        verify(view).setValue(eq(Arrays.asList("value1", "value2")));
        verify(view).initSelect();
        verify(optionsListPresenter).setup(eq(selectElement), eq(options), any());
        assertEquals("bar", container.innerHTML);
    }

    @Test
    public void testOnChange() {
        final AtomicInteger i = new AtomicInteger(0);
        doReturn(Arrays.asList("value1", "value2")).when(kieSelectElement).getValue();

        kieSelectElement.onChange = value -> {
            assertEquals(Arrays.asList("value1", "value2"), value);
            i.incrementAndGet();
        };

        kieSelectElement.onChange();

        assertEquals(1, i.get());
    }
}