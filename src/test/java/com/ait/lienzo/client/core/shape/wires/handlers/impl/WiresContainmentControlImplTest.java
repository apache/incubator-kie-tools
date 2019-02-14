/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 */

package com.ait.lienzo.client.core.shape.wires.handlers.impl;

import com.ait.lienzo.client.core.shape.wires.handlers.WiresParentPickerControl;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.tooling.common.api.java.util.function.Supplier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresContainmentControlImplTest {

    @Mock
    private WiresParentPickerControl parentPickerControl;

    private WiresContainmentControlImpl tested;

    @Before
    public void setUp() {
        tested = spy(new WiresContainmentControlImpl(new Supplier<WiresParentPickerControl>() {
            @Override
            public WiresParentPickerControl get() {
                return parentPickerControl;
            }
        }));
    }

    @Test
    public void destroyTest() {
        tested.destroy();
        verify(tested).clear();
    }
}
