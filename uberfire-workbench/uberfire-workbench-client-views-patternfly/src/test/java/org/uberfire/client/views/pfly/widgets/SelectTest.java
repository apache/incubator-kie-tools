/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.widgets;

import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.Option;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SelectTest {

    @Mock
    Document document;

    @Mock
    org.jboss.errai.common.client.dom.Select selectMock;

    @InjectMocks
    Select select;

    @Test
    public void testAddOption() {
        final Option option = mock(Option.class);
        when(document.createElement("option")).thenReturn(option);

        select.addOption("text",
                         "subText",
                         "value",
                         true);

        verify(option).setText("text");
        verify(option).setValue("value");
        verify(option).setSelected(true);
        option.setAttribute("data-subtext",
                            "subText");
        verify(selectMock).add(option);
    }

    @Test
    public void testTitle() {
        final String title = "title";

        select.setTitle(title);

        verify(selectMock).setTitle(title);
    }

    @Test
    public void testLiveSearch() {
        select.setLiveSearch(true);

        verify(selectMock).setAttribute("data-live-search", "true");
    }

    @Test
    public void testWidth() {
        select.setWidth("auto");

        verify(selectMock).setAttribute("data-width", "auto");
    }

    @Test
    public void testValue() {
        final String value = "somevalue";
        when(selectMock.getValue()).thenReturn(value);

        assertEquals(value, select.getValue());
    }

}
