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
package org.kie.uberfire.wires.bayesian.network.client.variables;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.uberfire.wires.bayesian.network.client.factory.ProbabilityFactory;
import org.kie.uberfire.wires.bayesian.network.client.shapes.EditableBayesianNode;
import com.emitrom.lienzo.client.core.shape.Layer;
import com.emitrom.lienzo.client.widget.LienzoPanel;
import com.google.gwt.user.client.ui.Composite;
import org.kie.uberfire.wires.core.api.events.ClearEvent;
import org.kie.uberfire.wires.core.api.events.ShapeSelectedEvent;

@Dependent
public class PorcentualsGroup extends Composite {

    private Layer layer;
    private LienzoPanel panel;

    @Inject
    private ProbabilityFactory factory;

    @PostConstruct
    public void init() {
        panel = new LienzoPanel( 1200,
                                 600 );
        layer = new Layer();
        panel.getScene().add( layer );
        initWidget( panel );
    }

    public void onShapeSelectedEvent( @Observes ShapeSelectedEvent event ) {
        layer.removeAll();
        if ( event.getShape() instanceof EditableBayesianNode ) {
            final EditableBayesianNode node = (EditableBayesianNode) event.getShape();
            layer.add( factory.init( node.getVariable() ) );
        }
        layer.draw();
    }

    public void clearPanel( @Observes ClearEvent event ) {
        layer.removeAll();
        layer.draw();
    }

}