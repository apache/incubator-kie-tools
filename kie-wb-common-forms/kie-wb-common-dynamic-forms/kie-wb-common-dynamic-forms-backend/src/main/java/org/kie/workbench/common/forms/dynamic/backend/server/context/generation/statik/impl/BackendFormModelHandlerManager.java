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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.service.FormModelHandler;
import org.kie.workbench.common.forms.service.FormModelHandlerManager;

@Dependent
public class BackendFormModelHandlerManager implements FormModelHandlerManager {

    protected Map<Class<? extends FormModel>, FormModelHandler> handlers = new HashMap<>();

    @Inject
    public BackendFormModelHandlerManager( Instance<FormModelHandler<? extends FormModel>> instances ) {
        instances.forEach( handler -> handlers.put( handler.getModelType(), handler ) );
    }

    @Override
    public FormModelHandler getFormModelHandler( Class<? extends FormModel> clazz ) {
        FormModelHandler handler = handlers.get( clazz );

        if ( handler != null ) {
            return handler.newInstance();
        }

        return null;
    }
}
