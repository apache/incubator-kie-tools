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
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.DecimalSelectorOption;

public class DecimalListBoxFieldDefinitionTest extends AbstractFieldDefinitionTest<DecimalListBoxFieldDefinition> {

    @Override
    protected DecimalListBoxFieldDefinition getEmptyFieldDefinition() {
        return new DecimalListBoxFieldDefinition();
    }

    @Override
    protected DecimalListBoxFieldDefinition getFullFieldDefinition() {
        DecimalListBoxFieldDefinition decimalListBoxFieldDefinition = getEmptyFieldDefinition();

        List<DecimalSelectorOption> options = new ArrayList<>();
        options.add(new DecimalSelectorOption(1d,
                                              "one"));
        options.add(new DecimalSelectorOption(2d,
                                              "two"));
        options.add(new DecimalSelectorOption(3d,
                                              "three"));

        decimalListBoxFieldDefinition.setOptions(options);

        decimalListBoxFieldDefinition.setDefaultValue(1d);

        return decimalListBoxFieldDefinition;
    }
}
