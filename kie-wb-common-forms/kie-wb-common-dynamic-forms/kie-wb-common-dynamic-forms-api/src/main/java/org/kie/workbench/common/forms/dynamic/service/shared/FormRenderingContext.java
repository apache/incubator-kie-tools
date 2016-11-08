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

package org.kie.workbench.common.forms.dynamic.service.shared;

import java.util.Map;

import org.kie.workbench.common.forms.model.FormDefinition;

public interface FormRenderingContext<T> {
    FormDefinition getRootForm();

    void setRootForm( FormDefinition rootForm );

    void setModel( T model );

    T getModel();

    void setRenderMode( RenderMode renderMode );

    RenderMode getRenderMode();

    FormRenderingContext getParentContext();

    void setParentContext( FormRenderingContext parentContext );

    Map<String, FormDefinition> getAvailableForms();

    FormRenderingContext getCopyFor( String formKey, T model );
}
