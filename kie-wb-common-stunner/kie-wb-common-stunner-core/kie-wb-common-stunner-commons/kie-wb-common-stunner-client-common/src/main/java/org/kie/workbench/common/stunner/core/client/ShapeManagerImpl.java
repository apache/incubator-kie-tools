/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client;

import com.google.gwt.safehtml.shared.SafeUri;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@ApplicationScoped
public class ShapeManagerImpl implements ShapeManager {

    protected SyncBeanManager beanManager;
    protected DefinitionManager definitionManager;
    private final List<ShapeSet<?>> shapeSets = new LinkedList<>();
    private final List<ShapeFactory> shapeFactories = new LinkedList<>();
    private final List<ShapeSetThumbProvider> thumbProviders = new LinkedList<>();

    protected ShapeManagerImpl() {
    }

    @Inject
    public ShapeManagerImpl( final SyncBeanManager beanManager,
                             final DefinitionManager definitionManager ) {
        this.beanManager = beanManager;
        this.definitionManager = definitionManager;
    }

    @PostConstruct
    public void init() {
        initShapeSets();
        initShapeFactories();
        initThumbProviders();
    }

    private void initShapeSets() {
        shapeSets.clear();
        Collection<SyncBeanDef<ShapeSet>> beanDefs = beanManager.lookupBeans( ShapeSet.class );
        for ( SyncBeanDef<ShapeSet> beanDef : beanDefs ) {
            ShapeSet shapeSet = beanDef.getInstance();
            shapeSets.add( shapeSet );
        }

    }

    private void initShapeFactories() {
        shapeFactories.clear();
        Collection<SyncBeanDef<ShapeFactory>> beanDefs = beanManager.lookupBeans( ShapeFactory.class );
        for ( SyncBeanDef<ShapeFactory> beanDef : beanDefs ) {
            ShapeFactory shapeSet = beanDef.getInstance();
            shapeFactories.add( shapeSet );
        }

    }

    private void initThumbProviders() {
        thumbProviders.clear();
        Collection<SyncBeanDef<ShapeSetThumbProvider>> beanDefs = beanManager.lookupBeans( ShapeSetThumbProvider.class );
        for ( SyncBeanDef<ShapeSetThumbProvider> beanDef : beanDefs ) {
            ShapeSetThumbProvider shapeSet = beanDef.getInstance();
            thumbProviders.add( shapeSet );
        }

    }

    @Override
    public Collection<ShapeSet<?>> getShapeSets() {
        return shapeSets;
    }

    @Override
    public ShapeSet<?> getShapeSet( final String id ) {
        if ( null != id && !shapeSets.isEmpty() ) {
            for ( final ShapeSet<?> shapeSet : shapeSets ) {
                if ( id.equals( shapeSet.getId() ) ) {
                    return shapeSet;
                }
            }

        }
        return null;
    }

    @Override
    public ShapeSet<?> getDefaultShapeSet( final String defSetId ) {
        if ( null != defSetId && !shapeSets.isEmpty() ) {
            for ( final ShapeSet<?> shapeSet : shapeSets ) {
                if ( defSetId.equals( shapeSet.getDefinitionSetId() ) ) {
                    return shapeSet;
                }
            }

        }
        return null;
    }

    @Override
    public ShapeFactory getFactory( final String definitionId ) {
        for ( final ShapeFactory factory : shapeFactories ) {
            if ( factory.accepts( definitionId ) ) {
                return factory;
            }
        }
        return null;
    }

    @Override
    public SafeUri getThumbnail( final String definitionSetId ) {
        for ( final ShapeSetThumbProvider thumbProvider : thumbProviders ) {
            if ( thumbProvider.thumbFor( definitionSetId ) ) {
                return thumbProvider.getThumbnailUri();
            }
        }
        return null;
    }

}