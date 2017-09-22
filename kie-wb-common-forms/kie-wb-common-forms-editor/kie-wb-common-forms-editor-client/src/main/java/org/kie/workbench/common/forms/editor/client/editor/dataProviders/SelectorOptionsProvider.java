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

package org.kie.workbench.common.forms.editor.client.editor.dataProviders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SystemSelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorFieldBaseDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorOption;

@Dependent
public class SelectorOptionsProvider implements SystemSelectorDataProvider {

    @Override
    public String getProviderName() {
        return SelectorOptionsProvider.class.getName();
    }

    @Override
    public SelectorData getSelectorData(FormRenderingContext context) {
        SelectorData data = new SelectorData<>();

        Map<Object, String> values = new HashMap<>();

        Object selectedValue = null;

        if (context.getModel() instanceof SelectorFieldBaseDefinition) {
            SelectorFieldBaseDefinition selector = (SelectorFieldBaseDefinition) context.getModel();
            selectedValue = selector.getDefaultValue();
            List<SelectorOption> options = selector.getOptions();
            options.forEach(option -> values.put(option.getValue(),
                                                 option.getText()));

            if (!values.containsKey(selectedValue)) {
                selectedValue = null;
                selector.setDefaultValue(null);
            }
        }

        data.setValues(values);
        data.setSelectedValue(selectedValue);
        return data;
    }
}
