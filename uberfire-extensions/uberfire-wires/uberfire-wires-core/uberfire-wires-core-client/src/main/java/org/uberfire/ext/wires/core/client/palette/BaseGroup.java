/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.wires.core.client.palette;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.user.client.ui.Composite;
import org.uberfire.ext.wires.core.api.factories.ShapeFactory;
import org.uberfire.ext.wires.core.api.factories.categories.Category;
import org.uberfire.ext.wires.core.client.canvas.FocusableLienzoPanel;
import org.uberfire.ext.wires.core.client.factories.ShapeFactoryCache;
import org.uberfire.ext.wires.core.client.factories.StringFactoryHelper;
import org.uberfire.ext.wires.core.client.util.ShapeFactoryUtil;
import org.uberfire.ext.wires.core.client.util.ShapesUtils;

public abstract class BaseGroup extends Composite {

    private Layer layer;
    private LienzoPanel panel;

    @Inject
    private ShapeFactoryCache factoriesCache;

    @Inject
    private StencilPaletteBuilder stencilBuilder;

    @PostConstruct
    public void init() {
        panel = new FocusableLienzoPanel( ShapeFactoryUtil.WIDTH_PANEL,
                                          ShapesUtils.calculateHeight( ShapesUtils.getNumberOfShapesInCategory( getCategory(),
                                                                                                                factoriesCache.getShapeFactories() ) ) );
        layer = new Layer();
        panel.getScene().add( layer );
        initWidget( panel );

        drawStencils();
    }

    public abstract Category getCategory();

    protected void drawStencils() {
        //Get PaletteShape for each Factory
        final Category category = getCategory();
        final List<PaletteShape> shapes = new ArrayList<PaletteShape>();
        for ( ShapeFactory factory : factoriesCache.getShapeFactories() ) {
            if ( factory.getCategory().equals( category ) ) {
                shapes.add( stencilBuilder.build( panel,
                                                  new StringFactoryHelper( factory.getShapeDescription() ),
                                                  factory ) );
            }
        }

        //Add PaletteShapes to the UI
        int shapeCount = 1;
        for ( PaletteShape shape : shapes ) {
            shape.setX( PaletteLayoutUtilities.getX( shapeCount ) );
            shape.setY( PaletteLayoutUtilities.getY( shapeCount ) );
            layer.add( shape );
            shapeCount++;
        }

        layer.batch();
    }

}