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

package org.uberfire.ext.widgets.common.client.dropdown.noItems;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class NoItemsComponentTest {

    private static final String MESSAGE = "a message";

    @Mock
    private NoItemsComponentView view;

    private NoItemsComponent component;

    @Test
    public void testFunctionality() {
        component = new NoItemsComponent(view);

        component.getElement();
        verify(view).getElement();

        component.show();
        verify(view).show();

        component.hide();
        verify(view).hide();

        component.setMessage(MESSAGE);
        verify(view).setMessage(MESSAGE);

        component.setMessage(null);
        verify(view, times(2)).setMessage(anyString());
    }
}
