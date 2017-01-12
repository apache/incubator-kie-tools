/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.lienzo.shape.view.glyph;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.wires.WiresConnector;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.types.BoundingBox;
import org.kie.workbench.common.stunner.client.lienzo.util.LienzoUtils;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.AbstractGlyphShapeBuilder;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphShapeDef;

@ApplicationScoped
public class LienzoShapesGlyphBuilder extends AbstractGlyphShapeBuilder<Group> {

    private final FactoryManager factoryManager;

    protected LienzoShapesGlyphBuilder() {
        this( null );
    }

    @Inject
    public LienzoShapesGlyphBuilder( final FactoryManager factoryManager ) {
        this.factoryManager = factoryManager;
    }

    @Override
    protected FactoryManager getFactoryManager() {
        return factoryManager;
    }

    @Override
    protected Glyph<Group> doBuild( final Shape<?> shape ) {
        final ShapeView<?> view = shape.getShapeView();
        Group group = null;
        BoundingBox bb = null;
        if ( view instanceof WiresShape ) {
            group = ( ( WiresShape ) view ).getGroup();
            bb = ( ( WiresShape ) view ).getPath().getBoundingBox();
        } else if ( view instanceof WiresConnector ) {
            final WiresConnector wiresConnector = ( WiresConnector ) view;
            group = wiresConnector.getGroup();
            bb = wiresConnector.getGroup().getBoundingBox();
        }

        if ( null == group ) {
            throw new RuntimeException( "Shape view [" + view.toString() + "] not supported for " +
                                                "this shape glyph builder [" + this.getClass().getName() );
        }

        if ( view instanceof HasTitle ) {
            final HasTitle hasTitle = ( HasTitle ) view;
            hasTitle.setTitle( null );
        }

        // Create a copy of this view.
        group = group.copy();
        // Scale, if necessary, to the given glyph size.
        final double[] scale = LienzoUtils.getScaleFactor( bb.getWidth(),
                                                           bb.getHeight(),
                                                           width,
                                                           height );
        group.setScale( scale[ 0 ],
                        scale[ 1 ] );
        return new LienzoShapeGlyph( group,
                                     width,
                                     height );
    }

    @Override
    public Class<?> getType() {
        return GlyphShapeDef.class;
    }
}
