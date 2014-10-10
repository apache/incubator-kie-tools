/*
 * Copyright 2014 JBoss Inc
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
package org.kie.uberfire.wires.core.client.actions;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.emitrom.lienzo.client.core.event.NodeMouseClickEvent;
import com.emitrom.lienzo.client.core.event.NodeMouseClickHandler;
import com.emitrom.lienzo.client.core.shape.Layer;
import com.emitrom.lienzo.client.core.shape.Picture;
import com.emitrom.lienzo.client.widget.LienzoPanel;
import com.google.gwt.user.client.ui.Composite;
import org.kie.uberfire.wires.core.api.events.ClearEvent;
import org.kie.uberfire.wires.core.client.canvas.FocusableLienzoPanel;
import org.kie.uberfire.wires.core.client.palette.PaletteLayoutUtilities;
import org.kie.uberfire.wires.core.client.resources.AppResource;
import org.kie.uberfire.wires.core.client.util.ShapeFactoryUtil;
import org.kie.uberfire.wires.core.client.util.ShapesUtils;

@Dependent
public class ActionsGroup extends Composite {

    private static final String PICTURE_CATEGORY = "ActionsGroupPictureCategory";

    private Layer layer;
    private LienzoPanel panel;

    @Inject
    private Event<ClearEvent> clearEvent;

    @Inject
    private StencilActionBuilder stencilBuilder;

    @PostConstruct
    public void init() {
        panel = new FocusableLienzoPanel( ShapeFactoryUtil.WIDTH_PANEL,
                                          ShapesUtils.calculateHeight( 1 ) );
        layer = new Layer();
        panel.getScene().add( layer );
        initWidget( panel );

        drawActions();
    }

    private void drawActions() {
        //Hard-coded list of ActionShapes
        final List<ActionShape> shapes = new ArrayList<ActionShape>();
        shapes.add( stencilBuilder.build( PICTURE_CATEGORY,
                                          getClearCanvasClickHandler(),
                                          AppResource.INSTANCE.images().clear() ) );

        //Draw containing Layer when images have been loaded
        Picture.onCategoryLoaded( PICTURE_CATEGORY,
                                  new Runnable() {
                                      @Override
                                      public void run() {
                                          layer.draw();
                                      }
                                  } );

        //Add ActionShapes to the UI
        int shapeCount = 1;
        for ( ActionShape shape : shapes ) {
            shape.setX( 0 );
            shape.setY( PaletteLayoutUtilities.getY( shapeCount ) );
            layer.add( shape );
            shapeCount++;
        }
    }

    private NodeMouseClickHandler getClearCanvasClickHandler() {
        return new NodeMouseClickHandler() {
            @Override
            public void onNodeMouseClick( final NodeMouseClickEvent event ) {
                clearEvent.fire( new ClearEvent() );
            }
        };
    }

}
