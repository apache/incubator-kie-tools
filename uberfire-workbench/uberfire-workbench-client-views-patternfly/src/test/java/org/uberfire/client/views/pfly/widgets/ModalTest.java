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

import org.jboss.errai.common.client.dom.Body;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ModalTest {

    @Mock
    Document document;

    @Mock
    Body body;

    @Mock
    Div div;

    @InjectMocks
    @Spy
    Modal modal;

    @Before
    public void setup() {
        doNothing().when(modal).hide(any(Element.class));
        doNothing().when(modal).show(any(Element.class));
        when(document.getBody()).thenReturn(body);
    }

    @Test
    public void testShow() {
        modal.show();

        verify(body).appendChild(div);
    }

    @Test
    public void testHide() {
        modal.hide();

        verify(body).removeChild(div);
    }
}
