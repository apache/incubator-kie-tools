/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.backend.definition.adapter.binding;

import org.kie.workbench.common.stunner.core.definition.adapter.binding.*;
import org.kie.workbench.common.stunner.core.definition.util.DefinitionUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class RuntimeBindableAdapterFactory implements BindableAdapterFactory {

    DefinitionUtils definitionUtils;

    protected RuntimeBindableAdapterFactory() {
        this( null );
    }

    @Inject
    public RuntimeBindableAdapterFactory( final DefinitionUtils definitionUtils ) {
        this.definitionUtils = definitionUtils;
    }

    public BindableDefinitionAdapter newBindableDefinitionAdapter() {
        return new RuntimeBindableDefinitionAdapter( definitionUtils );
    }

    public BindableDefinitionSetAdapter newBindableDefinitionSetAdapter() {
        return new RuntimeBindableDefinitionSetAdapter();
    }

    public BindablePropertyAdapter newBindablePropertyAdapter() {
        return new RuntimeBindablePropertyAdapter();
    }

    public BindablePropertySetAdapter<Object> newBindablePropertySetAdapter() {
        return new RuntimeBindablePropertySetAdapter<>();
    }

}
