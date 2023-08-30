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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.databinding.client.BindableListWrapper;
import org.kie.workbench.common.forms.dynamic.client.config.ClientSelectorDataProviderManager;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.service.shared.BackendSelectorDataProviderService;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.SelectorDataProviderManager;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorFieldBaseDefinition;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorOption;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeListener;

public abstract class SelectorFieldRenderer<FIELD extends SelectorFieldBaseDefinition<OPTION, TYPE>, OPTION extends SelectorOption<TYPE>, TYPE> extends FieldRenderer<FIELD, DefaultFormGroup> {

    @Inject
    protected SelectorDataProviderManager clientProviderManager;

    @Inject
    protected Caller<BackendSelectorDataProviderService> backendSelectorDataProviderService;

    @Override
    public void init(FormRenderingContext renderingContext,
                     FIELD field) {
        super.init(renderingContext,
                   field);
        if (field.getRelatedField() != null) {
            fieldChangeListeners.add(new FieldChangeListener(field.getRelatedField(),
                                                             (fieldName, newValue) -> refreshSelectorOptions()));
        }
    }

    public void refreshSelectorOptions() {
        if (field.getDataProvider() != null && !field.getDataProvider().isEmpty()) {
            if (field.getDataProvider().startsWith(ClientSelectorDataProviderManager.PREFFIX)) {
                refreshSelectorOptions(clientProviderManager.getDataFromProvider(
                        renderingContext,
                        field.getDataProvider()));
            } else {
                backendSelectorDataProviderService.call(new RemoteCallback<SelectorData>() {
                    @Override
                    public void callback(SelectorData data) {
                        refreshSelectorOptions(data);
                    }
                }).getDataFromProvider(renderingContext,
                                       field.getDataProvider());
            }
        } else {
            refreshSelectorOptions(field.getOptions());
        }
    }

    public void refreshSelectorOptions(List<OPTION> options) {

        if (options instanceof BindableListWrapper) {
            options = ((BindableListWrapper) options).deepUnwrap();
        }

        Map<TYPE, String> optionsValues = new HashMap<>();

        for (OPTION option : options) {
            optionsValues.put(option.getValue(),
                              option.getText());
        }

        refreshInput(optionsValues,
                     field.getDefaultValue());
    }

    public void refreshSelectorOptions(SelectorData<TYPE> data) {

        Optional<TYPE> selectedValue = Optional.ofNullable(data.getSelectedValue());

        refreshInput(data.getValues(),
                     selectedValue.orElse(field.getDefaultValue()));
    }

    protected abstract void refreshInput(Map<TYPE, String> optionsValues,
                                         TYPE defaultValue);
}
