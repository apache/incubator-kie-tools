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
package org.uberfire.ext.wires.core.client.actions;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import org.uberfire.ext.wires.core.api.events.ClearEvent;
import org.uberfire.ext.wires.core.client.canvas.FocusableLienzoPanel;
import org.uberfire.ext.wires.core.client.palette.PaletteLayoutUtilities;
import org.uberfire.ext.wires.core.client.resources.AppResource;
import org.uberfire.ext.wires.core.client.util.ShapeFactoryUtil;
import org.uberfire.ext.wires.core.client.util.ShapesUtils;

@Dependent
public class ActionsGroup extends Composite {

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
        shapes.add( stencilBuilder.build( getClearCanvasClickHandler(),
                                          AppResource.INSTANCE.images().clear() ) );
        layer.batch();

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
                if ( Window.confirm( "Are you sure to clean the canvas?" ) ) {
                    clearEvent.fire( new ClearEvent() );
                }
            }
        };
    }

}
