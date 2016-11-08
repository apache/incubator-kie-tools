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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.binding;

import org.jboss.errai.databinding.client.BindableProxy;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.model.impl.relations.MultipleSubFormFieldDefinition;

public abstract class AbstractBindingHelper<C extends FormRenderingContext, P extends BindableProxy, M> implements BindingHelper<C, P, M> {

    protected C context;
    protected MultipleSubFormFieldDefinition field;

    @Override
    public void setUp( MultipleSubFormFieldDefinition field, C context ) {
        this.field = field;
        this.context = context;
    }
}
