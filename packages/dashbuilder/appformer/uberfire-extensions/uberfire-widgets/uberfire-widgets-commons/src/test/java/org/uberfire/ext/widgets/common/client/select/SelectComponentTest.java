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


package org.uberfire.ext.widgets.common.client.select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SelectComponentTest {

    @Mock
    private SelectComponent.View view;

    @Mock
    private ManagedInstance<SelectOptionComponent> selectOptionComponents;
    private SelectComponent component;
    private SelectOption option1;

    @Mock
    private Consumer<SelectOption> callback;

    @Mock
    private SelectOptionComponent selectOptionComponent;

    @Before
    public void setUp() {

        option1 = new SelectOptionImpl("FIRST",
                                       "first");

        component = spy(new SelectComponent(view,
                                            selectOptionComponents));

        when(this.selectOptionComponents.get()).thenReturn(this.selectOptionComponent);

        component.init();
    }

    @Test
    public void testSetup() {
        component.setup(Arrays.asList(option1),
                        callback);
        verify(selectOptionComponent).initialize(eq(option1),
                                                 any());
    }

    @Test
    public void testEmptySelectOptions() {
        component.setup(Arrays.asList(),
                        callback);
        verify(selectOptionComponent,
               never()).initialize(eq(option1),
                                   any());
    }

    @Test
    public void testSelectOption() {

        List<Consumer<SelectOption>> consumer = new ArrayList();

        doAnswer(invocation -> {
            consumer.add((Consumer<SelectOption>) invocation.getArguments()[1]);
            return null;
        }).when(this.selectOptionComponent).initialize(any(),
                                                       any());

        doAnswer(invocation -> {
            consumer.get(0).accept(option1);
            return null;
        }).when(selectOptionComponent).select();

        component.setup(Arrays.asList(option1),
                        callback);
        selectOptionComponent.select();

        verify(this.component,
               times(1)).deactivateAll();

        verify(this.callback,
               times(1)).accept(option1);

        verify(selectOptionComponent,
               times(1)).activate();

        verify(view,
               atLeastOnce()).setSelected(eq(this.option1.getName()));
    }
}