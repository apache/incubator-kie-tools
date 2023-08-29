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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors;

import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class StringSelectorOptionTest extends AbstractSelectorOptionTest<String> {

    public StringSelectorOptionTest() {
        super("A", "B");
    }

    @Override
    protected SelectorOption newSelectorOption(String value, String text) {
        return new StringSelectorOption(value, text);
    }

    @Test
    public void testDefaultConstructor() {
        option = new DecimalSelectorOption();
        assertNull(option.getText());
        assertNull(option.getValue());
    }

    @Test
    public void testSetValue() {
        StringSelectorOption opt = new StringSelectorOption();
        opt.setValue(valueB);
        assertSame(valueB, opt.getValue());
    }

    @Test
    public void testSetText() {
        StringSelectorOption opt = new StringSelectorOption();
        opt.setText(LABEL_B);
        assertSame(LABEL_B, opt.getText());
    }
}
