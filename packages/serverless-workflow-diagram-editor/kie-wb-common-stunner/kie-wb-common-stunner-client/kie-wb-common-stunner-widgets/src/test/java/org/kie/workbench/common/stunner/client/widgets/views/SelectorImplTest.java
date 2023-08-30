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


package org.kie.workbench.common.stunner.client.widgets.views;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class SelectorImplTest {

    @Mock
    private SelectorView view;

    @Mock
    private Command valueChangedCommand;

    private SelectorImpl<SelectorTestObject> tested;

    @Before
    public void setup() throws Exception {
        tested = new SelectorImpl<>(view);
        tested
                .setTextProvider(obj -> obj.text)
                .setValueProvider(Enum::name)
                .setItemProvider(SelectorTestObject::valueOf)
                .setValueChangedCommand(valueChangedCommand);
    }

    @Test
    public void testInit() {
        tested.init();
        verify(view, times(1)).init(eq(tested));
    }

    @Test
    public void testAddItem() {
        tested.addItem(SelectorTestObject.ITEM1);
        verify(view, times(1)).add(eq(SelectorTestObject.ITEM1.text),
                                   eq(SelectorTestObject.ITEM1.name()));
    }

    @Test
    public void testSetSelectedItem() {
        tested.setSelectedItem(SelectorTestObject.ITEM1);
        verify(view, times(1)).setValue(eq(SelectorTestObject.ITEM1.name()));
    }

    @Test
    public void testGetSelectedItem() {
        when(view.getValue()).thenReturn(SelectorTestObject.ITEM1.name());
        SelectorTestObject selectedItem = tested.getSelectedItem();
        assertEquals(SelectorTestObject.ITEM1, selectedItem);
    }

    @Test
    public void testClear() {
        tested.clear();
        verify(view, times(1)).clear();
    }

    @Test
    public void testOnValueChanged() {
        tested.onValueChanged();
        verify(valueChangedCommand, times(1)).execute();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(view, times(1)).clear();
    }

    private enum SelectorTestObject {
        ITEM1("name1"),
        ITEM2("name2");

        private final String text;

        SelectorTestObject(String text) {
            this.text = text;
        }
    }
}
