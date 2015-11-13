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
package org.uberfire.ext.wires.core.client.layers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.user.client.ui.Composite;
import org.uberfire.ext.wires.core.api.factories.ShapeFactory;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.ext.wires.core.client.canvas.FocusableLienzoPanel;
import org.uberfire.ext.wires.core.client.util.ShapeFactoryUtil;

@Dependent
public class LayersGroup extends Composite {

    private Layer layer;
    private LienzoPanel panel;
    private List<LayerShape> stencils = new ArrayList<LayerShape>();
    private List<WiresBaseShape> shapes = new ArrayList<WiresBaseShape>();

    @Inject
    private StencilLayerBuilder stencilBuilder;

    @PostConstruct
    public void init() {
        panel = new FocusableLienzoPanel( ShapeFactoryUtil.WIDTH_PANEL,
                                          ShapeFactoryUtil.HEIGHT_PANEL );
        super.initWidget( panel );
        layer = new Layer();
        panel.getScene().add( layer );
    }

    public void addShape( final WiresBaseShape shape,
                          final ShapeFactory factory ) {
        final LayerShape stencil = stencilBuilder.build( shape,
                                                         factory );
        shapes.add( shape );
        stencils.add( stencil );

        //Add LayerShape to the UI
        stencil.setX( 0 );
        stencil.setY( ( ShapeFactoryUtil.HEIGHT_BOUNDING_LAYER + 5 ) * ( shapes.size() - 1 ) );
        layer.add( stencil );
        layer.batch();
    }

    public void deleteShape( final WiresBaseShape shape ) {
        //Remove from UI
        final int index = shapes.indexOf( shape );
        layer.remove( stencils.get( index ) );
        shapes.remove( index );
        stencils.remove( index );

        int shapeCount = 0;
        for ( LayerShape stencil : stencils ) {
            stencil.setY( ( ShapeFactoryUtil.HEIGHT_BOUNDING_LAYER + 5 ) * shapeCount );
            shapeCount++;
        }
        layer.batch();
    }

    public void clearPanel() {
        shapes.clear();
        stencils.clear();
        layer.removeAll();
        layer.batch();
    }

}