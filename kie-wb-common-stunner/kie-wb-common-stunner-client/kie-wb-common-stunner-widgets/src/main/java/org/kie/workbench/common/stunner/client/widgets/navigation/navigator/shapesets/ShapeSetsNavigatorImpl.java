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

package org.kie.workbench.common.stunner.client.widgets.navigation.navigator.shapesets;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.client.widgets.event.CreateEmptyDiagramEvent;
import org.kie.workbench.common.stunner.client.widgets.navigation.navigator.NavigatorItem;
import org.kie.workbench.common.stunner.client.widgets.navigation.navigator.NavigatorView;
import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.definition.util.DefinitionUtils;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

@Dependent
public class ShapeSetsNavigatorImpl implements ShapeSetsNavigator {

    private static Logger LOGGER = Logger.getLogger( ShapeSetsNavigatorImpl.class.getName() );

    ShapeManager shapeManager;
    DefinitionUtils definitionUtils;
    Event<CreateEmptyDiagramEvent> createEmptyDiagramEventEvent;
    Instance<ShapeSetNavigatorItem> shapeSetNavigatorItemInstances;
    NavigatorView<?> view;

    private int width;
    private int height;

    private List<NavigatorItem<ShapeSet>> items = new LinkedList<>();

    @Inject
    public ShapeSetsNavigatorImpl( final ShapeManager shapeManager,
                                   final DefinitionUtils definitionUtils,
                                   final Event<CreateEmptyDiagramEvent> createEmptyDiagramEventEvent,
                                   final Instance<ShapeSetNavigatorItem> shapeSetNavigatorItemInstances,
                                   final NavigatorView<?> view ) {
        this.shapeManager = shapeManager;
        this.definitionUtils = definitionUtils;
        this.createEmptyDiagramEventEvent = createEmptyDiagramEventEvent;
        this.shapeSetNavigatorItemInstances = shapeSetNavigatorItemInstances;
        this.view = view;
        this.width = 140;
        this.height = 140;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public ShapeSetsNavigatorImpl setItemPxSize( final int width,
                                                 final int height ) {
        this.width = width;
        this.height = height;
        return this;
    }

    @SuppressWarnings( "unchecked" )
    public ShapeSetsNavigatorImpl show() {
        final Collection<ShapeSet<?>> shapeSets = shapeManager.getShapeSets();
        if ( shapeSets != null && !shapeSets.isEmpty() ) {
            for ( final ShapeSet shapeSet : shapeSets ) {
                final String id = shapeSet.getId();
                final ShapeSetNavigatorItem item = newNavigatorItem();
                items.add( item );
                view.add( item.getView() );
                item.show( shapeSet,
                        width,
                        height,
                        () -> createEmptyDiagramEventEvent.fire( new CreateEmptyDiagramEvent( id ) ) );

            }

        }
        return this;
    }

    private ShapeSetNavigatorItem newNavigatorItem() {
        return shapeSetNavigatorItemInstances.get();
    }

    public ShapeSetsNavigatorImpl clear() {
        items.clear();
        view.clear();
        return this;
    }

    @Override
    public List<NavigatorItem<ShapeSet>> getItems() {
        return items;
    }

    public NavigatorView<?> getView() {
        return view;
    }
}
