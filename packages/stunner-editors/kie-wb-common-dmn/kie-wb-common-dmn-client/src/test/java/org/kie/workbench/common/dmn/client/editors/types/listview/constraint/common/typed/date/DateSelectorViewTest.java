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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date;

import java.util.function.Consumer;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLInputElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DateSelectorViewTest {

    @Mock
    private HTMLInputElement dateInput;

    @Mock
    private DateValueFormatter valueFormatter;

    @Mock
    private JSONObject jsonObject;

    @Mock
    private BlurEvent blurEvent;

    @Mock
    private Object target;

    @Mock
    private Consumer<BlurEvent> consumer;

    private DateSelectorView dateSelectorView;

    @Before
    public void setup() {
        dateSelectorView = spy(new DateSelectorView(dateInput, valueFormatter));
    }

    @Test
    public void testOnDateInputBlur() {

        dateSelectorView.onValueInputBlur(consumer);
        doReturn(target).when(dateSelectorView).getEventTarget(any());

        dateSelectorView.onDateInputBlur(blurEvent);

        verify(consumer).accept(blurEvent);
    }

    @Test
    public void testOnDateInputBlurTargetNull() {

        dateSelectorView.onValueInputBlur(consumer);

        doReturn(null).when(dateSelectorView).getEventTarget(any());

        dateSelectorView.onDateInputBlur(blurEvent);

        verify(consumer, never()).accept(blurEvent);
    }

    @Test
    public void testProperties() {

        doReturn(jsonObject).when(dateSelectorView).makeJsonObject();

        dateSelectorView.properties();

        verify(dateSelectorView).makeJsonObject();
        verify(jsonObject).put("format", new JSONString("d M yyyy"));
    }
}