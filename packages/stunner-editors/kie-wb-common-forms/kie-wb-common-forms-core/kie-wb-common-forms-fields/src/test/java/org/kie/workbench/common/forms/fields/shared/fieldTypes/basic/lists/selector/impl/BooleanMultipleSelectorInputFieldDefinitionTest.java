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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.AbstractFieldDefinitionTest;

public class BooleanMultipleSelectorInputFieldDefinitionTest extends AbstractFieldDefinitionTest<BooleanMultipleSelectorFieldDefinition> {

    @Override
    protected BooleanMultipleSelectorFieldDefinition getEmptyFieldDefinition() {
        return new BooleanMultipleSelectorFieldDefinition();
    }

    @Override
    protected BooleanMultipleSelectorFieldDefinition getFullFieldDefinition() {
        BooleanMultipleSelectorFieldDefinition fieldDefinition = new BooleanMultipleSelectorFieldDefinition();

        List<Boolean> values = new ArrayList<>();
        values.add(Boolean.TRUE);
        values.add(Boolean.FALSE);

        fieldDefinition.setListOfValues(values);

        fieldDefinition.setAllowClearSelection(false);
        fieldDefinition.setAllowFilter(false);
        fieldDefinition.setMaxDropdownElements(11);
        fieldDefinition.setMaxElementsOnTitle(2);

        return fieldDefinition;
    }
}
