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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.dynamic.impl;

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.SessionScoped;

import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContextManager;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;

@SessionScoped
public class BackendFormRenderingContextManagerImpl implements BackendFormRenderingContextManager {

    protected Map<Long, BackendFormRenderingContextImpl> contexts = new HashMap<>();

    @Override
    public BackendFormRenderingContextImpl registerContext( MapModelRenderingContext renderingContext,
                                                            Map<String, Object> formData,
                                                            ClassLoader classLoader ) {

        BackendFormRenderingContextImpl context = new BackendFormRenderingContextImpl( System.currentTimeMillis(),
                                                                                       renderingContext,
                                                                                       formData,
                                                                                       classLoader );
        contexts.put( context.getTimestamp(), context );
        return context;
    }

    @Override
    public BackendFormRenderingContextImpl getContext( Long timestamp ) {
        return contexts.get( timestamp );
    }

    @Override
    public boolean removeContext( Long timestamp ) {
        return contexts.remove( timestamp ) != null;
    }
}
