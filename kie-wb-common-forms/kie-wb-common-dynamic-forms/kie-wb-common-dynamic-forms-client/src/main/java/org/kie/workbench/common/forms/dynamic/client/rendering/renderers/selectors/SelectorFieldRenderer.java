/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.selectors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.forms.dynamic.client.config.ClientSelectorDataProviderManager;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.service.shared.BackendSelectorDataProviderService;
import org.kie.workbench.common.forms.dynamic.service.shared.SelectorDataProviderManager;
import org.kie.workbench.common.forms.model.impl.basic.selectors.SelectorFieldBase;
import org.kie.workbench.common.forms.model.impl.basic.selectors.SelectorOption;

public abstract class SelectorFieldRenderer<F extends SelectorFieldBase, O extends SelectorOption<T>, T> extends FieldRenderer<F> {

    @Inject
    protected SelectorDataProviderManager clientProviderManager;

    @Inject
    protected Caller<BackendSelectorDataProviderService> backendSelectorDataProviderService;

    public void refreshSelectorOptions() {
        if ( field.getDataProvider() != null && !field.getDataProvider().isEmpty() ) {
            if ( field.getDataProvider().startsWith( ClientSelectorDataProviderManager.PREFFIX ) ) {
                refreshSelectorOptions( clientProviderManager.getDataFromProvider(
                        renderingContext,
                        field.getDataProvider() ) );
            } else {
                backendSelectorDataProviderService.call( new RemoteCallback<SelectorData>() {
                    @Override
                    public void callback( SelectorData data ) {
                        refreshSelectorOptions( data );
                    }
                } ).getDataFromProvider( renderingContext, field.getDataProvider() );
            }
        } else {
            refreshSelectorOptions( field.getOptions() );
        }
    }

    public void refreshSelectorOptions( List<O> options ) {
        Map<T, String> optionsValues = new HashMap<>();
        T defaultValue = null;

        for ( O option : options ) {
            optionsValues.put( option.getValue(), option.getText() );
            if ( option.isDefaultValue() ) {
                defaultValue = option.getValue();
            }
        }

        refreshInput( optionsValues, defaultValue );
    }

    public void refreshSelectorOptions( SelectorData<T> data ) {
        refreshInput( data.getValues(), data.getSelectedValue() );
    }

    protected abstract void refreshInput( Map<T, String> optionsValues, T defaultValue );
}
