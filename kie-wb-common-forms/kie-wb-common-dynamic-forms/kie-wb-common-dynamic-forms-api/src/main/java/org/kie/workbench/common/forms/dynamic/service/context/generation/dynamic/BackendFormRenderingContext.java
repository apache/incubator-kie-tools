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

package org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic;

import java.io.Serializable;
import java.util.Map;

import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;

/**
 * Backend copy of the FormRenderingContext
 */
public interface BackendFormRenderingContext extends Serializable {

    /**
     * Retrieves the timestamp when the context was created.
     */
    Long getTimestamp();

    /**
     * Returns the client version of the context
     */
    MapModelRenderingContext getRenderingContext();

    /**
     * Returns the initial form data before send it to the form.
     */
    Map<String, Object> getFormData();

    /**
     * Class loader able to generate all the object instances required by the form
     */
    ClassLoader getClassLoader();
}
