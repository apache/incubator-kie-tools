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

import java.util.Map;

import org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic.BackendFormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;


public class BackendFormRenderingContextImpl implements BackendFormRenderingContext {
    protected Long timestamp;

    protected MapModelRenderingContext renderingContext;

    protected Map<String, Object> formData;

    protected ClassLoader classLoader;

    public BackendFormRenderingContextImpl( Long timestamp,
                                            MapModelRenderingContext renderingContext,
                                            Map<String, Object> formData,
                                            ClassLoader classLoader ) {
        this.timestamp = timestamp;
        this.renderingContext = renderingContext;
        this.formData = formData;
        this.classLoader = classLoader;
    }

    @Override
    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public MapModelRenderingContext getRenderingContext() {
        return renderingContext;
    }

    @Override
    public Map<String, Object> getFormData() {
        return formData;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
