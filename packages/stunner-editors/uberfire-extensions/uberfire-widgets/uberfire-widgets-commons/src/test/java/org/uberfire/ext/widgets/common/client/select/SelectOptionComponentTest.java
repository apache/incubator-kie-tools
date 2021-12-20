/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.uberfire.ext.widgets.common.client.select;

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SelectOptionComponentTest {

    @Mock
    private SelectOptionComponent.View view;

    @Mock
    private Consumer<SelectOption> callback;

    private SelectOptionComponent component;

    private SelectOptionImpl option;

    @Before
    public void setUp() {
        this.option = new SelectOptionImpl("OPTION",
                                           "Option");

        component = new SelectOptionComponent(this.view);
        component.initialize(option,
                             callback);
    }

    @Test
    public void testSelectOption() {
        this.component.select();
        verify(this.callback,
               times(1)).accept(eq(this.option));
    }

    @Test
    public void testActivate() {
        this.component.activate();
        verify(this.view,
               times(1)).setActive(true);
    }

    @Test
    public void testDeactivate() {
        this.component.deactivate();
        verify(this.view,
               times(1)).setActive(false);
    }
}