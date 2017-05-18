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

import org.jboss.errai.common.client.dom.DOMTokenList;
import org.jboss.errai.common.client.dom.Div;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FormGroupTest {

    @Mock
    Div div;

    @Mock
    DOMTokenList domTokenList;

    @InjectMocks
    FormGroup formGroup;

    @Before
    public void setup() {
        when(div.getClassList()).thenReturn(domTokenList);
    }

    @Test
    public void testClearValidationState() {
        when(domTokenList.contains(anyString())).thenReturn(true);

        formGroup.clearValidationState();

        for (ValidationState v : ValidationState.values()) {
            verify(domTokenList).remove(v.getCssName());
        }
    }

    @Test
    public void testSetValidationState() {
        formGroup.setValidationState(ValidationState.SUCCESS);

        verify(domTokenList).add(ValidationState.SUCCESS.getCssName());
    }
}
