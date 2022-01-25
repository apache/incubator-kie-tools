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
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.CharacterSelectorOption;

public class CharacterListBoxFieldDefinitionTest extends AbstractFieldDefinitionTest<CharacterListBoxFieldDefinition> {

    @Override
    protected CharacterListBoxFieldDefinition getEmptyFieldDefinition() {
        return new CharacterListBoxFieldDefinition();
    }

    @Override
    protected CharacterListBoxFieldDefinition getFullFieldDefinition() {
        CharacterListBoxFieldDefinition characterListBoxFieldDefinition = getEmptyFieldDefinition();

        List<CharacterSelectorOption> options = new ArrayList<>();
        options.add(new CharacterSelectorOption('a',
                                                "one"));
        options.add(new CharacterSelectorOption('b',
                                                "two"));
        options.add(new CharacterSelectorOption('c',
                                                "three"));

        characterListBoxFieldDefinition.setOptions(options);

        characterListBoxFieldDefinition.setDefaultValue('a');

        return characterListBoxFieldDefinition;
    }
}
