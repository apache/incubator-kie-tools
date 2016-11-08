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

package org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers.impl.configError.ConfigErrorFormGroupDisplayer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers.impl.def.DefaultFormGroupDisplayer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers.impl.fieldSet.FieldSetFormGroupDisplayer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers.impl.labelManaged.LabelManagedFormGroupDisplayer;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers.impl.slider.SliderFormGroupDisplayer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.CheckBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.SliderFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.MultipleSubFormFieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.subform.SubFormFieldRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;

public class FormGroupDisplayerFactory {
    protected static Map<Class<? extends FieldRenderer>, SyncBeanDef<? extends FormGroupDisplayerWidgetAware>> formGroups = new HashMap<>();

    protected static SyncBeanDef<DefaultFormGroupDisplayer> defaultDisplayer;

    protected static SyncBeanDef<ConfigErrorFormGroupDisplayer> configErrorDisplayer;

    static {
        SyncBeanManager beanManager = IOC.getBeanManager();

        formGroups.put( SliderFieldRenderer.class, beanManager.lookupBean( SliderFormGroupDisplayer.class ) );
        formGroups.put( CheckBoxFieldRenderer.class, beanManager.lookupBean( LabelManagedFormGroupDisplayer.class ) );
        formGroups.put( MultipleSubFormFieldRenderer.class, beanManager.lookupBean( FieldSetFormGroupDisplayer.class ) );
        formGroups.put( SubFormFieldRenderer.class, beanManager.lookupBean( FieldSetFormGroupDisplayer.class ) );

        defaultDisplayer = beanManager.lookupBean( DefaultFormGroupDisplayer.class );
        configErrorDisplayer = beanManager.lookupBean( ConfigErrorFormGroupDisplayer.class );
    }

    public static FormGroupDisplayerWidgetAware getGeneratorForRenderer( FormRenderingContext context,
                                                                         FieldRenderer renderer ) {
        SyncBeanDef<? extends FormGroupDisplayerWidgetAware> beanDef = formGroups.get( renderer.getClass() );

        if ( beanDef == null ) {
            beanDef = defaultDisplayer;
        }
        return beanDef.newInstance();
    }

    public static ConfigErrorFormGroupDisplayer getErrorGroup() {
        return configErrorDisplayer.newInstance();
    }
}
