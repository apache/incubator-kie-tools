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

package org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.definition;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.AbstractFieldDefinitionTest;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.TableColumnMeta;

public class MultipleSubFormFieldDefinitionTest extends AbstractFieldDefinitionTest<MultipleSubFormFieldDefinition> {

    @Override
    protected MultipleSubFormFieldDefinition getEmptyFieldDefinition() {
        return new MultipleSubFormFieldDefinition();
    }

    @Override
    protected MultipleSubFormFieldDefinition getFullFieldDefinition() {
        MultipleSubFormFieldDefinition multipleSubFormFieldDefinition = new MultipleSubFormFieldDefinition();

        multipleSubFormFieldDefinition.setCreationForm("creationForm");
        multipleSubFormFieldDefinition.setEditionForm("editionForm");

        List<TableColumnMeta> columns = new ArrayList<>();
        columns.add(new TableColumnMeta("prop",
                                        "prop"));
        columns.add(new TableColumnMeta("prop2",
                                        "prop2"));

        multipleSubFormFieldDefinition.setColumnMetas(columns);

        return multipleSubFormFieldDefinition;
    }
}
