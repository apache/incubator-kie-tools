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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.AbstractFieldDefinitionTest;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption;

public class StringListBoxFieldDefinitionTest extends AbstractFieldDefinitionTest<StringListBoxFieldDefinition> {

    @Override
    protected StringListBoxFieldDefinition getEmptyFieldDefinition() {
        return new StringListBoxFieldDefinition();
    }

    @Override
    protected StringListBoxFieldDefinition getFullFieldDefinition() {
        StringListBoxFieldDefinition stringListBoxFieldDefinition = new StringListBoxFieldDefinition();

        List<StringSelectorOption> options = new ArrayList<>();
        options.add(new StringSelectorOption("a",
                                             "a"));
        options.add(new StringSelectorOption("b",
                                             "b"));
        options.add(new StringSelectorOption("c",
                                             "c"));

        stringListBoxFieldDefinition.setOptions(options);

        stringListBoxFieldDefinition.setDefaultValue("a");

        return stringListBoxFieldDefinition;
    }
}
