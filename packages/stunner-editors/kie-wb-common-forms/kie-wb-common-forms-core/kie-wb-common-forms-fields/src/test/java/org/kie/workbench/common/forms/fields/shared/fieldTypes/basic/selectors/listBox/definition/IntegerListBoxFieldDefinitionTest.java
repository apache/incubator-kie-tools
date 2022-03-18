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
 */

package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.definition;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.AbstractFieldDefinitionTest;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption;

public class IntegerListBoxFieldDefinitionTest extends AbstractFieldDefinitionTest<IntegerListBoxFieldDefinition> {

    @Override
    protected IntegerListBoxFieldDefinition getEmptyFieldDefinition() {
        return new IntegerListBoxFieldDefinition();
    }

    @Override
    protected IntegerListBoxFieldDefinition getFullFieldDefinition() {
        IntegerListBoxFieldDefinition integerListBoxFieldDefinition = getEmptyFieldDefinition();

        List<IntegerSelectorOption> options = new ArrayList<>();
        options.add(new IntegerSelectorOption(1l,
                                              "one"));
        options.add(new IntegerSelectorOption(2l,
                                              "two"));
        options.add(new IntegerSelectorOption(3l,
                                              "three"));

        integerListBoxFieldDefinition.setOptions(options);

        integerListBoxFieldDefinition.setDefaultValue(1l);

        return integerListBoxFieldDefinition;
    }
}
