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

package org.kie.workbench.common.stunner.client.lienzo.components.palette.factory;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.stunner.client.lienzo.components.palette.LienzoDefinitionSetPalette;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.AbstractPaletteFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.factory.DefaultDefSetPaletteDefinitionFactory;
import org.kie.workbench.common.stunner.core.client.components.palette.model.definition.DefinitionSetPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.view.PaletteGrid;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@Dependent
public class LienzoDefinitionSetPaletteFactoryImpl
        extends AbstractPaletteFactory<DefinitionSetPalette, LienzoDefinitionSetPalette>
        implements LienzoDefinitionSetPaletteFactory {

    @Inject
    public LienzoDefinitionSetPaletteFactoryImpl( final ShapeManager shapeManager,
                                                  final SyncBeanManager beanManager,
                                                  final Instance<DefaultDefSetPaletteDefinitionFactory> defaultPaletteDefinitionFactoryInstance,
                                                  final LienzoDefinitionSetPalette palette ) {
        super( shapeManager, beanManager, defaultPaletteDefinitionFactoryInstance, palette );
    }

    @PostConstruct
    @SuppressWarnings( "unchecked" )
    public void init() {
        super.init();
    }

    @Override
    protected void beforeBindPalette( final DefinitionSetPalette paletteDefinition,
                                      final String shapeSetId ) {
        super.beforeBindPalette( paletteDefinition, shapeSetId );
        palette.setShapeSetId( shapeSetId );
    }

    @Override
    protected void applyGrid( final PaletteGrid grid ) {
        palette.setIconSize( grid.getIconSize() );
        palette.setPadding( grid.getPadding() );
    }

}
