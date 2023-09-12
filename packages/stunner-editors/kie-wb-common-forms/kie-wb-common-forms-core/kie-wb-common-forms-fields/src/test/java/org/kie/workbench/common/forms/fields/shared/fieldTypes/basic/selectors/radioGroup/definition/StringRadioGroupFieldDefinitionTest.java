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


package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.AbstractFieldDefinitionTest;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.StringSelectorOption;

public class StringRadioGroupFieldDefinitionTest extends AbstractFieldDefinitionTest<StringRadioGroupFieldDefinition> {

    @Override
    protected StringRadioGroupFieldDefinition getEmptyFieldDefinition() {
        return new StringRadioGroupFieldDefinition();
    }

    @Override
    protected StringRadioGroupFieldDefinition getFullFieldDefinition() {
        StringRadioGroupFieldDefinition stringRadioGroupFieldDefinition = new StringRadioGroupFieldDefinition();

        List<StringSelectorOption> options = new ArrayList<>();
        options.add(new StringSelectorOption("a",
                                             "a"));
        options.add(new StringSelectorOption("b",
                                             "b"));
        options.add(new StringSelectorOption("c",
                                             "c"));

        stringRadioGroupFieldDefinition.setOptions(options);

        stringRadioGroupFieldDefinition.setDefaultValue("a");

        return stringRadioGroupFieldDefinition;
    }
}
