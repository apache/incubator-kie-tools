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
package org.kie.uberfire.wires.bayesian.network.client.templates;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.uberfire.wires.bayesian.network.client.events.BayesianTemplateSelectedEvent;
import com.emitrom.lienzo.client.core.event.NodeMouseClickEvent;
import com.emitrom.lienzo.client.core.event.NodeMouseClickHandler;
import com.emitrom.lienzo.client.core.shape.Layer;
import com.emitrom.lienzo.client.widget.LienzoPanel;
import com.google.common.collect.ImmutableSet;
import com.google.gwt.user.client.ui.Composite;
import org.kie.uberfire.wires.core.client.util.ShapeFactoryUtil;

@Dependent
public class TemplatesGroup extends Composite {

    private Layer layer;
    private LienzoPanel panel;

    private final ImmutableSet<String> templateNames = ImmutableSet.of( "dog-problem.xml03", "cancer.xml03", "asia.xml03", "car-starts.xml03", "elimbel2.xml03", "john-mary-call.xml03" );

    @Inject
    private Event<BayesianTemplateSelectedEvent> bayesianEvent;

    @Inject
    private StencilTemplateBuilder stencilBuilder;

    @PostConstruct
    public void init() {
        panel = new LienzoPanel( ShapeFactoryUtil.WIDTH_PANEL,
                                 ShapeFactoryUtil.HEIGHT_PANEL );
        layer = new Layer();
        panel.getScene().add( layer );
        initWidget( panel );

        drawTemplates();
    }

    private void drawTemplates() {
        //Add Template files to panel
        final List<TemplateShape> shapes = new ArrayList<TemplateShape>();
        for ( String templateName : templateNames ) {
            shapes.add( stencilBuilder.build( templateName,
                                              getTemplateClickHandler( templateName ) ) );
        }

        //Add TemplateShapes to the UI
        int shapeCount = 0;
        for ( TemplateShape shape : shapes ) {
            shape.setX( 0 );
            shape.setY( shapeCount * ( ShapeFactoryUtil.HEIGHT_BOUNDING_LAYER + ShapeFactoryUtil.SPACE_BETWEEN_BOUNDING ) );
            layer.add( shape );
            shapeCount++;
        }

        layer.draw();
    }

    private NodeMouseClickHandler getTemplateClickHandler( final String templateName ) {
        return new NodeMouseClickHandler() {
            @Override
            public void onNodeMouseClick( final NodeMouseClickEvent event ) {
                bayesianEvent.fire( new BayesianTemplateSelectedEvent( templateName ) );
            }
        };
    }

}
