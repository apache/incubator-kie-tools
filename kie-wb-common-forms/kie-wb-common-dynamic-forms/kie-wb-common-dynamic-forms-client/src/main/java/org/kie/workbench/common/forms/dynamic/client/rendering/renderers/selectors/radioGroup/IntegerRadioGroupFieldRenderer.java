/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors.radioGroup;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.common.rendering.client.widgets.selectors.radiogroup.IntegerRadioGroup;
import org.kie.workbench.common.forms.common.rendering.client.widgets.selectors.radiogroup.RadioGroupBase;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.IntegerSelectorOption;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.definition.IntegerRadioGroupFieldDefinition;

@Dependent
public class IntegerRadioGroupFieldRenderer
        extends RadioGroupFieldRendererBase<IntegerRadioGroupFieldDefinition, IntegerSelectorOption, Long> {

    @Override
    public Class<IntegerRadioGroupFieldDefinition> getSupportedFieldDefinition() {
        return IntegerRadioGroupFieldDefinition.class;
    }

    @Override
    protected RadioGroupBase<Long> getRadioGroup() {
        return new IntegerRadioGroup(fieldNS);
    }
}
