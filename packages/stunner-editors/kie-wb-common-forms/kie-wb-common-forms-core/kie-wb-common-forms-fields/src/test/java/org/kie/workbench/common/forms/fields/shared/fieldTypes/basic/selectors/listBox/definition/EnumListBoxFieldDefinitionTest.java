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
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.EnumSelectorOption;

public class EnumListBoxFieldDefinitionTest extends AbstractFieldDefinitionTest<EnumListBoxFieldDefinition> {

    @Override
    protected EnumListBoxFieldDefinition getEmptyFieldDefinition() {
        return new EnumListBoxFieldDefinition();
    }

    @Override
    protected EnumListBoxFieldDefinition getFullFieldDefinition() {
        EnumListBoxFieldDefinition enumListBoxFieldDefinition = new EnumListBoxFieldDefinition();

        List<EnumSelectorOption> options = new ArrayList<>();
        options.add(new EnumSelectorOption(Values.ONE));
        options.add(new EnumSelectorOption(Values.TWO));
        options.add(new EnumSelectorOption(Values.THREE));

        enumListBoxFieldDefinition.setOptions(options);

        enumListBoxFieldDefinition.setDefaultValue(Values.ONE);

        return enumListBoxFieldDefinition;
    }

    public enum Values {
        ONE,
        TWO,
        THREE
    }
}
