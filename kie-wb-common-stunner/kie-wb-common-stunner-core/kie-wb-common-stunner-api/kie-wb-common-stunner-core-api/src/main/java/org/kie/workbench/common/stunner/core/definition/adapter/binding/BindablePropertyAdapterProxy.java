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

package org.kie.workbench.common.stunner.core.definition.adapter.binding;

import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapterWrapper;

public abstract class BindablePropertyAdapterProxy<T, V> extends PropertyAdapterWrapper<T, V, BindablePropertyAdapter<T, V>> {

    protected abstract void setBindings(BindablePropertyAdapter<T, V> adapter);

    protected BindablePropertyAdapterProxy() {
    }

    @SuppressWarnings("unchecked")
    public BindablePropertyAdapterProxy(final BindableAdapterFactory adapterFactory) {
        super(adapterFactory.newBindablePropertyAdapter());
        setBindings(adapter);
    }
}
