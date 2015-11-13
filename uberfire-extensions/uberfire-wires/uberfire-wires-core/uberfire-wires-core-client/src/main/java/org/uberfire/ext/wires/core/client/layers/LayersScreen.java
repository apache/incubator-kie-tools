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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.wires.core.api.events.ClearEvent;
import org.uberfire.ext.wires.core.api.events.ShapeAddedEvent;
import org.uberfire.ext.wires.core.api.events.ShapeDeletedEvent;
import org.uberfire.ext.wires.core.api.factories.ShapeFactory;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.ext.wires.core.client.factories.ShapeFactoryCache;

@Dependent
@WorkbenchScreen(identifier = "WiresLayersScreen")
public class LayersScreen extends Composite {

    interface ViewBinder extends UiBinder<Widget, LayersScreen> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    PanelGroup accordion;

    @UiField
    PanelHeader headerLayers;

    @UiField
    PanelCollapse collapseLayers;

    @UiField
    public SimplePanel layers;

    @Inject
    private LayersGroup layersGroup;

    @Inject
    private ShapeFactoryCache factoriesCache;

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );

        accordion.setId( DOM.createUniqueId() );
        headerLayers.setDataParent( accordion.getId() );
        headerLayers.setDataTargetWidget( collapseLayers );

        layers.add( layersGroup );
    }

    @WorkbenchPartTitle
    @Override
    public String getTitle() {
        return "Layers";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return this;
    }

    public void onShapeAdded( @Observes ShapeAddedEvent shapeAddedEvent ) {
        final WiresBaseShape shape = shapeAddedEvent.getShape();
        for ( ShapeFactory factory : factoriesCache.getShapeFactories() ) {
            if ( factory.builds( shape ) ) {
                layersGroup.addShape( shape,
                                      factory );
            }
        }
    }

    public void onShapeDeleted( @Observes ShapeDeletedEvent shapeDeletedEvent ) {
        final WiresBaseShape shape = shapeDeletedEvent.getShape();
        layersGroup.deleteShape( shape );
    }

    public void onClear( @Observes ClearEvent event ) {
        layersGroup.clearPanel();
    }

}