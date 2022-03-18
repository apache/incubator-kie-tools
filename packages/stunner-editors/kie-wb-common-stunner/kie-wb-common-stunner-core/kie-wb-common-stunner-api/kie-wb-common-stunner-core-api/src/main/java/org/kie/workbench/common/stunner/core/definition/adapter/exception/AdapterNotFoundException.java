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

package org.kie.workbench.common.stunner.core.definition.adapter.exception;

import org.kie.workbench.common.stunner.core.definition.adapter.Adapter;

public class AdapterNotFoundException extends RuntimeException {

    private final Class<? extends Adapter> adapterClass;
    private final Class<?> type;

    public AdapterNotFoundException(final Class<? extends Adapter> adapterClass,
                                    final Class<?> type) {
        this.adapterClass = adapterClass;
        this.type = type;
    }

    @Override
    public String getMessage() {
        return "Adapter of type [" + adapterClass.getName() + "] not found for [" + type.getName() + "].";
    }
}
