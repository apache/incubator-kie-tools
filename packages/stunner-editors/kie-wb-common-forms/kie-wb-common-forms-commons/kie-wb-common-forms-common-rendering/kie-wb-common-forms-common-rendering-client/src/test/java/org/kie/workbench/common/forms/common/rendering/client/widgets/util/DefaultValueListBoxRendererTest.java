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


package org.kie.workbench.common.forms.common.rendering.client.widgets.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultValueListBoxRendererTest {

    private static final String VAL_1 = "val1";
    private static final String VAL_2 = "val2";
    private static final String VAL_3 = "val3";
    private static final String VAL_4 = "val4";

    private static final String NON_EXISTING_OPTION = "val5";

    @Mock
    private Appendable appendable;

    @Spy
    private DefaultValueListBoxRenderer<String> renderer = new DefaultValueListBoxRenderer<>();

    @Test
    public void testRenderWithoutOptions() throws Exception {
        renderer.render(NON_EXISTING_OPTION,
                        appendable);
        verify(renderer,
               times(1)).render(NON_EXISTING_OPTION);
        verify(appendable,
               times(1)).append(DefaultValueListBoxRenderer.NULL_STR);
    }

    @Test
    public void testRenderNullWithoutOptions() throws Exception {
        renderer.render(null,
                        appendable);
        verify(renderer,
               times(1)).render(null);
        verify(appendable,
               times(1)).append(DefaultValueListBoxRenderer.NULL_STR);
    }

    @Test
    public void testRendererWithOptions() throws Exception {
        Map<String, String> values = new HashMap<>();
        values.put(VAL_1,
                   VAL_1);
        values.put(VAL_2,
                   VAL_2);
        values.put(VAL_3,
                   VAL_3);
        values.put(VAL_4,
                   VAL_4);

        renderer.setValues(values);

        renderer.render(VAL_1,
                        appendable);
        verify(renderer,
               times(1)).render(VAL_1);
        verify(appendable,
               times(1)).append(VAL_1);

        renderer.render(VAL_2,
                        appendable);
        verify(renderer,
               times(1)).render(VAL_2);
        verify(appendable,
               times(1)).append(VAL_2);

        renderer.render(VAL_3,
                        appendable);
        verify(renderer,
               times(1)).render(VAL_3);
        verify(appendable,
               times(1)).append(VAL_3);

        renderer.render(VAL_4,
                        appendable);
        verify(renderer,
               times(1)).render(VAL_4);
        verify(appendable,
               times(1)).append(VAL_4);

        renderer.render(NON_EXISTING_OPTION,
                        appendable);
        verify(appendable,
               times(1)).append(DefaultValueListBoxRenderer.NULL_STR);
    }
}
