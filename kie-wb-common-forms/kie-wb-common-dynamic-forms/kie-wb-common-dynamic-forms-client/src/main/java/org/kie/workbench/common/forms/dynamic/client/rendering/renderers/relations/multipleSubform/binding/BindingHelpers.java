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

import org.jboss.errai.ioc.client.container.IOC;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.model.impl.relations.MultipleSubFormFieldDefinition;

public class BindingHelpers {
    private static Map<Class<? extends FormRenderingContext>, Class<? extends BindingHelper>> helpers = new HashMap<>();

    public static BindingHelper getHelper( FormRenderingContext context, MultipleSubFormFieldDefinition field ) {

        Class<? extends BindingHelper> helperClazz = null;

        if ( context instanceof MapModelRenderingContext ) {
            helperClazz = DynamicBindingHelper.class;
        } else {
            helperClazz = StaticBindingHelper.class;
        }

        BindingHelper helper = IOC.getBeanManager().lookupBean( helperClazz ).newInstance();

        helper.setUp( field, context );

        return helper;
    }
}
