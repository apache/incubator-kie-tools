/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.columns.ColumnGenerator;

@ApplicationScoped
public class ColumnGeneratorManager {

    protected ColumnGenerator defaultColumnGenerator;

    protected Map<String, ColumnGenerator> generators = new HashMap<String, ColumnGenerator>();

    @Inject
    public ColumnGeneratorManager( @Default ColumnGenerator defaultColumnGenerator ) {
        this.defaultColumnGenerator = defaultColumnGenerator;
    }

    @PostConstruct
    protected void init() {
        Collection<SyncBeanDef<ColumnGenerator>> generatorDefs = IOC.getBeanManager().lookupBeans( ColumnGenerator.class );
        for ( SyncBeanDef<ColumnGenerator> generatorDef : generatorDefs ) {
            ColumnGenerator generator = generatorDef.getInstance();
            if ( !defaultColumnGenerator.equals( generator ) ) {
                generators.put( generator.getType(), generator );
            }
        }
    }

    public ColumnGenerator getGeneratorByType( String type ) {
        ColumnGenerator generator = generators.get( type );

        if ( generator != null ) {
            return generator;
        }

        return defaultColumnGenerator;
    }
}
