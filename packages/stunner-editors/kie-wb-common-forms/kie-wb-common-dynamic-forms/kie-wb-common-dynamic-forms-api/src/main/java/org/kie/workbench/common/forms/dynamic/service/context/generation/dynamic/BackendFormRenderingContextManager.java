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

package org.kie.workbench.common.forms.dynamic.service.context.generation.dynamic;

import java.io.Serializable;
import java.util.Map;

import org.kie.workbench.common.forms.model.FormDefinition;

/**
 * Manager that handles
 */
public interface BackendFormRenderingContextManager extends Serializable {

    BackendFormRenderingContext registerContext(FormDefinition rootForm,
                                                Map<String, Object> formData,
                                                ClassLoader classLoader,
                                                FormDefinition... nestedForms);

    BackendFormRenderingContext registerContext(FormDefinition rootForm,
                                                Map<String, Object> formData,
                                                ClassLoader classLoader,
                                                Map<String, String> params,
                                                FormDefinition... nestedForms);

    BackendFormRenderingContext updateContextData(long timestamp,
                                                  Map<String, Object> formValues);

    BackendFormRenderingContext getContext(Long timestamp);

    boolean removeContext(Long timestamp);
}
