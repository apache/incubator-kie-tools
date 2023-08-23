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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.number;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.BaseSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.BaseSelector.InputType;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

@RunWith(GwtMockitoTestRunner.class)
public class NumberSelectorTest {

    @Mock
    private BaseSelector.View view;

    private NumberSelector numberSelector;

    @Before
    public void setup() {
        numberSelector = spy(new NumberSelector(view));
    }

    @Test
    public void testGetInputType() {

        final InputType expected = InputType.NUMBER;
        final InputType actual = numberSelector.getInputType();

        assertEquals(expected, actual);
    }
}
