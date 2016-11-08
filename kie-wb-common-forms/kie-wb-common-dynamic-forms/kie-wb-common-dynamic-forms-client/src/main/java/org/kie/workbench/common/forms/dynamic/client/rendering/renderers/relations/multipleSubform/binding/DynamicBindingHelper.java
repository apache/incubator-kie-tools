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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.databinding.client.MapBindableProxy;
import org.jboss.errai.databinding.client.MapPropertyType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.kie.workbench.common.forms.dynamic.client.helper.MapModelBindingHelper;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.model.impl.relations.MultipleSubFormFieldDefinition;

@Dependent
public class DynamicBindingHelper extends AbstractBindingHelper<MapModelRenderingContext, MapBindableProxy, Map<String, Object>> {

    protected MapModelBindingHelper helper;

    protected MapPropertyType dynamicDefinition = null;

    @Inject
    public DynamicBindingHelper( MapModelBindingHelper helper ) {
        this.helper = helper;
    }

    @Override
    public void setUp( MultipleSubFormFieldDefinition field, MapModelRenderingContext context ) {
        super.setUp( field, context );
        dynamicDefinition = helper.getModeldefinitionFor( field, context, new HashMap<>() );
    }

    @Override
    public MapBindableProxy getProxyDefinition() {
        return (MapBindableProxy) DataBinder.forMap( dynamicDefinition.getPropertyTypes() ).getModel();
    }

    @Override
    public MapBindableProxy getNewProxy() {
        return getProxyForModel( new HashMap<>() );
    }

    @Override
    public MapBindableProxy getProxyForModel( Map<String, Object> model ) {
        Map<String, Object> content = DataBinder.forMap( dynamicDefinition.getPropertyTypes() ).getModel();

        helper.prepareMapContent( content,
                                  context.getAvailableForms().get( field.getCreationForm() ),
                                  model,
                                  context );

        return (MapBindableProxy) content;
    }

    @Override
    public void afterEdit( Map<String, Object> model ) {
        model.put( MapModelRenderingContext.FORM_ENGINE_EDITED_OBJECT, Boolean.TRUE );
    }
}
