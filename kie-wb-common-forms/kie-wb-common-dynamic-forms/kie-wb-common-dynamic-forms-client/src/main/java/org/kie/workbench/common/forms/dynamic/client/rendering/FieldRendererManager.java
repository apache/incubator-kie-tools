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

package org.kie.workbench.common.forms.dynamic.client.rendering;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.forms.model.FieldDefinition;

@ApplicationScoped
public class FieldRendererManager {

    @Inject
    private SyncBeanManager iocBeanManager;

    private Map<String, FieldRenderer> availableRenderers = new HashMap<String, FieldRenderer>();

    @PostConstruct
    protected void init() {
        Collection<SyncBeanDef<FieldRenderer>> renderers = iocBeanManager.lookupBeans( FieldRenderer.class );
        for ( SyncBeanDef<FieldRenderer> rendererDef : renderers ) {
            FieldRenderer renderer = rendererDef.getInstance();
            if ( renderer != null ) {
                availableRenderers.put( renderer.getSupportedCode(), renderer );
            }
        }
    }

    public FieldRenderer getRendererForField( FieldDefinition fieldDefinition ) {
        FieldRenderer def = availableRenderers.get( fieldDefinition.getCode() );

        if ( def != null ) {
            return iocBeanManager.lookupBean( def.getClass() ).newInstance();
        }

        return null;
    }
}
