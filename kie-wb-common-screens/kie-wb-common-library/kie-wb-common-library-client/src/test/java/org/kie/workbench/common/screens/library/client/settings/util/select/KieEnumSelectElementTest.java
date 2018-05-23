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

package org.kie.workbench.common.screens.library.client.settings.util.select;

import java.util.Arrays;
import java.util.List;

import elemental2.dom.Element;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.widget.KieSelectElement;
import org.kie.workbench.common.widgets.client.widget.KieSelectElement.Option;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class KieEnumSelectElementTest {

    @Mock
    private KieSelectElement kieSelectElement;

    @Mock
    private TranslationService translationService;

    private KieEnumSelectElement<TestEnum> kieEnumSelectElement;

    enum TestEnum {
        FOO,
        BAR;
    }

    @Before
    public void before() {
        kieEnumSelectElement = spy(new KieEnumSelectElement<TestEnum>(kieSelectElement, translationService));
    }

    @Test
    public void testSetup() {

        final Element container = spy(new Element());
        final List<Option> options = Arrays.asList(new Option("FOO", "foo"), new Option("Bar", "bar"));

        doReturn(options).when(kieEnumSelectElement).buildOptions(any());

        kieEnumSelectElement.setup(
                container,
                TestEnum.values(),
                TestEnum.FOO,
                value -> {
                });

        assertEquals(TestEnum.class, kieEnumSelectElement.componentType);
        verify(kieSelectElement).setup(eq(container), eq(options), eq("FOO"), any());
    }

    @Test
    public void testBuildOptions() {
        doReturn(new Option("A", "a")).when(kieEnumSelectElement).newOption(any());

        final List<Option> options = kieEnumSelectElement.buildOptions(TestEnum.values());

        assertEquals(2, options.size());
        assertEquals("A", options.get(0).label);
        assertEquals("a", options.get(0).value);
        assertEquals("A", options.get(1).label);
        assertEquals("a", options.get(1).value);
    }

    @Test
    public void testNewOption() {
        doReturn("A").when(kieEnumSelectElement).getLabel(eq(TestEnum.FOO));

        final Option option = kieEnumSelectElement.newOption(TestEnum.FOO);

        assertEquals("A", option.label);
        assertEquals("FOO", option.value);
    }

    @Test
    public void testGetLabel() {
        doReturn("A").when(translationService).format(any());

        final String label = kieEnumSelectElement.getLabel(TestEnum.FOO);

        assertEquals("A", label);
        assertEquals("A", label);
    }

    @Test
    public void testToEnum() {
        kieEnumSelectElement.componentType = TestEnum.class;

        assertEquals(TestEnum.FOO, kieEnumSelectElement.toEnum("FOO"));
        assertEquals(TestEnum.BAR, kieEnumSelectElement.toEnum("BAR"));
    }

    @Test
    public void testGetValue() {
        doReturn("FOO").when(kieSelectElement).getValue();
        kieEnumSelectElement.componentType = TestEnum.class;

        final TestEnum value = kieEnumSelectElement.getValue();

        assertEquals(TestEnum.FOO, value);
    }
}
